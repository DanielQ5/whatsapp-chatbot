package com.chatbot.whatsapp_chatbot.insurance.controller;


import com.chatbot.whatsapp_chatbot.insurance.service.InsuranceMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")

public class InsuranceWhatsAppController {


    private final InsuranceMessageService insuranceMessageService;  // ← ADD THIS!

    public InsuranceWhatsAppController(InsuranceMessageService insuranceMessageService) {
        this.insuranceMessageService = insuranceMessageService;
    }

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

        // Process message using the service!
        String response = insuranceMessageService.processMessages(senderNumber, messageContent);

        System.out.println("Sending response: " + response);

// Return TwiML format for Twilio
        String twimlResponse =
                "<Response>" +
                        "  <Message>" + response + "</Message>" +
                        "</Response>";

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/xml")
                .body(twimlResponse);
    }

    @GetMapping("/test")
    public String testWebhook() {
        return "Insurance WhatsApp webhook is ONLINE and ready to receive messages!";
    }
}
