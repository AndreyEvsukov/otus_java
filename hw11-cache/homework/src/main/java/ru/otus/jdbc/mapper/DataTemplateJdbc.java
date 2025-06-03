package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.jdbc.exceptions.CustomJdbcMappingException;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings({"java:S1068", "java:S3011"})
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData<T> entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData<T> entitySQLMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entitySQLMetaData.getEntityClassMetaData();
        this.allFields = this.entityClassMetaData.getAllFields();
        this.fieldsWithoutId = this.entityClassMetaData.getFieldsWithoutId();
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        final var sql = entitySQLMetaData.getSelectByIdSql();
        return dbExecutor.executeSelect(connection, sql, List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return createEntityFromResultSet(rs);
                }
                return null;
            } catch (SQLException e) {
                throw new CustomJdbcMappingException("Error mapping result set to entity", e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        final var sql = entitySQLMetaData.getSelectAllSql();
        return dbExecutor
                .executeSelect(connection, sql, Collections.emptyList(), rs -> {
                    List<T> entities = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            entities.add(createEntityFromResultSet(rs));
                        }
                        return entities;
                    } catch (SQLException e) {
                        throw new CustomJdbcMappingException("Error mapping result set to entities", e);
                    }
                })
                .orElse(Collections.emptyList());
    }

    @Override
    public long insert(Connection connection, T entity) {
        final var sql = entitySQLMetaData.getInsertSql();
        final List<Object> params = getFieldValues(entity, fieldsWithoutId);
        return dbExecutor.executeStatement(connection, sql, params);
    }

    @Override
    public void update(Connection connection, T entity) {
        final var sql = entitySQLMetaData.getUpdateSql();
        final List<Object> params = new ArrayList<>(getFieldValues(entity, allFields));
        dbExecutor.executeStatement(connection, sql, params);
    }

    private T createEntityFromResultSet(ResultSet rs) throws SQLException {
        try {
            T entity = createNewInstance();
            for (Field field : allFields) {
                setFieldValue(entity, field, rs.getObject(field.getName()));
            }
            return entity;
        } catch (Exception e) {
            throw new CustomJdbcMappingException("Error creating entity instance", e);
        }
    }

    private T createNewInstance() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> constructor = entityClassMetaData.getConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private void setFieldValue(T entity, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(entity, value);
    }

    private List<Object> getFieldValues(T entity, List<Field> fields) {
        List<Object> values = new ArrayList<>();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                values.add(field.get(entity));
            } catch (IllegalAccessException e) {
                throw new CustomJdbcMappingException("Error accessing field: " + field.getName(), e);
            }
        }
        return values;
    }
}
