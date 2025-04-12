package dev.coderbin;

import io.muserver.MuRequest;
import io.muserver.MuResponse;
import io.muserver.RouteHandler;
import jakarta.ws.rs.NotFoundException;

import java.util.Map;
import java.util.UUID;

public record ViewRestaurantHandler(RestaurantDB db, ViewRenderer renderer) implements RouteHandler {

    @Override
    public void handle(MuRequest request, MuResponse response, Map<String, String> pathParams) throws Exception {
        Restaurant restaurant = db.get(UUID.fromString(pathParams.get("id")));
        if (restaurant == null) throw new NotFoundException();
        var model = renderer.model(request);
        model.put("restaurant", restaurant);
        renderer.render(response, model, "view-restaurant.html.peb");
    }
}
