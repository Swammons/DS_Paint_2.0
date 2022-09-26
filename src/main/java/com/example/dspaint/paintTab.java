package com.example.dspaint;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
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
    MenuItem OpenBlankOp;
    MenuItem SaveOp;
    MenuItem SaveAsOp;
    MenuItem CloseOp;
    MenuItem AboutOp;
    Tab tab;
    BorderPane borderPane;
    BorderPane saveBorderPane;
    ToolBar toolBar;
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
        AboutOp = new MenuItem("About");
        SaveAsOp = new MenuItem("Save All As");
        OpenBlankOp = new MenuItem("Open Blank");
        CloseOp = new MenuItem("Close");
        SeparatorMenuItem sep = new SeparatorMenuItem();
        menuBar.getMenus().add(File);
        menuBar.getMenus().add(Help);
        File.getItems().add(OpenOp);
        File.getItems().add(OpenBlankOp);
        File.getItems().add(SaveOp);
        File.getItems().add(SaveAsOp);
        File.getItems().add(4, sep);
        File.getItems().add(CloseOp);
        Help.getItems().add(AboutOp);
        OpenOp.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        tab = new Tab(name);
        borderPane = new BorderPane();
        saveBorderPane = new BorderPane();
        scrollPane = new ScrollPane();
        stackPane = new StackPane();
        canvas = new paintCanvas();
        isSaved = false;
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

    public Tab paintTabInstance(File file){
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        Image image = new Image(fileInputStream);
        saveBorderPane.setTop(menuBar);
        saveBorderPane.setCenter(borderPane);
        borderPane.setTop(canvas.tabToolBar());
        borderPane.setCenter(scrollPane);
        scrollPane.setContent(stackPane);
        stackPane.getChildren().add(canvas.makeNewCanvas(image));
        tab.setContent(saveBorderPane);
        filePath = file.getAbsolutePath();
        return tab;
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



}

