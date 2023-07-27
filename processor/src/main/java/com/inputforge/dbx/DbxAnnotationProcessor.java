package com.inputforge.dbx;

import com.inputforge.dbx.codegen.DaoClassBuilder;
import com.inputforge.dbx.codegen.MethodSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

@SupportedAnnotationTypes("com.inputforge.dbx.Dao")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class DbxAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var daoClasses = new HashMap<String, String>();
        for (var element : roundEnv.getElementsAnnotatedWith(Dao.class)) {
            var daoImplClass = this.processDaoInterface(element);
            daoImplClass.ifPresent(s -> daoClasses.put(element.toString(), s));
        }

        if (!daoClasses.isEmpty()) {
            try {
                writeDaoRegistry(daoClasses);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        return true;
    }

    private void writeDaoRegistry(HashMap<String, String> daoClasses) throws IOException {
        String daoRegistryClassName = "DaoRegistry" + System.currentTimeMillis();

        writeDaoRegistryImpl(daoClasses, "dbx.internal", daoRegistryClassName);
        writeDaoService("dbx.internal", daoRegistryClassName);
    }

    private void writeDaoService(String packageName, String className) throws IOException {
        var file = processingEnv.getFiler().createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                "META-INF/services/com.inputforge.dbx.DaoRegistry");
        try (var writer = file.openWriter()) {
            writer.append(packageName).append(".").append(className);
        }
    }

    private void writeDaoRegistryImpl(HashMap<String, String> daoClasses, String packageName, String className) throws IOException {
        JavaFileObject file = processingEnv.getFiler()
                .createSourceFile(packageName + "." + className);

        try (var writer = file.openWriter()) {
            writer.append("package ").append(packageName).append(";\n\n")
                    .append("import java.sql.Connection;\n")
                    .append("import java.util.Optional;\n\n")
                    .append("import com.inputforge.dbx.DaoRegistry;\n\n")

                    .append("public class ")
                    .append(className)
                    .append(" implements DaoRegistry {\n\n");

            writer.append("@Override\n")
                    .append("@SuppressWarnings(\"unchecked\")\n")
                    .append("public <T> Optional<? extends T> get(Class<T> daoClass, Connection connection) {");

            writer.append("switch (daoClass.getName()) {\n");
            for (var entry : daoClasses.entrySet()) {
                writer.append("case \"")
                        .append(entry.getKey())
                        .append("\":\n")
                        .append("return (Optional<? extends T>) Optional.of(new ")
                        .append(entry.getValue())
                        .append("(connection));\n");
            }
            writer.append("default:\n")
                    .append("return Optional.empty();\n")
                    .append("}\n");

            writer.append("}\n\n");
            writer.append("}");
        }
    }

    private Optional<String> processDaoInterface(Element element) {
        if (!(element instanceof TypeElement)) {
            processingEnv.getMessager().printMessage(
                    javax.tools.Diagnostic.Kind.ERROR,
                    "Dao annotation can only be used on interfaces",
                    element
            );
            return Optional.empty();
        }

        var daoInterface = (TypeElement) element;
        if (!daoInterface.getKind().isInterface()) {
            processingEnv.getMessager().printMessage(
                    javax.tools.Diagnostic.Kind.ERROR,
                    "Dao annotation can only be used on interfaces",
                    daoInterface
            );
            return Optional.empty();
        }

        var packageName = processingEnv.getElementUtils().getPackageOf(daoInterface).toString();
        var className = daoInterface.getSimpleName().toString();

        var daoClassBuilder = new DaoClassBuilder(packageName, className);

        var daoElements = daoInterface.getEnclosedElements();


        for (var e : daoElements) {
            if (!(e instanceof ExecutableElement)) {
                continue;
            }

            try {
                var method = (ExecutableElement) e;
                var queryAnnotation = method.getAnnotation(Query.class);
                if (queryAnnotation == null) {
                    processingEnv.getMessager().printMessage(
                            javax.tools.Diagnostic.Kind.ERROR,
                            "Only methods annotated with @Query are supported",
                            method
                    );
                }
                daoClassBuilder.addMethodSpec(MethodSpec.from(method));
            } catch (IllegalArgumentException ex) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        ex.getMessage(),
                        e
                );
                return Optional.empty();
            }
        }

        try {
            daoClassBuilder.write(processingEnv);
            return Optional.of(daoClassBuilder.getCanoncialName());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }


    }
}
