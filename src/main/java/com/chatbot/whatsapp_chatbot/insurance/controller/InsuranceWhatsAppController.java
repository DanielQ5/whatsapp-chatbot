package com.chatbot.whatsapp_chatbot.insurance.controller;


import com.chatbot.whatsapp_chatbot.insurance.service.InsuranceMessageService;
import com.chatbot.whatsapp_chatbot.insurance.service.TwilioSignatureValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")

public class InsuranceWhatsAppController {

    private final InsuranceMessageService insuranceMessageService;
    private final TwilioSignatureValidator twilioSignatureValidator;

    public InsuranceWhatsAppController(InsuranceMessageService insuranceMessageService,
                                        TwilioSignatureValidator twilioSignatureValidator) {
        this.insuranceMessageService = insuranceMessageService;
        this.twilioSignatureValidator = twilioSignatureValidator;
    }

    @PostMapping("/insurance")
    public ResponseEntity<String> receiveWhatsAppMessage(
            @RequestParam Map<String, String> payload,
            @RequestHeader(value = "X-Twilio-Signature", required = false) String twilioSignature,
            HttpServletRequest request) {

        String requestUrl = request.getRequestURL().toString();

        if (!twilioSignatureValidator.isValid(requestUrl, payload, twilioSignature)) {
            System.out.println("Rejected webhook request with invalid Twilio signature");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

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
