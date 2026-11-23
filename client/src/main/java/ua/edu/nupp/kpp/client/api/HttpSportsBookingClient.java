package ua.edu.nupp.kpp.client.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.client.model.Booking;
import ua.edu.nupp.kpp.client.model.BookingState;
import ua.edu.nupp.kpp.client.model.SportFacility;
import ua.edu.nupp.kpp.client.model.TimeSlot;
import ua.edu.nupp.kpp.client.model.User;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpSportsBookingClient {
    private static final String BASE_URL = "http://localhost:8080/sports-booking-server";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // --- SportFacility ---
    public List<SportFacility> getFacilities() {
        return Arrays.asList(read(get("/facilities"), SportFacility[].class));
    }

    public void addFacility(SportFacility f) {
        post("/facilities", facilityForm("add", f));
    }

    public void updateFacility(SportFacility f) {
        post("/facilities", facilityForm("update", f) + "&id=" + f.getId());
    }

    public void deleteFacility(long id) {
        post("/facilities", "action=delete&id=" + id);
    }

    // --- User ---
    public List<User> getUsers() {
        return Arrays.asList(read(get("/users"), User[].class));
    }

    public void addUser(User u) {
        post("/users", userForm("add", u));
    }

    public void updateUser(User u) {
        post("/users", userForm("update", u) + "&id=" + u.getId());
    }

    public void deleteUser(long id) {
        post("/users", "action=delete&id=" + id);
    }

    // --- TimeSlot ---
    public List<TimeSlot> getTimeSlots() {
        return Arrays.asList(read(get("/slots"), TimeSlot[].class));
    }

    public void addTimeSlot(TimeSlot s) {
        post("/slots", slotForm("add", s));
    }

    public void updateTimeSlot(TimeSlot s) {
        post("/slots", slotForm("update", s) + "&id=" + s.getId());
    }

    public void deleteTimeSlot(long id) {
        post("/slots", "action=delete&id=" + id);
    }

    // --- Booking ---
    public List<Booking> getBookings() {
        return Arrays.asList(read(get("/bookings"), Booking[].class));
    }

    public void addBooking(long userId, long timeSlotId, int participantsCount, double equipmentScore) {
        post("/bookings", bookingForm("add", userId, timeSlotId, participantsCount, equipmentScore));
    }

    public void updateBooking(long id, long userId, long timeSlotId, int participantsCount, double equipmentScore) {
        post("/bookings", bookingForm("update", userId, timeSlotId, participantsCount, equipmentScore) + "&id=" + id);
    }

    public void changeBookingState(long id, BookingState state) {
        post("/bookings", "action=transition&id=" + id + "&state=" + state.name());
    }

    public void deleteBooking(long id) {
        post("/bookings", "action=delete&id=" + id);
    }

    private String facilityForm(String action, SportFacility f) {
        return "action=" + action
                + "&code=" + enc(f.getCode())
                + "&name=" + enc(f.getName())
                + "&sportType=" + enc(f.getSportType())
                + "&maxCapacity=" + f.getMaxCapacity();
    }

    private String userForm(String action, User u) {
        return "action=" + action
                + "&fullName=" + enc(u.getFullName())
                + "&email=" + enc(u.getEmail())
                + "&phone=" + enc(u.getPhone())
                + "&category=" + enc(u.getCategory());
    }

    private String slotForm(String action, TimeSlot s) {
        return "action=" + action
                + "&facilityId=" + s.getFacilityId()
                + "&slotDate=" + enc(s.getSlotDate())
                + "&startTime=" + enc(s.getStartTime())
                + "&endTime=" + enc(s.getEndTime())
                + "&durationMinutes=" + s.getDurationMinutes();
    }

    private String bookingForm(String action, long userId, long timeSlotId, int participantsCount, double equipmentScore) {
        return "action=" + action
                + "&userId=" + userId
                + "&timeSlotId=" + timeSlotId
                + "&participantsCount=" + participantsCount
                + "&equipmentScore=" + equipmentScore;
    }

    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .GET()
                .build();
        return send(req);
    }

    private String post(String path, String form) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                .build();
        return send(req);
    }

    private String send(HttpRequest req) {
        try {
            HttpResponse<String> response = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400) {
                throw new RuntimeException(extractErrorMessage(response.body()));
            }
            return response.body();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Помилка з'єднання із сервером: " + e.getMessage(), e);
        }
    }

    private String extractErrorMessage(String body) {
        try {
            JsonNode node = mapper.readTree(body);
            if (node.has("error")) {
                return node.get("error").asText();
            }
        } catch (Exception ignored) {
        }
        return body;
    }

    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Помилка розбору JSON-відповіді сервера", e);
        }
    }

    private String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}