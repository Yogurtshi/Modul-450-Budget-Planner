package org.m450.budgetplanner.service;

import org.m450.budgetplanner.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CategoryService {

  private final List<Category> categories;

  public CategoryService(List<Category> categories) {
    if (categories == null) {
      throw new IllegalArgumentException("Category list must not be null");
    }
    this.categories = categories;
  }

  public Category createCategory(String name, int customerId) {
    if (!Category.isNameUnique(name, customerId, categories)) {
      throw new IllegalArgumentException("Category name already exists: " + name);
    }
    int nextId = categories.stream().mapToInt(Category::getId).max().orElse(0) + 1;
    Category category = new Category(nextId, name, customerId);
    categories.add(category);
    return category;
  }

  public List<Category> listCategories(int customerId) {
    return categories.stream()
            .filter(c -> c.getCustomerId() == customerId)
            .collect(Collectors.toUnmodifiableList());
  }

  public void deleteCategory(int id) {
    boolean removed = categories.removeIf(c -> c.getId() == id);
    if (!removed) {
      throw new IllegalArgumentException("No category found with id " + id);
    }
  }

  public Optional<Category> findCategoryById(int id) {
    return categories.stream().filter(c -> c.getId() == id).findFirst();
  }

  public Optional<Category> findCategoryByName(String name, int customerId) {
    if (name == null) {
      throw new IllegalArgumentException("Name must not be null");
    }
    return categories.stream()
            .filter(c -> c.getCustomerId() == customerId)
            .filter(c -> c.getName().equalsIgnoreCase(name))
            .findFirst();
  }
}