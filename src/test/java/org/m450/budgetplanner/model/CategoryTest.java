package org.m450.budgetplanner.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

  // ---- isNameUnique: Sammlungen & Listen ----

  @Test
  void isNameUnique_withEmptyList_returnsTrue() {
    assertTrue(Category.isNameUnique("Rent", 1, List.of()));
  }

  @Test
  void isNameUnique_withNoMatchInList_returnsTrue() {
    List<Category> categories = List.of(new Category(1, "Food", 1), new Category(2, "Subscriptions", 1));

    assertTrue(Category.isNameUnique("Rent", 1, categories));
  }

  @Test
  void isNameUnique_withExactMatch_returnsFalse() {
    List<Category> categories = List.of(new Category(1, "Rent", 1));

    assertFalse(Category.isNameUnique("Rent", 1, categories));
  }

  @Test
  void isNameUnique_withDifferentCase_returnsFalse() {
    // "rent" vs "Rent" must count as a duplicate
    List<Category> categories = List.of(new Category(1, "Rent", 1));

    assertFalse(Category.isNameUnique("rent", 1, categories));
  }

  @Test
  void isNameUnique_withDifferentCustomer_returnsTrue() {
    // Same name but belongs to a different customer -> must NOT count as duplicate
    List<Category> categories = List.of(new Category(1, "Rent", 1));

    assertTrue(Category.isNameUnique("Rent", 2, categories));
  }

  @Test
  void isNameUnique_withNullName_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> Category.isNameUnique(null, 1, List.of()));
  }

  @Test
  void isNameUnique_withNullList_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> Category.isNameUnique("Rent", 1, null));
  }

  // ---- Constructor / editCategoryName ----

  @Test
  void constructor_withBlankName_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Category(1, " ", 1));
  }

  @Test
  void constructor_withValidName_createsCategory() {
    Category category = new Category(1, "Food", 1);

    assertEquals("Food", category.getName());
    assertEquals(1, category.getId());
    assertEquals(1, category.getCustomerId());
  }

  @Test
  void editCategoryName_withBlankName_throwsIllegalArgumentException() {
    Category category = new Category(1, "Food", 1);

    assertThrows(IllegalArgumentException.class, () -> category.editCategoryName(""));
  }

  @Test
  void editCategoryName_withValidName_updatesName() {
    Category category = new Category(1, "Food", 1);

    category.editCategoryName("Groceries");

    assertEquals("Groceries", category.getName());
  }
}
