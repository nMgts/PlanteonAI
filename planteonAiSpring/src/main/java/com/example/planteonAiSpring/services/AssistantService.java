package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.dtos.ChatMessageDTO;
import com.example.planteonAiSpring.requests.MessageRequest;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AssistantService {
    private final Assistant assistant;

    public String chat(String chatId, MessageRequest userMessage, Authentication authentication) {
        String email = authentication.getName();

        Map<String, Object> input = new HashMap<>();
        input.put("message", userMessage.getMessage());
        input.put("email", email);

        TokenStream tokenStream = assistant.chat(chatId, input);
        tokenStream.onPartialResponse((String partialResponse) -> System.out.println(partialResponse))
                .onRetrieved((List<Content> contents) -> System.out.println(contents))
                .onToolExecuted((ToolExecution toolExecution) -> System.out.println(toolExecution))
                .onCompleteResponse((ChatResponse response) -> System.out.println(response))
                .onError((Throwable error) -> error.printStackTrace())
                .start();

        return "test";
        //return assistant.chat(chatId, input);
    }
}
