package dev.coderbin;

import io.muserver.MuRequest;
import io.muserver.MuResponse;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.FileLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewRenderer {

    private final PebbleEngine engine;

    public ViewRenderer(PebbleEngine engine) {
        this.engine = engine;
    }

    public static ViewRenderer create(boolean isLocal) {
        var builder = new PebbleEngine.Builder();
        if (isLocal) {
            FileLoader fileLoader = new FileLoader();
            fileLoader.setCharset("utf-8");
            fileLoader.setPrefix("src/main/resources/views");
            builder.loader(fileLoader).cacheActive(false);
        } else {
            ClasspathLoader classpathLoader = new ClasspathLoader();
            classpathLoader.setCharset("utf-8");
            classpathLoader.setPrefix("views/");
            builder.loader(classpathLoader).cacheActive(true);
        }

        var engine = builder.build();
        return new ViewRenderer(engine);
    }

    public Map<String,Object> model(MuRequest request) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("request", request);
        return model;
    }

    public void render(MuResponse response, Map<String,Object> model, String viewName) throws IOException {
        response.contentType("text/html;charset=utf-8");
        var template = engine.getTemplate(viewName);
        try (var writer = response.writer()) {
            template.evaluate(writer, model);
        }
    }






}
