package ru.nsd.bankomat.interfaces;

import java.util.Map;

public interface ATM {
    void deposit(int nominal, int count);

    Map<Integer, Integer> withdraw(int amount);

    int getTotalBalance();
}
