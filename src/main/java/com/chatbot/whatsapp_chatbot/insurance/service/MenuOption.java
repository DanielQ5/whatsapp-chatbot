package com.chatbot.whatsapp_chatbot.insurance.service;

import com.chatbot.whatsapp_chatbot.insurance.production.entity.Policy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public enum MenuOption {
    POLICY_TYPE("1", "policy_type", "Quiero saber que tipo de poliza tengo.",
            policy -> "Tipo de Poliza: " + policy.getPolicyType()),
    DEDUCTIBLE("2", "deductible", "Quiero saber cuanto es mi deducible",
            policy -> "El Deducible es: $" + policy.getDeductible()),
    MAX_COVERAGE("3", "max_coverage", "Quiero saber cual es mi maximo vitalicio o anual",
            policy -> "El Maximo a cubrir es de: $" + policy.getMaxCoverage()),
    COVERAGE_START_DATE("4", "coverage_start_date", "Quiero saber cuando inicio mi cobertura",
            policy -> "La fecha efectiva de cobertura es: " + policy.getEffectiveDate()),
    MONTHLY_PREMIUM("5", "monthly_premium", "Quiero saber cuanto es mi pago por la cobertura",
            policy -> "La cuota a cancelar es de: $" + policy.getMonthlyPremium()),
    PAYMENT_CYCLE("6", "payment_cycle", "Quiero saber la frecuencia con la que debo realizar mis pagos",
            policy -> "La frecuencia de pago es: " + policy.getPaymentCycle()),
    CLAIM_FORMAT("7", "claim_format", "Quiero saber cuando expira mi cobertura",
            policy -> "La poliza se encuentra " + policy.getPolicyStatus() + " , con fecha de expiracion de: " + policy.getExpirationDate()),
    SPEAK_TO_REPRESENTATIVE("8", "speak_to_representative", "Deseo ser atendido por un ejecutivo de atencion",
            policy -> "Conectandote con un ejecutivo en estos momentos...");

    private final String key;
    private final String action;
    private final String description;
    private final Function<Policy, String> handler;

    MenuOption(String key, String action, String description, Function<Policy, String> handler) {
        this.key = key;
        this.action = action;
        this.description = description;
        this.handler = handler;
    }

    public String getKey() {
        return key;
    }

    public String getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public String respond(Policy policy) {
        return handler.apply(policy);
    }

    private static final Map<String, MenuOption> BY_KEY = new LinkedHashMap<>();

    static {
        for (MenuOption option : values()) {
            BY_KEY.put(option.key, option);
        }
    }

    public static MenuOption byKey(String key) {
        return BY_KEY.get(key);
    }
}
