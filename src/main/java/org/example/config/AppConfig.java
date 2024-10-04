package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import org.example.controllers.ExceptionController;
import org.example.exceptions.ApiException;
import org.example.routes.Routes;
import org.example.security.controller.SecurityController;
import org.example.security.routes.SecurityRoutes;
import org.example.util.ApiProps;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import lombok.NoArgsConstructor;
import org.example.util.Utils;

import java.util.Set;
import java.util.stream.Collectors;



@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AppConfig {

    private static Routes routes;
    private static final ExceptionController exceptionController = new ExceptionController();
    private static final SecurityRoutes securityRoutes = new SecurityRoutes();
    private static ObjectMapper jsonMapper = new Utils().getObjectMapper();

    private static void configuration(JavalinConfig config) {
        //Server
        config.router.contextPath = ApiProps.API_CONTEXT; // Base path for all routes.

        //Plugin
        config.bundledPlugins.enableRouteOverview("/routes");
        config.bundledPlugins.enableDevLogging();

        // Routes
        config.router.apiBuilder(routes.getApiRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecuredRoutes());
    }

    //Exceptions
    private static void exceptionContext(Javalin app) {
        app.exception(ApiException.class, exceptionController::apiExceptionHandler);
    }

    public static void startServer(EntityManagerFactory emf) {
        routes = new Routes(emf);
        Javalin app = Javalin.create(AppConfig::configuration);

        app.beforeMatched(ctx -> { // Before matched is different from before, in that it is not called for 404 etc.
            if (ctx.routeRoles().isEmpty()) // no roles were added to the route endpoint so OK
                return;
            // 1. Get permitted roles
            Set<String> allowedRoles = ctx.routeRoles().stream().map(role -> role.toString().toUpperCase()).collect(Collectors.toSet());
            if (allowedRoles.contains("ANYONE")) {
                return;
            }
            // 2. Get user roles
            UserDTO user = ctx.attribute("user"); // the User was put in the context by the SecurityController.authenticate method (in a before filter on the route)

            // 3. Compare
            if (user == null)
                ctx.status(HttpStatus.FORBIDDEN)
                        .json(jsonMapper.createObjectNode()
                                .put("msg", "Not authorized. No username was added from the token"));

            if (!SecurityController.getInstance().authorize(user, allowedRoles)) {
                // throw new UnAuthorizedResponse(); // version 6 migration guide
                throw new ApiException(HttpStatus.FORBIDDEN.getCode(), "Unauthorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
            }
        });

        app.start(ApiProps.PORT);
        exceptionContext(app);
    }

    public static void stopServer() {
        Javalin app = Javalin.create(AppConfig::configuration);
        app.stop();
    }
}
