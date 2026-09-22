package org.m450.budgetplanner.ui;

import org.m450.budgetplanner.model.Account;
import org.m450.budgetplanner.model.Category;
import org.m450.budgetplanner.model.Customer;
import org.m450.budgetplanner.model.PlannedTransaction;
import org.m450.budgetplanner.model.enums.RecurrenceType;
import org.m450.budgetplanner.service.AccountService;
import org.m450.budgetplanner.service.CategoryService;
import org.m450.budgetplanner.service.CustomerService;
import org.m450.budgetplanner.service.TransactionService;
import org.m450.budgetplanner.storage.AppData;
import org.m450.budgetplanner.storage.JsonStorageService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

  private final Scanner scanner = new Scanner(System.in);
  private final JsonStorageService storageService;
  private final AppData data;
  private final CustomerService customerService;
  private final AccountService accountService;
  private final CategoryService categoryService;
  private final TransactionService transactionService;

  private Customer activeCustomer;
  private Account activeAccount;

  public ConsoleMenu(String dataFilePath) {
    this.storageService = new JsonStorageService(dataFilePath);
    this.data = storageService.load();
    this.customerService = new CustomerService(data.customers);
    this.accountService = new AccountService(data.accounts);
    this.categoryService = new CategoryService(data.categories);
    this.transactionService = new TransactionService(data.transactions);
  }

  public void run() {
    System.out.println("=== Budget Planner ===");
    activeCustomer = showLoginOrRegisterMenu();
    if (activeCustomer == null) {
      System.out.println("Goodbye!");
      return;
    }
    activeAccount = manageAccounts();

    boolean running = true;
    while (running) {
      printMenu();
      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> showBalance();
        case "2" -> createCategory();
        case "3" -> addTransaction();
        case "4" -> showTransactionsByCategory();
        case "5" -> activeAccount = manageAccounts();
        case "6" -> {
          storageService.save(data);
          System.out.println("Saved. Goodbye!");
          running = false;
        }
        default -> System.out.println("Invalid input.");
      }
    }
  }

  private void printMenu() {
    System.out.println();
    System.out.println("Logged in as: " + activeCustomer.getName()
            + " | Active account: " + activeAccount.getName());
    System.out.println("1) Show balance");
    System.out.println("2) Create category");
    System.out.println("3) Add transaction");
    System.out.println("4) Show transactions by category");
    System.out.println("5) Manage / switch account");
    System.out.println("6) Save & exit");
    System.out.print("Choice: ");
  }

  // ==========================================================
  // Login / Register
  // ==========================================================

  private Customer showLoginOrRegisterMenu() {
    while (true) {
      System.out.println();
      System.out.println("1) Login");
      System.out.println("2) Register");
      System.out.println("3) Exit");
      System.out.print("Choice: ");
      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1": {
          Customer customer = login();
          if (customer != null) {
            return customer;
          }
          break;
        }
        case "2": {
          Customer customer = register();
          if (customer != null) {
            return customer;
          }
          break;
        }
        case "3":
          return null;
        default:
          System.out.println("Invalid input.");
      }
    }
  }

  private Customer login() {
    while (true) {
      System.out.print("Name (or type 'back' to return): ");
      String name = scanner.nextLine().trim();
      if (name.equalsIgnoreCase("back")) {
        return null;
      }
      try {
        return customerService.login(name);
      } catch (IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

  private Customer register() {
    while (true) {
      System.out.print("Choose a name: ");
      String name = scanner.nextLine().trim();
      LocalDate birthday = readValidDate("Your birthday (YYYY-MM-DD): ");
      try {
        return customerService.createCustomer(name, birthday);
      } catch (IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

  // ==========================================================
  // Account management: list, select, create, delete
  // ==========================================================

  private Account manageAccounts() {
    while (true) {
      List<Account> accounts = accountService.listAccounts(activeCustomer.getId());

      if (accounts.isEmpty()) {
        System.out.println("You have no accounts yet. Let's create one.");
        Account created = createNewAccount();
        if (created != null) {
          return created;
        }
        continue;
      }

      System.out.println();
      System.out.println("Your accounts:");
      for (Account a : accounts) {
        System.out.println("  " + a.getId() + ": " + a.getName() + " (Balance: " + a.getBalance() + ")");
      }
      System.out.println();
      System.out.println("1) Select an account");
      System.out.println("2) Create a new account");
      System.out.println("3) Delete an account");
      System.out.print("Choice: ");
      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1" -> {
          Account selected = selectExistingAccount(accounts);
          if (selected != null) {
            return selected;
          }
        }
        case "2" -> {
          Account created = createNewAccount();
          if (created != null) {
            return created;
          }
        }
        case "3" -> deleteExistingAccount(accounts);
        default -> System.out.println("Invalid input.");
      }
    }
  }

  private Account selectExistingAccount(List<Account> accounts) {
    int id = readValidInt("Account ID: ");
    for (Account a : accounts) {
      if (a.getId() == id) {
        return a;
      }
    }
    System.out.println("No account with that ID belongs to you.");
    return null;
  }

  private Account createNewAccount() {
    System.out.print("Account name: ");
    String name = scanner.nextLine().trim();
    BigDecimal startingBalance = readValidAmount("Starting balance: ");
    try {
      return accountService.createAccount(name, startingBalance, activeCustomer.getId());
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
      return null;
    }
  }

  private void deleteExistingAccount(List<Account> accounts) {
    int id = readValidInt("Account ID to delete: ");
    boolean belongsToCustomer = accounts.stream().anyMatch(a -> a.getId() == id);
    if (!belongsToCustomer) {
      System.out.println("No account with that ID belongs to you.");
      return;
    }
    try {
      accountService.deleteAccount(id);
      System.out.println("Account deleted.");
      if (activeAccount != null && activeAccount.getId() == id) {
        System.out.println("That was your active account — please select or create another.");
      }
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  // ==========================================================
  // Categories / Transactions (unchanged from before)
  // ==========================================================

  private void showBalance() {
    System.out.println("Current balance: " + activeAccount.getBalance());
  }

  private void createCategory() {
    System.out.print("New category name: ");
    String name = scanner.nextLine().trim();
    try {
      Category category = categoryService.createCategory(name, activeCustomer.getId());
      System.out.println("Category created: " + category.getName());
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void addTransaction() {
    List<Category> categories = categoryService.listCategories(activeCustomer.getId());
    if (categories.isEmpty()) {
      System.out.println("Please create a category first (option 2).");
      return;
    }

    System.out.println("Available categories:");
    for (Category c : categories) {
      System.out.println("  " + c.getId() + ": " + c.getName());
    }
    int categoryId = readValidInt("Category ID: ");

    BigDecimal amount = readValidAmount("Amount (negative = expense, positive = income): ");
    RecurrenceType recurrenceType = readRecurrenceType();

    try {
      PlannedTransaction transaction = transactionService.createTransaction(
              amount, LocalDate.now(), recurrenceType, categoryId, activeAccount.getId());
      if (amount.compareTo(BigDecimal.ZERO) > 0) {
        activeAccount.deposit(amount);
      } else {
        activeAccount.withdraw(amount);
      }
      System.out.println("Transaction recorded, ID " + transaction.getId());
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void showTransactionsByCategory() {
    int categoryId = readValidInt("Category ID: ");
    List<PlannedTransaction> result = transactionService.filterByCategory(categoryId);
    if (result.isEmpty()) {
      System.out.println("No transactions for this category.");
      return;
    }
    for (PlannedTransaction t : result) {
      System.out.println("  " + t.getDate() + " | " + t.getAmount() + " | " + t.getRecurrenceType());
    }
    System.out.println("Total: " + transactionService.sumByCategory(categoryId));
  }

  // ==========================================================
  // Input helpers
  // ==========================================================

  private LocalDate readValidDate(String prompt) {
    while (true) {
      System.out.print(prompt);
      try {
        LocalDate date = LocalDate.parse(scanner.nextLine().trim());
        if (Customer.isValidBirthday(date)) {
          return date;
        }
        System.out.println("Invalid birthday.");
      } catch (Exception e) {
        System.out.println("Invalid format. Example: 2000-05-14");
      }
    }
  }

  private BigDecimal readValidAmount(String prompt) {
    while (true) {
      System.out.print(prompt);
      try {
        return new BigDecimal(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        System.out.println("Invalid amount.");
      }
    }
  }

  private int readValidInt(String prompt) {
    while (true) {
      System.out.print(prompt);
      try {
        return Integer.parseInt(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        System.out.println("Please enter a number.");
      }
    }
  }

  private RecurrenceType readRecurrenceType() {
    while (true) {
      System.out.print("Recurrence (ONCE, DAILY, WEEKLY, MONTHLY): ");
      try {
        return RecurrenceType.valueOf(scanner.nextLine().trim().toUpperCase());
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid recurrence.");
      }
    }
  }
}