package org.m450.budgetplanner.model;

import java.time.LocalDate;
import java.time.Period;

public class Customer {

  private static final LocalDate EARLIEST_PLAUSIBLE_BIRTHDAY = LocalDate.of(1900, 1, 1);

  private final int id;
  private String name;
  private LocalDate birthday;

  public Customer(int id, String name, LocalDate birthday) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name must not be null or blank");
    }
    if (!isValidBirthday(birthday)) {
      throw new IllegalArgumentException("Birthday is not valid: " + birthday);
    }
    this.id = id;
    this.name = name;
    this.birthday = birthday;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public LocalDate getBirthday() {
    return birthday;
  }

  public void editName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name must not be null or blank");
    }
    this.name = name;
  }

  public void editBirthday(LocalDate birthday) {
    if (!isValidBirthday(birthday)) {
      throw new IllegalArgumentException("Birthday is not valid: " + birthday);
    }
    this.birthday = birthday;
  }

  public static boolean isValidBirthday(LocalDate birthday) {
    if (birthday == null) {
      return false;
    }
    if (birthday.isAfter(LocalDate.now())) {
      return false;
    }
    return !birthday.isBefore(EARLIEST_PLAUSIBLE_BIRTHDAY);
  }

  public int calculateAge() {
    return Period.between(birthday, LocalDate.now()).getYears();
  }
}