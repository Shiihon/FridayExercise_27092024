package org.example.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import org.example.config.HibernateConfig;
import org.example.entities.Hotel;
import org.example.entities.Room;

import java.util.Set;
import java.util.stream.Collectors;

public class RoomDAO implements IDAO<Room> {

    private EntityManagerFactory emf;

    public RoomDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Set<Room> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r", Room.class);

            return query.getResultStream().collect(Collectors.toSet());
        }
    }

    @Override
    public Room getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Room> query = em.createQuery("SELECT r FROM Room r WHERE r.id = :id", Room.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        }
    }

    @Override
    public Room create(Room room) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(room);
            em.getTransaction().commit();
        }
        return room;
    }

    @Override
    public void update(Room room) {
        try (EntityManager em = emf.createEntityManager()) {
            Room existingRoom = em.find(Room.class, room.getRoomId());
            if (existingRoom == null) {
                throw new EntityNotFoundException("Room not found");
            }
            em.getTransaction().begin();
            if (room.getRoomNumber() != null) {
                existingRoom.setRoomNumber(room.getRoomNumber());
            }
            if (room.getPrice() != null) {
                existingRoom.setPrice(room.getPrice());
            }

            em.merge(existingRoom);
            em.getTransaction().commit();
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

    public static void main(String[] args) {
        //make a tester if you can!
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("hotel");
        RoomDAO dao = new RoomDAO(emf);
        HotelDAO hDAO = new HotelDAO(emf);

        //Testing getAll
//        Set<Room> allRooms = dao.getAll();
//        allRooms.forEach(room -> System.out.println("All Rooms : " + room));

        //Testing getByID
//        System.out.println(dao.getById(1L));

        //Testing create
        Hotel hotel = hDAO.getById(1L);
        Room room = new Room(hotel, 18, 8000.0);
        dao.create(room);
    }
}
