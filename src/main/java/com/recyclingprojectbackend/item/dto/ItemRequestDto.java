package com.recyclingprojectbackend.item.dto;

public class ItemRequestDto {

    private String name;
    private String description;
    private String secondTitle;
    private Long categoryId;
    private String image;

    public ItemRequestDto(String name, String description, String secondTitle, String image, Long categoryId) {
        this.name = name;
        this.description = description;
        this.secondTitle = secondTitle;
        this.categoryId = categoryId;
        this.image = image;
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
}
