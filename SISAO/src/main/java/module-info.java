module com.example.sisao {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sisao to javafx.fxml;
    exports com.example.sisao;
}