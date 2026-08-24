package org.m450.model;

import java.math.BigDecimal;

public class Account {

    private int id;
    private String name;
    private BigDecimal balance;
    private int customerId;

    public Account(int id, String name, BigDecimal balance, int customerId) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.customerId = customerId;
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

    public BigDecimal getBalance() {
        return balance;
    }
}