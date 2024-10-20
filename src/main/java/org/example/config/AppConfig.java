package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import org.example.controllers.ExceptionController;
import org.example.exceptions.ApiException;
import org.example.routes.Routes;
import org.example.security.controller.AccessController;
import org.example.security.controller.SecurityController;
import org.example.security.routes.SecurityRoutes;
import org.example.util.ApiProps;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import lombok.NoArgsConstructor;
import org.example.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;


@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AppConfig {

    private static Routes routes;
    private static final ExceptionController exceptionController = new ExceptionController();
    private static SecurityRoutes securityRoutes;
    private static ObjectMapper jsonMapper = new Utils().getObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(AppConfig.class); //logger ansvarlig for appconfig filen.
    private static SecurityController securityController;

    private static void configuration(JavalinConfig config) {
        //Server
        config.router.contextPath = ApiProps.API_CONTEXT; // Base path for all routes.

        //Plugin
        config.bundledPlugins.enableRouteOverview("/routes");
        config.bundledPlugins.enableDevLogging();

        // Routes
        config.router.apiBuilder(routes.getApiRoutes());
        config.router.apiBuilder(securityRoutes.getSecuredRoutes());
        config.router.apiBuilder(securityRoutes.getSecurityRoutes());
    }

    //Exceptions
    private static void exceptionContext(Javalin app) {
        app.exception(ApiException.class, exceptionController::apiExceptionHandler);

        app.exception(Exception.class, (e, ctx) -> {
            logger.error("{} {}", 400, e.getMessage());
            ctx.status(400);
            ctx.result(e.getMessage());
        });
    }

    public static Javalin startServer(EntityManagerFactory emf) {
        logger.info("Starting the Javalin server...");  // <--- Log here

        routes = new Routes(emf);
        securityRoutes = new SecurityRoutes(emf);
        Javalin app = Javalin.create(AppConfig::configuration);
        AccessController accessController = new AccessController(emf);

        app.beforeMatched(accessController::accessHandler); // metoden håndtere access.
        app.start(ApiProps.PORT);
        exceptionContext(app);
        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
