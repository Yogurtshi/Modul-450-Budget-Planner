package org.m450.budgetplanner.service;

import org.m450.budgetplanner.model.PlannedTransaction;
import org.m450.budgetplanner.model.enums.RecurrenceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionService {

  private final List<PlannedTransaction> transactions;

  public TransactionService(List<PlannedTransaction> transactions) {
    if (transactions == null) {
      throw new IllegalArgumentException("Transaction list must not be null");
    }
    this.transactions = transactions;
  }

  public PlannedTransaction createTransaction(
          BigDecimal amount, LocalDate date, RecurrenceType recurrenceType, int categoryId, int accountId) {
    int nextId = transactions.stream().mapToInt(PlannedTransaction::getId).max().orElse(0) + 1;
    PlannedTransaction transaction =
            new PlannedTransaction(nextId, amount, date, recurrenceType, categoryId, accountId);
    transactions.add(transaction);
    return transaction;
  }

  public List<PlannedTransaction> listTransactions(int accountId) {
    return transactions.stream()
            .filter(t -> t.getAccountId() == accountId)
            .collect(Collectors.toUnmodifiableList());
  }

  public void deleteTransaction(int id) {
    boolean removed = transactions.removeIf(t -> t.getId() == id);
    if (!removed) {
      throw new IllegalArgumentException("No transaction found with id " + id);
    }
  }

  public List<PlannedTransaction> filterByCategory(int categoryId) {
    return PlannedTransaction.filterByCategory(transactions, categoryId);
  }

  public List<PlannedTransaction> filterByCategoryAndRecurrence(int categoryId, RecurrenceType recurrenceType) {
    return PlannedTransaction.filterByCategoryAndRecurrence(transactions, categoryId, recurrenceType);
  }

  public BigDecimal sumByCategory(int categoryId) {
    return PlannedTransaction.sumByCategory(transactions, categoryId);
  }
}