package org.m450.model;
import org.m450.model.enums.RecurrenceType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PlannedTransaction {

    private int id;
    private BigDecimal amount;
    private LocalDate date;
    private RecurrenceType recurrenceType;
    private int categoryId;
    private int accountId;

    public PlannedTransaction(
            int id,
            BigDecimal amount,
            LocalDate date,
            RecurrenceType recurrenceType,
            int categoryId,
            int accountId
    ) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.recurrenceType = recurrenceType;
        this.categoryId = categoryId;
        this.accountId = accountId;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) >= 0;
    }
}