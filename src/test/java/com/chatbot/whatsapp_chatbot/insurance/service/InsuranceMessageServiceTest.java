package com.chatbot.whatsapp_chatbot.insurance.service;

import com.chatbot.whatsapp_chatbot.insurance.production.entity.Policy;
import com.chatbot.whatsapp_chatbot.insurance.production.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsuranceMessageServiceTest {

    private static final String PHONE = "whatsapp:+50370000000";
    private static final String DUI = "12345678-9";
    private static final String POLICY_NUMBER = "POL-001";
    private static final String MENU_OPTION_1_KEYCAP =
            "1" + String.valueOf(Character.toChars(0xFE0F)) + String.valueOf(Character.toChars(0x20E3));

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private InteractionLogService interactionLogService;

    @Mock
    private GeminiIntentDetectionService geminiIntentDetectionService;

    private InsuranceMessageService service;

    private Policy samplePolicy;

    @BeforeEach
    void setUp() {
        service = new InsuranceMessageService(policyRepository, interactionLogService, geminiIntentDetectionService);

        samplePolicy = new Policy();
        samplePolicy.setPolicyNumber(POLICY_NUMBER);
        samplePolicy.setNationalId(DUI);
        samplePolicy.setCustomerFirstName("Juan");
        samplePolicy.setCustomerLastName("Perez");
        samplePolicy.setPolicyType("Salud");
        samplePolicy.setDeductible(BigDecimal.valueOf(100));
        samplePolicy.setMaxCoverage(BigDecimal.valueOf(50000));
        samplePolicy.setEffectiveDate(LocalDate.of(2026, 1, 1));
        samplePolicy.setExpirationDate(LocalDate.of(2027, 1, 1));
        samplePolicy.setMonthlyPremium(BigDecimal.valueOf(45));
        samplePolicy.setPaymentCycle("Mensual");
        samplePolicy.setPolicyStatus("Activa");
    }

    @Test
    void welcomeMessage_promptsForDui() {
        String response = service.processMessages(PHONE, "hola");

        assertThat(response).contains("DUI");
        verifyNoInteractions(policyRepository, interactionLogService, geminiIntentDetectionService);
    }

    @Test
    void duiRegistration_knownPolicy_showsMenu() {
        when(policyRepository.findByNationalId(DUI)).thenReturn(Optional.of(samplePolicy));

        String response = service.processMessages(PHONE, DUI);

        assertThat(response)
                .contains("Juan")
                .contains(POLICY_NUMBER)
                .contains(MENU_OPTION_1_KEYCAP);
    }

    @Test
    void duiRegistration_unknownPolicy_returnsNotFound() {
        when(policyRepository.findByNationalId(DUI)).thenReturn(Optional.empty());

        String response = service.processMessages(PHONE, DUI);

        assertThat(response).contains("no encontrado");
    }

    @Test
    void menuChoice_afterRegistration_returnsPolicyField() {
        when(policyRepository.findByNationalId(DUI)).thenReturn(Optional.of(samplePolicy));
        when(policyRepository.findByPolicyNumber(POLICY_NUMBER)).thenReturn(Optional.of(samplePolicy));

        service.processMessages(PHONE, DUI);
        String response = service.processMessages(PHONE, "1");

        assertThat(response).contains("Tipo de Poliza").contains("Salud");
    }

    @Test
    void freeText_geminiMatchesMenuOption_routesToMenuChoice() {
        when(policyRepository.findByNationalId(DUI)).thenReturn(Optional.of(samplePolicy));
        when(policyRepository.findByPolicyNumber(POLICY_NUMBER)).thenReturn(Optional.of(samplePolicy));
        when(geminiIntentDetectionService.detectIntent(anyList(), anyString())).thenReturn(2);

        service.processMessages(PHONE, DUI);
        String response = service.processMessages(PHONE, "cuanto es mi deducible?");

        assertThat(response).contains("Deducible").contains("100");
    }

    @Test
    void freeText_geminiFindsNoMatch_returnsClarification() {
        when(policyRepository.findByNationalId(DUI)).thenReturn(Optional.of(samplePolicy));
        when(geminiIntentDetectionService.detectIntent(anyList(), anyString())).thenReturn(0);

        service.processMessages(PHONE, DUI);
        String response = service.processMessages(PHONE, "que tal el clima?");

        assertThat(response).contains("No logr");
    }

    @Test
    void endConversation_savesLogAndClearsSession() {
        service.processMessages(PHONE, "adios");

        verify(interactionLogService).saveInteractionLog(any(UserSession.class));
    }

    private static List<Map<String, String>> anyList() {
        return any();
    }
}
