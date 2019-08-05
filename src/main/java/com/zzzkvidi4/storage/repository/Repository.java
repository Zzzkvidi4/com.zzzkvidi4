package com.zzzkvidi4.storage.repository;

import com.zzzkvidi4.storage.annotation.Column;
import com.zzzkvidi4.storage.annotation.Id;
import com.zzzkvidi4.storage.annotation.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Date;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Repository to work with database.
 *
 * @param <T>
 * @param <ID>
 */
public abstract class Repository<T, ID> {
    @NotNull
    private final DataSource dataSource;
    @NotNull
    private final String name;
    @Nullable
    private final Constructor<T> noArgumentsConstructor;
    @NotNull
    private final Map<String, InternalField> fields = new HashMap<>();
    @Nullable
    private Pair<String, InternalField> idField = null;
    @NotNull
    private static final Map<Class, BiFunction<ResultSet, String, Object>> extractors;
    @NotNull
    private static final Map<Class, ThreeConsumer<PreparedStatement, Integer, Object>> setters;
    @NotNull
    private static final Map<Class, Pair<Constructor, Map<String, InternalField>>> CACHE = new HashMap<>();

    static {
        extractors = new HashMap<>();
        extractors.put(
                String.class,
                (rs, key) -> {
                    try {
                        return rs.getString(key);
                    } catch (SQLException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
        );
        extractors.put(
                Instant.class,
                (rs, key) -> {
                    try {
                        return rs.getTimestamp(key).toInstant();
                    } catch (SQLException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
        );
        extractors.put(
                int.class,
                (rs, key) -> {
                    try {
                        return rs.getInt(key);
                    } catch (SQLException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
        );
        extractors.put(
                double.class,
                (rs, key) -> {
                    try {
                        return rs.getDouble(key);
                    } catch (SQLException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
        );
        setters = new HashMap<>();
        setters.put(
                String.class,
                (p, i, v) -> {
                    try {
                        p.setString(i, (String) v);
                    } catch (SQLException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
        );
        setters.put(
                Instant.class,
                (p, i, v) -> {
                    try {
                        p.setTimestamp(i, new Timestamp(((Instant)v).toEpochMilli()));
                    } catch (SQLException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
        );
        setters.put(
                Double.class,
                (p, i, v) -> {
                    try {
                        p.setDouble(i, (Double) v);
                    } catch (SQLException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
        );
        setters.put(
                LocalDate.class,
                (p, i, v) -> {
                    try {
                        p.setDate(i, Date.valueOf((LocalDate) v));
                    } catch (SQLException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
        );
    }

    Repository(@NotNull DataSource dataSource) {
        ParameterizedType someClass = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<T> clazz = (Class<T>)someClass.getActualTypeArguments()[0];

        Pair<Constructor<T>, Pair<String, InternalField>> info = getInformationAboutClass(this.fields, clazz);
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            throw new RuntimeException("No @Table on model!");
        }
        name = table.value();
        noArgumentsConstructor = info.getValue1();
        idField = info.getValue2();
        this.dataSource = dataSource;
    }

    /**
     * Method to find all entities of T class.
     * @param sql       - request
     * @param arguments - arguments
     * @return          - list of entities from sql
     */
    @NotNull
    public List<T> findAllByQuery(@NotNull String sql, @NotNull Object... arguments) {
        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getName(), dataSource.getPassword())) {
            PreparedStatement statement = connection.prepareStatement(sql);
            int index = 1;
            for (Object argument : arguments) {
                ThreeConsumer<PreparedStatement, Integer, Object> setter = setters.get(argument.getClass());
                if (setter == null) {
                    throw new RuntimeException("Setter not specified");
                }
                setter.accept(statement, index++, argument);
            }
            ResultSet resultSet = statement.executeQuery();
            List<T> result = listEntities(resultSet, noArgumentsConstructor, fields);
            statement.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method to get any entity by request.
     *
     * @param clazz     - class of result entity list
     * @param sql       - request
     * @param arguments - arguments
     * @param <K>       - type of entity
     * @return          - list of entities
     */
    @NotNull
    public <K> List<K> findAllByQuery(@NotNull Class<K> clazz, @NotNull String sql, @NotNull Object... arguments) {
        Pair<Constructor, Map<String, InternalField>> classInfo = CACHE.get(clazz);
        if (classInfo == null) {
            Map<String, InternalField> fields = new HashMap<>();
            Pair<Constructor<K>, Pair<String, InternalField>> info = getInformationAboutClass(fields, clazz);
            classInfo = new Pair<>(info.getValue1(), fields);
            CACHE.put(clazz, classInfo);
        }
        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getName(), dataSource.getPassword())) {
            PreparedStatement statement = connection.prepareStatement(sql);
            int index = 1;
            for (Object argument : arguments) {
                ThreeConsumer<PreparedStatement, Integer, Object> setter = setters.get(argument.getClass());
                if (setter == null) {
                    throw new RuntimeException("Setter not specified");
                }
                setter.accept(statement, index++, argument);
            }

            ResultSet resultSet = statement.executeQuery();
            List<K> result = listEntities(resultSet, (Constructor<K>)classInfo.getValue1(), classInfo.getValue2());
            statement.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method to get all entities from db.
     *
     * @return - list with all entities.
     */
    @NotNull
    public List<T> findAll() {
        return findAllByQuery("SELECT * FROM " + name);
    }

    /**
     * Method to find entity by id.
     *
     * @param id - entity id
     * @return   - optional of entity
     */
    @NotNull
    public Optional<T> findById(@NotNull ID id) {
        if (idField == null) {
            throw new RuntimeException("Id column is not specified!");
        }
        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getName(), dataSource.getPassword())) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + name + " WHERE " + idField.getValue1() + " = ?");
            setField(statement, 1, idField.getValue2(), id);
            ResultSet resultSet = statement.executeQuery();
            List<T> list = listEntities(resultSet, noArgumentsConstructor, fields);
            statement.close();
            if (list.isEmpty()) {
                return Optional.empty();
            } else if (list.size() == 1) {
                return Optional.of(list.get(0));
            } else {
                throw new RuntimeException("Too many results!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method to update exitsing entity in db.
     *
     * @param entity - updated entity
     * @return       - {@literal true} if update affected some rows
     */
    public boolean update(@NotNull T entity) {
        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getName(), dataSource.getPassword())) {
            Set<String> fieldsToUpdate = new HashSet<>(fields.keySet());
            if (idField != null) {
                fieldsToUpdate.remove(idField.getValue1());
            }
            StringBuilder sql = new StringBuilder("UPDATE ")
                    .append(name);
            boolean firstField = true;
            for (String fieldToUpdate : fieldsToUpdate) {
                if (!firstField) {
                    sql.append(", ");
                } else {
                    sql.append(" SET ");
                }
                firstField = false;
                sql.append(fieldToUpdate).append(" = ?");
            }
            sql.append(" WHERE ").append(idField.getValue1()).append(" = ?");
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            int index = 1;
            for (String fieldToUpdate : fieldsToUpdate) {
                InternalField field = fields.get(fieldToUpdate);
                setField(statement, index++, field, getFieldValue(fieldToUpdate, entity));
            }
            setField(statement, index, idField.getValue2(), getFieldValue(idField.getValue1(), entity));
            int rowsCount = statement.executeUpdate();
            statement.close();
            return rowsCount != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method to create entity in db.
     *
     * @param entity - entity to create
     * @return       - {@literal true} if entity was created
     */
    public boolean create(@NotNull T entity) {
        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getName(), dataSource.getPassword())) {
            StringBuilder sql = new StringBuilder("INSERT INTO ").append(name).append(" (");
            boolean firstField = true;
            for (String field : fields.keySet()) {
                if (!firstField) {
                    sql.append(", ");
                }
                firstField = false;
                sql.append(field);
            }
            sql.append(") VALUES (");
            for (int i = 0; i < fields.size(); ++i) {
                if (i != 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }
            sql.append(")");
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            int index = 1;
            for (Map.Entry<String, InternalField> internalField : fields.entrySet()) {
                setField(statement, index++, internalField.getValue(), getFieldValue(internalField.getKey(), entity));
            }
            int rowCount = statement.executeUpdate();
            statement.close();
            return rowCount != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method to delete entity.
     *
     * @param id - entity id to delete
     * @return   - {@literal true} if some rows affected
     */
    public boolean deleteById(ID id) {
        if (idField == null) {
            throw new NotImplementedException();
        }
        try (Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getName(), dataSource.getPassword())) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + name + " WHERE " + idField.getValue1() + " = ?");
            setField(statement, 1, idField.getValue2(), id);
            int rowsCount = statement.executeUpdate();
            statement.close();
            return rowsCount != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method to convert result set to list of entities.
     *
     * @param resultSet              - result set
     * @param noArgumentsConstructor - constructor
     * @param fields                 - fields mappings
     * @param <K>                    - type of result entity
     * @return                       - list of converted entities
     */
    @NotNull
    private <K> List<K> listEntities(@NotNull ResultSet resultSet, @NotNull Constructor<K> noArgumentsConstructor, @NotNull Map<String, InternalField> fields) {
        try {
            List<K> entities = new LinkedList<>();
            while (resultSet.next()) {
                K entity = noArgumentsConstructor.newInstance();
                for (Map.Entry<String, InternalField> entry : fields.entrySet()) {
                    String key = entry.getKey();
                    InternalField field = entry.getValue();
                    BiFunction<ResultSet, String, Object> extractor = extractors.get(field.getClazz());
                    if (extractor == null) {
                        throw new RuntimeException("No extractor!");
                    }
                    Object object = extractor.apply(resultSet, key);
                    if (field.getSetter() != null) {
                        field.getSetter().invoke(entity, object);
                    } else {
                        Field internalField = field.getField();
                        boolean isAccessible = internalField.isAccessible();
                        internalField.setAccessible(true);
                        internalField.set(entity, object);
                        internalField.setAccessible(isAccessible);
                    }
                }
                entities.add(entity);
            }
            return entities;
        } catch (SQLException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method to retrieve all information about field.
     *
     * @param field  - field
     * @param fields - fields info storage
     * @return       - {@literal null} if field is not id
     */
    @Nullable
    private Pair<String, InternalField> computeField(@NotNull Field field, @NotNull Map<String, InternalField> fields) {
        Column column = field.getAnnotation(Column.class);
        if (column == null) {
            column = field.getDeclaredAnnotation(Column.class);
        }
        if (column == null) {
            return null;
        }
        fields.put(column.value(), new InternalField(field.getType(), field, null, null));
        Id id = field.getAnnotation(Id.class);
        if (id == null) {
            id = field.getDeclaredAnnotation(Id.class);
        }
        if (id != null) {
            return new Pair<>(column.value(), new InternalField(field.getType(), field, null, null));
        }
        return null;
    }

    /**
     * Method to get information about class.
     *
     * @param fields - field info storage
     * @param clazz  - class
     * @param <K>    - type of class
     * @return       - constructor with id field info
     */
    @NotNull
    private <K> Pair<Constructor<K>, Pair<String, InternalField>> getInformationAboutClass(@NotNull Map<String, InternalField> fields, @NotNull Class<K> clazz) {
        Field[] entityFields = clazz.getFields();
        Field[] declaredEntityFields = clazz.getDeclaredFields();

        Pair<String, InternalField> idField = null;
        for (Field field : entityFields) {
            Pair<String, InternalField> tmp = computeField(field, fields);
            if (idField == null) {
                idField = tmp;
            }
        }
        for (Field field : declaredEntityFields) {
            Pair<String, InternalField> tmp = computeField(field, fields);
            if (idField == null) {
                idField = tmp;
            }
        }

        Constructor<K> noArgumentConstructor;
        try {
            noArgumentConstructor = clazz.getConstructor();
            if (noArgumentConstructor == null) {
                noArgumentConstructor = clazz.getDeclaredConstructor();
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No constructor without arguments!");
        }
        return new Pair<>(noArgumentConstructor, idField);
    }

    /**
     * Interface of method accepting three arguments.
     *
     * @param <T1> - first argument type
     * @param <T2> - second argument type
     * @param <T3> - third argument type
     */
    private interface ThreeConsumer<T1, T2, T3> {
        void accept(@NotNull T1 v1, @NotNull T2 v2, @NotNull T3 v3);
    }

    /**
     * Method to get value from field.
     *
     * @param columnName - column name
     * @param object     - object to get value from
     * @return           - value of field by column name
     */
    @NotNull
    private Object getFieldValue(@NotNull String columnName, @NotNull Object object) {
        try {
            InternalField internalField = fields.get(columnName);
            Field field = internalField.getField();
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            Object result = field.get(object);
            field.setAccessible(isAccessible);
            return result;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Method to set up argument for statement.
     *
     * @param statement     - statement
     * @param index         - index of argument
     * @param internalField - field
     * @param value         - argument
     */
    private void setField(@NotNull PreparedStatement statement, int index, @NotNull InternalField internalField, @NotNull Object value) {
        Class clazz = internalField.getClazz();
        ThreeConsumer<PreparedStatement, Integer, Object> setter = setters.get(clazz);
        if (setter == null) {
            throw new RuntimeException("Setter not specified!");
        }
        setter.accept(statement, index, value);
    }
}
