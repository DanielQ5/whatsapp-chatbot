package com.chatbot.whatsapp_chatbot.insurance.service;

import com.chatbot.whatsapp_chatbot.insurance.production.entity.Policy;
import com.chatbot.whatsapp_chatbot.insurance.production.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class InsuranceMessageService {


    private final PolicyRepository policyRepository;

    private final InteractionLogService interactionLogService;

    public InsuranceMessageService(PolicyRepository policyRepository, InteractionLogService interactionLogService) {
        this.policyRepository = policyRepository;
        this.interactionLogService = interactionLogService;
    }

    //TODO Insert Section 2
    private final Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();


    private static final Map<String, String> MENU_TO_ACTION = Map.of(
            "1", "policy_type",
            "2", "deductible",
            "3", "max_coverage",
            "4", "coverage_start_date",
            "5", "monthly_premium",
            "6", "payment_cycle",
            "7", "claim_format",
            "8", "speak_to_representative"
    );

    private static final Map<String, Function<Policy, String>> MENU_HANDLERS = Map.of(
            "1", policy -> "Tipo de Poliza: " + policy.getPolicyType(),
            "2", policy -> "El Deducible es: $" + policy.getDeductible(),
            "3", policy -> "El Maximo a cubrir es de: $" + policy.getMaxCoverage(),
            "4", policy -> "La fecha efectiva de cobertura es: " + policy.getEffectiveDate(),
            "5", policy -> "La cuota a cancelar es de: $" + policy.getMonthlyPremium(),
            "6", policy -> "La frecuencia de pago es: " + policy.getPaymentCycle(),
            "7", policy -> "La poliza se encuentra " + policy.getPolicyStatus() + " , con fecha de expiracion de: " + policy.getExpirationDate(),
            "8", policy -> "Conectandote con un ejecutivo en estos momentos..."
            );

    public String processMessages(String phoneNumber, String messageContent) {
        String cleanPhone = phoneNumber.startsWith("whatsapp:") ? phoneNumber.substring("whatsapp:".length()) : phoneNumber;

        UserSession userSession = activeSessions.computeIfAbsent(cleanPhone, k -> new UserSession(cleanPhone));

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
        } else {
            response = "Lo sentimos, pero no logramos entender lo que necesitas.\n\n" +
                    "Favor ingresa 'hola' para dar inicio o\n" +
                    "ingresa 'adios' para finalizar";
        }

        return response;
    }

    private String showWelcome() {
        return "Gracias por contactarte con nuestra aseguradora! 👋\n\n" +
                "Por favor brindanos tu No. de DUI, tal cual aparece en tu documento";
    }

    private String registerPolicy(UserSession session, String nationalId) {
        // 1. Query database: policyRepo.findByPolicyNumber(policyNumber)
        Optional<Policy> policyOptional = policyRepository.findByNationalId(nationalId);

        // 2. If policy not found, return error message
        if (policyOptional.isEmpty()) {
            return "DUI no encontrado: " + nationalId + "\n\n" +
                    "Por favor verifica el número e intenta de nuevo.";
        }

        // Store policy number in session (for querying later)
        Policy policy = policyOptional.get();

        session.setPolicyNumber(policy.getPolicyNumber());
        session.setPolicy(policy);

        // 4. Return welcome message with showMenu()

        return " Bienvenido: " + policy.getCustomerFirstName() + " " + policy.getCustomerLastName() + "\n" +
                "Póliza: " + policy.getPolicyNumber() + "\n\n" +
                showMenu();
    }

    private String showMenu() {
        return "📋 Por favor, presiona el numero correspondiente a la accion que deseas ejecutar?\n\n" +
                "1️⃣ Quiero saber que tipo de poliza tengo.\n" +
                "2️⃣ Quiero saber cuanto es mi deducible\n" +
                "3️⃣ Quiero saber cual es mi maximo vitalicio o anual\n" +
                "4️⃣ Quiero saber cuando inicio mi cobertura\n" +
                "5️⃣ Quiero saber cuanto es mi pago por la cobertura\n" +
                "6️⃣ Quiero saber la frecuencia con la que debo realizar mis pagos\n" +
                "7️⃣ Quiero saber cuando expira mi cobertura\n" +
                "8️⃣ Deseo ser atendido por un ejecutivo de atencion\n" +
                "0️⃣ Deseo finalizar la sesion\n";
    }

    private String handleMenuChoice(UserSession session, String menuChoice) {

        // 1. Get policy number from session
        Policy policyBeingWorked = session.getPolicy();

        // 3. Track action: session.addAction(MENU_TO_ACTION.get(choice))
        String trackAction = MENU_TO_ACTION.get(menuChoice);
        session.addAction(trackAction);

        if (menuChoice.equals("8")) {
            session.setRequestedRepresentative(true);
        }
        // 4. Use switch/if-else to return appropriate response

        String response = MENU_HANDLERS.get(menuChoice).apply(policyBeingWorked);


        // 5. Append showMenu() at the end
        return response + "\n\n" + showMenu();
    }

    private String endConversation(UserSession session, String phoneNumber) {
        // TODO:
        // 1. Save to database (call saveInteractionLog)

        if (session.getPolicyNumber() != null) {
            interactionLogService.saveInteractionLog(session);
        }

        // 2. Remove from activeSessions
        activeSessions.remove(phoneNumber);
        // 3. Return goodbye message

        return "Has finalizado la sesion, buen dia!";
    }


    private boolean looksLikeNationalId(String text) {
        // El Salvador DUI format: 12345678-9 (8 digits, dash, 1 digit)
        return text.matches("^\\d{8}-\\d$");
    }

    private boolean isMenuOption(String text) {
        return text.matches("^[1-8]$");
    }
}
