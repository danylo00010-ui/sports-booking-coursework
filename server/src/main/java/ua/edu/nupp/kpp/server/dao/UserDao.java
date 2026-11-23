package ua.edu.nupp.kpp.server.dao;

import ua.edu.nupp.kpp.server.DBUtil;
import ua.edu.nupp.kpp.server.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public void add(User user) throws SQLException {
        String sql = "INSERT INTO app_user (full_name, email, phone, category) VALUES (?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, user);
            p.executeUpdate();
        }
    }

    public List<User> getAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, full_name, email, phone, category FROM app_user ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public User getById(long id) throws SQLException {
        String sql = "SELECT id, full_name, email, phone, category FROM app_user WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE app_user SET full_name = ?, email = ?, phone = ?, category = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, user);
            p.setLong(5, user.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM app_user WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, User u) throws SQLException {
        p.setString(1, u.getFullName());
        p.setString(2, u.getEmail());
        p.setString(3, u.getPhone());
        p.setString(4, u.getCategory());
    }

    private User map(ResultSet r) throws SQLException {
        return new User(
                r.getLong("id"),
                r.getString("full_name"),
                r.getString("email"),
                r.getString("phone"),
                r.getString("category")
        );
    }
}