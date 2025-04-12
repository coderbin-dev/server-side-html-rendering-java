package dev.coderbin;

import io.muserver.MuRequest;
import io.muserver.MuResponse;
import io.muserver.RouteHandler;

import java.util.List;
import java.util.Map;

public record HomeHandler(RestaurantDB db, ViewRenderer renderer) implements RouteHandler {

    @Override
    public void handle(MuRequest request, MuResponse response, Map<String, String> pathParams) throws Exception {
        List<Restaurant> restaurants = db.getAll();
        var model = renderer.model(request);
        model.put("restaurants", restaurants);
        renderer.render(response, model, "index.html.peb");
    }
}
