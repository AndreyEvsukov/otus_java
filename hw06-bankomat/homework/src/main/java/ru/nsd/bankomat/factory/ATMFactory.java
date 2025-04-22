package ru.nsd.bankomat.factory;

import java.util.Collection;
import ru.nsd.bankomat.interfaces.ATM;

public abstract class ATMFactory {
    public abstract ATM getATM(Collection<Integer> nominals);

    public static ATMFactory getBankomatFactory(String param) {
        if ("regular".equals(param)) {
            return new RegularATMFactory();
        }
        throw new IllegalArgumentException("unknown param:" + param);
    }
}
