package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ua.edu.nupp.kpp.client.api.HttpSportsBookingClient;
import ua.edu.nupp.kpp.client.model.Booking;
import ua.edu.nupp.kpp.client.model.BookingState;
import ua.edu.nupp.kpp.client.model.SportFacility;
import ua.edu.nupp.kpp.client.model.TimeSlot;
import ua.edu.nupp.kpp.client.model.User;
import ua.edu.nupp.kpp.client.task.ImportTimeSlotsTask;

import java.nio.file.Path;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpSportsBookingClient api = new HttpSportsBookingClient();

    private final TableView<SportFacility> facilitiesTable = new TableView<>();
    private final TableView<User> usersTable = new TableView<>();
    private final TableView<TimeSlot> slotsTable = new TableView<>();
    private final TableView<Booking> bookingsTable = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Спортивні об'єкти", facilityPane()),
                new Tab("Користувачі", userPane()),
                new Tab("Часові слоти", slotPane()),
                new Tab("Бронювання", bookingPane())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));

        stage.setScene(new Scene(tabs, 1120, 680));
        stage.setTitle("Інформаційна система бронювання спортивних об'єктів — Варіант 14");
        stage.show();

        refreshAll();
    }

    private Pane facilityPane() {
        facilitiesTable.getColumns().setAll(
                col("ID", "id"),
                col("Код", "code"),
                col("Назва", "name"),
                col("Вид спорту", "sportType"),
                col("Місткість", "maxCapacity")
        );

        TextField code = new TextField();
        TextField name = new TextField();
        TextField sportType = new TextField();
        TextField capacity = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addFacility(readFacility(null, code, name, sportType, capacity));
            refreshFacilities();
        }));

        update.setOnAction(e -> safe(() -> {
            SportFacility selected = selected(facilitiesTable);
            api.updateFacility(readFacility(selected.getId(), code, name, sportType, capacity));
            refreshFacilities();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteFacility(selected(facilitiesTable).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshFacilities());

        facilitiesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, f) -> {
            if (f != null) {
                code.setText(f.getCode());
                name.setText(f.getName());
                sportType.setText(f.getSportType());
                capacity.setText(String.valueOf(f.getMaxCapacity()));
            }
        });

        VBox form = new VBox(8,
                new Label("Код об'єкта"), code,
                new Label("Назва спортивного об'єкта"), name,
                new Label("Вид спорту"), sportType,
                new Label("Максимальна місткість (осіб)"), capacity,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(facilitiesTable, form);
    }

    private Pane userPane() {
        usersTable.getColumns().setAll(
                col("ID", "id"),
                col("ПІБ", "fullName"),
                col("Email", "email"),
                col("Телефон", "phone"),
                col("Категорія", "category")
        );

        TextField fullName = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        TextField category = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addUser(readUser(null, fullName, email, phone, category));
            refreshUsers();
        }));

        update.setOnAction(e -> safe(() -> {
            User selected = selected(usersTable);
            api.updateUser(readUser(selected.getId(), fullName, email, phone, category));
            refreshUsers();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteUser(selected(usersTable).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshUsers());

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, u) -> {
            if (u != null) {
                fullName.setText(u.getFullName());
                email.setText(u.getEmail());
                phone.setText(u.getPhone());
                category.setText(u.getCategory());
            }
        });

        VBox form = new VBox(8,
                new Label("ПІБ користувача"), fullName,
                new Label("Електронна пошта"), email,
                new Label("Телефон"), phone,
                new Label("Категорія (Студент / Тренер / Команда)"), category,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(usersTable, form);
    }

    private Pane slotPane() {
        slotsTable.getColumns().setAll(
                col("ID", "id"),
                col("ID об'єкта", "facilityId"),
                col("Дата", "slotDate"),
                col("Початок", "startTime"),
                col("Завершення", "endTime"),
                col("Тривалість (хв)", "durationMinutes")
        );

        TextField facilityId = new TextField();
        TextField slotDate = new TextField();
        TextField startTime = new TextField();
        TextField endTime = new TextField();
        TextField duration = new TextField();

        ProgressBar progress = new ProgressBar(0);
        progress.setMaxWidth(Double.MAX_VALUE);
        Label status = new Label("CSV-імпорт не запущено");

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");
        Button importCsv = new Button("Імпортувати CSV слотів");
        Button cancelCsv = new Button("Скасувати імпорт");
        cancelCsv.setDisable(true);

        add.setOnAction(e -> safe(() -> {
            api.addTimeSlot(readSlot(null, facilityId, slotDate, startTime, endTime, duration));
            refreshSlots();
        }));

        update.setOnAction(e -> safe(() -> {
            TimeSlot selected = selected(slotsTable);
            api.updateTimeSlot(readSlot(selected.getId(), facilityId, slotDate, startTime, endTime, duration));
            refreshSlots();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteTimeSlot(selected(slotsTable).getId());
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshSlots());

        slotsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, s) -> {
            if (s != null) {
                facilityId.setText(String.valueOf(s.getFacilityId()));
                slotDate.setText(s.getSlotDate());
                startTime.setText(s.getStartTime());
                endTime.setText(s.getEndTime());
                duration.setText(String.valueOf(s.getDurationMinutes()));
            }
        });

        importCsv.setOnAction(e -> {
            ImportTimeSlotsTask task = new ImportTimeSlotsTask(Path.of("sample-data", "time_slots.csv"), api);
            progress.progressProperty().bind(task.progressProperty());
            status.textProperty().bind(task.messageProperty());
            importCsv.setDisable(true);
            cancelCsv.setDisable(false);
            cancelCsv.setOnAction(ev -> task.cancel());

            task.setOnSucceeded(ev -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                status.setText("Готово. Імпортовано слотів: " + task.getValue());
                importCsv.setDisable(false);
                cancelCsv.setDisable(true);
                refreshSlots();
            });

            task.setOnCancelled(ev -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                importCsv.setDisable(false);
                cancelCsv.setDisable(true);
                refreshSlots();
            });

            task.setOnFailed(ev -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                importCsv.setDisable(false);
                cancelCsv.setDisable(true);
                showError(task.getException());
            });

            new Thread(task, "csv-import-slots").start();
        });

        VBox form = new VBox(6,
                new Label("ID спортивного об'єкта"), facilityId,
                new Label("Дата (YYYY-MM-DD)"), slotDate,
                new Label("Час початку (HH:MM)"), startTime,
                new Label("Час завершення (HH:MM)"), endTime,
                new Label("Тривалість (хв)"), duration,
                new HBox(8, add, update, delete, refresh),
                new HBox(8, importCsv, cancelCsv),
                progress,
                status
        );
        return layout(slotsTable, form);
    }

    private Pane bookingPane() {
        bookingsTable.getColumns().setAll(
                col("ID", "id"),
                col("User ID", "userId"),
                col("Slot ID", "timeSlotId"),
                col("Стан", "state"),
                col("Учасники", "participantsCount"),
                col("Обладн.", "equipmentScore"),
                col("F1", "f1"),
                col("F2", "f2"),
                col("F3", "f3"),
                col("Індекс I", "priorityIndex")
        );

        TextField userId = new TextField();
        TextField slotId = new TextField();
        TextField participants = new TextField();
        TextField equipmentScore = new TextField();
        ComboBox<BookingState> stateBox = new ComboBox<>(FXCollections.observableArrayList(BookingState.values()));

        Button add = new Button("Створити");
        Button update = new Button("Перерахувати");
        Button changeState = new Button("Змінити стан");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addBooking(
                    parseLong(userId, "ID користувача"),
                    parseLong(slotId, "ID слоту"),
                    parseInt(participants, "Кількість учасників"),
                    parseDouble(equipmentScore, "Потреба в обладнанні (0..100)")
            );
            refreshBookings();
        }));

        update.setOnAction(e -> safe(() -> {
            Booking selected = selected(bookingsTable);
            api.updateBooking(
                    selected.getId(),
                    parseLong(userId, "ID користувача"),
                    parseLong(slotId, "ID слоту"),
                    parseInt(participants, "Кількість учасників"),
                    parseDouble(equipmentScore, "Потреба в обладнанні (0..100)")
            );
            refreshBookings();
        }));

        changeState.setOnAction(e -> safe(() -> {
            Booking selected = selected(bookingsTable);
            BookingState target = stateBox.getValue();
            if (target == null) {
                throw new IllegalArgumentException("Оберіть новий стан зі списку");
            }
            api.changeBookingState(selected.getId(), target);
            refreshBookings();
        }));

        delete.setOnAction(e -> safe(() -> {
            api.deleteBooking(selected(bookingsTable).getId());
            refreshBookings();
        }));

        refresh.setOnAction(e -> refreshBookings());

        bookingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, b) -> {
            if (b != null) {
                userId.setText(String.valueOf(b.getUserId()));
                slotId.setText(String.valueOf(b.getTimeSlotId()));
                participants.setText(String.valueOf(b.getParticipantsCount()));
                equipmentScore.setText(String.valueOf(b.getEquipmentScore()));
                stateBox.setValue(b.getState());
            }
        });

        VBox form = new VBox(7,
                new Label("ID користувача"), userId,
                new Label("ID часового слоту"), slotId,
                new Label("Кількість учасників"), participants,
                new Label("Потреба в дод. обладнанні (0..100)"), equipmentScore,
                new HBox(8, add, update),
                new Label("Наступний стан життєвого циклу"), stateBox,
                new HBox(8, changeState, delete, refresh)
        );
        return layout(bookingsTable, form);
    }

    private SportFacility readFacility(Long id, TextField code, TextField name, TextField sportType, TextField capacity) {
        if (code.getText().isBlank() || name.getText().isBlank() || sportType.getText().isBlank()) {
            throw new IllegalArgumentException("Заповніть усі текстові поля спортивного об'єкта");
        }
        return new SportFacility(id, code.getText().trim(), name.getText().trim(), sportType.getText().trim(),
                parseInt(capacity, "Місткість"));
    }

    private User readUser(Long id, TextField fullName, TextField email, TextField phone, TextField category) {
        if (fullName.getText().isBlank() || email.getText().isBlank()
                || phone.getText().isBlank() || category.getText().isBlank()) {
            throw new IllegalArgumentException("Заповніть усі поля користувача");
        }
        return new User(id, fullName.getText().trim(), email.getText().trim(), phone.getText().trim(), category.getText().trim());
    }

    private TimeSlot readSlot(Long id, TextField facilityId, TextField date, TextField start, TextField end, TextField duration) {
        if (date.getText().isBlank() || start.getText().isBlank() || end.getText().isBlank()) {
            throw new IllegalArgumentException("Заповніть дату та час слоту");
        }
        return new TimeSlot(id, parseLong(facilityId, "ID об'єкта"), date.getText().trim(),
                start.getText().trim(), end.getText().trim(), parseInt(duration, "Тривалість (хв)"));
    }

    private long parseLong(TextField field, String label) {
        try {
            return Long.parseLong(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + label + "» має містити ціле число");
        }
    }

    private int parseInt(TextField field, String label) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + label + "» має містити ціле число");
        }
    }

    private double parseDouble(TextField field, String label) {
        try {
            return Double.parseDouble(field.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле «" + label + "» має містити числове значення");
        }
    }

    private void refreshAll() {
        refreshFacilities();
        refreshUsers();
        refreshSlots();
        refreshBookings();
    }

    private void refreshFacilities() {
        safe(() -> facilitiesTable.setItems(FXCollections.observableArrayList(api.getFacilities())));
    }

    private void refreshUsers() {
        safe(() -> usersTable.setItems(FXCollections.observableArrayList(api.getUsers())));
    }

    private void refreshSlots() {
        safe(() -> slotsTable.setItems(FXCollections.observableArrayList(api.getTimeSlots())));
    }

    private void refreshBookings() {
        safe(() -> bookingsTable.setItems(FXCollections.observableArrayList(api.getBookings())));
    }

    private <T> T selected(TableView<T> table) {
        T item = table.getSelectionModel().getSelectedItem();
        if (item == null) {
            throw new IllegalArgumentException("Оберіть запис у таблиці");
        }
        return item;
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                e == null || e.getMessage() == null ? "Невідома помилка" : e.getMessage(),
                ButtonType.OK);
        alert.setHeaderText("Помилка виконання операції");
        alert.showAndWait();
    }

    private Pane layout(TableView<?> table, Pane form) {
        SplitPane split = new SplitPane(table, form);
        split.setDividerPositions(0.68);
        BorderPane root = new BorderPane(split);
        root.setPadding(new Insets(10));
        return root;
    }

    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    public static void main(String[] args) {
        launch(args);
    }
}