package org.example.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {
    private HotelRoutes hotelRoutes = new HotelRoutes();

    public EndpointGroup getApiRoutes() {
        return () -> {
            path("/hotel", hotelRoutes.getHotelRoutes());
        };
    }
}

