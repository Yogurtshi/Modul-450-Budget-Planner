package org.m450.budgetplanner.model;

import java.util.List;

public class Category {

  private final int id;
  private String name;
  private final int customerId;

  public Category(int id, String name, int customerId) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Category name must not be null or blank");
    }
    this.id = id;
    this.name = name;
    this.customerId = customerId;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getCustomerId() {
    return customerId;
  }

  public void editCategoryName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Category name must not be null or blank");
    }
    this.name = name;
  }

  // Uniqueness is now scoped per customer — two different users CAN both have "Food"
  public static boolean isNameUnique(String name, int customerId, List<Category> existingCategories) {
    if (name == null) {
      throw new IllegalArgumentException("Name must not be null");
    }
    if (existingCategories == null) {
      throw new IllegalArgumentException("Category list must not be null");
    }
    return existingCategories.stream()
            .filter(c -> c.getCustomerId() == customerId)
            .noneMatch(c -> c.getName().equalsIgnoreCase(name));
  }
}