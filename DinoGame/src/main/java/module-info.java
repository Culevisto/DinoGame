module com.example.dinogame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.dinogame to javafx.fxml;
    exports com.example.dinogame;
}