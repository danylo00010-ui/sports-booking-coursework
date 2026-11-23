package ua.edu.nupp.kpp.server.service;

import ua.edu.nupp.kpp.server.dao.UserDao;
import ua.edu.nupp.kpp.server.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class UserService {
    private static final Logger LOG = Logger.getLogger(UserService.class.getName());
    private final UserDao dao = new UserDao();

    public List<User> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(User user) throws SQLException {
        validate(user);
        dao.add(user);
        LOG.info("Додано користувача: " + user.getFullName());
    }

    public void update(User user) throws SQLException {
        if (user == null || user.getId() == null || user.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор користувача");
        }
        validate(user);
        dao.update(user);
        LOG.info("Оновлено користувача ID=" + user.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено користувача ID=" + id);
    }

    public static void validate(User u) {
        if (u == null || isBlank(u.getFullName()) || isBlank(u.getEmail())
                || isBlank(u.getPhone()) || isBlank(u.getCategory())) {
            throw new IllegalArgumentException("Усі поля користувача є обов'язковими");
        }
        if (!u.getEmail().contains("@")) {
            throw new IllegalArgumentException("Некоректний формат електронної пошти");
        }
    }

    private static boolean isBlank(String v) {
        return v == null || v.isBlank();
    }
}