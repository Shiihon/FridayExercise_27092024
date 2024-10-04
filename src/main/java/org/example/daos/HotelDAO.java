package org.example.daos;

import jakarta.persistence.*;
import org.example.dtos.HotelDTO;
import org.example.dtos.RoomDTO;
import org.example.entities.Hotel;
import org.example.entities.Room;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HotelDAO implements IDAO<HotelDTO> {

    private EntityManagerFactory emf;

    public HotelDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Set<HotelDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Hotel> query = em.createQuery("SELECT h FROM Hotel h LEFT JOIN FETCH h.rooms", Hotel.class);

            return query.getResultStream().map(HotelDTO::new).collect(Collectors.toSet());
        }
    }

    @Override
    public HotelDTO getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Hotel hotel = em.find(Hotel.class, id);

            return new HotelDTO(hotel);

        } catch (NullPointerException e) {
            throw new EntityNotFoundException(String.format("Hotel with id %d could not be found.", id));
        }
    }

    public Set<RoomDTO> getRoomsByHotelId(Long hotelId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r WHERE r.hotel.hotelId = :hotelId", Room.class);
            query.setParameter("hotelId", hotelId);

            return query.getResultList().stream().map(RoomDTO::new).collect(Collectors.toSet());
        }
    }

    @Override
    public HotelDTO create(HotelDTO hotelDTO) {
        Hotel hotel = hotelDTO.getAsEntity();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Set<Room> roomEntities = new HashSet<>();
            for (Room room : hotel.getRooms()) {
                Room foundRoom = em.find(Room.class, room.getRoomId()); //checking if room already exists

                if (foundRoom != null) {
                    roomEntities.add(foundRoom);
                } else {
                    em.persist(room);
                    roomEntities.add(room);
                }

                room.setHotel(hotel);
            }
            hotel.setRooms(roomEntities);
            em.persist(hotel);
            em.getTransaction().commit();

        }
        return new HotelDTO(hotel);
    }

    @Override
    public HotelDTO update(HotelDTO hoteldto) {
        Hotel hotel = hoteldto.getAsEntity();
        try (EntityManager em = emf.createEntityManager()) {

            Hotel existingHotel = em.find(Hotel.class, hotel.getHotelId());
            if (existingHotel == null) {
                throw new EntityNotFoundException("Hotel not found");
            }
            em.getTransaction().begin();

            if (hotel.getHotelName() != null) {
                existingHotel.setHotelName(hotel.getHotelName());
            }
            if (hotel.getHotelAddress() != null) {
                existingHotel.setHotelAddress(hotel.getHotelAddress());
            }
            if (hotel.getRooms() != null) {
                existingHotel.getRooms().addAll(hotel.getRooms()); // add all new rooms
            }

            em.getTransaction().commit();
            return new HotelDTO(existingHotel);

        } catch (RollbackException e) {
            throw new RollbackException(String.format("Unable to update hotel, with id: %d : %s", hoteldto.getId(), e.getMessage()));
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Hotel hotel = em.find(Hotel.class, id);

            if (hotel == null) {
                throw new EntityNotFoundException("Hotel not found");
            }

            em.getTransaction().begin();
            em.remove(hotel);
            em.getTransaction().commit();

        } catch (RollbackException e) {
            throw new RollbackException(String.format("Unable to delete hotel, with id: %d : %s", id, e.getMessage()));
        }
    }
}
