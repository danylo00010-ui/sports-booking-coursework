package ua.edu.nupp.kpp.server.service;

import ua.edu.nupp.kpp.server.dao.BookingDao;
import ua.edu.nupp.kpp.server.dao.SportFacilityDao;
import ua.edu.nupp.kpp.server.dao.TimeSlotDao;
import ua.edu.nupp.kpp.server.dao.UserDao;
import ua.edu.nupp.kpp.server.model.Booking;
import ua.edu.nupp.kpp.server.model.BookingState;
import ua.edu.nupp.kpp.server.model.SportFacility;
import ua.edu.nupp.kpp.server.model.TimeSlot;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class BookingService {
    private static final Logger LOG = Logger.getLogger(BookingService.class.getName());
    private final BookingDao dao = new BookingDao();
    private final UserDao userDao = new UserDao();
    private final TimeSlotDao slotDao = new TimeSlotDao();
    private final SportFacilityDao facilityDao = new SportFacilityDao();

    public List<Booking> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(long userId, long timeSlotId, int participantsCount, double equipmentScore) throws SQLException {
        Booking prepared = buildBooking(null, userId, timeSlotId, BookingState.CREATED, participantsCount, equipmentScore);
        dao.add(prepared);
        LOG.info("Створено нове бронювання для користувача ID=" + userId);
    }

    public void update(long id, long userId, long timeSlotId, int participantsCount, double equipmentScore) throws SQLException {
        Booking existing = dao.getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Бронювання із ID=" + id + " не знайдено");
        }
        Booking updated = buildBooking(id, userId, timeSlotId, existing.getState(), participantsCount, equipmentScore);
        dao.update(updated);
        LOG.info("Оновлено бронювання ID=" + id);
    }

    public void changeState(long id, BookingState nextState) throws SQLException {
        if (nextState == null) {
            throw new IllegalArgumentException("Новий стан бронювання не задано");
        }
        Booking current = dao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Бронювання із ID=" + id + " не знайдено");
        }
        if (!isAllowedTransition(current.getState(), nextState)) {
            throw new IllegalArgumentException(
                    "Недопустимий перехід стану: " + current.getState().getTitleUa() + " -> " + nextState.getTitleUa()
            );
        }
        dao.updateState(id, nextState);
        LOG.info("Змінено стан бронювання ID=" + id + " на " + nextState.name());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено бронювання ID=" + id);
    }

    public static boolean isAllowedTransition(BookingState from, BookingState to) {
        return (from == BookingState.CREATED && to == BookingState.CONFIRMED)
                || (from == BookingState.CONFIRMED && to == BookingState.ACTIVE)
                || (from == BookingState.ACTIVE && to == BookingState.COMPLETED);
    }

    public static void validateParameters(long userId, long timeSlotId, int participantsCount, double equipmentScore) {
        if (userId <= 0 || timeSlotId <= 0) {
            throw new IllegalArgumentException("Необхідно вказати коректні ID користувача та часового слоту");
        }
        if (participantsCount <= 0) {
            throw new IllegalArgumentException("Кількість учасників має бути більшою за 0");
        }
        if (equipmentScore < 0.0 || equipmentScore > 100.0) {
            throw new IllegalArgumentException("Потреба в обладнанні (F3) має бути в діапазоні 0..100");
        }
    }

    private Booking buildBooking(Long id, long userId, long timeSlotId, BookingState state,
                                 int participantsCount, double equipmentScore) throws SQLException {
        validateParameters(userId, timeSlotId, participantsCount, equipmentScore);

        if (userDao.getById(userId) == null) {
            throw new IllegalArgumentException("Користувача із ID=" + userId + " не знайдено");
        }
        TimeSlot slot = slotDao.getById(timeSlotId);
        if (slot == null) {
            throw new IllegalArgumentException("Часовий слот із ID=" + timeSlotId + " не знайдено");
        }
        SportFacility facility = facilityDao.getById(slot.getFacilityId());
        if (facility == null) {
            throw new IllegalArgumentException("Спортивний об'єкт для обраного слоту не знайдено");
        }

        double f1 = PriorityCalculator.factor1(participantsCount, facility.getMaxCapacity());
        double f2 = PriorityCalculator.factor2(slot.getDurationMinutes());
        double f3 = Math.round(equipmentScore * 100.0) / 100.0;
        double priorityIndex = PriorityCalculator.calculate(f1, f2, f3);

        return new Booking(id, userId, timeSlotId, state, participantsCount, equipmentScore, f1, f2, f3, priorityIndex);
    }
}