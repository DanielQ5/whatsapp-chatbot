package com.chatbot.whatsapp_chatbot.insurance.controller;


import com.chatbot.whatsapp_chatbot.insurance.service.InsuranceMessageService;
import com.chatbot.whatsapp_chatbot.insurance.service.TwilioSignatureValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")

public class InsuranceWhatsAppController {

    private static final Logger log = LoggerFactory.getLogger(InsuranceWhatsAppController.class);

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

        String senderNumber = payload.get("From");
        String requestUrl = request.getRequestURL().toString();

        if (!twilioSignatureValidator.isValid(requestUrl, payload, twilioSignature)) {
            log.warn("Rejected webhook request with invalid Twilio signature from {}", maskPhone(senderNumber));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String messageContent = payload.get("Body");

        log.debug("Received WhatsApp message from {}", maskPhone(senderNumber));

        String response = insuranceMessageService.processMessages(senderNumber, messageContent);

        log.debug("Sent response to {}", maskPhone(senderNumber));

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

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "****" + phone.substring(phone.length() - 4);
    }
}
