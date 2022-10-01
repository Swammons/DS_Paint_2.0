module com.example.dspaint {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.dspaint to javafx.fxml;
    exports com.example.dspaint;
}