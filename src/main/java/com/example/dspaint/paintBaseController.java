package com.example.dspaint;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class paintBaseController {
    @FXML
    private Label welcomeText;
    TabPane tabPane = new TabPane();
    Menu File = new Menu("File");
    Menu Help = new Menu("Help");
    MenuItem OpenOp = new MenuItem("Open");
    MenuItem SaveOp = new MenuItem("Save");
    MenuItem SaveAsOp = new MenuItem("Save As");
    MenuItem CloseOp = new MenuItem("Close");
    MenuItem AboutOp = new MenuItem("About");
    SeparatorMenuItem sep = new SeparatorMenuItem();

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}