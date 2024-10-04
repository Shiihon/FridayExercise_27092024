package org.example.routes;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {
    private HotelRoutes hotelRoutes;
    private RoomRoutes roomRoutes;

    public Routes(EntityManagerFactory emf) {
        hotelRoutes = new HotelRoutes(emf);
        roomRoutes = new RoomRoutes(emf);
    }

    public EndpointGroup getApiRoutes() {
        return () -> {
            path("/hotel", hotelRoutes.getHotelRoutes());
            path("/room", roomRoutes.getRoomRoutes());
        };
    }
}

