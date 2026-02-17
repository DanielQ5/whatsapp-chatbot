package com.chatbot.whatsapp_chatbot.insurance.service;


import com.chatbot.whatsapp_chatbot.insurance.chatanalytics.repository.InteractionLogRepository;
import com.chatbot.whatsapp_chatbot.insurance.production.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
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

       if (cleanMessage.equals("buenos dias") || cleanMessage.equals("hola")){
           response = showWelcome();
       }
       else if (cleanMessage.equals("adios") || cleanMessage.equals("0")){
           response=endConversation(userSession, phoneNumber);
       }

       else if (userSession.getPolicyNumber() != null && isMenuOption(messageContent.trim())){
           response = handleMenuChoice(userSession, messageContent.trim());
       }

       else if (looksLikePolicyNumber(messageContent.trim().toUpperCase())){
           response = registerPolicy(userSession, messageContent.trim().toUpperCase());
       }

       else {
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
        // 2. If policy not found, return error message
        // 3. If found, save to session: session.setPolicyNumber(policyNumber)
        // 4. Return welcome message with showMenu()

        return "TODO: Implement registerPolicy";
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
        // 2. Query database: policyRepo.findByPolicyNumber(...)
        // 3. Track action: session.addAction(MENU_TO_ACTION.get(choice))
        // 4. Use switch/if-else to return appropriate response
        // 5. Append showMenu() at the end

        return "TODO: Implement handleMenuChoice";
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
