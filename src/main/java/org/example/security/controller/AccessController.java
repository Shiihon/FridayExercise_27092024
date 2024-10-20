package org.example.security.controller;

import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import jakarta.persistence.EntityManagerFactory;
import org.example.exceptions.ApiException;
import org.example.security.routes.SecurityRoutes;

import java.util.Set;
import java.util.stream.Collectors;

public class AccessController implements IAccessController {
    SecurityController securityController;

    public AccessController(EntityManagerFactory emf) {
        securityController = SecurityController.getInstance(emf);
    }

    /**
     * This method checks if the user has the necessary roles to access the route.
     *
     * @param ctx
     */
    public void accessHandler(Context ctx) {

        // If no roles are specified on the endpoint, then anyone can access the route
        if (ctx.routeRoles().isEmpty() || ctx.routeRoles().contains(SecurityRoutes.Role.ANYONE)) {
            return;
        }

        // Check if the user is authenticated
        try {
            securityController.authenticate().handle(ctx);
        } catch (UnauthorizedResponse e) {
            throw new UnauthorizedResponse(e.getMessage());
        } catch (Exception e) {
            throw new UnauthorizedResponse("You need to log in, dude! Or you token is invalid.");
        }

        // Check if the user has the necessary roles to access the route
        UserDTO user = ctx.attribute("user");
        if (user == null) {
            throw new ApiException(HttpStatus.FORBIDDEN.getCode(), "Not authorized. No username was added from the token");
        }

        Set<String> allowedRoles = ctx.routeRoles().stream().map(role -> role.toString().toUpperCase()).collect(Collectors.toSet());
        if (!securityController.authorize(user, allowedRoles)) {
            throw new UnauthorizedResponse("Unauthorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
        }
    }
}
