package ua.edu.nupp.kpp.server.service;

import ua.edu.nupp.kpp.server.dao.SportFacilityDao;
import ua.edu.nupp.kpp.server.dao.TimeSlotDao;
import ua.edu.nupp.kpp.server.model.TimeSlot;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class TimeSlotService {
    private static final Logger LOG = Logger.getLogger(TimeSlotService.class.getName());
    private final TimeSlotDao dao = new TimeSlotDao();
    private final SportFacilityDao facilityDao = new SportFacilityDao();

    public List<TimeSlot> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(TimeSlot slot) throws SQLException {
        validate(slot);
        requireFacility(slot.getFacilityId());
        dao.add(slot);
        LOG.info("Додано часовий слот для об'єкта ID=" + slot.getFacilityId());
    }

    public void update(TimeSlot slot) throws SQLException {
        if (slot == null || slot.getId() == null || slot.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор часового слоту");
        }
        validate(slot);
        requireFacility(slot.getFacilityId());
        dao.update(slot);
        LOG.info("Оновлено часовий слот ID=" + slot.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено часовий слот ID=" + id);
    }

    public static void validate(TimeSlot s) {
        if (s == null || s.getFacilityId() <= 0) {
            throw new IllegalArgumentException("Необхідно вказати коректний ID спортивного об'єкта");
        }
        if (isBlank(s.getSlotDate()) || isBlank(s.getStartTime()) || isBlank(s.getEndTime())) {
            throw new IllegalArgumentException("Дата та час початку/завершення слоту є обов'язковими");
        }
        if (s.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Тривалість слоту має бути більшою за 0 хвилин");
        }
    }

    private void requireFacility(long facilityId) throws SQLException {
        if (facilityDao.getById(facilityId) == null) {
            throw new IllegalArgumentException("Спортивний об'єкт із ID=" + facilityId + " не знайдено");
        }
    }

    private static boolean isBlank(String v) {
        return v == null || v.isBlank();
    }
}