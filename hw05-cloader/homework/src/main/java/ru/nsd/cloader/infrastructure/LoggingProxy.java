package ru.nsd.cloader.infrastructure;

import java.lang.reflect.Proxy;

public class LoggingProxy {
    @SuppressWarnings("unchecked")
    public static <T> T create(T target, Class<T> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("interfaceClass must be an interface");
        }
        if (!interfaceClass.isInstance(target)) {
            throw new IllegalArgumentException("target must implement " + interfaceClass.getName());
        }

        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class<?>[] {interfaceClass},
                new LoggingInvocationHandler(target));
    }
}
