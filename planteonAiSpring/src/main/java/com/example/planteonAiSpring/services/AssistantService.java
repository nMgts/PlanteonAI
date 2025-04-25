package com.example.planteonAiSpring.services;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AssistantService {
    private final Assistant assistant;

    public Flux<String> chat(String chatId, String userMessage
                             //Authentication authentication
    ) {
        //String email = authentication.getName();
        String email = "admin@example.com";

        Map<String, Object> input = new HashMap<>();
        input.put("message", userMessage);
        input.put("email", email);

        return Flux.create(emitter -> {
            TokenStream tokenStream = assistant.chat(chatId, input);
            StringBuilder buffer = new StringBuilder();

            tokenStream
                    .onPartialResponse(partialResponse -> {
                        buffer.append(partialResponse);

                        // Sprawdzamy, czy końcowa część bufora zawiera pełne słowo (np. kończy się spacją, interpunkcją lub nową linią)
                        if (partialResponse.matches(".*[\\s.,!?;:\\n]$")) {
                            buffer.append(" ");
                            emitter.next(buffer.toString());  // Wysyłamy pełne słowo lub frazę
                            buffer.setLength(0);  // Czyścimy bufor po wysłaniu pełnego słowa
                        }
                    })
                    .onCompleteResponse(response -> {
                        emitter.next("__END__");
                        emitter.complete();
                        System.out.println(response);
                    })  // Zakończenie strumienia
                    .onError(error -> {
                        System.out.println("Wystąpił błąd: " + error.getMessage());
                        error.printStackTrace(); // (opcjonalnie) wypisanie całego stosu
                        emitter.error(error);
                    })  // Obsługa błędów
                    .start();
        });


        /*
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
                    //emitter.complete();


                        try {
        if (buffer.length() > 0) {
            emitter.send(SseEmitter.event().data(buffer.toString()));
        }
    } catch (IOException e) {
        emitter.completeWithError(e);
    }


                })
                .onError((Throwable error) -> {
                    error.printStackTrace();
                    emitter.completeWithError(error);
                })
                .start();

        return emitter;
        */
    }
}
