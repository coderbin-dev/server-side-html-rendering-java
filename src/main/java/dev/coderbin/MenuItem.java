package dev.coderbin;

import org.json.JSONObject;

import java.util.UUID;

public record MenuItem(
        UUID id,
        UUID restaurantId,
        String name,
        String description,
        long priceCents
) implements Jsonable {
    public JSONObject toJSON() {
        return new JSONObject()
                .put("id", id)
                .put("restaurantId", restaurantId)
                .put("name", name)
                .put("description", description)
                .put("priceCents", priceCents);
    }

    public static MenuItem fromJSON(JSONObject json) {
        return new MenuItem(
                UUID.fromString(json.getString("id")),
                UUID.fromString(json.getString("restaurantId")),
                json.getString("name"),
                json.getString("description"),
                json.getLong("priceCents")
        );
    }
}
