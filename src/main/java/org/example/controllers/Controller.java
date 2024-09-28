package org.example.controllers;
import io.javalin.http.Context;

public interface Controller {
    public void getAllHotels(Context ctx);
    public void getHotelById(Context ctx);
    public void createHotel(Context ctx);
    public void updateHotel(Context ctx);
    public void deleteHotel(Context ctx);
}
