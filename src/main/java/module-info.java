module org.example.sem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;

    opens org.example.sem to javafx.fxml;
    exports org.example.sem;
}