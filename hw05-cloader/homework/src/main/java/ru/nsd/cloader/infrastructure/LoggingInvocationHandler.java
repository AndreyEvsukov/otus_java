package ru.nsd.cloader.infrastructure;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import ru.nsd.cloader.annotations.Log;

public class LoggingInvocationHandler implements InvocationHandler {
    private final Object target;
    private final Map<Method, Boolean> methodLoggingCache = new HashMap<>();

    public LoggingInvocationHandler(Object target) {
        this.target = target;
        initializeLoggingMethods();
    }

    /**
     * Инициализирует кэш методов, помеченных аннотацией @Log
     */
    private void initializeLoggingMethods() {
        Class<?> targetClass = target.getClass();

        // Собираем все публичные методы целевого класса
        for (Method method : targetClass.getMethods()) {
            // Проверяем наличие аннотации @Log
            if (method.isAnnotationPresent(Log.class)) {
                // Находим соответствующий метод интерфейса
                try {
                    Method interfaceMethod = findInterfaceMethod(method);
                    methodLoggingCache.put(interfaceMethod, true);
                } catch (NoSuchMethodException ignored) {
                    // Метод не принадлежит интерфейсу (для JDK proxy это невозможно)
                }
            }
        }
    }

    /**
     * Находит метод интерфейса по методу целевого класса
     */
    private Method findInterfaceMethod(Method targetMethod) throws NoSuchMethodException {
        for (Class<?> intf : target.getClass().getInterfaces()) {
            try {
                return intf.getMethod(targetMethod.getName(), targetMethod.getParameterTypes());
            } catch (NoSuchMethodException ignored) {
            }
        }
        throw new NoSuchMethodException();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Проверяем кэш вместо поиска через рефлексию
        if (methodLoggingCache.getOrDefault(method, false)) {
            logMethodCall(method.getName(), args);
        }
        return method.invoke(target, args);
    }

    private void logMethodCall(String methodName, Object[] args) {
        String paramsLog = "";
        if (args != null && args.length > 0) {
            String paramPart = args.length == 1 ? "param" : "params";
            paramsLog =
                    Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(", ", paramPart + ": ", ""));
        }
        System.out.printf("executed method: %s%s%n", methodName, paramsLog.isEmpty() ? "" : ", " + paramsLog);
    }
}
