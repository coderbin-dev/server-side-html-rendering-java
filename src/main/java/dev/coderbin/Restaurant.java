package dev.coderbin;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.UUID;

public record Restaurant(UUID id, String name, String description) {

    public static Restaurant fromJSON(JSONObject jsonObject) {
        String name = jsonObject.optString("name");
        if (name.isBlank()) throw new IllegalArgumentException("Restaurant name cannot be blank.");
        return new Restaurant(UUID.fromString(jsonObject.optString("id")),
                name, jsonObject.optString("description"));
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("description", description);
    }
}

@Consumes("application/json")
class RestaurantBodyReader implements MessageBodyReader<Restaurant> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAssignableFrom(Restaurant.class);
    }

    @Override
    public Restaurant readFrom(Class<Restaurant> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        try {
            return Restaurant.fromJSON(new JSONObject(new JSONTokener(entityStream)));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (JSONException e) {
            throw new BadRequestException("Invalid JSON format.");
        }
    }
}

@Produces("application/json")
class RestaurantBodyWriter implements MessageBodyWriter<Restaurant> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAssignableFrom(Restaurant.class);
    }

    @Override
    public void writeTo(Restaurant restaurant, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write(restaurant.toJSON().toString().getBytes());
    }
}
