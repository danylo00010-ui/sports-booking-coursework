package ua.edu.nupp.kpp.server.dao;

import ua.edu.nupp.kpp.server.DBUtil;
import ua.edu.nupp.kpp.server.model.SportFacility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SportFacilityDao {

    public void add(SportFacility facility) throws SQLException {
        String sql = "INSERT INTO sport_facility (code, name, sport_type, max_capacity) VALUES (?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, facility);
            p.executeUpdate();
        }
    }

    public List<SportFacility> getAll() throws SQLException {
        List<SportFacility> list = new ArrayList<>();
        String sql = "SELECT id, code, name, sport_type, max_capacity FROM sport_facility ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public SportFacility getById(long id) throws SQLException {
        String sql = "SELECT id, code, name, sport_type, max_capacity FROM sport_facility WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(SportFacility facility) throws SQLException {
        String sql = "UPDATE sport_facility SET code = ?, name = ?, sport_type = ?, max_capacity = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, facility);
            p.setLong(5, facility.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM sport_facility WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, SportFacility f) throws SQLException {
        p.setString(1, f.getCode());
        p.setString(2, f.getName());
        p.setString(3, f.getSportType());
        p.setInt(4, f.getMaxCapacity());
    }

    private SportFacility map(ResultSet r) throws SQLException {
        return new SportFacility(
                r.getLong("id"),
                r.getString("code"),
                r.getString("name"),
                r.getString("sport_type"),
                r.getInt("max_capacity")
        );
    }
}