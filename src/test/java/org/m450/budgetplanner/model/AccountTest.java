import org.junit.jupiter.api.Test;
import org.m450.budgetplanner.model.Account;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    // Checks if the range is positive of negative, these are both accepted
    @Test
    void isValidAmount_withPositiveAmount_returnsTrue() {
        Account account = new Account(
                1,
                "Main account",
                new BigDecimal("100.00"),
                1
        );
        boolean result = account.isValidAmount(new BigDecimal("0.01"));
        assertTrue(result);
    }

    @Test
    void isValidAmount_withNegativeAmount_returnsTrue() {
        Account account = new Account(
                1,
                "Main account",
                new BigDecimal("100.00"),
                1
        );
        boolean result = account.isValidAmount(new BigDecimal("-0.01"));
        assertTrue(result);
    }

    // Edgecases 0 or null

    @Test
    void isValidAmount_withZeroAmount_returnsFalse() {
        Account account = new Account(
                1,
                "Main account",
                new BigDecimal("100.00"),
                1
        );
        boolean result = account.isValidAmount(new BigDecimal("0"));
        assertFalse(result);
    }

    @Test
    void isValidAmount_withNullAmount_returnsFalse() {
        Account account = new Account(
                1,
                "Main account",
                new BigDecimal("100.00"),
                1
        );
        boolean result = account.isValidAmount(null);
        assertFalse(result);
    }

// Deposit Test
    @Test
    void deposit_withPositiveAmount_increasesBalance() {
        Account account = new Account(
                1,
                "Main account",
                new BigDecimal("100.00"),
                1
        );
        account.deposit(new BigDecimal("50.00"));
        assertEquals(new BigDecimal("150.00"), account.getBalance());
    }

    @Test
    void deposit_withNegativeAmount_throwsIllegalArgumentException() {
        Account account = new Account(
                1,
                "Main account",
                new BigDecimal("100.00"),
                1
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(new BigDecimal("-50.00"))
        );
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }

    // Withdraw Test
    @Test
    void withdraw_withNegativeAmount_decreasesBalance() {
        Account account = new Account(
                1,
                "Main account",
                new BigDecimal("100.00"),
                1
        );
        account.withdraw(new BigDecimal("-50.00"));
        assertEquals(new BigDecimal("50.00"), account.getBalance());
    }

    @Test
    void withdraw_withPositiveAmount_throwsIllegalArgumentException() {
        Account account = new Account(
                1,
                "Main account",
                new BigDecimal("100.00"),
                1
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(new BigDecimal("50.00"))
        );
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }
}
