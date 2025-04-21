package ru.nsd.bankomat.interfaces;

public interface Cell {
    void add(int count);

    void withdraw(int count);

    int getTotal();

    int getNominal();

    int getCount();
}
