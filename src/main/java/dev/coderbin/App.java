package dev.coderbin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.muserver.Method;
import org.flywaydb.core.Flyway;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.muserver.MuServerBuilder.muServer;

public record App(HikariDataSource ds, io.muserver.MuServer server) {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static App start(DataSource directDataSource) {

        Flyway.configure().dataSource(directDataSource).load().migrate();

        // create connection pool:
        var config = new HikariConfig();
        config.setDataSource(directDataSource);
        config.setMaximumPoolSize(10);
        var connectionPool = new HikariDataSource(config);

        var server = muServer()
                .withHttpPort(3000)
                .addHandler(Method.GET, "/users", (req, resp, pp) -> {
                    resp.contentType("application/json");
                    var json = new JSONObject();
                    try (var con = connectionPool.getConnection();
                    var s = con.prepareStatement("SELECT * FROM users");
                    var rs = s.executeQuery()) {
                        while (rs.next()) {
                            var username = rs.getString("username");
                            var email = rs.getString("email");
                            var id = (UUID) rs.getObject("id");
                            json.put(username, new JSONObject().put("email", email).put("id", id));
                        }
                    }

                    resp.write(json.toString(2));
                })
                .start();

        log.info("Started user endpoint at " + server.uri().resolve("/users"));
        return new App(connectionPool, server);

    }

    public String getPGVersion() throws SQLException {
        try (
                var con = ds.getConnection();
                var s = con.prepareStatement("SELECT version()");
                var rs = s.executeQuery()
        ) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            rs.next();
            return rs.getString(1);
        }
    }

    public List<String> getUsernames() throws SQLException {
        try (var con = ds.getConnection();
             var s = con.prepareStatement("SELECT username FROM users");
             var rs = s.executeQuery()) {
            var results = new ArrayList<String>();
            while (rs.next()) {
                results.add(rs.getString("username"));
            }
            return results;
        }
    }

    public void logConnectionPool() {
        int totalConnections = ds.getHikariPoolMXBean().getTotalConnections();
        log.info("Total connections: " + totalConnections);
    }

    public void close() {
        server.stop();
        ds.close();
    }

}