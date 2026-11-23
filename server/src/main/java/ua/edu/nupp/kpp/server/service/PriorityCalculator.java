package ua.edu.nupp.kpp.server.service;

public class PriorityCalculator {
    public static final double MAX_SESSION_MINUTES = 240.0;

    /**
     * F1 — нормована кількість учасників відносно місткості об'єкта (0..100).
     */
    public static double factor1(int participantsCount, int maxCapacity) {
        if (participantsCount <= 0 || maxCapacity <= 0) {
            throw new IllegalArgumentException("Некоректні дані для розрахунку фактора F1");
        }
        double value = ((double) participantsCount / maxCapacity) * 100.0;
        return clampAndRound(value);
    }

    /**
     * F2 — нормована тривалість використання відносно еталонної сесії 240 хв (0..100).
     */
    public static double factor2(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Тривалість слоту має бути більшою за 0");
        }
        double value = (durationMinutes / MAX_SESSION_MINUTES) * 100.0;
        return clampAndRound(value);
    }

    /**
     * Інтегральний показник: I = 0.40 * F1 + 0.35 * F2 + 0.25 * F3.
     */
    public static double calculate(double f1, double f2, double f3) {
        checkRange(f1);
        checkRange(f2);
        checkRange(f3);
        double raw = 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
        return Math.round(raw * 100.0) / 100.0;
    }

    private static double clampAndRound(double value) {
        double clamped = Math.max(0.0, Math.min(100.0, value));
        return Math.round(clamped * 100.0) / 100.0;
    }

    private static void checkRange(double value) {
        if (value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException("Значення фактора має лежати в діапазоні 0..100");
        }
    }
}