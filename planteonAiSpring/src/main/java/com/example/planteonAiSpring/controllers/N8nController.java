package com.example.planteonAiSpring.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/n8n")
@AllArgsConstructor
public class N8nController {

    private final RestTemplate restTemplate;

    @PostMapping("/trigger")
    public ResponseEntity<?> triggerN8nWorkflow(@RequestBody Map<String, Object> payload) {
        String webhookUrl = "http://192.168.1.114:32769/webhook/run";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<?> response = restTemplate.postForEntity(webhookUrl, request, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
