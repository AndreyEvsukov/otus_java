package ru.nsd.bankomat;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsd.bankomat.factory.ATMFactory;
import ru.nsd.bankomat.interfaces.ATM;

public class ApplicationATM {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationATM.class);
    private static final String TOTAL_BALANCE_INFO = "Всего денег в банкомате: {}";

    public static void main(String[] args) {
        ATMFactory factory = ATMFactory.getBankomatFactory("regular");
        ATM atm = factory.getATM(List.of(100, 50, 1000, 500));

        logger.info(TOTAL_BALANCE_INFO, atm.getTotalBalance());
        atm.deposit(100, 10);
        logger.info(TOTAL_BALANCE_INFO, atm.getTotalBalance());
        atm.deposit(50, 10);
        logger.info(TOTAL_BALANCE_INFO, atm.getTotalBalance());
        atm.deposit(1000, 10);
        logger.info(TOTAL_BALANCE_INFO, atm.getTotalBalance());
        atm.deposit(500, 10);
        logger.info(TOTAL_BALANCE_INFO, atm.getTotalBalance());
        atm.withdraw(5000);
        logger.info(TOTAL_BALANCE_INFO, atm.getTotalBalance());
    }
}
