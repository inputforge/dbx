package com.inputforge.dbx.codegen;

import javax.lang.model.element.VariableElement;

public class ParameterSpec {
    private final String name;
    private final String type;
    private final String sqlType;

    public ParameterSpec(String name, String type, String sqlType) {
        this.name = name;
        this.type = type;
        this.sqlType = sqlType;
    }

    public static ParameterSpec from(VariableElement parameter) {
        return new ParameterSpec(
                parameter.getSimpleName().toString(),
                parameter.asType().toString(),
                getSqlType(parameter.asType().toString())
        );
    }

    private static String getSqlType(String type) {
        switch (type) {
            case "java.lang.String":
                return "java.sql.JDBCType.VARCHAR";
            case "java.lang.Integer":
            case "int":
                return "java.sql.JDBCType.INTEGER";
            case "java.lang.Long":
            case "long":
                return "java.sql.JDBCType.BIGINT";
            case "java.lang.Float":
            case "float":
                return "java.sql.JDBCType.FLOAT";
            case "java.lang.Double":
            case "double":
                return "java.sql.JDBCType.DOUBLE";
            case "java.lang.Boolean":
            case "boolean":
                return "java.sql.JDBCType.BOOLEAN";
            case "java.time.LocalDate":
            case "java.sql.Date":
            case "java.util.Date":
                return "java.sql.JDBCType.DATE";
            case "java.time.LocalDateTime":
            case "java.time.Instant":
            case "java.sql.Timestamp":
                return "java.sql.JDBCType.TIMESTAMP";
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);

        }
    }

    public String getSqlType() {
        return sqlType;
    }

    @Override
    public String toString() {
        return type + " " + name;
    }

    public String getName() {
        return name;
    }
}
