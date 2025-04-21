package ru.nsd.bankomat.entities;

import java.util.*;
import ru.nsd.bankomat.exceptions.InsufficientFundsException;
import ru.nsd.bankomat.exceptions.InvalidNominalException;
import ru.nsd.bankomat.interfaces.ATM;
import ru.nsd.bankomat.interfaces.Cell;

public class RegularATM implements ATM {
    private final Map<Integer, Cell> cells = new TreeMap<>(Comparator.reverseOrder());

    public RegularATM(Collection<Cell> atmCells) {
        for (Cell cell : atmCells) {
            cells.put(cell.getNominal(), cell);
        }
    }

    @Override
    public void deposit(int nominal, int count) {
        Cell cell = cells.get(nominal);
        if (cell == null) {
            throw new InvalidNominalException(nominal);
        }
        cell.add(count);
    }

    @Override
    public Map<Integer, Integer> withdraw(int amount) {
        Map<Integer, Integer> result = new HashMap<>();
        int remaining = amount;

        for (Cell cell : cells.values()) {
            int nominal = cell.getNominal();
            int maxPossible = Math.min(remaining / nominal, cell.getCount());
            if (maxPossible > 0) {
                result.put(nominal, maxPossible);
                remaining -= maxPossible * nominal;
                cell.withdraw(maxPossible);
            }
            if (remaining == 0) break;
        }

        if (remaining != 0) {
            throw new InsufficientFundsException("Невозможно выдать сумму " + amount);
        }

        return result;
    }

    @Override
    public int getTotalBalance() {
        return cells.values().stream().mapToInt(Cell::getTotal).sum();
    }
}
