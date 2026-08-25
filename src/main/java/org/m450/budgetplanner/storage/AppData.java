package org.m450.budgetplanner.storage;

import org.m450.budgetplanner.model.Account;
import org.m450.budgetplanner.model.Category;
import org.m450.budgetplanner.model.Customer;
import org.m450.budgetplanner.model.PlannedTransaction;

import java.util.ArrayList;
import java.util.List;

public class AppData {
  public List<Customer> customers = new ArrayList<>();
  public List<Account> accounts = new ArrayList<>();
  public List<Category> categories = new ArrayList<>();
  public List<PlannedTransaction> transactions = new ArrayList<>();
}