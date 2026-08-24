import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


public class PlannedTransactionTest {

    // Creates PlannedTransaction object and checks if 0 is equal to positive.
    @Test
    void isPositive_withZeroAmount_returnsTrue() {
        PlannedTransaction transaction = new PlannedTransaction(
                1,
                BigDecimal.ZERO,
                LocalDate.of(2026, 8,21),
                RecurrenceType.ONCE,
                1,
                1
        );
        assertTrue(transaction.isPositive());
    }
}
