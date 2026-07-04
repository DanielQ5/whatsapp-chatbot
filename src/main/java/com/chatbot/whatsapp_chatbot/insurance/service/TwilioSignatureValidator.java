package com.chatbot.whatsapp_chatbot.insurance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

@Service
public class TwilioSignatureValidator {

    private static final Logger log = LoggerFactory.getLogger(TwilioSignatureValidator.class);
    private static final String HMAC_SHA1 = "HmacSHA1";

    @Value("${twilio.auth.token}")
    private String authToken;

    public boolean isValid(String requestUrl, Map<String, String> params, String twilioSignature) {
        if (twilioSignature == null || twilioSignature.isBlank()) {
            return false;
        }

        StringBuilder data = new StringBuilder(requestUrl);
        new TreeMap<>(params).forEach((key, value) -> data.append(key).append(value));

        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            mac.init(new SecretKeySpec(authToken.getBytes(StandardCharsets.UTF_8), HMAC_SHA1));
            byte[] hash = mac.doFinal(data.toString().getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(hash);

            return MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    twilioSignature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            log.warn("Failed to compute Twilio signature: {}: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
}
