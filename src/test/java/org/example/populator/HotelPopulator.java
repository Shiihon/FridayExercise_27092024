package org.example.populator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.daos.HotelDAO;
import org.example.daos.RoomDAO;
import org.example.dtos.HotelDTO;
import org.example.dtos.RoomDTO;

import java.util.List;
import java.util.Set;

public class HotelPopulator {
    private HotelDAO hotelDAO;
    private RoomDAO roomDAO;
    private static EntityManagerFactory emf;

    public HotelPopulator(HotelDAO hotelDAO, EntityManagerFactory emf) {
        this.hotelDAO = new HotelDAO(emf);
        this.roomDAO = new RoomDAO(emf);
        this.emf = emf;
    }

    // Populate 3 hotels with rooms
    public List<HotelDTO> populateHotels() {
        HotelDTO h1, h2, h3;
        RoomDTO r1, r2, r3, r4;

        h1 = new HotelDTO(null, "Hotel 1", "Hotel address 1", Set.of());
        h2 = new HotelDTO(null, "Hotel 2", "Hotel address 2", Set.of());
        h3 = new HotelDTO(null, "Hotel 3", "Hotel address 3", Set.of());

        //Creates and returns the new hotel back with an id.
        HotelDTO updatedH1 = hotelDAO.create(h1);
        HotelDTO updatedH2 = hotelDAO.create(h2);
        HotelDTO updatedH3 = hotelDAO.create(h3);

        // Create rooms for h1, h2, and h3.
        r1 = new RoomDTO(null, updatedH1.getId(), 1, 3000.0);
        r2 = new RoomDTO(null, updatedH1.getId(), 2, 3500.0);
        r3 = new RoomDTO(null, updatedH2.getId(), 3, 4000.0);
        r4 = new RoomDTO(null, updatedH3.getId(), 4, 4500.0);

        //Creates, and returns back rooms with an id.
        RoomDTO updatedR1 = roomDAO.create(r1);
        RoomDTO updatedR2 = roomDAO.create(r2);
        RoomDTO updatedR3 = roomDAO.create(r3);
        RoomDTO updatedR4 = roomDAO.create(r4);

        // Set rooms for each hotel (all rooms in Set.Of()).
        updatedH1.setRooms(Set.of(updatedR1, updatedR2));
        updatedH2.setRooms(Set.of(updatedR3));
        updatedH3.setRooms(Set.of(updatedR4));

        // Return the populated updated hotels as a list
        return List.of(updatedH1, updatedH2, updatedH3);
    }

    // Clean up the database by deleting all hotels and resetting the ID sequence
    public void cleanUpHotels() {
        // Delete all data from the database
        try (EntityManager em = emf.createEntityManager()) {

            em.getTransaction().begin();

            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE room_room_id_seq RESTART WITH 1").executeUpdate();

            em.createQuery("DELETE FROM Hotel").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE hotel_hotel_id_seq RESTART WITH 1").executeUpdate();

            em.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


