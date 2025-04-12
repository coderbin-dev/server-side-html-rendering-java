package dev.coderbin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.muserver.Method;
import io.muserver.MuRequest;
import io.muserver.MuResponse;
import io.muserver.RouteHandler;
import org.flywaydb.core.Flyway;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.muserver.MuServerBuilder.muServer;
import static io.muserver.handlers.ResourceHandlerBuilder.fileOrClasspath;
import static io.muserver.rest.RestHandlerBuilder.restHandler;

public record App(HikariDataSource ds, io.muserver.MuServer server) {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static App start(DataSource directDataSource) {

        // Set up the database schema
        Flyway.configure().dataSource(directDataSource).load().migrate();

        // create connection pool:
        var config = new HikariConfig();
        config.setDataSource(directDataSource);
        config.setMaximumPoolSize(10);
        var connectionPool = new HikariDataSource(config);

        // create the DB access classes
        var restaurantDB = new RestaurantDB(connectionPool);
        var menuItemDB = new MenuItemDB(connectionPool);

        // start the web server
        ViewRenderer renderer = ViewRenderer.create(true);
        var server = muServer()
                .withHttpPort(3000)
                .addHandler(Method.GET, "/", new HomeHandler(restaurantDB, renderer))
                .addHandler(Method.GET, "/restaurants/{id}", new ViewRestaurantHandler(restaurantDB, renderer))
                .addHandler(restHandler(new RestaurantResource(restaurantDB), new MenuItemResource(menuItemDB)).addCustomReader(new JsonableBodyReader()).addCustomWriter(new JsonableBodyWriter()))
                .addHandler(fileOrClasspath("src/main/resources/web", "/web"))
                .start();

        log.info("Started user endpoint at " + server.uri());
        return new App(connectionPool, server);

    }

    public URI uri() {
        return server.uri();
    }

    public void close() {
        server.stop();
        ds.close();
    }

}