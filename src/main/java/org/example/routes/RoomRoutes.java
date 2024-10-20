package org.example.routes;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import org.example.config.HibernateConfig;
import org.example.controllers.RoomController;
import org.example.daos.RoomDAO;
import org.example.security.routes.SecurityRoutes;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class RoomRoutes {
    private final RoomController roomController;

    public RoomRoutes (EntityManagerFactory emf) {
        RoomDAO roomDAO = new RoomDAO(emf);
        roomController = new RoomController(roomDAO);
    }

    public EndpointGroup getRoomRoutes() {
        return () -> {

            get("/", roomController::getAll, SecurityRoutes.Role.ADMIN, SecurityRoutes.Role.USER);
            get("/{id}", roomController::getById, SecurityRoutes.Role.ADMIN, SecurityRoutes.Role.USER);
            post("/", roomController::create, SecurityRoutes.Role.ADMIN);
            put("/{id}", roomController::update, SecurityRoutes.Role.ADMIN);
            delete("/{id}", roomController::delete, SecurityRoutes.Role.ADMIN);
        };
    }
}
