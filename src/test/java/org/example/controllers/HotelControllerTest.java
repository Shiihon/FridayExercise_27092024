package org.example.controllers;

import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.example.config.AppConfig;
import org.example.config.HibernateConfig;
import org.example.daos.HotelDAO;
import org.example.dtos.HotelDTO;
import org.example.populator.HotelPopulator;
import org.example.populator.UserPopulator;
import org.example.security.routes.SecurityRoutes;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.*;
import static org.example.util.Util.loginAccount;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HotelControllerTest {

    private static Javalin app;
    private static EntityManagerFactory emfTest;
    private static final String BASE_URL = "http://localhost:7000/api";
    private static HotelDAO hotelDAO;
    private static HotelPopulator populator;
    private static UserPopulator userPopulator;

    private static HotelDTO h1, h2, h3;
    private static List<HotelDTO> hotels;

    @BeforeAll
    void init() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();

        populator = new HotelPopulator(hotelDAO, emfTest);
        userPopulator = new UserPopulator(emfTest);

        hotelDAO = new HotelDAO(emfTest);

        app = AppConfig.startServer(emfTest);
    }

    @BeforeEach
    void setUp() {
        userPopulator.populateUsers();
        hotels = populator.populateHotels();
        h1 = hotels.get(0);
        h2 = hotels.get(1);
        h3 = hotels.get(2);
    }

    @AfterEach
    void tearDown() {
        userPopulator.cleanUpUsers();
        populator.cleanUpHotels();
    }

    @AfterAll
    void closeDown() {
        AppConfig.stopServer(app);
    }

    @Test
    void testGetAll() {
        String token = loginAccount("User1", "user123");

        HotelDTO[] hotels =
                given()
                        .header("authorization", "Bearer " + token)
                        .when()
                        .get(BASE_URL + "/hotel")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(HotelDTO[].class);

        for (HotelDTO hotel : hotels) {
            System.out.println(hotel);
        }
        assertEquals(3, hotels.length);
        assertThat(hotels, arrayContainingInAnyOrder(h1, h2, h3));
    }

    @Test
    void testGetById(){
        HotelDTO hotelDTO =
                given()
                        .when()
                        .get(BASE_URL + "/hotel/" + h1.getId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(HotelDTO.class);

        assertThat(hotelDTO, equalTo(h1));
    }

    @Test
    void testGetAllRoomsByHotel(){

    }

    @Test
    void testCreate(){

    }

    @Test
    @DisplayName("Create hotel succes")
    void createHotelSuccess(){
        HotelDTO hotelDTO = new HotelDTO(null, "Hotel1", "adresse 1", Set.of());
        given()
                .contentType("application/json")
                .body(hotelDTO)
                .when()
                .post(BASE_URL + "/hotel")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    void testUpdate(){

    }

    @Test
    void testDelete(){

    }

}