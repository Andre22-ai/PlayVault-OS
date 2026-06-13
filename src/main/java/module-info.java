module com.progetto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.progetto to javafx.fxml;
    exports com.progetto;
}
