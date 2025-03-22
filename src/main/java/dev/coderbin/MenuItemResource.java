package dev.coderbin;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/menuItems")
public class MenuItemResource {

    private final MenuItemDB db;

    public MenuItemResource(MenuItemDB db) {
        this.db = db;
    }

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response createMenuItem(MenuItem item) throws SQLException {
        db.insert(item);
        return Response.status(201).entity(item).build();
    }

    @GET
    @Produces("application/json")
    public String filter(@QueryParam("restaurantId") UUID restaurantId) throws SQLException {
        List<MenuItem> items = db.filter(restaurantId);
        return new JSONObject()
                .put("results", new JSONArray(items.stream().map(MenuItem::toJSON).toList()))
                .toString();
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public MenuItem getMenuItem(@PathParam("id") UUID id) throws SQLException {
        MenuItem menuItem = db.get(id);
        if (menuItem == null) {
            throw new NotFoundException();
        }
        return menuItem;
    }

}
