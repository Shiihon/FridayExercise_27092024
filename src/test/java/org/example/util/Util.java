package org.example.util;

import dk.bugelhartmann.UserDTO;

import static io.restassured.RestAssured.given;

public class Util {
    private static final String BASE_URL = "http://localhost:7000/api";

    public static String loginAccount(String username, String password) {
        UserDTO userDTO = new UserDTO(username, password);

        return given()
                .body(userDTO)
                .when()
                .post(BASE_URL + "/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
