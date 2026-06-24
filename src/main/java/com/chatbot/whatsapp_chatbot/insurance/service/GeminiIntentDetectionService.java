package com.chatbot.whatsapp_chatbot.insurance.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GeminiIntentDetectionService {

    private final GeminiService geminiService;

    public GeminiIntentDetectionService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    public int detectIntent(List<Map<String, String>> conversationHistory, String content) {
        String prompt = "Tienes estas opciones de menu:\n" +
                "1 = tipo de poliza\n" +
                "2 = deducible\n" +
                "3 = maximo anual o vitalicio\n" +
                "4 = fecha de inicio de cobertura\n" +
                "5 = pago mensual\n" +
                "6 = frecuencia de pago\n" +
                "7 = estado y expiracion de poliza\n" +
                "8 = hablar con un ejecutivo\n\n" +
                "Mensaje del usuario: " + content + "\n\n" +
                "Responde SOLO con el numero de la opcion que mejor corresponda al mensaje. " +
                "Si el mensaje no corresponde a ninguna opcion, responde con 0.";

        String geminiResponse = geminiService.inquiry(prompt);

        JsonObject root = JsonParser.parseString(geminiResponse).getAsJsonObject();
        String text = root.get("candidates").getAsJsonArray().get(0)
                .getAsJsonObject().get("content")
                .getAsJsonObject().get("parts").getAsJsonArray().get(0)
                .getAsJsonObject().get("text").getAsString();
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
