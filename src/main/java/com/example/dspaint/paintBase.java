package com.example.dspaint;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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
                for(paintTab pTab : paintTabs) {
                    if(pTab != null) {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save Image");
                        fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                                new FileChooser.ExtensionFilter("Other", "*.*"));
                        File file = fileChooser.showSaveDialog(stage);
                        pTab.Saving(file);
                    }
                }
            }
        });
        SaveOp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                for (paintTab pTab : paintTabs) {
                    if(pTab != null) {
                        File file = new File(pTab.filePath);
                        pTab.Saving(file);
                    }
                }
            }
        });
        CloseOp.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                System.exit(0);
            }
        });

        AboutOp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                launchAbout(stage);
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


    private void launchAbout(Stage s){
        final Stage dialog = new Stage();
        dialog.setTitle("About DS Paint");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(s);
        GridPane gridPane = new GridPane();
        Label labelTitle = new Label("What DS Paint tools do?");
        Label labelClear = new Label("Clear:");
        Label whatItDoClear = new Label("Will completely reset the canvas (This includes any opened images");
        Label labelDraw = new Label("Draw:");
        Label whatItDoDraw = new Label("Toggles the ability to draw on the canvas with the users mouse");
        Label labelFill = new Label("Fill:");
        Label whatItDoFill = new Label("Will fill the last shape drawn with the Draw tool");
        VBox dialogVbox = new VBox(20);
        gridPane.add(labelTitle, 0, 0, 2, 1);
        gridPane.add(labelClear, 0, 1, 2, 1);
        gridPane.add(whatItDoClear, 2, 1, 2, 1);
        gridPane.add(labelDraw, 0, 2, 2, 1);
        gridPane.add(whatItDoDraw, 2, 2, 2, 1);
        gridPane.add(labelFill, 0, 3, 2, 1);
        gridPane.add(whatItDoFill, 2, 3, 2, 1);
        gridPane.setVgap(10);
        dialogVbox.getChildren().add(gridPane);
        Scene dialogScene = new Scene(dialogVbox, 500, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}