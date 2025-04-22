package ru.nsd.bankomat;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.nsd.bankomat.exceptions.InsufficientFundsException;
import ru.nsd.bankomat.exceptions.InvalidNominalException;
import ru.nsd.bankomat.factory.ATMFactory;
import ru.nsd.bankomat.interfaces.ATM;

class ATMTest {
    private ATM atm;

    @BeforeEach
    void setUp() {
        ATMFactory factory = ATMFactory.getBankomatFactory("regular");
        // создаём банкомат, который способен принимать 100, 500, 50 и 1000 банкноты
        atm = factory.getATM(List.of(100, 500, 50, 1000));
    }

    @Test
    @DisplayName("Проверка на возможность внесения денег и получение верного баланса")
    void testDepositValid() {
        atm.deposit(100, 3);
        assertEquals(300, atm.getTotalBalance());
    }

    @Test
    @DisplayName("Проверка на невозможность внесения банкноты с неподдерживаемым номиналом")
    void testDepositInvalidNominal() {
        assertThrows(InvalidNominalException.class, () -> atm.deposit(25, 2));
    }

    @Test
    @DisplayName("Проверка на корректность снятия денег из банкомата")
    void testWithdrawSuccess() {
        atm.deposit(100, 10);
        atm.deposit(50, 10);
        atm.deposit(1000, 10);

        Map<Integer, Integer> expected = Map.of(100, 2, 50, 1);
        assertEquals(expected, atm.withdraw(250));
        assertEquals(1000 * 10 + 50 * 10 + 100 * 10 - 250, atm.getTotalBalance());
    }

    @Test
    @DisplayName("Проверка на невозможность снятия денег из банкомата, если их недостаточно")
    void testWithdrawFailure() {
        atm.deposit(100, 1);
        assertThrows(InsufficientFundsException.class, () -> atm.withdraw(150));
    }

    @Test
    @DisplayName("Проверка на корректность получения неверного банкомата")
    void testGettingWrongATM() {
        assertThrows(IllegalArgumentException.class, () -> ATMFactory.getBankomatFactory("wrong"));
    }
}
