package com.chatbot.whatsapp_chatbot.insurance.service;

import com.chatbot.whatsapp_chatbot.insurance.production.entity.Policy;
import com.chatbot.whatsapp_chatbot.insurance.production.repository.PolicyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InsuranceMessageService {

    private static final int MAX_HISTORY_ENTRIES = 6;

    private final PolicyRepository policyRepository;
    private final GeminiIntentDetectionService geminiIntentDetectionService;
    private final InteractionLogService interactionLogService;

    public InsuranceMessageService(PolicyRepository policyRepository, InteractionLogService interactionLogService, GeminiIntentDetectionService geminiIntentDetectionService) {
        this.policyRepository = policyRepository;
        this.interactionLogService = interactionLogService;
        this.geminiIntentDetectionService = geminiIntentDetectionService;
    }

    private final Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();

    public String processMessages(String phoneNumber, String messageContent) {
        String cleanPhone = phoneNumber.startsWith("whatsapp:") ? phoneNumber.substring("whatsapp:".length()) : phoneNumber;

        UserSession userSession = activeSessions.computeIfAbsent(cleanPhone, k -> new UserSession(cleanPhone));

        userSession.setLastInteractionTime(LocalDateTime.now());

        String cleanMessage = messageContent.trim().toLowerCase();

        String response;

        if (cleanMessage.equals("buenos dias") || cleanMessage.equals("hola")) {
            response = showWelcome();
        } else if (cleanMessage.equals("adios") || cleanMessage.equals("0")) {
            response = endConversation(userSession, cleanPhone);
        } else if (userSession.getPolicyNumber() != null && isMenuOption(cleanMessage)) {
            response = handleMenuChoice(userSession, cleanMessage);
        } else if (looksLikeNationalId(cleanMessage)) {
            response = registerPolicy(userSession, cleanMessage);
        } else if (userSession.getPolicyNumber() != null) {
            response = handleFreeText(userSession, cleanMessage);
        } else {
            response = "Lo sentimos, pero no logramos entender lo que necesitas.\n\n" +
                    "Favor ingresa 'hola' para dar inicio o\n" +
                    "ingresa 'adios' para finalizar";
        }

        List<Map<String, String>> history = userSession.getConversationHistory();
        history.add(Map.of("role", "user", "content", cleanMessage));
        history.add(Map.of("role", "model", "content", response));
        while (history.size() > MAX_HISTORY_ENTRIES) {
            history.remove(0);
        }

        return response;
    }

    private String showWelcome() {
        return "Gracias por contactarte con nuestra aseguradora! 👋\n\n" +
                "Por favor brindanos tu No. de DUI, tal cual aparece en tu documento";
    }

    private String registerPolicy(UserSession session, String nationalId) {
        Optional<Policy> policyOptional = policyRepository.findByNationalId(nationalId);

        if (policyOptional.isEmpty()) {
            return "DUI no encontrado: " + nationalId + "\n\n" +
                    "Por favor verifica el número e intenta de nuevo.";
        }

        Policy policy = policyOptional.get();

        session.setPolicyNumber(policy.getPolicyNumber());
        session.setPolicy(policy);

        return " Bienvenido: " + policy.getCustomerFirstName() + " " + policy.getCustomerLastName() + "\n" +
                "Póliza: " + policy.getPolicyNumber() + "\n\n" +
                showMenu();
    }

    private String showMenu() {
        StringBuilder menu = new StringBuilder("📋 Por favor, presiona el numero correspondiente a la accion que deseas ejecutar?\n\n");
        for (MenuOption option : MenuOption.values()) {
            menu.append(option.getKey()).append("️⃣ ").append(option.getDescription()).append("\n");
        }
        menu.append("0️⃣ Deseo finalizar la sesion\n");
        return menu.toString();
    }

    private String handleMenuChoice(UserSession session, String menuChoice) {
        MenuOption option = MenuOption.byKey(menuChoice);
        if (option == null) {
            return "Opción inválida. Por favor elige una opción entre 1 y 8.\n\n" + showMenu();
        }

        Optional<Policy> policyOptional = policyRepository.findByPolicyNumber(session.getPolicyNumber());
        if (policyOptional.isEmpty()) {
            return "No pudimos encontrar tu poliza. Por favor ingresa tu DUI nuevamente.";
        }
        Policy policyBeingWorked = policyOptional.get();

        session.addAction(option.getAction());

        if (menuChoice.equals("8")) {
            session.setRequestedRepresentative(true);
        }

        String response = option.respond(policyBeingWorked);

        return response + "\n\n" + showMenu();
    }

    private String handleFreeText(UserSession session, String message) {
        int intent = geminiIntentDetectionService.detectIntent(session.getConversationHistory(), message);

        if (intent >= 1 && intent <= 8) {
            return handleMenuChoice(session, String.valueOf(intent));
        }

        return "No logré identificar tu solicitud. ¿Podrías ser más específico?\n\n" + showMenu();
    }

    private String endConversation(UserSession session, String phoneNumber) {
        interactionLogService.saveInteractionLog(session);

        activeSessions.remove(phoneNumber);

        return "Has finalizado la sesion, buen dia!";
    }


    private boolean looksLikeNationalId(String text) {
        // El Salvador DUI format: 12345678-9 (8 digits, dash, 1 digit)
        return text.matches("^\\d{8}-\\d$");
    }

    private boolean isMenuOption(String text) {
        return text.matches("^[1-8]$");
    }

    @Scheduled(fixedRate = 60000)
    private void cleanUpSessions() {
        activeSessions.entrySet().removeIf(entry -> entry.getValue().getLastInteractionTime().isBefore(LocalDateTime.now().minusMinutes(30)));

    }
}
