package ua.edu.nupp.kpp.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.server.model.User;
import ua.edu.nupp.kpp.server.service.UserService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(UserServlet.class.getName());
    private final UserService service = new UserService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("delete".equals(action)) {
                service.delete(parseId(req));
            } else if ("add".equals(action) || "update".equals(action)) {
                User user = new User(
                        "update".equals(action) ? parseId(req) : null,
                        req.getParameter("fullName"),
                        req.getParameter("email"),
                        req.getParameter("phone"),
                        req.getParameter("category")
                );
                if ("add".equals(action)) {
                    service.add(user);
                } else {
                    service.update(user);
                }
            } else {
                throw new IllegalArgumentException("Невідома операція: " + action);
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        boolean clientError = e instanceof IllegalArgumentException;
        if (!clientError) {
            LOG.log(Level.SEVERE, "Помилка сервера під час обробки запиту до /users", e);
        }
        int status = clientError
                ? HttpServletResponse.SC_BAD_REQUEST
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = clientError
                ? (e.getMessage() == null ? "Некоректний запит" : e.getMessage())
                : "Внутрішня помилка сервера";
        resp.setStatus(status);
        writeJson(resp, Map.of("error", message));
    }

    private long parseId(HttpServletRequest req) {
        return Long.parseLong(req.getParameter("id"));
    }
}