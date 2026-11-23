package ua.edu.nupp.kpp.client.model;

public class Booking {
    private Long id;
    private long userId;
    private long timeSlotId;
    private BookingState state;
    private int participantsCount;
    private double equipmentScore;
    private double f1;
    private double f2;
    private double f3;
    private double priorityIndex;

    public Booking() {
    }

    public Booking(Long id, long userId, long timeSlotId, BookingState state,
                   int participantsCount, double equipmentScore,
                   double f1, double f2, double f3, double priorityIndex) {
        this.id = id;
        this.userId = userId;
        this.timeSlotId = timeSlotId;
        this.state = state;
        this.participantsCount = participantsCount;
        this.equipmentScore = equipmentScore;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.priorityIndex = priorityIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public BookingState getState() {
        return state;
    }

    public void setState(BookingState state) {
        this.state = state;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public double getEquipmentScore() {
        return equipmentScore;
    }

    public void setEquipmentScore(double equipmentScore) {
        this.equipmentScore = equipmentScore;
    }

    public double getF1() {
        return f1;
    }

    public void setF1(double f1) {
        this.f1 = f1;
    }

    public double getF2() {
        return f2;
    }

    public void setF2(double f2) {
        this.f2 = f2;
    }

    public double getF3() {
        return f3;
    }

    public void setF3(double f3) {
        this.f3 = f3;
    }

    public double getPriorityIndex() {
        return priorityIndex;
    }

    public void setPriorityIndex(double priorityIndex) {
        this.priorityIndex = priorityIndex;
    }
}