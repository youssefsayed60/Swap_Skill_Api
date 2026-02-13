package com.example.demo.Services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OllamaService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/chat";
    private static final String MODEL = "mistral";

    public List<String> generateQuestions(String skill, String difficulty, int count) {

        RestTemplate restTemplate = new RestTemplate();

        String prompt = String.format(
                "Generate %d %s technical interview questions about %s. Only return the questions.",
                count, difficulty, skill
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("stream", false);

        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content", "You are a senior technical interviewer."
        ));

        messages.add(Map.of(
                "role", "user",
                "content", prompt
        ));

        body.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(OLLAMA_URL, request, Map.class);

        Map message = (Map) response.getBody().get("message");

        String content = (String) message.get("content");

        return Arrays.stream(content.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }
}
