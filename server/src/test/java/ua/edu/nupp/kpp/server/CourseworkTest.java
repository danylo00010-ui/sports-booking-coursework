package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;
import ua.edu.nupp.kpp.server.model.BookingState;
import ua.edu.nupp.kpp.server.model.SportFacility;
import ua.edu.nupp.kpp.server.model.TimeSlot;
import ua.edu.nupp.kpp.server.model.User;
import ua.edu.nupp.kpp.server.service.BookingService;
import ua.edu.nupp.kpp.server.service.PriorityCalculator;
import ua.edu.nupp.kpp.server.service.SportFacilityService;
import ua.edu.nupp.kpp.server.service.TimeSlotService;
import ua.edu.nupp.kpp.server.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourseworkTest {

    @Test
    void factor1CalculatesPercentageOfCapacity() {
        assertEquals(50.0, PriorityCalculator.factor1(15, 30));
    }

    @Test
    void factor1ClampsAboveHundred() {
        assertEquals(100.0, PriorityCalculator.factor1(45, 30));
    }

    @Test
    void factor2CalculatesDurationPercentage() {
        assertEquals(50.0, PriorityCalculator.factor2(120));
    }

    @Test
    void integralPriorityCalculatesCorrectly() {
        // I = 0.40 * 50 + 0.35 * 50 + 0.25 * 60 = 20 + 17.5 + 15 = 52.5
        assertEquals(52.5, PriorityCalculator.calculate(50.0, 50.0, 60.0));
    }

    @Test
    void integralPriorityRejectsFactorOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> PriorityCalculator.calculate(105.0, 50.0, 40.0));
        assertThrows(IllegalArgumentException.class, () -> PriorityCalculator.calculate(50.0, -1.0, 40.0));
    }

    @Test
    void transitionCreatedToConfirmedAllowed() {
        assertTrue(BookingService.isAllowedTransition(BookingState.CREATED, BookingState.CONFIRMED));
    }

    @Test
    void transitionConfirmedToActiveAllowed() {
        assertTrue(BookingService.isAllowedTransition(BookingState.CONFIRMED, BookingState.ACTIVE));
    }

    @Test
    void transitionActiveToCompletedAllowed() {
        assertTrue(BookingService.isAllowedTransition(BookingState.ACTIVE, BookingState.COMPLETED));
    }

    @Test
    void transitionCreatedToCompletedRejected() {
        assertFalse(BookingService.isAllowedTransition(BookingState.CREATED, BookingState.COMPLETED));
        assertFalse(BookingService.isAllowedTransition(BookingState.COMPLETED, BookingState.CREATED));
    }

    @Test
    void sportFacilityValidationRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class,
                () -> SportFacilityService.validate(new SportFacility(null, "", "Зал", "Баскетбол", 20)));
        assertThrows(IllegalArgumentException.class,
                () -> SportFacilityService.validate(new SportFacility(null, "SF-1", "Зал", "Баскетбол", 0)));
    }

    @Test
    void userValidationRejectsInvalidEmailOrBlankFields() {
        assertThrows(IllegalArgumentException.class,
                () -> UserService.validate(new User(null, "Іваненко Іван", "invalid-email", "+380501112233", "Студент")));
    }

    @Test
    void timeSlotAndBookingValidationRejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class,
                () -> TimeSlotService.validate(new TimeSlot(null, 1L, "2026-11-25", "10:00", "11:00", -30)));
        assertThrows(IllegalArgumentException.class,
                () -> BookingService.validateParameters(1L, 1L, 10, 120.0));
    }
}