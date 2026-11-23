package ua.edu.nupp.kpp.client.task;

import javafx.concurrent.Task;
import ua.edu.nupp.kpp.client.api.HttpSportsBookingClient;
import ua.edu.nupp.kpp.client.model.TimeSlot;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ImportTimeSlotsTask extends Task<Integer> {
    private static final Logger LOG = Logger.getLogger(ImportTimeSlotsTask.class.getName());
    private final Path file;
    private final HttpSportsBookingClient api;

    public ImportTimeSlotsTask(Path file, HttpSportsBookingClient api) {
        this.file = file;
        this.api = api;
    }

    @Override
    protected Integer call() throws Exception {
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("CSV-файл не знайдено: " + file);
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line.replace("\uFEFF", ""));
                }
            }
        }

        int total = Math.max(1, lines.size() - 1);
        int imported = 0;
        int skipped = 0;

        for (int i = 1; i < lines.size(); i++) {
            if (isCancelled()) {
                updateMessage("Імпорт скасовано. Імпортовано: " + imported);
                LOG.info("CSV-імпорт скасовано користувачем");
                return imported;
            }

            String[] parts = lines.get(i).split(";");
            if (parts.length != 5) {
                skipped++;
                LOG.warning("Пропущено рядок CSV №" + (i + 1) + ": очікується 5 полів");
                updateProgress(i, total);
                continue;
            }

            try {
                TimeSlot slot = new TimeSlot(
                        null,
                        Long.parseLong(parts[0].trim()),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        Integer.parseInt(parts[4].trim())
                );
                api.addTimeSlot(slot);
                imported++;
            } catch (Exception rowError) {
                skipped++;
                LOG.warning("Помилка в рядку CSV №" + (i + 1) + ": " + rowError.getMessage());
            }

            updateProgress(i, total);
            updateMessage("Імпортовано: " + imported + " із " + total + (skipped > 0 ? " (пропущено: " + skipped + ")" : ""));
        }

        LOG.info("CSV-імпорт завершено. Успішно: " + imported + ", пропущено: " + skipped);
        return imported;
    }
}