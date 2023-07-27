package com.inputforge.dbx.codegen;

import com.inputforge.dbx.Query;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodSpec {
    private static final Set<String> supportedCollections = Set.of(
            "java.util.List",
            "java.util.Set",
            "java.util.Collection"
    );
    private static final Set<String> supportedClasses = Set.of(
            "java.sql.ResultSet",
            "java.lang.Byte",
            "java.lang.Short",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Float",
            "java.lang.Double",
            "java.lang.Boolean",
            "java.lang.String",
            "java.sql.Date",
            "java.util.Date",
            "java.time.LocalDate",
            "java.sql.Time",
            "java.time.LocalTime",
            "java.sql.Timestamp",
            "java.time.LocalDateTime",
            "java.time.OffsetDateTime",
            "java.time.Instant",
            "java.math.BigDecimal",
            "java.sql.Array",
            "java.sql.Blob",
            "java.sql.Clob",
            "java.sql.Ref",
            "java.sql.RowId",
            "java.sql.NClob",
            "java.sql.SQLXML",
            "java.net.URL"
    );
    private final String query;
    private final String name;
    private final TypeMirror returnType;
    private final List<ParameterSpec> params;


    public MethodSpec(String query, String name, TypeMirror returnType, List<ParameterSpec> params) {
        this.query = query;
        this.name = name;
        this.returnType = returnType;
        this.params = params;
    }

    public static MethodSpec from(ExecutableElement method) {
        var params = method.getParameters()
                .stream()
                .map(ParameterSpec::from)
                .collect(Collectors.toList());

        var returnType = method.getReturnType();

        if (!isReturnTypeSupported(returnType)) {
            throw new IllegalArgumentException(
                    "Unsupported return type: " + returnType);
        }

        return new MethodSpec(
                method.getAnnotation(Query.class).value(),
                method.getSimpleName().toString(),
                returnType,
                params
        );
    }

    private static boolean isReturnTypeSupported(TypeMirror returnType) {
        if (returnType.getKind().isPrimitive()) {
            return true;
        }

        if (returnType.toString().equals("void")) {
            return true;
        }

        if (supportedClasses.contains(returnType.toString())) {
            return true;
        }

        return isCollectionTypeSupported(returnType);
    }

    private static boolean isCollectionTypeSupported(TypeMirror returnType) {
        if (!(returnType instanceof DeclaredType)) {
            return false;
        }

        var type = ((DeclaredType) returnType);
        var collectionType = type.asElement().toString();

        System.out.println("Collection type: " + returnType);
        if (!supportedCollections.contains(collectionType)) {
            return false;
        }

        var typeArg = type.getTypeArguments().get(0);
        return supportedClasses.contains(typeArg.toString());
    }

    public void write(Writer writer) throws IOException {
        writer.append("public ")
                .append(returnType.toString())
                .append(" ")
                .append(name)
                .append("(");

        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                writer.append(", ");
            }

            writer.append(params.get(i).toString());
        }

        writer.append(") {\n");

        generateMethodBody(writer);

        writer.append("}\n");

    }

    private void generateMethodBody(Writer writer) throws IOException {
        writer.append("try (var statement = connection.prepareStatement(\"")
                .append(quoteJavaString(query))
                .append("\")) {\n");

        for (int i = 0; i < params.size(); i++) {
            var param = params.get(i);
            writer.append("statement.setObject(").append(String.valueOf(i + 1)).append(", ")
                    .append(param.getName())
                    .append(",")
                    .append(param.getSqlType())
                    .append(");\n");
        }

        writer.append("var resultSet = statement.executeQuery();\n");

        processResultSet(writer);

        writer.append("} catch (java.sql.SQLException e) {\n")
                .append("throw new RuntimeException(e);\n")
                .append("}\n");
    }

    private void processResultSet(Writer writer) throws IOException {
        if (returnType.toString().equals("void")) {
            writer.append("return;\n");
            return;
        }

        switch (returnType.toString()) {
            case "java.sql.ResultSet":
                writer.append("return resultSet;\n");
                break;
            case "byte":
            case "java.lang.Byte":
                writer.append("return resultSet.getByte(1);\n");
                break;
            case "short":
            case "java.lang.Short":
                writer.append("return resultSet.getShort(1);\n");
                break;
            case "int":
            case "java.lang.Integer":
                writer.append("return resultSet.getInt(1);\n");
                break;
            case "long":
            case "java.lang.Long":
                writer.append("return resultSet.getLong(1);\n");
                break;
            case "float":
            case "java.lang.Float":
                writer.append("return resultSet.getFloat(1);\n");
                break;
            case "double":
            case "java.lang.Double":
                writer.append("return resultSet.getDouble(1);\n");
                break;
            case "boolean":
            case "java.lang.Boolean":
                writer.append("return resultSet.getBoolean(1);\n");
                break;
            case "java.lang.String":
                writer.append("return resultSet.getString(1);\n");
                break;
            case "java.sql.Date":
                writer.append("return resultSet.getDate(1);\n");
                break;
            case "java.util.Date":
                writer.append("return resultSet.getDate(1)\n");
                break;
            case "java.time.LocalDate":
                writer.append("return resultSet.getObject(1, java.time.LocalDate)\n");
                break;
            case "java.sql.Time":
                writer.append("return resultSet.getTime(1);\n");
                break;
            case "java.time.LocalTime":
                writer.append("return resultSet.getObject(1, java.time.LocalTime)\n");
                break;
            case "java.sql.Timestamp":
                writer.append("return resultSet.getTimestamp(1);\n");
                break;
            case "java.time.LocalDateTime":
                writer.append("return resultSet.getObject(1, java.time.LocalDateTime)\n");
                break;
            case "java.time.OffsetDateTime":
                writer.append("return resultSet.getObject(1, java.time.OffsetDateTime)\n");
                break;
            case "java.time.Instant":
                writer.append("return resultSet.getObject(1, java.time.Instant)\n");
                break;
            case "java.math.BigDecimal":
                writer.append("return resultSet.getBigDecimal(1);\n");
                break;
            case "java.sql.Array":
                writer.append("return resultSet.getArray(1);\n");
                break;
            case "java.sql.Blob":
                writer.append("return resultSet.getBlob(1);\n");
                break;
            case "java.sql.Clob":
                writer.append("return resultSet.getClob(1);\n");
                break;
            case "java.sql.Ref":
                writer.append("return resultSet.getRef(1);\n");
                break;
            case "java.sql.RowId":
                writer.append("return resultSet.getRowId(1);\n");
                break;
            case "java.sql.NClob":
                writer.append("return resultSet.getNClob(1);\n");
                break;
            case "java.sql.SQLXML":
                writer.append("return resultSet.getSQLXML(1);\n");
                break;
            case "java.net.URL":
                writer.append("return resultSet.getURL(1);\n");
                break;
            default:
                mapCompoundType(writer, (DeclaredType) returnType);
        }

    }

    private void mapCompoundType(Writer writer, DeclaredType returnType) throws IOException {
        var clazz = returnType.asElement().toString();
        var type = returnType.getTypeArguments().get(0);

        switch (clazz) {
            case "java.util.List":
            case "java.util.Collection":
                writer.append("return com.inputforge.dbx.ResultSetMapper.mapToList(resultSet, 1,")
                        .append(type.toString())
                        .append(".class);\n");
                break;
            case "java.util.Set":
                writer.append("return com.inputforge.dbx.ResultSetMapper.mapToSet(resultSet, 1,")
                        .append(type.toString())
                        .append(".class);\n");
                break;
            default:
                throw new IllegalArgumentException("Unsupported return type: " + clazz);
        }
    }

    private String quoteJavaString(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
