package dev.coderbin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RestaurantDB {
    private final DataSource db;

    public RestaurantDB(DataSource db) {
        this.db = db;
    }

    public void insert(Restaurant restaurant) throws SQLException {
        try (Connection con = db.getConnection();
             var stmt = con.prepareStatement("INSERT INTO restaurants(id, name, description) VALUES(?, ?, ?)")) {
            stmt.setObject(1, restaurant.id());
            stmt.setString(2, restaurant.name());
            stmt.setString(3, restaurant.description());
            stmt.executeUpdate();
        }
    }

    public Restaurant get(UUID id) throws SQLException {
        try (var con = db.getConnection();
        var stmt = con.prepareStatement("SELECT * FROM restaurants WHERE id = ?")) {
            stmt.setObject(1, id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rowToRestaurant(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public List<Restaurant> getAll() throws SQLException {
        try (var con = db.getConnection();
             var stmt = con.prepareStatement("SELECT * FROM restaurants ORDER BY name");
             var rs = stmt.executeQuery()) {
            var results = new ArrayList<Restaurant>();
            while (rs.next()) {
                results.add(rowToRestaurant(rs));
            }
            return results;
        }
    }

    private static Restaurant rowToRestaurant(ResultSet rs) throws SQLException {
        return new Restaurant(
                (UUID) rs.getObject("id"),
                rs.getString("name"),
                rs.getString("description"));
    }

}
