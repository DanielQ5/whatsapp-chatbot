package com.chatbot.whatsapp_chatbot.insurance.service;


import com.chatbot.whatsapp_chatbot.insurance.chatanalytics.repository.InteractionLogRepository;
import com.chatbot.whatsapp_chatbot.insurance.production.entity.Policy;
import com.chatbot.whatsapp_chatbot.insurance.production.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InsuranceMessageService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private InteractionLogRepository interactionLogRepository;

    //TODO Insert Section 2
    private Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();


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

    public String processMessages(String phoneNumber, String messageContent) {
        UserSession userSession = activeSessions.computeIfAbsent(phoneNumber, k -> new UserSession(phoneNumber));

        String cleanMessage = messageContent.trim().toLowerCase();

        String response;

        if (cleanMessage.equals("buenos dias") || cleanMessage.equals("hola")) {
            response = showWelcome();
        } else if (cleanMessage.equals("adios") || cleanMessage.equals("0")) {
            response = endConversation(userSession, phoneNumber);
        } else if (userSession.getPolicyNumber() != null && isMenuOption(messageContent.trim())) {
            response = handleMenuChoice(userSession, messageContent.trim());
        } else if (looksLikePolicyNumber(messageContent.trim().toUpperCase())) {
            response = registerPolicy(userSession, messageContent.trim().toUpperCase());
        } else {
            response = "Lo sentimos, pero no logrammos entender lo que necesitas.\n\n" +
                    "Favor ingresa 'hola' para dar inicio o\n" +
                    "ingresa 'adios' para finalizar";
        }

        return response;
    }

    private String showWelcome() {
        return "Gracias por contactarte con nuestra aseguradora! 👋\n\n" +
                "Por favor brindanos tu numero de poliza.";
    }

    private String registerPolicy(UserSession session, String policyNumber) {
        // TODO:
        // 1. Query database: policyRepo.findByPolicyNumber(policyNumber)
        Optional<Policy> policyOptional = policyRepository.findByPolicyNumber(policyNumber);

        // 2. If policy not found, return error message
        if (policyOptional.isEmpty()) {
            return "Póliza no encontrada: " + policyNumber + "\n\n" +
                    "Por favor verifica el número e intenta de nuevo.";
        }

        // 3. If found, save to session: session.setPolicyNumber(policyNumber)
        Policy policy = policyOptional.get();
        session.setPolicyNumber(policyNumber);


        // 4. Return welcome message with showMenu()

        return " Bienvenido: " + policy.getCustomerFirstName() + " " + policy.getCustomerLastName() + "\n\n" +
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
        // TODO:
        // 1. Get policy number from session
        String policyBeingWorked = session.getPolicyNumber();
        // 2. Query database: policyRepo.findByPolicyNumber(...)
        Optional<Policy> policyOptional = policyRepository.findByPolicyNumber(policyBeingWorked);

        if (policyOptional.isEmpty()) {
            return "Ocurrio un error al cargar la póliza. Por favor intenta de nuevo.";
        }

        Policy policy = policyOptional.get();
        // 3. Track action: session.addAction(MENU_TO_ACTION.get(choice))
        String trackAction = MENU_TO_ACTION.get(menuChoice);
        session.addAction(trackAction);

        if (menuChoice.equals("8")) {
            session.setRequestedRepresentative(true);
        }
        // 4. Use switch/if-else to return appropriate response
        String response;

        switch (menuChoice) {
            case "1": //Policy Type
                response = "Tipo de poliza: " + policy.getPolicyType();
                break;

            case "2": //Deductible
                response = "El Deducible es: " + "$" + policy.getDeductible();
                break;

            case "3": //Max Coverage
                response = "El Maximo a cubrir es de: " + "$" + policy.getMaxCoverage();
                break;

            case "4": //Coverage Start DAte
                response = "La fecha efectiva de cobertura es: " + policy.getEffectiveDate();
                break;

            case "5": //Monthly Premium
                response = "La cuota a cancelar es de: " + "$" + policy.getMonthlyPremium();
                break;

            case "6": //Payment Cycle
                response = "La frecuencia de pago es: " + policy.getPaymentCycle();
                break;

            case "7": //Status
                response = "La poliza se encuentra " + policy.getPolicyStatus() + " , con fecha de expiracion de: " + policy.getExpirationDate();
                break;

            case "8": //Ask for a Representative
                response = "Conectandote con un ejecutivo en estos momentos...";
                break;

            default:
                response = "Opción inválida. Por favor elige 1-8.";
                break;
        }
        // 5. Append showMenu() at the end
        return response + "\n\n" + showMenu();
    }

    private String endConversation(UserSession session, String phoneNumber) {
        // TODO:
        // 1. Save to database (call saveInteractionLog)
        // 2. Remove from activeSessions
        // 3. Return goodbye message

        return "TODO: Implement endConversation";
    }

    private void saveInteractionLog(UserSession session) {
        // TODO:
        // 1. Create new InteractionLog object
        // 2. Set all fields from session
        // 3. Calculate duration
        // 4. Save: logRepo.save(log)
    }

    private boolean looksLikePolicyNumber(String text) {
        // TODO: Check if text matches pattern: POL-12345
        // Hint: text.matches("^POL-\\d{5}$")
        return false;
    }

    private boolean isMenuOption(String text) {
        // TODO: Check if text is 0-8
        // Hint: text.matches("^[0-8]$")
        return false;
    }
}
