package ru.nsd.homework.business;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import ru.nsd.homework.annotations.After;
import ru.nsd.homework.annotations.Before;
import ru.nsd.homework.annotations.Test;

@SuppressWarnings({"java:S106", "java:S2095"})
public class ClassForTestSecond {
    @Before
    public void setup() {
        System.out.println("setup test class");
    }

    @Before
    public void init() {
        System.out.println("init test class");
    }

    @Test
    public void firstTest() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("fileforexample.txt"));
        System.out.println(in.read());
        System.out.println("run first test");
    }

    @Test
    public void secondTest() {
        System.out.println("run second test");
    }

    @Test
    public void thirdTest() {
        System.out.println("run third test");
    }

    @Test
    public void fourthTest() {
        System.out.println("run fourth test");
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
