package ua.edu.nupp.kpp.client.model;

public class User {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String category;

    public User() {
    }

    public User(Long id, String fullName, String email, String phone, String category) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}