package com.chatbot.whatsapp_chatbot.insurance.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GeminiIntentDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiIntentDetectionService.class);

    private final GeminiService geminiService;

    public GeminiIntentDetectionService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    public int detectIntent(List<Map<String, String>> conversationHistory, String content) {
        List<Map<String, String>> recentHistory = conversationHistory.subList(
                Math.max(0, conversationHistory.size() - 6),
                conversationHistory.size()
        );

        StringBuilder historyText = new StringBuilder();
        for (Map<String, String> entry : recentHistory) {
            String role = entry.get("role").equals("user") ? "Usuario" : "Asistente";
            historyText.append(role).append(": ").append(entry.get("content")).append("\n");
        }

        StringBuilder optionsText = new StringBuilder();
        for (MenuOption option : MenuOption.values()) {
            optionsText.append(option.getKey()).append(" = ").append(option.getDescription()).append("\n");
        }

        String prompt = "Tienes estas opciones de menu:\n" +
                optionsText +
                "\n" +
                (historyText.length() > 0 ? "Conversacion previa:\n" + historyText + "\n" : "") +
                "Mensaje del usuario: " + content + "\n\n" +
                "Responde SOLO con el numero de la opcion que mejor corresponda al mensaje. " +
                "Si el mensaje no corresponde a ninguna opcion, responde con 0.";

        try {
            String geminiResponse = geminiService.inquiry(prompt);

            JsonObject root = JsonParser.parseString(geminiResponse).getAsJsonObject();
            String text = root.get("candidates").getAsJsonArray().get(0)
                    .getAsJsonObject().get("content")
                    .getAsJsonObject().get("parts").getAsJsonArray().get(0)
                    .getAsJsonObject().get("text").getAsString();

            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            logger.error("Failed to detect intent from Gemini response", e);
            return 0;
        }
    }
}
