package com.inputforge.dbx.codegen;

import java.util.ArrayList;
import java.util.List;

public class JavaFileComposer {
    private final String packageName;
    private final List<String> imports;
    private List<ClassSpec> classSpecs;

    private JavaFileComposer(String packageName) {
        this.packageName = packageName;
        this.imports = new ArrayList<>();
        this.classSpecs = new ArrayList<>();
    }

    public static JavaFileComposer withPackage(String packageName) {
        return new JavaFileComposer(packageName);
    }

    public JavaFileComposer addImport(String importName) {
        this.imports.add(importName);
        return this;
    }

    public JavaFileComposer importClass(Class<?> clazz) {
        this.imports.add(clazz.getCanonicalName());
        return this;
    }

    public JavaFileComposer declare(ClassSpec classSpec) {
        this.classSpecs.add(classSpec);
        return this;
    }
}
