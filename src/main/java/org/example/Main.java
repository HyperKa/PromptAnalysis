package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // <-- ЭТО САМАЯ ВАЖНАЯ АННОТАЦИЯ!
public class Main { // Лучше переименовать в PromptAnalysisApplication

    public static void main(String[] args) {
        // Эта строка запускает весь Spring Boot: веб-сервер, сервисы и т.д.
        SpringApplication.run(Main.class, args);
    }
}