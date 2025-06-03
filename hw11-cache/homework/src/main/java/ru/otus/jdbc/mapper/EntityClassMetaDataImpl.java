package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import ru.otus.annotations.Id;
import ru.otus.jdbc.exceptions.WrongClassConfigException;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> entityClass;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.constructor = findNoArgConstructor();
        this.allFields = Arrays.asList(entityClass.getDeclaredFields());
        this.idField = findIdField();
        this.fieldsWithoutId = filterFieldsWithoutId();
    }

    @Override
    public String getName() {
        return entityClass.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }

    private Constructor<T> findNoArgConstructor() {
        try {
            return entityClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new WrongClassConfigException("There is no declared constructor in class " + entityClass.getName());
        }
    }

    private List<Field> getAnnotatedIdFields() {
        return allFields.stream().filter(f -> f.isAnnotationPresent(Id.class)).toList();
    }

    private Field findIdField() {
        List<Field> idFields = getAnnotatedIdFields();
        if (idFields.isEmpty()) {
            throw new WrongClassConfigException(
                    "There is no field with annotation @Id in class " + entityClass.getName());
        }
        if (idFields.size() > 1) {
            throw new WrongClassConfigException(
                    "There are several fields with annotation @Id in class " + entityClass.getName());
        }
        return idFields.getFirst();
    }

    private List<Field> filterFieldsWithoutId() {
        return allFields.stream().filter(f -> f != idField).toList();
    }
}
