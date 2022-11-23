package com.example.dspaint;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents an instance of the tab.
 * This includes the instance of the menu bar.
 */
public class paintTab {
    MenuBar menuBar;
    Menu File;
    Menu Help;
    MenuItem OpenOp;
    MenuItem SaveOp;
    MenuItem SaveAsOp;
    MenuItem AutoSaveToggle;
    MenuItem CloseOp;
    MenuItem AboutOp;
    Tab tab;
    BorderPane borderPane;
    BorderPane saveBorderPane;
    ScrollPane scrollPane;
    StackPane stackPane;
    paintCanvas canvas;
    boolean isSaved;
    boolean autoSaveEnable;
    Timer autoSaveTimer;
    TimerTask autoSave;
    String filePath;
    String lastTabEventLog;
    String lastCanvasEventLog;
    boolean isSelected;
    paintTab(String name, Stage stage){
        // initialize variables
        menuBar = new MenuBar();
        File = new Menu("File");
        Help = new Menu("Help");
        OpenOp = new MenuItem("Open");
        SaveOp = new MenuItem("Save");
        SaveAsOp = new MenuItem("Save As");
        CloseOp = new MenuItem("Close");
        AboutOp = new MenuItem("About");
        AutoSaveToggle = new MenuItem("Auto Save: (OFF)");
        SeparatorMenuItem sep = new SeparatorMenuItem();
        menuBar.getMenus().add(File);
        menuBar.getMenus().add(Help);
        File.getItems().add(OpenOp);
        File.getItems().add(SaveOp);
        File.getItems().add(SaveAsOp);
        File.getItems().add(AutoSaveToggle);
        File.getItems().add(4, sep);
        File.getItems().add(CloseOp);
        Help.getItems().add(AboutOp);
        // Make keyboard shortcuts
        OpenOp.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        SaveOp.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        SaveAsOp.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));
        tab = new Tab(name);
        borderPane = new BorderPane();
        saveBorderPane = new BorderPane();
        scrollPane = new ScrollPane();
        stackPane = new StackPane();
        canvas = new paintCanvas();
        // When the file has just been opened it has not been saved
        isSaved = false;
        autoSaveEnable = false;
        isSelected = false;
        // Set up the event log
        lastTabEventLog = "Tab created";
        lastCanvasEventLog = "Tab created";
        // Set up auto save
        autoSaveTimer = new Timer();
        autoSave = new TimerTask(){
            //override run method
            @Override
            public void run(){
                if(autoSaveEnable) {
                    Platform.runLater(() -> {
                        System.out.println("Saving . . . .");
                        File file = new File(filePath);
                        Saving(file);
                    });
                }
            }
        };
        OpenOp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // Open up file explorer so the user can pick a file to open
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Other", "*.*"),
                        new FileChooser.ExtensionFilter("PNG", "*.png"),
                        new FileChooser.ExtensionFilter("GIF", "*.gif"),
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"));
                // Get the file the user selected
                File file = fileChooser.showOpenDialog(stage);
                // Make the file an input stream
                FileInputStream fileInputStream;
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                // make the input stream an image
                Image image = new Image(fileInputStream);
                // paste the image on to the canvas
                canvas.addImage(image);
                filePath = file.getAbsolutePath();
                tab.setText(file.getName());
                isSaved = false;
                lastTabEventLog = "Image opened in tab";
            }
        });
        SaveAsOp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // Note that this tab has been saved before
                isSaved = true;
                // Save it using a Save As File Chooser
                Saving(SaveAsWindow(stage));
                lastTabEventLog = "Tab Saved As";
            }
        });
        SaveOp.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // If it has been saved, save it to the last know file path
                if(isSaved){
                    File file = new File(filePath);
                    Saving(file);
                }
                else{
                    // If is has not been saved yet, do Save As
                    isSaved = true;
                    Saving(SaveAsWindow(stage));
                }
                lastTabEventLog = "Tab Saved";
            }
        });
        AutoSaveToggle.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (!autoSaveEnable) {
                    // If it has been saved, save it to the last know file path
                    if (isSaved) {
                        File file = new File(filePath);
                        Saving(file);
                    }
                    else {
                        // If is has not been saved yet, do Save As
                        isSaved = true;
                        Saving(SaveAsWindow(stage));
                    }
                    AutoSaveToggle.setText("Auto Save: (ON)");
                    autoSaveEnable = true;
                }
                else{
                    AutoSaveToggle.setText("Auto Save: (OFF)");
                    autoSaveEnable = false;
                }
            }
        });

        CloseOp.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                // Create pop up asking the user if they would like to save the tab before closing
                Alert areYouSureAlert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save this tab before closing?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = areYouSureAlert.showAndWait();
                if (areYouSureAlert.getResult() == ButtonType.YES) {
                    // If they said yes preform a Save
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
                    // if they said no close the dialog box
                    e.consume();
                }
                // Close the tab
                tab.getTabPane().getTabs().remove(tab);
            }
        });

        AboutOp.setOnAction(new EventHandler<ActionEvent>() {
            // Launch the about pop up box
            public void handle(ActionEvent e) {
                launchAbout(stage);
            }
        });


        tab.setOnClosed((Event t) ->
        {
            // Create pop up asking the user if they would like to save the tab before closing
            Alert areYouSureAlert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save this tab before closing?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = areYouSureAlert.showAndWait();
                if (areYouSureAlert.getResult() == ButtonType.YES) {
                    // If they said yes preform a Save
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
                    // if they said no close the dialog box
                    t.consume();
                }
        });

        tab.setOnSelectionChanged((Event t) -> {
            isSelected = !isSelected;
        });

        autoSaveTimer.scheduleAtFixedRate(autoSave, 0, 30000);
    }

    public Tab paintTabBlankInstance(){
        // Make the Turducken
        saveBorderPane.setTop(menuBar);
        saveBorderPane.setCenter(borderPane);
        borderPane.setTop(canvas.tabToolBar());
        borderPane.setCenter(scrollPane);
        scrollPane.setContent(stackPane);
        stackPane.getChildren().add(canvas.makeNewBlankCanvas());
        tab.setContent(saveBorderPane);
        return tab;
    }

    /**
     * This function takes in a file and saves a snapshot of the paint tab canvas and saves it to that file
     * @param file The file that will be saved to
     */
    public void Saving(File file){
        try {
            // take a snapshot of the canvas and save it to the file, defaulting to a png if non is given
            ImageIO.write(SwingJank.fromFXImage(getSnapshot(), null), "png", file);
            // make the path to the new file the file path
            filePath = file.getAbsolutePath();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    /**
     * Returns a Snap shot the tabs canvas
     * @return A snapshot of the tabs canvas
     */
    private WritableImage getSnapshot(){
        // get a snapshot of the  current canvas
        return canvas.captureCanvas();
    }

    /**
     * This method lunches a File chooser window so the user can save a snapshot of the canvas to the file to the path they pick
     * @param stage The stage
     * @return the file that the snapshot of the canvas was saved to.
     */
    private File SaveAsWindow(Stage stage){
        // Pop us with the Save As file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("Other", "*.*"));
        File file = fileChooser.showSaveDialog(stage);
        return file;
    }

    /**
    *This Void function launched the about pop up
    */
    private void launchAbout(Stage s) {
        // Launch the About window
        final Stage dialog = new Stage();
        dialog.setTitle("About DS Paint");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(s);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        Label labelTitle = new Label("What is DS Paint?");
        Label labelClear = new Label("DS Paint is a digital drawing and light image editing software \r\n" +
                "made my Drew Simmons in 2022 as part of his CS 250 Object Oriented Programing class");
        VBox dialogVbox = new VBox(20);
        gridPane.add(labelTitle, 0, 0, 2, 1);
        gridPane.add(labelClear, 0, 1, 2, 1);
        gridPane.setVgap(10);
        dialogVbox.getChildren().add(gridPane);
        Scene dialogScene = new Scene(dialogVbox, 500, 200);
        dialogScene.getRoot().setStyle("-fx-accent: #1e74c6;" +
                "    -fx-focus-color: -fx-accent;" +
                "    -fx-base: #373e43;" +
                "    -fx-font-size: 16pt;\n" +
                "    -fx-font-family: \"Comic Sans MS\";" +
                "    -fx-control-inner-background: derive(-fx-base, 35%);" +
                "    -fx-control-inner-background-alt: -fx-control-inner-background;");
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Get the last event in the tab and the last event in the canvas and build a string to be output to the log
     * @return String with the last Tab and Canvas event
     */
    public String getLastEventLog(){
        String output = " |Last Action in Tab: " + lastTabEventLog + " |Last Action in Canvas: " + canvas.canvasEventLog;
        return output;
    }

}



