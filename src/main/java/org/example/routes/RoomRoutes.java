package org.example.routes;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import org.example.config.HibernateConfig;
import org.example.controllers.RoomController;
import org.example.daos.RoomDAO;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class RoomRoutes {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("hotel");
    private final RoomDAO roomDAO = new RoomDAO(emf);
    private final RoomController roomController = new RoomController(roomDAO);

    public EndpointGroup getRoomRoutes() {
        return () -> {

            get("/", roomController::getAll);
            get("/{id}", roomController::getById);
            post("/", roomController::create);
            put("/{id}", roomController::update);
            delete("/{id}", roomController::delete);
        };
    }
}
