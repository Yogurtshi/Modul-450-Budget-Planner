package org.m450.budgetplanner.model;

import org.m450.budgetplanner.model.PlannedTransaction;
import org.m450.budgetplanner.model.enums.RecurrenceType;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class PlannedTransactionTest {

    private static PlannedTransaction tx(int id, int categoryId, RecurrenceType recurrenceType) {
        return new PlannedTransaction(
                id, new BigDecimal("10.00"), LocalDate.of(2026, 1, 1), recurrenceType, categoryId, 1);
    }

    // Creates PlannedTransaction object and checks if 0 is equal to positive.
    @Test
    void isPositive_withZeroAmount_returnsTrue() {
        PlannedTransaction transaction = new PlannedTransaction(
                1,
                BigDecimal.ZERO,
                LocalDate.of(2026, 8, 21),
                RecurrenceType.ONCE,
                1,
                1
        );
        assertTrue(transaction.isPositive());
    }
    @Test
    void isPositive_withNegativeAmount_returnsFalse() {
        PlannedTransaction transaction = new PlannedTransaction(
                1, new BigDecimal("-5.00"), LocalDate.of(2026, 8, 21), RecurrenceType.ONCE, 1, 1);

        assertFalse(transaction.isPositive());
    }

    // ===================================================================
    // isValidTransaction --> Fehlerbehandlung & Edge Cases
    // ===================================================================

    @Test
    void isValidTransaction_withNullAmount_returnsFalse() {
        assertFalse(PlannedTransaction.isValidTransaction(null, LocalDate.now(), 1));
    }

    @Test
    void isValidTransaction_withNullDate_returnsFalse() {
        assertFalse(PlannedTransaction.isValidTransaction(new BigDecimal("10.00"), null, 1));
    }

    @Test
    void isValidTransaction_withZeroCategoryId_returnsFalse() {
        assertFalse(PlannedTransaction.isValidTransaction(new BigDecimal("10.00"), LocalDate.now(), 0));
    }

    @Test
    void isValidTransaction_withNegativeCategoryId_returnsFalse() {
        assertFalse(PlannedTransaction.isValidTransaction(new BigDecimal("10.00"), LocalDate.now(), -1));
    }

    @Test
    void isValidTransaction_withValidData_returnsTrue() {
        assertTrue(PlannedTransaction.isValidTransaction(new BigDecimal("10.00"), LocalDate.now(), 1));
    }

    @Test
    void constructor_withInvalidData_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlannedTransaction(1, null, LocalDate.now(), RecurrenceType.ONCE, 1, 1)
        );
    }

    @Test
    void constructor_withNullRecurrenceType_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlannedTransaction(1, new BigDecimal("10.00"), LocalDate.now(), null, 1, 1)
        );
    }

    // ===================================================================
    // filterByCategory --> Sammlungen & Listen
    // ===================================================================

    @Test
    void filterByCategory_withEmptyList_returnsEmptyList() {
        assertTrue(PlannedTransaction.filterByCategory(new ArrayList<>(), 1).isEmpty());
    }

    @Test
    void filterByCategory_withNoMatches_returnsEmptyList() {
        List<PlannedTransaction> transactions = List.of(
                tx(1, 2, RecurrenceType.ONCE), tx(2, 3, RecurrenceType.MONTHLY));

        assertTrue(PlannedTransaction.filterByCategory(transactions, 1).isEmpty());
    }

    @Test
    void filterByCategory_withMatches_returnsOnlyMatchingTransactions() {
        List<PlannedTransaction> transactions = List.of(
                tx(1, 1, RecurrenceType.ONCE), tx(2, 2, RecurrenceType.ONCE), tx(3, 1, RecurrenceType.MONTHLY));

        List<PlannedTransaction> result = PlannedTransaction.filterByCategory(transactions, 1);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getCategoryId() == 1));
    }

    @Test
    void filterByCategory_withNullList_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> PlannedTransaction.filterByCategory(null, 1));
    }

    // ---- sumByCategory --> Sammlungen & Listen ----

    @Test
    void sumByCategory_withEmptyList_returnsZero() {
        assertEquals(0, BigDecimal.ZERO.compareTo(PlannedTransaction.sumByCategory(new ArrayList<>(), 1)));
    }

    @Test
    void sumByCategory_withNoMatches_returnsZero() {
        List<PlannedTransaction> transactions = List.of(tx(1, 2, RecurrenceType.ONCE));

        assertEquals(0, BigDecimal.ZERO.compareTo(PlannedTransaction.sumByCategory(transactions, 1)));
    }

    @Test
    void sumByCategory_withMultipleMatches_returnsSum() {
        List<PlannedTransaction> transactions = List.of(
                tx(1, 1, RecurrenceType.ONCE),    // 10.00
                tx(2, 1, RecurrenceType.MONTHLY), // 10.00
                tx(3, 2, RecurrenceType.ONCE)     // not category 1
        );

        BigDecimal result = PlannedTransaction.sumByCategory(transactions, 1);

        assertEquals(0, new BigDecimal("20.00").compareTo(result));
    }

    @Test
    void sumByCategory_withNullList_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> PlannedTransaction.sumByCategory(null, 1));
    }

    // ===================================================================
    // filterByCategoryAndRecurrence --> Kombinatorische Logik (AND)
    //
    // Truth table, all 4 cases covered:
    //   category matches | recurrence matches | expected in result?
    //         yes         |         yes        |          yes
    //         yes         |         no         |          no
    //         no          |         yes        |          no
    //         no          |         no         |          no
    // ===================================================================

    @Test
    void filterByCategoryAndRecurrence_withBothMatching_isIncluded() {
        List<PlannedTransaction> transactions = List.of(tx(1, 1, RecurrenceType.MONTHLY));

        List<PlannedTransaction> result =
                PlannedTransaction.filterByCategoryAndRecurrence(transactions, 1, RecurrenceType.MONTHLY);

        assertEquals(1, result.size());
    }

    @Test
    void filterByCategoryAndRecurrence_withOnlyCategoryMatching_isExcluded() {
        List<PlannedTransaction> transactions = List.of(tx(1, 1, RecurrenceType.ONCE));

        List<PlannedTransaction> result =
                PlannedTransaction.filterByCategoryAndRecurrence(transactions, 1, RecurrenceType.MONTHLY);

        assertTrue(result.isEmpty());
    }

    @Test
    void filterByCategoryAndRecurrence_withOnlyRecurrenceMatching_isExcluded() {
        List<PlannedTransaction> transactions = List.of(tx(1, 2, RecurrenceType.MONTHLY));

        List<PlannedTransaction> result =
                PlannedTransaction.filterByCategoryAndRecurrence(transactions, 1, RecurrenceType.MONTHLY);

        assertTrue(result.isEmpty());
    }

    @Test
    void filterByCategoryAndRecurrence_withNeitherMatching_isExcluded() {
        List<PlannedTransaction> transactions = List.of(tx(1, 2, RecurrenceType.ONCE));

        List<PlannedTransaction> result =
                PlannedTransaction.filterByCategoryAndRecurrence(transactions, 1, RecurrenceType.MONTHLY);

        assertTrue(result.isEmpty());
    }

    @Test
    void filterByCategoryAndRecurrence_withEmptyList_returnsEmptyList() {
        List<PlannedTransaction> result =
                PlannedTransaction.filterByCategoryAndRecurrence(new ArrayList<>(), 1, RecurrenceType.ONCE);

        assertTrue(result.isEmpty());
    }

    @Test
    void filterByCategoryAndRecurrence_withNullList_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PlannedTransaction.filterByCategoryAndRecurrence(null, 1, RecurrenceType.ONCE)
        );
    }

    @Test
    void filterByCategoryAndRecurrence_withNullRecurrenceType_throwsIllegalArgumentException() {
        List<PlannedTransaction> transactions = List.of(tx(1, 1, RecurrenceType.ONCE));

        assertThrows(
                IllegalArgumentException.class,
                () -> PlannedTransaction.filterByCategoryAndRecurrence(transactions, 1, null)
        );
    }

    // ---- editAmount / editDate / editOnRepeat / editFkCategory ----

    @Test
    void editAmount_withNull_throwsIllegalArgumentException() {
        PlannedTransaction transaction = tx(1, 1, RecurrenceType.ONCE);

        assertThrows(IllegalArgumentException.class, () -> transaction.editAmount(null));
    }

    @Test
    void editAmount_withValidAmount_updatesAmount() {
        PlannedTransaction transaction = tx(1, 1, RecurrenceType.ONCE);

        transaction.editAmount(new BigDecimal("99.99"));

        assertEquals(0, new BigDecimal("99.99").compareTo(transaction.getAmount()));
    }

    @Test
    void editDate_withNull_throwsIllegalArgumentException() {
        PlannedTransaction transaction = tx(1, 1, RecurrenceType.ONCE);

        assertThrows(IllegalArgumentException.class, () -> transaction.editDate(null));
    }

    @Test
    void editOnRepeat_withNull_throwsIllegalArgumentException() {
        PlannedTransaction transaction = tx(1, 1, RecurrenceType.ONCE);

        assertThrows(IllegalArgumentException.class, () -> transaction.editOnRepeat(null));
    }

    @Test
    void editFkCategory_withZero_throwsIllegalArgumentException() {
        PlannedTransaction transaction = tx(1, 1, RecurrenceType.ONCE);

        assertThrows(IllegalArgumentException.class, () -> transaction.editFkCategory(0));
    }

    @Test
    void editFkCategory_withValidId_updatesCategory() {
        PlannedTransaction transaction = tx(1, 1, RecurrenceType.ONCE);

        transaction.editFkCategory(5);

        assertEquals(5, transaction.getCategoryId());
    }
}
