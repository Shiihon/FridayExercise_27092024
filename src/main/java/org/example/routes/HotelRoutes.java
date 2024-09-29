package org.example.routes;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import org.example.config.HibernateConfig;
import org.example.controllers.HotelController;
import org.example.daos.HotelDAO;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class HotelRoutes {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("hotel");
    private final HotelDAO hotelDao = new HotelDAO(emf);
    private final HotelController hotelController = new HotelController(hotelDao);

    public EndpointGroup getHotelRoutes() {
        return () -> {

            get("/", hotelController::getAll);
            get("/{id}", hotelController::getById);
            get("/{id}/rooms", hotelController::getAllRoomsByHotel);
            post("/", hotelController::create);
            put("/{id}", hotelController::update);
            delete("/{id}", hotelController::delete);
        };
    }
}
