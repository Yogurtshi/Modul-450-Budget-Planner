package org.m450.budgetplanner.service;

import org.m450.budgetplanner.model.PlannedTransaction;
import org.m450.budgetplanner.model.enums.RecurrenceType;
import org.m450.budgetplanner.storage.AppData;
import org.m450.budgetplanner.storage.JsonStorageService;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionWorkflowService {

  private final TransactionService transactionService;
  private final JsonStorageService storageService;
  private final AppData data;

  public TransactionWorkflowService(
          TransactionService transactionService,
          JsonStorageService storageService,
          AppData data
  ) {
    if (transactionService == null) {
      throw new IllegalArgumentException("TransactionService must not be null");
    }
    if (storageService == null) {
      throw new IllegalArgumentException("JsonStorageService must not be null");
    }
    if (data == null) {
      throw new IllegalArgumentException("AppData must not be null");
    }
    this.transactionService = transactionService;
    this.storageService = storageService;
    this.data = data;
  }

  public PlannedTransaction recordTransaction(
          BigDecimal amount,
          LocalDate date,
          RecurrenceType recurrenceType,
          int categoryId,
          int accountId
  ) {
    PlannedTransaction transaction =
            transactionService.createTransaction(amount, date, recurrenceType, categoryId, accountId);

    storageService.save(data);

    return transaction;
  }
}