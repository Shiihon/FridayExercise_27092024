package org.example.controllers;

import io.javalin.http.Context;
import jakarta.persistence.EntityNotFoundException;
import org.example.daos.RoomDAO;
import org.example.dtos.RoomDTO;
import org.example.exceptions.ApiException;

import java.util.Set;

public class RoomController implements Controller {
    private RoomDAO roomDAO;

    public RoomController(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    @Override
    public void getAll(Context ctx) {
        try {
            Set<RoomDTO> rooms = roomDAO.getAll();

            if (rooms.isEmpty()) {
                ctx.status(404);
                ctx.result("No rooms where found");
            } else {
                ctx.res().setStatus(200);
                ctx.json(rooms);
            }

        } catch (Exception e) {
            throw new ApiException(404, e.getMessage());
        }
    }

    @Override
    public void getById(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            RoomDTO room = roomDAO.getById(id);

            ctx.res().setStatus(200);
            ctx.json(room);

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(400, e.getMessage());
        }
    }

    @Override
    public void create(Context ctx) {
        try {
            RoomDTO roomDTO = ctx.bodyAsClass(RoomDTO.class);
            RoomDTO newRoomDTO = roomDAO.create(roomDTO);

            if (newRoomDTO != null) {
                ctx.res().setStatus(201);
                ctx.json(newRoomDTO);
            } else {
                ctx.res().setStatus(400);
                ctx.result("Room could not be created");
            }

        } catch (Exception e) {
            throw new ApiException(400, e.getMessage());
        }
    }

    @Override
    public void update(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            RoomDTO roomDTO = ctx.bodyAsClass(RoomDTO.class);

            roomDTO.setRoomId(id);
            RoomDTO updatedRoomDTO = roomDAO.update(roomDTO);

            ctx.res().setStatus(200);
            ctx.json(updatedRoomDTO);

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(400, e.getMessage());
        }
    }

    @Override
    public void delete(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));

            RoomDTO roomDTO = new RoomDTO();
            roomDTO.setRoomId(id);

            roomDAO.delete(id);
            ctx.res().setStatus(204);

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(400, e.getMessage());
        }
    }
}
