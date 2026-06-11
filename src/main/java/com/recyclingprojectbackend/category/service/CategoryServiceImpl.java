package com.recyclingprojectbackend.category.service;

import com.recyclingprojectbackend.category.model.Category;
import com.recyclingprojectbackend.category.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kategorien eksisterer allerede");
        }

        return categoryRepository.save(category);
    }
}
