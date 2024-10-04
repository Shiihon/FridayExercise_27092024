package org.example;

import jakarta.persistence.EntityManagerFactory;
import org.example.config.AppConfig;
import org.example.config.HibernateConfig;

public class Main {
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("hotel");

    public static void main(String[] args) {
        AppConfig.startServer(emf);
    }
}