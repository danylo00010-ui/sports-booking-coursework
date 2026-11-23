package ua.edu.nupp.kpp.server.dao;

import ua.edu.nupp.kpp.server.DBUtil;
import ua.edu.nupp.kpp.server.model.TimeSlot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDao {

    public void add(TimeSlot slot) throws SQLException {
        String sql = "INSERT INTO time_slot (facility_id, slot_date, start_time, end_time, duration_minutes) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, slot);
            p.executeUpdate();
        }
    }

    public List<TimeSlot> getAll() throws SQLException {
        List<TimeSlot> list = new ArrayList<>();
        String sql = "SELECT id, facility_id, slot_date, start_time, end_time, duration_minutes FROM time_slot ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public TimeSlot getById(long id) throws SQLException {
        String sql = "SELECT id, facility_id, slot_date, start_time, end_time, duration_minutes FROM time_slot WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(TimeSlot slot) throws SQLException {
        String sql = "UPDATE time_slot SET facility_id = ?, slot_date = ?, start_time = ?, end_time = ?, duration_minutes = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, slot);
            p.setLong(6, slot.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM time_slot WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, TimeSlot s) throws SQLException {
        p.setLong(1, s.getFacilityId());
        p.setString(2, s.getSlotDate());
        p.setString(3, s.getStartTime());
        p.setString(4, s.getEndTime());
        p.setInt(5, s.getDurationMinutes());
    }

    private TimeSlot map(ResultSet r) throws SQLException {
        return new TimeSlot(
                r.getLong("id"),
                r.getLong("facility_id"),
                r.getString("slot_date"),
                r.getString("start_time"),
                r.getString("end_time"),
                r.getInt("duration_minutes")
        );
    }
}