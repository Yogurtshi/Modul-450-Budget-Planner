package org.m450.budgetplanner.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

  // ---- isValidBirthday: Fehlerbehandlung & Edge Cases ----

  @Test
  void isValidBirthday_withNull_returnsFalse() {
    assertFalse(Customer.isValidBirthday(null));
  }

  @Test
  void isValidBirthday_withFutureDate_returnsFalse() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);

    assertFalse(Customer.isValidBirthday(tomorrow));
  }

  @Test
  void isValidBirthday_withDateBeforeEarliestPlausibleYear_returnsFalse() {
    LocalDate tooOld = LocalDate.of(1899, 12, 31);

    assertFalse(Customer.isValidBirthday(tooOld));
  }

  @Test
  void isValidBirthday_withToday_returnsTrue() {
    // boundary: today is still valid (isAfter(now) is false on equality)
    assertTrue(Customer.isValidBirthday(LocalDate.now()));
  }

  @Test
  void isValidBirthday_withEarliestPlausibleDate_returnsTrue() {
    // boundary: 1900-01-01 is the lower bound and still valid
    assertTrue(Customer.isValidBirthday(LocalDate.of(1900, 1, 1)));
  }

  @Test
  void isValidBirthday_withNormalDate_returnsTrue() {
    assertTrue(Customer.isValidBirthday(LocalDate.of(2000, 5, 14)));
  }

  // ---- Constructor validates via isValidBirthday ----

  @Test
  void constructor_withInvalidBirthday_throwsIllegalArgumentException() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new Customer(1, "Anna", LocalDate.now().plusDays(1))
    );
  }

  @Test
  void constructor_withBlankName_throwsIllegalArgumentException() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new Customer(1, "  ", LocalDate.of(2000, 1, 1))
    );
  }

  @Test
  void constructor_withValidData_createsCustomer() {
    Customer customer = new Customer(1, "Anna", LocalDate.of(2000, 1, 1));

    assertEquals("Anna", customer.getName());
    assertEquals(LocalDate.of(2000, 1, 1), customer.getBirthday());
  }

  // ---- editName / editBirthday ----

  @Test
  void editName_withBlankName_throwsIllegalArgumentException() {
    Customer customer = new Customer(1, "Anna", LocalDate.of(2000, 1, 1));

    assertThrows(IllegalArgumentException.class, () -> customer.editName(""));
  }

  @Test
  void editName_withValidName_updatesName() {
    Customer customer = new Customer(1, "Anna", LocalDate.of(2000, 1, 1));

    customer.editName("Anna Holiuk");

    assertEquals("Anna Holiuk", customer.getName());
  }

  @Test
  void editBirthday_withInvalidDate_throwsIllegalArgumentException() {
    Customer customer = new Customer(1, "Anna", LocalDate.of(2000, 1, 1));

    assertThrows(
            IllegalArgumentException.class,
            () -> customer.editBirthday(LocalDate.now().plusDays(1))
    );
  }

  @Test
  void editBirthday_withValidDate_updatesBirthday() {
    Customer customer = new Customer(1, "Anna", LocalDate.of(2000, 1, 1));
    customer.editBirthday(LocalDate.of(1999, 6, 1));

    assertEquals(LocalDate.of(1999, 6, 1), customer.getBirthday());
  }

  // ---- calculateAge ----

  @Test
  void calculateAge_withBirthdayTenYearsAgo_returnsTen() {
    LocalDate tenYearsAgo = LocalDate.now().minusYears(10);
    Customer customer = new Customer(1, "Anna", tenYearsAgo);

    assertEquals(10, customer.calculateAge());
  }

  @Test
  void calculateAge_withBirthdayLaterThisYear_returnsAgeMinusOne() {
    // boundary: birthday hasn't occurred yet this cycle -> one year younger
    LocalDate birthday = LocalDate.now().minusYears(10).plusDays(1);
    Customer customer = new Customer(1, "Anna", birthday);

    assertEquals(9, customer.calculateAge());
  }
}