package com.hasjay.reppido.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Integer id) {
        super("Category not found with id: " + id);
    }
}
