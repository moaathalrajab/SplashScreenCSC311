module org.example.splashscreen {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.splashscreen to javafx.fxml;
    exports org.example.splashscreen;
}