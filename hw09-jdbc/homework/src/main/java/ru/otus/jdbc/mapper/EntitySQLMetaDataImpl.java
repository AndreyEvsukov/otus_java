package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData<T> {

    private final EntityClassMetaData<T> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        return String.format("SELECT * FROM %s", entityClassMetaData.getName());
    }

    @Override
    public String getSelectByIdSql() {
        return String.format(
                "SELECT * FROM %s WHERE %s = ?",
                entityClassMetaData.getName(), entityClassMetaData.getIdField().getName());
    }

    @Override
    public String getInsertSql() {
        final var fields = getFieldsWithoutId();
        final var columns = String.join(", ", fields);
        final var values = fields.stream().map(f -> "?").collect(Collectors.joining(", "));

        return String.format("INSERT INTO %s (%s) VALUES (%s)", entityClassMetaData.getName(), columns, values);
    }

    @Override
    public String getUpdateSql() {
        final var setExpressions = getSetExpressions();

        return String.format(
                "UPDATE %s SET %s WHERE %s = ?",
                entityClassMetaData.getName(),
                String.join(", ", setExpressions),
                entityClassMetaData.getIdField().getName());
    }

    @Override
    public EntityClassMetaData<T> getEntityClassMetaData() {
        return entityClassMetaData;
    }

    private List<String> getFieldsWithoutId() {
        return entityClassMetaData.getFieldsWithoutId().stream()
                .map(Field::getName)
                .toList();
    }

    private List<String> getSetExpressions() {
        return entityClassMetaData.getFieldsWithoutId().stream()
                .map(f -> f.getName() + " = ?")
                .toList();
    }
}
