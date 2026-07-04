module com.progetto {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;

    opens com.progetto to javafx.fxml;

    exports com.progetto;
    exports com.progetto.boundary;
    exports com.progetto.controllo;
    exports com.progetto.database;
    exports com.progetto.entita;
    exports com.progetto.exceptions;
}
