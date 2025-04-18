package ru.nsd.cloader.infrastructure;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import ru.nsd.cloader.annotations.Log;

public class LoggingInvocationHandler implements InvocationHandler {
    private final Object target;

    public LoggingInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        if (targetMethod.isAnnotationPresent(Log.class)) {
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
