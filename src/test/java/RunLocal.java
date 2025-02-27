import dev.coderbin.App;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.Executors;

public class RunLocal {
    private static final Logger log = LoggerFactory.getLogger(RunLocal.class);


    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        log.info("Starting locally...");


        EmbeddedPostgres pg = EmbeddedPostgres.builder()
                .setPort(21000)
                .setDataDirectory(Path.of("localdb"))
                .setCleanDataDirectory(false)
                .start();

        log.info("Local PG started");
        var app = App.start(pg.getPostgresDatabase());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                try {
                    app.close();
                } finally {
                    pg.close();
                }
                log.info("Local PG closed");
            } catch (IOException e) {
                log.warn("Error closing embedded postgres", e);
            }
        }));


        log.info("PG version is " + app.getPGVersion());

//        hammerTime(app);

        for (String username : app.getUsernames()) {
            log.info("Username: " + username);
        }

        Thread.sleep(Long.MAX_VALUE);


    }

    private static void hammerTime(App app) {
        var start = System.currentTimeMillis();
        int executions = 1000;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < executions; i++) {
                int finalI = i;
                executor.execute(() -> {
                    try {
                        log.info("Starting " + finalI);
                        app.getPGVersion();
                        app.logConnectionPool();
                        log.info("Completed " + finalI);
                    } catch (SQLException e) {
                        log.warn("Error getting PG version", e);
                    }
                });
            }
        }
        var duration = System.currentTimeMillis() - start;
        log.info("Hammer time: " + duration + " with average " + (Double.valueOf(duration) / Double.valueOf(executions)));
    }
}
