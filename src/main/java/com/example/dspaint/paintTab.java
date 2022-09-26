package com.example.dspaint;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class paintTab {
    MenuBar menuBar;
    Menu File;
    Menu Help;
    MenuItem OpenOp;
    MenuItem SaveOp;
    MenuItem SaveAsOp;
    MenuItem CloseOp;
    MenuItem AboutOp;
    Tab tab;
    BorderPane borderPane;
    BorderPane saveBorderPane;
    ScrollPane scrollPane;
    StackPane stackPane;
    paintCanvas canvas;
    boolean isSaved;

    String filePath;
    paintTab(String name, Stage stage){
        menuBar = new MenuBar();
        File = new Menu("File");
        Help = new Menu("Help");
        OpenOp = new MenuItem("Open");
        SaveOp = new MenuItem("Save All");
        SaveAsOp = new MenuItem("Save All As");
        CloseOp = new MenuItem("Close");
        AboutOp = new MenuItem("About");
        SeparatorMenuItem sep = new SeparatorMenuItem();
        menuBar.getMenus().add(File);
        menuBar.getMenus().add(Help);
        File.getItems().add(OpenOp);
        File.getItems().add(SaveOp);
        File.getItems().add(SaveAsOp);
        File.getItems().add(3, sep);
        File.getItems().add(CloseOp);
        Help.getItems().add(AboutOp);
        OpenOp.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        SaveOp.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        SaveAsOp.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));
        tab = new Tab(name);
        borderPane = new BorderPane();
        saveBorderPane = new BorderPane();
        scrollPane = new ScrollPane();
        stackPane = new StackPane();
        canvas = new paintCanvas();
        isSaved = false;
        OpenOp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // Open up file explorer so the user can pick a file to open
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Other", "*.*"),
                        new FileChooser.ExtensionFilter("PNG", "*.png"),
                        new FileChooser.ExtensionFilter("GIF", "*.gif"),
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                File file = fileChooser.showOpenDialog(stage);
                FileInputStream fileInputStream;
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                Image image = new Image(fileInputStream);
                canvas.pasteImage(image);
                filePath = file.getAbsolutePath();
                tab.setText(file.getName());
            }
        });
        SaveAsOp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                isSaved = true;
                Saving(SaveAsWindow(stage));
            }
        });
        SaveOp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if(isSaved){
                    File file = new File(filePath);
                    Saving(file);
                }
                else{
                    isSaved = true;
                    Saving(SaveAsWindow(stage));
                }
            }
        });
        CloseOp.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                Alert areYouSureAlert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save this tab before closing?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = areYouSureAlert.showAndWait();
                if (areYouSureAlert.getResult() == ButtonType.YES) {
                    if(isSaved){
                        File file = new File(filePath);
                        Saving(file);
                    }
                    else{
                        isSaved = true;
                        Saving(SaveAsWindow(stage));
                    }
                }
                else {
                    e.consume();
                }
            }
        });

        AboutOp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                launchAbout(stage);
            }
        });


        tab.setOnClosed((Event t) ->
        {
            Alert areYouSureAlert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save this tab before closing?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = areYouSureAlert.showAndWait();
                if (areYouSureAlert.getResult() == ButtonType.YES) {
                    if(isSaved){
                        File file = new File(filePath);
                        Saving(file);
                    }
                    else{
                        isSaved = true;
                        Saving(SaveAsWindow(stage));
                    }
                }
                else {
                    t.consume();
                }
        });

    }

    public Tab paintTabBlankInstance(){
        saveBorderPane.setTop(menuBar);
        saveBorderPane.setCenter(borderPane);
        borderPane.setTop(canvas.tabToolBar());
        borderPane.setCenter(scrollPane);
        scrollPane.setContent(stackPane);
        stackPane.getChildren().add(canvas.makeNewBlankCanvas());
        tab.setContent(saveBorderPane);
        return tab;
    }

    public void Saving(File file){
        try {
            ImageIO.write(SwingJank.fromFXImage(getSnapshot(), null), "png", file);
            filePath = file.getAbsolutePath();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private WritableImage getSnapshot(){
        return canvas.captureCanvas();
    }

    private File SaveAsWindow(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("Other", "*.*"));
        File file = fileChooser.showSaveDialog(stage);
        return file;
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



