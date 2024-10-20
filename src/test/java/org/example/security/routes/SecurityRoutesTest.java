package org.example.security.routes;

import dk.bugelhartmann.UserDTO;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.example.config.AppConfig;
import org.example.config.HibernateConfig;
import org.example.populator.UserPopulator;
import org.example.util.Util;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.example.util.Util.loginAccount;
import static org.hamcrest.CoreMatchers.*;

class SecurityRoutesTest {
    private static final String BASE_URL = "http://localhost:7000/api";
    private static UserPopulator populator;
    private static Javalin app;

    private List<UserDTO> userDTOList;
    private static EntityManagerFactory emf;

    @BeforeAll
    static void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        populator = new UserPopulator(emf);
        app = AppConfig.startServer(emf);
    }

    @BeforeEach
    void setUp() {
        // populator.cleanUpUsers();

        userDTOList = populator.populateUsers();
    }

    @AfterEach
    void tearDown() {
        populator.cleanUpUsers();
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer(app);
    }


    @Test
    void test() {
        given()
                .when()
                .get(BASE_URL + "/auth/test")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Hello from Open"));
    }

    @Test
    void login() {
        UserDTO userDTO = new UserDTO("User1", "user123");

        given()
                .body(userDTO)
                .when()
                .post(BASE_URL + "/auth/login")
                .then()
                .statusCode(200)
                .body("username", equalTo(userDTO.getUsername()))
                .body("token", is(notNullValue()));
    }

    @Test
    void register() {
        UserDTO userDTO = new UserDTO("User2", "user111");

        given()
                .body(userDTO)
                .when()
                .post(BASE_URL + "/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo(userDTO.getUsername()))
                .body("token", is(notNullValue()));
    }

    @Test
    void securedRoutes() {
       String token = loginAccount("User1", "user123");

    given()
            .header("authorization", "Bearer " + token)
            .when()
            .get(BASE_URL + "/protected/user_demo")
            .then()
            .statusCode(200)
            .body("msg", equalTo("Hello from USER Protected"));

    }

    @Test
    void securityRoutes(){
        String token = loginAccount("Admin1", "admin123");

        given()
                .header("authorization", "Bearer " + token)
                .when()
                .get(BASE_URL + "/protected/admin_demo")
                .then()
                .statusCode(200)
                .body("msg", equalTo("Hello from ADMIN Protected"));
    }
}