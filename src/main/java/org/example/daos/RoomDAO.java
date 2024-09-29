package org.example.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.example.config.HibernateConfig;
import org.example.dtos.RoomDTO;
import org.example.entities.Hotel;
import org.example.entities.Room;

import java.util.Set;
import java.util.stream.Collectors;

public class RoomDAO implements IDAO<RoomDTO> {

    private EntityManagerFactory emf;

    public RoomDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Set<RoomDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);

            return query.getResultStream().map(RoomDTO::new).collect(Collectors.toSet());
        }
    }

    @Override
    public RoomDTO getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Room room = em.find(Room.class, id);

            if (room != null) {
                return new RoomDTO(room);
            } else {
                throw new EntityNotFoundException(String.format("Room with id %d could not be found.", id));
            }
        }
    }

    @Override
    public RoomDTO create(RoomDTO roomDTO) {
        Room room = roomDTO.getAsEntity();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Hotel foundHotel = em.find(Hotel.class, roomDTO.getHotelId());
            if (foundHotel == null) {
                throw new IllegalArgumentException("Hotel with ID " + roomDTO.getHotelId() + " does not exist.");
            }

            room.setHotel(foundHotel);
            em.persist(room);
            em.getTransaction().commit();
        }
        return new RoomDTO(room);
    }

    @Override
    public RoomDTO update(RoomDTO roomDTO) {
        Room room = roomDTO.getAsEntity();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Room existingRoom = em.find(Room.class, room.getRoomId());
            if (existingRoom == null) {
                throw new EntityNotFoundException("Room not found");
            }

            if (room.getRoomNumber() != null) {
                existingRoom.setRoomNumber(room.getRoomNumber());
            }
            if (room.getPrice() != null) {
                existingRoom.setPrice(room.getPrice());
            }
            em.getTransaction().commit();
            return new RoomDTO(existingRoom);
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Room room = em.find(Room.class, id);
            em.getTransaction().begin();
            em.remove(room);
            em.getTransaction().commit();
        }
    }
}
