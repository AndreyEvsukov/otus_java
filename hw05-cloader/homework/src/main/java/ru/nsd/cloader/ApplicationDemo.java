package ru.nsd.cloader;

import ru.nsd.cloader.classes.TestLogging;
import ru.nsd.cloader.infrastructure.LoggingProxy;
import ru.nsd.cloader.interfaces.TestLoggingInterface;

public class ApplicationDemo {

    public static void main(String[] args) {
        TestLoggingInterface testLogging = LoggingProxy.create(new TestLogging(), TestLoggingInterface.class);
        testLogging.calculation(1);
        testLogging.calculation(1, 2);
        testLogging.calculation(1, 2, "3");
    }
}
