import org.junit.jupiter.api.Test;
import org.m450.model.Account;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountTest {

    @Test
    void isValidAmount_withAmountAboveZero_returnsTrue() {
        Account account = new Account(
                1,
                "Main account",
                new BigDecimal("100.00"),
                1
        );

        boolean result = account.isValidAmount(new BigDecimal("0.01"));

        assertTrue(result);
    }
}
