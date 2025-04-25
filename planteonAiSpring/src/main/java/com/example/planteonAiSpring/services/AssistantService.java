package com.example.planteonAiSpring.services;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AssistantService {
    private final Assistant assistant;

    public SseEmitter chat(String chatId,
                           //MessageRequest userMessage,
                           Authentication authentication) {
        String email = authentication.getName();

        Map<String, Object> input = new HashMap<>();
        //input.put("message", userMessage.getMessage());
        input.put("message", "cześć jak możesz mi pomóc");
        input.put("email", email);

        SseEmitter emitter = new SseEmitter(0L);
        StringBuilder buffer = new StringBuilder();

        TokenStream tokenStream = assistant.chat(chatId, input);
        tokenStream.onPartialResponse((String partialResponse) -> {
                    buffer.append(partialResponse);

                    // Sprawdź, czy bufor zawiera "pełne słowo"
                    String current = buffer.toString();
                    if (current.matches(".*[\\s.,!?;:]$")) { // kończy się spacją lub interpunkcją
                        try {
                            emitter.send(SseEmitter.event().data(current));
                            buffer.setLength(0); // czyścimy bufor
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    }
                })
                .onRetrieved((List<Content> contents) -> {
                    System.out.println(contents);
                })
                .onToolExecuted((ToolExecution toolExecution) -> {
                    System.out.println(toolExecution);
                })
                .onCompleteResponse((ChatResponse response) -> {
                    System.out.println("odpowiedź: " + response);
                    emitter.complete();

                    /*
                        try {
        if (buffer.length() > 0) {
            emitter.send(SseEmitter.event().data(buffer.toString()));
        }
    } catch (IOException e) {
        emitter.completeWithError(e);
    }
                     */

                })
                .onError((Throwable error) -> {
                    error.printStackTrace();
                    emitter.completeWithError(error);
                })
                .start();

        return emitter;
    }
}
