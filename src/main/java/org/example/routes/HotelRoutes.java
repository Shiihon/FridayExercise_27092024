package org.example.routes;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import org.example.controllers.HotelController;
import org.example.daos.HotelDAO;
import org.example.security.routes.SecurityRoutes;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class HotelRoutes {
    private final HotelController hotelController;

    public HotelRoutes(EntityManagerFactory emf) {
        HotelDAO hotelDao = new HotelDAO(emf);
        this.hotelController = new HotelController(hotelDao);
    }

    public EndpointGroup getHotelRoutes() {
        return () -> {
            get("/", hotelController::getAll, SecurityRoutes.Role.USER, SecurityRoutes.Role.ADMIN);
            get("/{id}", hotelController::getById, SecurityRoutes.Role.USER, SecurityRoutes.Role.ADMIN);
            get("/{id}/rooms", hotelController::getAllRoomsByHotel, SecurityRoutes.Role.ADMIN, SecurityRoutes.Role.USER);
            post("/", hotelController::create, SecurityRoutes.Role.ADMIN);
            put("/{id}", hotelController::update, SecurityRoutes.Role.ADMIN);
            delete("/{id}", hotelController::delete, SecurityRoutes.Role.ADMIN);
        };
    }
}
