package org.m450.budgetplanner.service;

import org.m450.budgetplanner.model.Account;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AccountService {

  private final List<Account> accounts;

  public AccountService(List<Account> accounts) {
    if (accounts == null) {
      throw new IllegalArgumentException("Account list must not be null");
    }
    this.accounts = accounts;
  }

  public Account createAccount(String name, BigDecimal startingBalance, int customerId) {
    int nextId = accounts.stream().mapToInt(Account::getId).max().orElse(0) + 1;
    Account account = new Account(nextId, name, startingBalance, customerId);
    accounts.add(account);
    return account;
  }

  public List<Account> listAccounts(int customerId) {
    return accounts.stream()
            .filter(a -> a.getCustomerId() == customerId)
            .collect(Collectors.toUnmodifiableList());
  }

  public List<Account> listAllAccounts() {
    return Collections.unmodifiableList(accounts);
  }

  public void deleteAccount(int id) {
    boolean removed = accounts.removeIf(a -> a.getId() == id);
    if (!removed) {
      throw new IllegalArgumentException("No account found with id " + id);
    }
  }

  public Optional<Account> selectAccount(int id) {
    return accounts.stream().filter(a -> a.getId() == id).findFirst();
  }
}