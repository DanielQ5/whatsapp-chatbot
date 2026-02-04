package com.chatbot.whatsapp_chatbot.insurance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller for Insurance Brokerage Bot
 * This proves our Spring Boot app is working!
 *
 * @RestController - Tells Spring this class handles web requests
 * @RequestMapping - All endpoints in this class start with /insurance
 */
@RestController
@RequestMapping("/insurance")

public class InsuranceTestController {

    /**
     * Simple test endpoint
     * Visit: http://localhost:8080/insurance/hello
     */
    @GetMapping("/hello")
    public String sayHello() {
        return "🏥 Insurance Brokerage Bot is ONLINE! Ready to help your clients!";
    }

    /**
     * Test endpoint to simulate a webhook
     * Visit: http://localhost:8080/insurance/webhook
     */
    @GetMapping("/webhook")
    public String testWebhook() {
        return "✅ Webhook endpoint is working! This is where WhatsApp will send messages.";
    }

    /**
     * Status check endpoint
     * Visit: http://localhost:8080/insurance/status
     */
    @GetMapping("/status")
    public String checkStatus() {
        return "Status: ACTIVE | Service: Insurance Bot | Ready: YES";
    }

}
