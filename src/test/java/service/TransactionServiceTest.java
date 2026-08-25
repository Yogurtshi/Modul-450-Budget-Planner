package org.m450.budgetplanner.service;

import org.junit.jupiter.api.Test;
import org.m450.budgetplanner.model.PlannedTransaction;
import org.m450.budgetplanner.model.enums.RecurrenceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceTest {

  // ---- Constructor ----

  @Test
  void constructor_withNullList_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new TransactionService(null));
  }

  // ---- createTransaction ----

  @Test
  void createTransaction_withValidData_addsToList() {
    TransactionService service = new TransactionService(new ArrayList<>());

    PlannedTransaction created =
            service.createTransaction(new BigDecimal("25.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    assertEquals(1, service.listTransactions(1).size());
    assertSame(created, service.listTransactions(1).get(0));
  }

  @Test
  void createTransaction_assignsIdOneToFirstTransaction() {
    TransactionService service = new TransactionService(new ArrayList<>());

    PlannedTransaction created =
            service.createTransaction(new BigDecimal("25.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    assertEquals(1, created.getId());
  }

  @Test
  void createTransaction_assignsIncrementingIds() {
    TransactionService service = new TransactionService(new ArrayList<>());

    PlannedTransaction first =
            service.createTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);
    PlannedTransaction second =
            service.createTransaction(new BigDecimal("20.00"), LocalDate.now(), RecurrenceType.MONTHLY, 1, 1);

    assertEquals(1, first.getId());
    assertEquals(2, second.getId());
  }

  @Test
  void createTransaction_afterDeletion_doesNotReuseId() {
    // Guards against a naive "list.size() + 1" id strategy, which WOULD reuse ids after a delete.
    TransactionService service = new TransactionService(new ArrayList<>());
    PlannedTransaction first =
            service.createTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);
    service.createTransaction(new BigDecimal("20.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    service.deleteTransaction(first.getId());
    PlannedTransaction third =
            service.createTransaction(new BigDecimal("30.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    assertEquals(3, third.getId());
  }

  @Test
  void createTransaction_withInvalidData_throwsIllegalArgumentException() {
    TransactionService service = new TransactionService(new ArrayList<>());

    assertThrows(
            IllegalArgumentException.class,
            () -> service.createTransaction(null, LocalDate.now(), RecurrenceType.ONCE, 1, 1)
    );
  }

  // ---- listTransactions ----

  @Test
  void listTransactions_withMatchingAccountId_returnsOnlyThoseTransactions() {
    List<PlannedTransaction> seed = new ArrayList<>();
    TransactionService service = new TransactionService(seed);
    service.createTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);
    service.createTransaction(new BigDecimal("20.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 2);

    List<PlannedTransaction> result = service.listTransactions(1);

    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getAccountId());
  }

  @Test
  void listTransactions_withNoMatches_returnsEmptyList() {
    TransactionService service = new TransactionService(new ArrayList<>());
    service.createTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    List<PlannedTransaction> result = service.listTransactions(99);

    assertTrue(result.isEmpty());
  }

  // ---- deleteTransaction ----

  @Test
  void deleteTransaction_withExistingId_removesTransaction() {
    TransactionService service = new TransactionService(new ArrayList<>());
    PlannedTransaction created =
            service.createTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    service.deleteTransaction(created.getId());

    assertTrue(service.listTransactions(1).isEmpty());
  }

  @Test
  void deleteTransaction_withUnknownId_throwsIllegalArgumentException() {
    TransactionService service = new TransactionService(new ArrayList<>());

    assertThrows(IllegalArgumentException.class, () -> service.deleteTransaction(999));
  }

  // ---- filterByCategory (delegates to PlannedTransaction.filterByCategory) ----

  @Test
  void filterByCategory_returnsOnlyTransactionsInThatCategory() {
    TransactionService service = new TransactionService(new ArrayList<>());
    service.createTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);
    service.createTransaction(new BigDecimal("20.00"), LocalDate.now(), RecurrenceType.ONCE, 2, 1);

    List<PlannedTransaction> result = service.filterByCategory(1);

    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getCategoryId());
  }

  // ---- filterByCategoryAndRecurrence (delegates to PlannedTransaction.filterByCategoryAndRecurrence) ----

  @Test
  void filterByCategoryAndRecurrence_returnsOnlyTransactionsMatchingBoth() {
    TransactionService service = new TransactionService(new ArrayList<>());
    service.createTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.MONTHLY, 1, 1);
    service.createTransaction(new BigDecimal("20.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    List<PlannedTransaction> result = service.filterByCategoryAndRecurrence(1, RecurrenceType.MONTHLY);

    assertEquals(1, result.size());
    assertEquals(RecurrenceType.MONTHLY, result.get(0).getRecurrenceType());
  }

  // ---- sumByCategory (delegates to PlannedTransaction.sumByCategory) ----

  @Test
  void sumByCategory_sumsOnlyMatchingCategory() {
    TransactionService service = new TransactionService(new ArrayList<>());
    service.createTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);
    service.createTransaction(new BigDecimal("15.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);
    service.createTransaction(new BigDecimal("999.00"), LocalDate.now(), RecurrenceType.ONCE, 2, 1);

    BigDecimal result = service.sumByCategory(1);

    assertEquals(0, new BigDecimal("25.00").compareTo(result));
  }

  @Test
  void sumByCategory_withNoMatches_returnsZero() {
    TransactionService service = new TransactionService(new ArrayList<>());
    service.createTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    BigDecimal result = service.sumByCategory(99);

    assertEquals(0, BigDecimal.ZERO.compareTo(result));
  }
}