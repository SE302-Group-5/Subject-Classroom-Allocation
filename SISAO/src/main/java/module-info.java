module team5.sisao {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens team5.sisao to javafx.fxml;
    exports team5.sisao;
}