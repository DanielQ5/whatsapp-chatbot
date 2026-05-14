package com.chatbot.whatsapp_chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WhatsappChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatsappChatbotApplication.class, args);
    }

}
