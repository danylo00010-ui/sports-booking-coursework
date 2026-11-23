package ua.edu.nupp.kpp.server.service;

import ua.edu.nupp.kpp.server.dao.SportFacilityDao;
import ua.edu.nupp.kpp.server.model.SportFacility;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class SportFacilityService {
    private static final Logger LOG = Logger.getLogger(SportFacilityService.class.getName());
    private final SportFacilityDao dao = new SportFacilityDao();

    public List<SportFacility> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(SportFacility facility) throws SQLException {
        validate(facility);
        dao.add(facility);
        LOG.info("Додано спортивний об'єкт: " + facility.getCode());
    }

    public void update(SportFacility facility) throws SQLException {
        if (facility == null || facility.getId() == null || facility.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор об'єкта");
        }
        validate(facility);
        dao.update(facility);
        LOG.info("Оновлено спортивний об'єкт ID=" + facility.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено спортивний об'єкт ID=" + id);
    }

    public static void validate(SportFacility f) {
        if (f == null || isBlank(f.getCode()) || isBlank(f.getName()) || isBlank(f.getSportType())) {
            throw new IllegalArgumentException("Усі текстові поля спортивного об'єкта є обов'язковими");
        }
        if (f.getMaxCapacity() <= 0) {
            throw new IllegalArgumentException("Максимальна місткість об'єкта має бути більшою за 0");
        }
    }

    private static boolean isBlank(String v) {
        return v == null || v.isBlank();
    }
}