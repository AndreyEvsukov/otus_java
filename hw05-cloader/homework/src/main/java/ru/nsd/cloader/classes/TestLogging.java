package ru.nsd.cloader.classes;

import ru.nsd.cloader.annotations.Log;
import ru.nsd.cloader.interfaces.TestLoggingInterface;

public class TestLogging implements TestLoggingInterface {

    @Override
    @Log
    public void calculation(int param1) {
        System.out.println("param1 = " + param1);
    }

    @Override
    public void calculation(int param1, int param2) {
        System.out.println("param1 = " + param1 + " param2 = " + param2);
    }

    @Log
    @Override
    public void calculation(int param1, int param2, String param3) {
        System.out.println("param1 = " + param1 + " param2 = " + param2 + " param3 = " + param3);
    }
}
