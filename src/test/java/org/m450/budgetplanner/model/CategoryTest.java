package org.m450.budgetplanner.model;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {

  // ---- isNameUnique: Sammlungen & Listen ----

  @Test
  void isNameUnique_withEmptyList_returnsTrue() {
    assertTrue(Category.isNameUnique("Rent", List.of()));
  }

  @Test
  void isNameUnique_withNoMatchInList_returnsTrue() {
    List<Category> categories = List.of(new Category(1, "Food"), new Category(2, "Subscriptions"));

    assertTrue(Category.isNameUnique("Rent", categories));
  }

  @Test
  void isNameUnique_withExactMatch_returnsFalse() {
    List<Category> categories = List.of(new Category(1, "Rent"));

    assertFalse(Category.isNameUnique("Rent", categories));
  }

  @Test
  void isNameUnique_withDifferentCase_returnsFalse() {
    // "rent" vs "Rent" must count as a duplicate
    List<Category> categories = List.of(new Category(1, "Rent"));

    assertFalse(Category.isNameUnique("rent", categories));
  }

  @Test
  void isNameUnique_withNullName_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> Category.isNameUnique(null, List.of()));
  }

  @Test
  void isNameUnique_withNullList_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> Category.isNameUnique("Rent", null));
  }

  // ---- Constructor / editCategoryName ----

  @Test
  void constructor_withBlankName_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Category(1, " "));
  }

  @Test
  void constructor_withValidName_createsCategory() {
    Category category = new Category(1, "Food");

    assertEquals("Food", category.getName());
    assertEquals(1, category.getId());
  }

  @Test
  void editCategoryName_withBlankName_throwsIllegalArgumentException() {
    Category category = new Category(1, "Food");

    assertThrows(IllegalArgumentException.class, () -> category.editCategoryName(""));
  }

  @Test
  void editCategoryName_withValidName_updatesName() {
    Category category = new Category(1, "Food");

    category.editCategoryName("Groceries");

    assertEquals("Groceries", category.getName());
  }
}