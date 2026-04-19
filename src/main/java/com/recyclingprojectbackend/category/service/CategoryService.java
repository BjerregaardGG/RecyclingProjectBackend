package com.recyclingprojectbackend.category.service;

import com.recyclingprojectbackend.category.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category createCategory(Category category);
}
