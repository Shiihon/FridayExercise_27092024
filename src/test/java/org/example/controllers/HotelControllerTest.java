package org.example.controllers;

import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.example.config.AppConfig;
import org.example.config.HibernateConfig;
import org.example.daos.HotelDAO;
import org.example.dtos.HotelDTO;
import org.example.populator.HotelPopulator;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
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

    private static HotelDTO h1, h2, h3;
    private static List<HotelDTO> hotels;

    @BeforeAll
    void init() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        populator = new HotelPopulator(hotelDAO, emfTest);
        hotelDAO = new HotelDAO(emfTest);
        AppConfig.startServer(emfTest);
    }

    @BeforeEach
    void setUp() {
        hotels = populator.populateHotels();
        h1 = hotels.get(0);
        h2 = hotels.get(1);
        h3 = hotels.get(2);
    }

    @AfterEach
    void tearDown() {
        populator.cleanUpHotels();
    }

    @AfterAll
    void closeDown() {
        AppConfig.stopServer();
    }

    @Test
    void testGetAll() {
        HotelDTO[] hotels =
                given()
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
}