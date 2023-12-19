module com.example.bd_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires fontawesomefx;


    opens com.example.bd_project to javafx.fxml;
    opens Models to javafx.base;
    exports com.example.bd_project;
}