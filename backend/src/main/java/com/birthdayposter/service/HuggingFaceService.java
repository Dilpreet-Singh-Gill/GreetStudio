package com.birthdayposter.service;

import com.birthdayposter.entity.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HuggingFaceService {

    private final RestTemplate restTemplate;

    @Value("${app.huggingface.api-key}")
    private String apiKey;

    private static final String MODEL_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";

    public String generateBirthdayWish(Person person) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Hugging Face API Key is missing. Falling back to default wish.");
            return generateDefaultWish(person);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = buildPrompt(person);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", prompt);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("max_new_tokens", 60);
            parameters.put("temperature", 0.7);
            parameters.put("return_full_text", false);
            requestBody.put("parameters", parameters);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<List> response = restTemplate.postForEntity(MODEL_URL, entity, List.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
                Map<String, Object> firstResult = (Map<String, Object>) response.getBody().get(0);
                String generatedText = (String) firstResult.get("generated_text");
                return generatedText != null ? generatedText.trim() : generateDefaultWish(person);
            } else {
                log.error("Failed to generate wish from Hugging Face: {}", response.getStatusCode());
                return generateDefaultWish(person);
            }
        } catch (Exception e) {
            log.error("Error communicating with Hugging Face API", e);
            return generateDefaultWish(person);
        }
    }

    private String buildPrompt(Person person) {
        StringBuilder prompt = new StringBuilder("[INST] Write a very short, warm, and professional birthday wish (max 2 sentences) for a colleague.\n");
        prompt.append("Name: ").append(person.getName()).append("\n");
        if (person.getDepartment() != null && !person.getDepartment().isBlank()) {
            prompt.append("Department: ").append(person.getDepartment()).append("\n");
        }
        if (person.getDesignation() != null && !person.getDesignation().isBlank()) {
            prompt.append("Designation: ").append(person.getDesignation()).append("\n");
        }
        prompt.append("Do not include placeholders, just output the final text wish. [/INST]");
        return prompt.toString();
    }

    private String generateDefaultWish(Person person) {
        return "Wishing you a very Happy Birthday, " + person.getName() + "! Hope you have a wonderful year ahead.";
    }
}
