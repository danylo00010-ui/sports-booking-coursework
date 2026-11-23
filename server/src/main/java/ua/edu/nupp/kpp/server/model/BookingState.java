package ua.edu.nupp.kpp.server.model;

public enum BookingState {
    CREATED("СТВОРЕНО"),
    CONFIRMED("ПІДТВЕРДЖЕНО"),
    ACTIVE("АКТИВНЕ"),
    COMPLETED("ЗАВЕРШЕНО");

    private final String titleUa;

    BookingState(String titleUa) {
        this.titleUa = titleUa;
    }

    public String getTitleUa() {
        return titleUa;
    }
}