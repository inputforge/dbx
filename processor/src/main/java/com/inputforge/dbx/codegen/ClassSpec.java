package com.inputforge.dbx.codegen;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassSpec {
    public final String accessModifier;
    public final String name;
    private String parent = null;
    private List<String> interfaces = List.of();
    private List<MethodSpec> methods = new ArrayList<>();

    private List<ConstructorSpec> constructors = new ArrayList<>();

    private ClassSpec(String accessModifier, String name) {
        this.accessModifier = accessModifier;
        this.name = name;
    }

    public static ClassSpec publicClass(String name) {
        return new ClassSpec("public", name);
    }

    public ClassSpec extend(Class<?> clazz) {
        this.parent = clazz.getCanonicalName();
        return this;
    }

    public ClassSpec extend(String className) {
        this.parent = className;
        return this;
    }

    public ClassSpec implement(Class<?>... interfaces) {
        this.interfaces = Stream.of(interfaces).map(Class::getCanonicalName).collect(
                Collectors.toList());
        return this;
    }

    public ClassSpec implement(String... interfaces) {
        this.interfaces = List.of(interfaces);
        return this;
    }

    public ClassSpec method(MethodSpec methodSpec) {
        this.methods.add(methodSpec);
        return this;
    }

    public void write(Writer writer) throws IOException {
        writer.append(accessModifier)
                .append(" class ")
                .append(name);
        if (parent != null) {
            writer.append(" extends ")
                    .append(parent);
        }
        if (!interfaces.isEmpty()) {
            writer.append(" implements ")
                    .append(String.join(", ", interfaces));
        }
        writer.append(" {\n\n");

        for (var constructor : constructors) {
            constructor.write(writer);
        }

        for (var method : methods) {
            method.write(writer);
        }

        writer.append("}\n");
    }

    public ClassSpec constructor(ConstructorSpec constructorSpec) {
        this.constructors.add(constructorSpec);
        return this;
    }
}
