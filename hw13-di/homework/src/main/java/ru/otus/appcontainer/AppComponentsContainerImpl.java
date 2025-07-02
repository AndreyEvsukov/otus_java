package ru.otus.appcontainer;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?>... initialConfigClass) {
        processConfigClasses(initialConfigClass);
    }

    public AppComponentsContainerImpl(String scanPath) {
        processConfigClasses(getAppComponentsContainerConfigClasses(scanPath));
    }

    private void processConfigClasses(Class<?>... initialConfigClasses) {
        final var configs = getConfigsByOrder(initialConfigClasses);
        for (var config : configs) {
            processConfig(config);
        }
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        final var configInstance = getConfigInstance(configClass);
        final var configMethods = getMethodsByOrder(configClass);
        for (var method : configMethods) {
            var componentName = getComponentName(method);

            if (appComponentsByName.containsKey(componentName)) {
                throw new RuntimeException(String.format("Component %s already exists", componentName));
            }

            final var configuredComponent = configureComponent(configInstance, method);

            appComponentsByName.put(componentName, configuredComponent);
            appComponents.add(configuredComponent);
        }
    }

    private Object configureComponent(Object configObject, Method method) {
        final var arguments = getMethodArguments(method);
        try {
            return method.invoke(configObject, arguments);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] getMethodArguments(Method method) {
        return Arrays.stream(method.getParameterTypes())
                .map(this::getAppComponent)
                .toArray();
    }

    private String getComponentName(Method method) {
        return method.getAnnotation(AppComponent.class).name();
    }

    private <T> T getConfigInstance(Class<T> configClass) {
        if (Modifier.isAbstract(configClass.getModifiers())) {
            throw new IllegalArgumentException("Config class must be non-abstract");
        }
        try {
            return configClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException
                | InvocationTargetException
                | IllegalAccessException
                | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        final var component = appComponents.stream()
                .filter(c -> componentClass.isAssignableFrom(c.getClass()))
                .findFirst();
        if (component.isEmpty()) {
            throw new RuntimeException(String.format("Component %s not found", componentClass.getName()));
        }
        return componentClass.cast(component.get());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(String componentName) {
        C component = (C) appComponentsByName.get(componentName);
        if (component == null) {
            throw new RuntimeException(String.format("Component %s not found", componentName));
        }
        return component;
    }

    private List<Method> getMethodsByOrder(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(
                        m -> m.getAnnotation(AppComponent.class).order()))
                .toList();
    }

    private List<Class<?>> getConfigsByOrder(Class<?>... initialConfigClasses) {
        return Arrays.stream(initialConfigClasses)
                .filter(c -> c.isAnnotationPresent(AppComponentsContainerConfig.class))
                .sorted(Comparator.comparingInt(
                        c -> c.getAnnotation(AppComponentsContainerConfig.class).order()))
                .toList();
    }

    private Class<?>[] getAppComponentsContainerConfigClasses(String scanPath) {
        return new Reflections(scanPath, TypesAnnotated)
                .getTypesAnnotatedWith(AppComponentsContainerConfig.class)
                .toArray(new Class[0]);
    }
}
