package com.inputforge.dbx.codegen;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DaoClassBuilder {
    private final String packageName;
    private final String className;

    private final List<MethodSpec> methods;

    public DaoClassBuilder(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
        this.methods = new ArrayList<>();
    }

    public void addMethodSpec(MethodSpec from) {
        methods.add(from);
    }

    public void write(ProcessingEnvironment processingEnv) throws IOException {
        String implClassName = getImplClassName();

        JavaFileObject file = processingEnv.getFiler()
                .createSourceFile(packageName + "." + implClassName);

        var source = JavaFileComposer.withPackage(packageName)
                .addImport("java.sql.Connection")
                .declare(ClassSpec.publicClass(implClassName)
                        .extend("com.inputforge.dbx.AbstractDao")
                        .implement(className)
                        .constructor("Connection connection", "super(connection)"));



        try (var writer = file.openWriter()) {
            writer.append("package ")
                    .append(packageName)
                    .append(";\n\n")
                    .append("import java.sql.Connection;\n\n")

                    .append("public class ")
                    .append(implClassName)
                    .append(" extends AbstractDao")
                    .append(" implements ")
                    .append(className)
                    .append(" {\n\n");

            writer.append("public ")
                    .append(implClassName)
                    .append("(Connection connection) {\n")
                    .append("super(connection);\n")
                    .append("}\n\n");

            for (var method : methods) {
                method.write(writer);
            }

            writer.append("}").close();
        }
    }

    private String getImplClassName() {
        return className + "Impl";
    }

    public String getCanoncialName() {
        return packageName + "." + getImplClassName();
    }
}
