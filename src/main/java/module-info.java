module com.example.jetfighter {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens com.example.jetfighter to javafx.fxml;
    exports com.example.jetfighter;
}