package org.m450.budgetplanner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.m450.budgetplanner.model.PlannedTransaction;
import org.m450.budgetplanner.model.enums.RecurrenceType;
import org.m450.budgetplanner.storage.AppData;
import org.m450.budgetplanner.storage.JsonStorageService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionWorkflowServiceTest {

  @Mock
  private TransactionService transactionService;

  @Mock
  private JsonStorageService storageService;

  private AppData data;
  private TransactionWorkflowService workflow;

  @BeforeEach
  void setUp() {
    data = new AppData();
    workflow = new TransactionWorkflowService(transactionService, storageService, data);
  }

  // ---- Behavior check 1: call order ----
  // The whole point of mocking here: a real (non-mocked) TransactionService + JsonStorageService
  // could never prove *order* of calls, only end results. Mocks let us assert the sequence itself.

  @Test
  void recordTransaction_callsCreateTransactionBeforeSave() {
    PlannedTransaction fake = mock(PlannedTransaction.class);
    when(transactionService.createTransaction(any(), any(), any(), anyInt(), anyInt())).thenReturn(fake);

    workflow.recordTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    InOrder inOrder = inOrder(transactionService, storageService);
    inOrder.verify(transactionService).createTransaction(any(), any(), any(), anyInt(), anyInt());
    inOrder.verify(storageService).save(data);
  }

  @Test
  void recordTransaction_callsSaveExactlyOnce() {
    PlannedTransaction fake = mock(PlannedTransaction.class);
    when(transactionService.createTransaction(any(), any(), any(), anyInt(), anyInt())).thenReturn(fake);

    workflow.recordTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    verify(storageService, times(1)).save(data);
  }

  @Test
  void recordTransaction_returnsTransactionCreatedByService() {
    PlannedTransaction fake = mock(PlannedTransaction.class);
    when(transactionService.createTransaction(any(), any(), any(), anyInt(), anyInt())).thenReturn(fake);

    PlannedTransaction result =
            workflow.recordTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1);

    assertSame(fake, result);
  }

  // ---- Behavior check 2: exception simulation at the storage boundary ----
  // Simulates a real-world failure (disk full, permission denied, corrupt file) without
  // needing an actual broken filesystem — that's the whole value of mocking an interface.

  @Test
  void recordTransaction_whenStorageFails_propagatesException() {
    PlannedTransaction fake = mock(PlannedTransaction.class);
    when(transactionService.createTransaction(any(), any(), any(), anyInt(), anyInt())).thenReturn(fake);
    doThrow(new RuntimeException("Disk write failed")).when(storageService).save(any());

    assertThrows(
            RuntimeException.class,
            () -> workflow.recordTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1)
    );
  }

  @Test
  void recordTransaction_whenTransactionServiceFails_neverCallsSave() {
    // Proves the workflow does NOT persist half-finished/invalid state.
    // Only a mock lets us prove a call did NOT happen — a real JsonStorageService
    // writing to a real file could not express this assertion at all.
    when(transactionService.createTransaction(any(), any(), any(), anyInt(), anyInt()))
            .thenThrow(new IllegalArgumentException("Invalid transaction data"));

    assertThrows(
            IllegalArgumentException.class,
            () -> workflow.recordTransaction(new BigDecimal("10.00"), LocalDate.now(), RecurrenceType.ONCE, 1, 1)
    );

    verify(storageService, never()).save(any());
  }
}