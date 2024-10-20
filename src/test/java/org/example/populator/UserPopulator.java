package org.example.populator;

import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.security.entities.Role;
import org.example.security.entities.User;

import java.util.List;

public class UserPopulator {

    private final EntityManagerFactory emf;

    public UserPopulator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<UserDTO> populateUsers() {
        List<User> users = List.of(
                new User(
                        "User1",
                        "user123"
                ),
                new User(
                        "Admin1",
                        "admin123"
                )
        );

        List<Role> roles = List.of(
                new Role(
                        "user"
                ),
                new Role(
                        "admin"
                )
        );


        users.get(0).addRole(roles.get(0));
        roles.get(0).addUser(users.get(0));

        users.get(1).addRole(roles.get(1));
        roles.get(1).addUser(users.get(1));

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            users.forEach(em::persist);
            roles.forEach(em::persist);
            em.getTransaction().commit();

            return users.stream().map(user -> new UserDTO(user.getUsername(), user.getPassword(), user.getRolesAsStrings())).toList();
        }
    }

    public void cleanUpUsers() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        }
    }
}
