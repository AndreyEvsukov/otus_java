package ru.nsd.bankomat.entities;

import ru.nsd.bankomat.exceptions.InsufficientFundsException;
import ru.nsd.bankomat.interfaces.Cell;

public class RegularCell implements Cell {
    private final int nominal; // Номинал банкнот
    private int count; // Количество банкнот

    public RegularCell(int nominal) {
        this.nominal = nominal;
    }

    @Override
    public void add(int count) {
        this.count += count;
    }

    @Override
    public void withdraw(int count) {
        if (this.count < count) {
            throw new InsufficientFundsException("Недостаточно банкнот в ячейке " + nominal);
        }
        this.count -= count;
    }

    @Override
    public int getTotal() {
        return nominal * count;
    }

    @Override
    public int getNominal() {
        return nominal;
    }

    @Override
    public int getCount() {
        return count;
    }
}
