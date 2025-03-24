package ru.nsd.homework.examino;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import ru.nsd.homework.annotations.After;
import ru.nsd.homework.annotations.Before;
import ru.nsd.homework.annotations.Test;

@SuppressWarnings({"java:S106", "java:S3011"})
public class Examino {

    private Examino() {}

    public static void performTest(String className) {
        TestContext context = new TestContext();
        Class<?> testClass = null;
        try {
            testClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + className);
        }

        if (testClass != null) {
            context.setTestClass(testClass);
            List<Method> beforeMethods = findMethodsByAnnotation(testClass, Before.class);
            List<Method> testMethods = findMethodsByAnnotation(testClass, Test.class);
            List<Method> afterMethods = findMethodsByAnnotation(testClass, After.class);
            context.setBeforeMethods(beforeMethods);
            context.setAfterMethods(afterMethods);

            for (Method method : testMethods) {
                runTest(context, method);
            }
            System.out.println("-------------------------------");
            System.out.println(ConsoleColors.YELLOW + "Test results for class: " + className + ConsoleColors.RESET);
            System.out.println(ConsoleColors.GREEN + "     Passed: " + context.getPassed() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.RED + "     Failed: " + context.getFailed() + ConsoleColors.RESET);
            System.out.println(ConsoleColors.BLUE + "     Total: " + context.getTotal() + ConsoleColors.RESET);
            System.out.println("-------------------------------");
        }
    }

    private static void runTest(TestContext context, Method testMethod) {
        boolean testFailed = false;
        Object instance = null;

        try {
            instance = context.getTestClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            testFailed = true;
        }

        if (instance != null) {
            testFailed = invokeMethods(instance, context.getBeforeMethods(), testFailed);

            if (!testFailed) {
                try {
                    testMethod.invoke(instance);
                } catch (IllegalAccessException e) {
                    testFailed = true;
                    System.out.println(
                            ConsoleColors.RED + "Test failed: " + testMethod.getName() + ConsoleColors.RESET);
                    System.out.println(ConsoleColors.RED + "Test error: " + e.getMessage() + ConsoleColors.RESET);
                } catch (InvocationTargetException e) {
                    testFailed = true;
                    System.out.println(
                            ConsoleColors.RED + "Test failed: " + testMethod.getName() + ConsoleColors.RESET);
                    System.out.println(
                            ConsoleColors.RED + "Test error: " + e.getTargetException() + ConsoleColors.RESET);
                }
                System.out.println(ConsoleColors.GREEN + "Test invoked: " + testMethod.getName() + ConsoleColors.RESET);
            }

            testFailed = invokeMethods(instance, context.getAfterMethods(), testFailed);
        } else {
            testFailed = true;
        }

        if (testFailed) {
            context.setFailed(context.getFailed() + 1);
        } else {
            context.setPassed(context.getPassed() + 1);
        }
    }

    private static boolean invokeMethods(Object instance, List<Method> methods, boolean currentFailed) {
        boolean failed = currentFailed;
        try {
            for (Method method : methods) {
                method.invoke(instance);
            }
        } catch (Exception e) {
            failed = true;
        }
        return failed;
    }

    private static List<Method> findMethodsByAnnotation(Class<?> testClass, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                method.setAccessible(true);
                methods.add(method);
            }
        }
        return methods;
    }
}
