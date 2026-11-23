package ua.edu.nupp.kpp.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.server.model.BookingState;
import ua.edu.nupp.kpp.server.service.BookingService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(BookingServlet.class.getName());
    private final BookingService service = new BookingService();
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
            if ("add".equals(action)) {
                service.add(
                        Long.parseLong(req.getParameter("userId")),
                        Long.parseLong(req.getParameter("timeSlotId")),
                        Integer.parseInt(req.getParameter("participantsCount")),
                        Double.parseDouble(req.getParameter("equipmentScore"))
                );
            } else if ("update".equals(action)) {
                service.update(
                        parseId(req),
                        Long.parseLong(req.getParameter("userId")),
                        Long.parseLong(req.getParameter("timeSlotId")),
                        Integer.parseInt(req.getParameter("participantsCount")),
                        Double.parseDouble(req.getParameter("equipmentScore"))
                );
            } else if ("transition".equals(action)) {
                String stateValue = req.getParameter("state");
                if (stateValue == null || stateValue.isBlank()) {
                    throw new IllegalArgumentException("Не задано новий стан бронювання");
                }
                service.changeState(parseId(req), BookingState.valueOf(stateValue));
            } else if ("delete".equals(action)) {
                service.delete(parseId(req));
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
            LOG.log(Level.SEVERE, "Помилка сервера під час обробки запиту до /bookings", e);
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