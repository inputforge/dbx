module dbx.processor {
    requires java.base;
    requires java.compiler;

    opens com.inputforge.dbx.codegen to java.compiler;
    exports com.inputforge.dbx;
}