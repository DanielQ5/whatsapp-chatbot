package com.chatbot.whatsapp_chatbot.insurance;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")

public class InsuranceWhatsAppController {

    @PostMapping("/insurance")
    public ResponseEntity<String> receiveWhatsAppMessage(
            @RequestParam Map<String, String> payload) {

        System.out.println("Message, Received");
        System.out.println("Full Payload: " + payload);

        String messageContent = payload.get("Body");
        String senderNumber = payload.get("From");
        String recipientNumber = payload.get("To");

        System.out.println("Message: " + messageContent);
        System.out.println("Sender: " + senderNumber);
        System.out.println("Receiver: " + recipientNumber);

        String acknowledgementMessage = "Estamos cerca bebito fiu fiu!";

        return ResponseEntity.ok(acknowledgementMessage);
    }

    @GetMapping("/test")
    public String testWebhook() {
        return "Insurance WhatsApp webhook is ONLINE and ready to receive messages!";
    }
}
