package org.m450.budgetplanner.model;

import java.util.List;

public class Category {

  private final int id;
  private String name;

  public Category(int id, String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Category name must not be null or blank");
    }
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void editCategoryName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Category name must not be null or blank");
    }
    this.name = name;
  }

  public static boolean isNameUnique(String name, List<Category> existingCategories) {
    if (name == null) {
      throw new IllegalArgumentException("Name must not be null");
    }
    if (existingCategories == null) {
      throw new IllegalArgumentException("Category list must not be null");
    }
    return existingCategories.stream()
            .noneMatch(c -> c.getName().equalsIgnoreCase(name));
  }
}