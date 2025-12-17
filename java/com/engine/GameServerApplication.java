package com.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GameServerApplication {

    public static void main(String[] args) {
        // Arranca el servidor Tomcat embebido en el puerto 8080 por defecto
        SpringApplication.run(GameServerApplication.class, args);
        System.out.println("\n🚀 ENGINE IO SERVER LISTO: http://localhost:8080\n");
    }
}