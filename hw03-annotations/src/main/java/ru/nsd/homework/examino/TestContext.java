package ru.nsd.homework.examino;

import java.lang.reflect.Method;
import java.util.List;

public class TestContext {
    private int passed;
    private int failed;
    private List<Method> beforeMethods;
    private List<Method> afterMethods;
    private Class<?> testClass;

    public int getPassed() {
        return passed;
    }

    public int getFailed() {
        return failed;
    }

    public int getTotal() {
        return passed + failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public List<Method> getBeforeMethods() {
        return beforeMethods;
    }

    public void setBeforeMethods(List<Method> beforeMethods) {
        this.beforeMethods = beforeMethods;
    }

    public List<Method> getAfterMethods() {
        return afterMethods;
    }

    public void setAfterMethods(List<Method> afterMethods) {
        this.afterMethods = afterMethods;
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }
}
