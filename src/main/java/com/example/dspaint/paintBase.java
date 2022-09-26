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
    int tabCount = 1;
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("DS Paint");
        final double[] windowSize = {1000, 500};
        // Declaring array of paintTabs, for save all and save as all
        final paintTab[] paintTabs = new paintTab[10];
        // Declaring outermost border pain used to position the menu bar
        BorderPane outsideBorderPane = new BorderPane();
        // Declaring the tab pane
        TabPane tabPane = new TabPane();
        // set the scene
        outsideBorderPane.setCenter(tabPane);
        paintTabs[tabCount] = new paintTab("New tab", stage);
        tabPane.getTabs().add(paintTabs[tabCount].paintTabBlankInstance());
        tabPane.getTabs().add(newTabButton(tabPane,stage, paintTabs));
        Scene scene = new Scene(outsideBorderPane, windowSize[0], windowSize[1]);
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private Tab newTabButton(TabPane tabPane, Stage stage, paintTab[] paintTabs) {
        Tab addTab = new Tab("+"); // You can replace the text with an icon
        addTab.setClosable(false);
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == addTab) {
                tabCount = tabCount + 1;
                paintTabs[tabCount] = new paintTab("New tab " + Integer.toString(tabCount), stage);
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, paintTabs[tabCount].paintTabBlankInstance()); // Adding new tab before the "button" tab
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2); // Selecting the tab before the button, which is the newly created one
            }
        });
        return addTab;
    }
}