package org.example.controllers;

import io.javalin.http.Context;
import jakarta.persistence.EntityNotFoundException;
import org.example.daos.HotelDAO;
import org.example.dtos.HotelDTO;
import org.example.exceptions.ApiException;

import java.util.Set;

public class HotelController implements Controller {
    private HotelDAO hotelDAO;

    public HotelController(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }

    @Override
    public void getAllHotels(Context ctx) {
        try {
            Set<HotelDTO> hotels = hotelDAO.getAll();

            if (hotels.isEmpty()) {
                ctx.res().setStatus(404);
                ctx.result("No Hotels found");
            } else {
                ctx.res().setStatus(200);
                ctx.json(hotels);
            }

        } catch (Exception e) {
            throw new ApiException(404, e.getMessage());
        }
    }

    @Override
    public void getHotelById(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            HotelDTO hotel = hotelDAO.getById(id);

            ctx.res().setStatus(200);
            ctx.json(hotel);

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(400, e.getMessage());
        }
    }

    public void getAllRoomsByHotel(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            HotelDTO hotelDTO = hotelDAO.getById(id);
            //getRoomsByID from DAO never used.
                ctx.json(hotelDTO.getRooms());
                ctx.res().setStatus(200);

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(400, e.getMessage());
        }
    }

    @Override
    public void createHotel(Context ctx) {
        try {
            HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);
            HotelDTO newHotelDTO = hotelDAO.create(hotelDTO);

            if (newHotelDTO != null) {
                ctx.res().setStatus(201);
                ctx.json(newHotelDTO);
            } else {
                ctx.res().setStatus(400);
                ctx.result("Hotel could not be created");
            }

        } catch (Exception e) {
            throw new ApiException(400, e.getMessage());
        }
    }

    @Override
    public void updateHotel(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);

            hotelDTO.setId(id);
            HotelDTO updatedHotelDTO = hotelDAO.update(hotelDTO);

                ctx.res().setStatus(200);
                ctx.json(updatedHotelDTO);

        } catch (EntityNotFoundException e){
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(400, e.getMessage());
        }
    }

    @Override
    public void deleteHotel(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));

            HotelDTO hotelDTO = new HotelDTO();
            hotelDTO.setId(id);

            hotelDAO.delete(id);
            ctx.res().setStatus(204);

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(400, e.getMessage());
        }
    }
}
