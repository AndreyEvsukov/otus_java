package ru.nsd.homework.business;

import ru.nsd.homework.annotations.After;
import ru.nsd.homework.annotations.Before;
import ru.nsd.homework.annotations.Test;

@SuppressWarnings({"java:S106", "java:S1854", "java:S3518"})
public class ClassForTestFirst {

    @Before
    public void setup() {
        System.out.println("setup test");
    }

    @Before
    public void init() {
        System.out.println("initialize test");
    }

    @Test
    public void firstTest() {
        System.out.println("perform first test");
    }

    @Test
    public void secondTest() {
        System.out.println("perform second test");
    }

    @Test
    private void thirdTest() {
        int i = 150;
        int j = 150 - i;

        System.out.println(i / j);
        System.out.println("perform third test");
    }

    @Test
    private void fourthTest() {
        System.out.println("perform fourth test");
    }

    @After
    public void clean() {
        System.out.println("clean test data");
    }

    @After
    public void shutdown() {
        System.out.println("shutdown test");
    }
}
