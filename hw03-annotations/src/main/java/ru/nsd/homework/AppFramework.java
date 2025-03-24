package ru.nsd.homework;

import ru.nsd.homework.examino.Examino;

public class AppFramework {
    public static void main(String[] args) {
        Examino.performTest("ru.nsd.homework.business.ClassForTestFirst");
        Examino.performTest("ru.nsd.homework.business.ClassForTestSecond");
    }
}
