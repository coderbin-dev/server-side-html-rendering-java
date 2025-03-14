import dev.coderbin.App;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public class RunLocal {
    private static final Logger log = LoggerFactory.getLogger(RunLocal.class);


    public static void main(String[] args) throws IOException, InterruptedException {
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

        Thread.sleep(Long.MAX_VALUE);

    }

}
