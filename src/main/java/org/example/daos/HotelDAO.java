package org.example.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.example.config.HibernateConfig;
import org.example.entities.Hotel;
import org.example.entities.Room;
import org.hibernate.exception.ConstraintViolationException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HotelDAO implements IDAO<Hotel> {

    private EntityManagerFactory emf;

    public HotelDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Set<Hotel> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Hotel> query = em.createQuery("SELECT h FROM Hotel h LEFT JOIN FETCH h.rooms", Hotel.class);

            return query.getResultList().stream().collect(Collectors.toSet());
        }
    }

    @Override
    public Hotel getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Hotel> query = em.createQuery("SELECT h FROM Hotel h LEFT JOIN FETCH h.rooms WHERE h.hotelId = :hotelId", Hotel.class);
            query.setParameter("hotelId", id);
            return query.getSingleResult();
        }
    }

    public Set<Room> getRoomsByHotelId(Long hotelId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r WHERE r.hotel.hotelId = :hotelId", Room.class);
            query.setParameter("hotelId", hotelId);
            return new HashSet<>(query.getResultList());
        }
    }


    public Hotel create(Hotel hotel) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(hotel);
            em.getTransaction().commit();
        }
        return hotel;
    }

    @Override
    public void update(Hotel hotel) {
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

            em.merge(existingHotel);
            em.getTransaction().commit();
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Hotel hotel = em.find(Hotel.class, id);
            em.getTransaction().begin();
            em.remove(hotel);
            em.getTransaction().commit();
        }
    }

    public static void main(String[] args) {
        //make a tester if you can!
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("hotel");
        HotelDAO dao = new HotelDAO(emf);

        //Testing out create
//        Hotel hotel;
//        // persisting rooms2.
//        Set<Room> rooms2 = new HashSet<>();
//
//        //persisting hotel
//        hotel = new Hotel("Zleep Hotel", "Strøget 12, Indre kbh", rooms2);
//
//        //adding rooms2 to hotel
//        rooms2.add(new Room(hotel, 1, 3000.00));
//        rooms2.add(new Room(hotel, 2, 3500.00));
//        rooms2.add(new Room(hotel, 3, 3500.00));
//        rooms2.add(new Room(hotel, 4, 3500.00));
//        rooms2.add(new Room(hotel, 5, 4000.00));
//        rooms2.add(new Room(hotel, 6, 4000.00));
//
//        dao.create(hotel);

        //Testing out getById
//        System.out.println(dao.getById(1L));

        //Testing getAll
        Set<Hotel> allHotels = dao.getAll();
        allHotels.forEach(hotel -> System.out.println("hotels : " + hotel));

        //Testing update
//        Hotel existingHotel = dao.getById(1L);
//        Hotel updateHotel = new Hotel();
//        updateHotel.setHotelId(existingHotel.getHotelId());
//        updateHotel.setHotelName("Ascot Hotel");
//        dao.update(updateHotel);
//
//        Hotel updatedHotel = dao.getById(1L);
//        System.out.println("updated hotel : " + updatedHotel);

        //Testing delete
//        dao.delete(2L);

        //Testing getRoomByHotelId
//        Long hotelId = 1L;
//        Set <Room> rooms = dao.getRoomsByHotelId(hotelId);
//        rooms.forEach(System.out::println);
    }
}
