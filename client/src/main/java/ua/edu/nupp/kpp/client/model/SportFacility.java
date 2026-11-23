package ua.edu.nupp.kpp.client.model;

public class SportFacility {
    private Long id;
    private String code;
    private String name;
    private String sportType;
    private int maxCapacity;

    public SportFacility() {
    }

    public SportFacility(Long id, String code, String name, String sportType, int maxCapacity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sportType = sportType;
        this.maxCapacity = maxCapacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}