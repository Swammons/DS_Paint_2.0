package com.example.dspaint;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class paintBase extends Application {
    int tabCount = 0;
    @Override
    public void start(Stage stage) throws IOException {
        final double[] windowSize = {1000, 500};
        final paintTab[] paintTabs = new paintTab[10];
        BorderPane outsideBorderPane = new BorderPane();
        TabPane tabPane = new TabPane();
        Menu File = new Menu("File");
        Menu Help = new Menu("Help");
        MenuItem OpenOp = new MenuItem("Open");
        MenuItem SaveOp = new MenuItem("Save");
        MenuItem SaveAsOp = new MenuItem("Save As");
        MenuItem CloseOp = new MenuItem("Close");
        MenuItem AboutOp = new MenuItem("About");
        SeparatorMenuItem sep = new SeparatorMenuItem();
        File.getItems().add(OpenOp);
        File.getItems().add(SaveOp);
        File.getItems().add(SaveAsOp);
        File.getItems().add(3, sep);
        File.getItems().add(CloseOp);
        Help.getItems().add(AboutOp);
        OpenOp.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        SaveOp.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        OpenOp.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Other", "*.*"),
                        new FileChooser.ExtensionFilter("PNG", "*.png"),
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                java.io.File file = fileChooser.showOpenDialog(stage);
                tabCount = tabCount + 1;
                paintTabs[tabCount] = new paintTab(file.getName());
                tabPane.getTabs().add(paintTabs[tabCount].paintTabInstance(file));


            }
        });
        SaveAsOp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Image");
                fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                        new FileChooser.ExtensionFilter("Other", "*.*"));
                File file = fileChooser.showSaveDialog(stage);
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                    latestFilePath[0] = file.getAbsolutePath();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        MenuBar menuBar = new MenuBar();

        // add menu to menu bar
        menuBar.getMenus().add(File);
        menuBar.getMenus().add(Help);
        // set the scene
        outsideBorderPane.setTop(menuBar);
        outsideBorderPane.setCenter(tabPane);
        Scene scene = new Scene(outsideBorderPane, windowSize[0], windowSize[1]);
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}