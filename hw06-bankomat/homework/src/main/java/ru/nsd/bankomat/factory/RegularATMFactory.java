package ru.nsd.bankomat.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.nsd.bankomat.entities.RegularATM;
import ru.nsd.bankomat.entities.RegularCell;
import ru.nsd.bankomat.interfaces.ATM;
import ru.nsd.bankomat.interfaces.Cell;

public class RegularATMFactory extends ATMFactory {
    @Override
    public ATM getATM(Collection<Integer> nominals) {
        List<Cell> cells = new ArrayList<>();
        for (int nominal : nominals) {
            cells.add(new RegularCell(nominal));
        }
        return new RegularATM(cells);
    }
}
