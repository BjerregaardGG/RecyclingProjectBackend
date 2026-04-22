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

    public ItemDto(long id, String name, String description, String secondDescription, String image, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.category = category;
        this.secondDescription = secondDescription;
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
}
