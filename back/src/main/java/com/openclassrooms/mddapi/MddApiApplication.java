package com.openclassrooms.mddapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal de l'application Spring Boot MDD API.
 */
@SpringBootApplication
public class MddApiApplication {

    /**
     * Démarre l'application Spring Boot.
     *
     * @param args arguments de ligne de commande transmis au démarrage
     */
    public static void main(String[] args) {
        SpringApplication.run(MddApiApplication.class, args);
    }
}
