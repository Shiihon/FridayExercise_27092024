package org.example.controllers;

import io.javalin.http.Context;
import org.example.daos.HotelDAO;
import org.example.dtos.HotelDTO;
import org.example.entities.Hotel;

public class HotelController implements Controller {
    private HotelDAO hotelDAO;

    public HotelController(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }

    @Override
    public void getAllHotels(Context ctx) {

    }

    @Override
    public void getHotelById(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));

        Hotel hotel = hotelDAO.getById(id);

        HotelDTO hotelDTO = new HotelDTO(hotel);
        ctx.res().setStatus(200);
        ctx.json(hotelDTO, HotelDTO.class);
    }

    @Override
    public void createHotel(Context ctx) {

    }

    @Override
    public void updateHotel(Context ctx) {

    }

    @Override
    public void deleteHotel(Context ctx) {

    }
}
