package dev.coderbin;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/restaurants")
public class RestaurantResource {

    private final RestaurantDB db;

    public RestaurantResource(RestaurantDB db) {
        this.db = db;
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response create(Restaurant restaurant) throws SQLException {
        db.insert(restaurant);
        return Response.status(201).entity(restaurant).build();
    }

    @GET
    @Produces("application/json")
    public Restaurant.RestaurantList getAll() throws SQLException {
        return new Restaurant.RestaurantList(db.getAll());
    }

    @GET
    @Produces("application/json")
    @Path("{id}")
    public Restaurant get(@PathParam("id") UUID id) throws SQLException {
        var restaurant = db.get(id);
        if (restaurant == null) {
            throw new NotFoundException();
        }
        return restaurant;
    }

}
