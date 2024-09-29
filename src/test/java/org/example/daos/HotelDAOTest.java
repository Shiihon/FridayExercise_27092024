package org.example.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.example.config.HibernateConfig;
import org.example.dtos.HotelDTO;
import org.example.dtos.RoomDTO;
import org.example.entities.Hotel;
import org.example.entities.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class HotelDAOTest {
    private static EntityManagerFactory emfTest;
    private static HotelDAO hotelDAO;
    private static Set<RoomDTO> roomDTOs;
    private static List<HotelDTO> hotelDTOs;

    @BeforeAll
    static void beforeAll() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        hotelDAO = new HotelDAO(emfTest);
    }

    @BeforeEach
    void setUp() {
        HotelDTO h1 = new HotelDTO(null, "Hotel 1", "Hotel address 1", Set.of());
        HotelDTO h2 = new HotelDTO(null, "Hotel 2", "Hotel address 2", Set.of());
        HotelDTO h3 = new HotelDTO(null, "Hotel 3", "Hotel address 3", Set.of());

        //Adding hotel to list.
        hotelDTOs = new ArrayList<>();

        hotelDTOs.add(h1);
        hotelDTOs.add(h2);
        hotelDTOs.add(h3);

        roomDTOs = Set.of(
                new RoomDTO(null, h3.getId(), 23, 5000.0),
                new RoomDTO(null, h3.getId(), 24, 5000.0),
                new RoomDTO(null, h3.getId(), 25, 6000.0),
                new RoomDTO(null, h3.getId(), 26, 7000.0)
        );
        h3.setRooms(roomDTOs);

        try (EntityManager em = emfTest.createEntityManager()) {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();


            hotelDTOs.forEach(hotelDTO -> {
                Hotel hotel = hotelDTO.getAsEntity();
                em.persist(hotel);

                hotel.getRooms().forEach(room -> {
                    room.setHotel(hotel);
                    em.persist(room);
                });

                hotelDTO.setId(hotel.getHotelId());
                hotelDTO.setRooms(hotel.getRooms().stream().map(RoomDTO::new).collect(Collectors.toSet()));
            });

            em.getTransaction().commit();
        }
    }


    @Test
    void getAll() {
        Set<HotelDTO> expected = new HashSet<>(hotelDTOs);
        Set<HotelDTO> actual = hotelDAO.getAll();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getById() {
        HotelDTO expected = hotelDTOs.get(0);
        HotelDTO actual = hotelDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getRoomsByHotelId() {
        HotelDTO hotelDTO = hotelDTOs.get(0);
        Set<RoomDTO> expected = hotelDTO.getRooms();
        Set<RoomDTO> actual = hotelDAO.getRoomsByHotelId(hotelDTO.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void create() {
        HotelDTO hotelDTO = new HotelDTO(null, "Hotel 1", "Hotel address 1", Set.of());
        HotelDTO expected = hotelDAO.create(hotelDTO);
        HotelDTO actual = hotelDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void update() {
        HotelDTO expected = hotelDTOs.get(0);
        expected.setHotelName("Updated Hotel Name");

        HotelDTO actual = hotelDAO.update(expected);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void delete() {
        HotelDTO expected = hotelDTOs.get(2);
        hotelDAO.delete(expected.getId());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> hotelDAO.getById(expected.getId()));
    }
}