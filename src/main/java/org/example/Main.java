package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        // Для отладки установки API ключа:
        System.out.println("Значение переменной SPRING_AI_MISTRALAI_API_KEY: "
                + System.getenv("SPRING_AI_MISTRALAI_API_KEY"));
        SpringApplication.run(Main.class, args);
    }
}