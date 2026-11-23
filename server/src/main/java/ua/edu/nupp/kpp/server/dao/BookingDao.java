package ua.edu.nupp.kpp.server.dao;

import ua.edu.nupp.kpp.server.DBUtil;
import ua.edu.nupp.kpp.server.model.Booking;
import ua.edu.nupp.kpp.server.model.BookingState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDao {

    public void add(Booking booking) throws SQLException {
        String sql = "INSERT INTO booking (user_id, time_slot_id, state, participants_count, equipment_score, f1, f2, f3, priority_index) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, booking);
            p.executeUpdate();
        }
    }

    public List<Booking> getAll() throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM booking ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public Booking getById(long id) throws SQLException {
        String sql = "SELECT * FROM booking WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(Booking booking) throws SQLException {
        String sql = "UPDATE booking SET user_id = ?, time_slot_id = ?, state = ?, participants_count = ?, " +
                "equipment_score = ?, f1 = ?, f2 = ?, f3 = ?, priority_index = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, booking);
            p.setLong(10, booking.getId());
            p.executeUpdate();
        }
    }

    public void updateState(long id, BookingState state) throws SQLException {
        String sql = "UPDATE booking SET state = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, state.name());
            p.setLong(2, id);
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM booking WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, Booking b) throws SQLException {
        p.setLong(1, b.getUserId());
        p.setLong(2, b.getTimeSlotId());
        p.setString(3, b.getState().name());
        p.setInt(4, b.getParticipantsCount());
        p.setDouble(5, b.getEquipmentScore());
        p.setDouble(6, b.getF1());
        p.setDouble(7, b.getF2());
        p.setDouble(8, b.getF3());
        p.setDouble(9, b.getPriorityIndex());
    }

    private Booking map(ResultSet r) throws SQLException {
        return new Booking(
                r.getLong("id"),
                r.getLong("user_id"),
                r.getLong("time_slot_id"),
                BookingState.valueOf(r.getString("state")),
                r.getInt("participants_count"),
                r.getDouble("equipment_score"),
                r.getDouble("f1"),
                r.getDouble("f2"),
                r.getDouble("f3"),
                r.getDouble("priority_index")
        );
    }
}