package com.recyclingprojectbackend.item.dto;

public class ItemRequestDto {

    private String name;
    private String description;
    private String secondTitle;
    private Long categoryId;
    private String image;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;

    public ItemRequestDto(String name, String description, String secondTitle, String image, Long categoryId, String address, String city, Double latitude, Double longitude) {
        this.name = name;
        this.description = description;
        this.secondTitle = secondTitle;
        this.categoryId = categoryId;
        this.image = image;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
