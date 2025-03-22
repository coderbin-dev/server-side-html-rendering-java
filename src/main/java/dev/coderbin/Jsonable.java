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

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface Jsonable {

    JSONObject toJSON();

}

@Consumes("application/json")
class JsonableBodyReader implements MessageBodyReader<Jsonable> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Jsonable.class.isAssignableFrom(type);
    }

    @Override
    public Jsonable readFrom(Class<Jsonable> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        try {
            JSONObject json = new JSONObject(new JSONTokener(entityStream));
            if (type.isAssignableFrom(Restaurant.class)) {
                return Restaurant.fromJSON(json);
            } else if (type.isAssignableFrom(MenuItem.class)) {
                return MenuItem.fromJSON(json);
            } else {
                throw new IllegalStateException("Unrecognised type " + type);
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (JSONException e) {
            throw new BadRequestException("Invalid JSON format.");
        }
    }
}

@Produces("application/json")
class JsonableBodyWriter implements MessageBodyWriter<Jsonable> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Jsonable.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(Jsonable jsonable, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try (Writer writer = new OutputStreamWriter(entityStream)) {
            jsonable.toJSON().write(writer);
        }
    }
}

