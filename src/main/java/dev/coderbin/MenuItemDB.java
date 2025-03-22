package dev.coderbin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MenuItemDB {
    private final DataSource db;

    public MenuItemDB(DataSource db) {
        this.db = db;
    }

    public void insert(MenuItem menuItem) throws SQLException {
        try (Connection con = db.getConnection();
             var stmt = con.prepareStatement("INSERT INTO menu_items(id, restaurant_id, name, description, price_cents) VALUES(?, ?, ?, ?, ?)")) {
            var i = 0;
            stmt.setObject(++i, menuItem.id());
            stmt.setObject(++i, menuItem.restaurantId());
            stmt.setString(++i, menuItem.name());
            stmt.setString(++i, menuItem.description());
            stmt.setLong(++i, menuItem.priceCents());
            stmt.executeUpdate();
        }
    }

    public MenuItem get(UUID id) throws SQLException {
        try (var con = db.getConnection();
        var stmt = con.prepareStatement("SELECT * FROM menu_items WHERE id = ?")) {
            stmt.setObject(1, id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rowToItem(rs);
                } else {
                    return null;
                }
            }
        }
    }

    private static MenuItem rowToItem(ResultSet rs) throws SQLException {
        return new MenuItem(
                (UUID) rs.getObject("id"),
                (UUID) rs.getObject("restaurant_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getLong("price_cents"));
    }

    public List<MenuItem> filter(UUID restaurantId) throws SQLException {
        var results = new ArrayList<MenuItem>();
        try (var con = db.getConnection();
             var stmt = con.prepareStatement("SELECT * FROM menu_items WHERE restaurant_id = ?")) {
            stmt.setObject(1, restaurantId);
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var item = rowToItem(rs);
                    results.add(item);
                }
            }
        }
        return results;
    }
}
