package org.example.security.routes;

import jakarta.persistence.EntityManagerFactory;
import org.example.security.controller.SecurityController;
import org.example.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.security.RouteRole;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityRoutes {
    private ObjectMapper jsonMapper = new Utils().getObjectMapper();
    // TODO : SOLVE PROBLEM WITH INJECTING EntityManagerFactory
    private SecurityController securityController;

    public SecurityRoutes(EntityManagerFactory emf) {
        securityController = SecurityController.getInstance(emf);
    }

    public EndpointGroup getSecurityRoutes() {
        return () -> {
            path("/auth", () -> {
                get("/test", ctx -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from Open")), Role.ANYONE);
                post("/login", securityController.login(), Role.ANYONE);
                post("/register", securityController.register(), Role.ANYONE);
//                post("/authenticate", securityController.authenticate());
//                get("/logout", securityController.logout());
            });
        };
    }

    public EndpointGroup getSecuredRoutes() {
        return () -> {
            path("/protected", () -> {
                get("/user_demo", (ctx) -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from USER Protected")), Role.USER);
                get("/admin_demo", (ctx) -> ctx.json(jsonMapper.createObjectNode().put("msg", "Hello from ADMIN Protected")), Role.ADMIN);
            });
        };
    }

    public enum Role implements RouteRole {ANYONE, USER, ADMIN}
}


