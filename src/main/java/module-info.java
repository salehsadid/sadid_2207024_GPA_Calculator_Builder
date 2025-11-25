module com.example.sadid_2207024_gpa_calculator_builder {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires transitive javafx.base;
    requires transitive javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // SQLite and GSON
    requires org.xerial.sqlitejdbc;
    requires com.google.gson;

    // Java SQL
    requires java.sql;

    opens com.example.sadid_2207024_gpa_calculator_builder to javafx.fxml, com.google.gson;
    exports com.example.sadid_2207024_gpa_calculator_builder;
}