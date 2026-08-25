package org.m450.budgetplanner.model;

import java.math.BigDecimal;
import java.util.List;

public class Account {

    private final int id;
    private String name;
    private BigDecimal balance;
    private final int customerId;

    public Account(int id, String name, BigDecimal balance, int customerId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name must not be null or blank");
        }
        if (balance == null) {
            throw new IllegalArgumentException("Balance must not be null");
        }
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void editAccountName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name must not be null or blank");
        }
        this.name = name;
    }

    public void editBalance(BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Balance must not be null");
        }
        this.balance = balance;
    }

    public boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) != 0;
    }

    public void deposit(BigDecimal amount) {
        if (!isValidAmount(amount) || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (!isValidAmount(amount) || amount.compareTo(BigDecimal.ZERO) >= 0) {
            throw new IllegalArgumentException("Withdraw amount must be negative");
        }
        balance = balance.add(amount);
    }

    public void recalculateBalance(List<PlannedTransaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transaction list must not be null");
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (PlannedTransaction t : transactions) {
            if (t.getAccountId() == this.id) {
                sum = sum.add(t.getAmount());
            }
        }
        this.balance = sum;
    }
}