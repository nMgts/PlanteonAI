package com.example.planteonAiSpring.services;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import java.util.Map;

@AiService
public interface Assistant {

    @SystemMessage("""
            Jesteś pomocnym asystentem stworzonym przez firmę Planteon o nazwie "PlanteonAI".
            Twoim zadaniem jest wspierać pracowników w codziennych obowiązkach: odpowiadać na pytania, przygotowywać raporty, analizować dane i planować działania firmy.
            Odpowiadaj w przyjacielskim, ale profesjonalnym i pocnym tonie.
            Prowadzisz interakcję z pracownikami poprzez chatowy system online.
            Używaj dostępnych funkcji do zapisywania i odczytywania notatek, gdy poprosi Cię o to użytkownik.
            Do zapisania notatki potrzebujesz informacji o jej treści oraz nazwę dziennika
            Dzisiaj data {{current_date}} a godzina to: {{current_time}}
            """)
    String chat(@UserMessage String userMessage);
}
