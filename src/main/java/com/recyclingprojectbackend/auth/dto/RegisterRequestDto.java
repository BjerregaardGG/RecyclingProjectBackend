package com.recyclingprojectbackend.auth.dto;

public class RegisterRequestDto {
    private String email;
    private String name;
    private String password;
    private String city;
    private String postalCode;

    public RegisterRequestDto(String email, String name, String password, String city, String postalCode) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.city = city;
        this.postalCode = postalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
