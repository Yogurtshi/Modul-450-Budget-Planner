package org.m450.budgetplanner.model;

import org.m450.budgetplanner.model.enums.RecurrenceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlannedTransaction {

    private final int id;
    private BigDecimal amount;
    private LocalDate date;
    private RecurrenceType recurrenceType;
    private int categoryId;
    private final int accountId;

    public PlannedTransaction(
            int id,
            BigDecimal amount,
            LocalDate date,
            RecurrenceType recurrenceType,
            int categoryId,
            int accountId
    ) {
        if (!isValidTransaction(amount, date, categoryId)) {
            throw new IllegalArgumentException("Invalid transaction data");
        }
        if (recurrenceType == null) {
            throw new IllegalArgumentException("RecurrenceType must not be null");
        }
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.recurrenceType = recurrenceType;
        this.categoryId = categoryId;
        this.accountId = accountId;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void editAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        this.amount = amount;
    }

    public void editDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        this.date = date;
    }

    public void editOnRepeat(RecurrenceType recurrenceType) {
        if (recurrenceType == null) {
            throw new IllegalArgumentException("RecurrenceType must not be null");
        }
        this.recurrenceType = recurrenceType;
    }

    public void editFkCategory(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("categoryId must be positive");
        }
        this.categoryId = categoryId;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static boolean isValidTransaction(BigDecimal amount, LocalDate date, int categoryId) {
        if (amount == null) {
            return false;
        }
        if (date == null) {
            return false;
        }
        return categoryId > 0;
    }

    public static List<PlannedTransaction> filterByCategory(List<PlannedTransaction> transactions, int categoryId) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transaction list must not be null");
        }
        List<PlannedTransaction> result = new ArrayList<>();
        for (PlannedTransaction t : transactions) {
            if (t.categoryId == categoryId) {
                result.add(t);
            }
        }
        return result;
    }

    public static BigDecimal sumByCategory(List<PlannedTransaction> transactions, int categoryId) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transaction list must not be null");
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (PlannedTransaction t : transactions) {
            if (t.categoryId == categoryId) {
                sum = sum.add(t.amount);
            }
        }
        return sum;
    }

    public static List<PlannedTransaction> filterByCategoryAndRecurrence(
            List<PlannedTransaction> transactions,
            int categoryId,
            RecurrenceType recurrenceType
    ) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transaction list must not be null");
        }
        if (recurrenceType == null) {
            throw new IllegalArgumentException("RecurrenceType must not be null");
        }
        List<PlannedTransaction> result = new ArrayList<>();
        for (PlannedTransaction t : transactions) {
            if (t.categoryId == categoryId && t.recurrenceType == recurrenceType) {
                result.add(t);
            }
        }
        return result;
    }
}