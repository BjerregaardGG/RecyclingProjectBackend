package com.recyclingprojectbackend.item.dto;

import com.recyclingprojectbackend.category.model.Category;
import jakarta.persistence.*;

public class ItemDto {
    private long id;
    private String name;
    private String description;
    private String secondDescription;
    private String image;
    private String category;
    private Long userId;
    private Double latitude;
    private Double longitude;

    public ItemDto(long id, String name, String description, String secondDescription, String image, String category, Long userId, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.secondDescription = secondDescription;
        this.image = image;
        this.category = category;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSecondDescription() {
        return secondDescription;
    }

    public void setSecondDescription(String secondDescription) {
        this.secondDescription = secondDescription;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
