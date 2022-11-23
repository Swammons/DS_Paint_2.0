package com.example.dspaint;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.lang.System.currentTimeMillis;

/**
 * Represents base application of paint.
 * It contains the tab pane.
 */
public class paintBase extends Application {

    /**
     * Represents the number of tabs
     */
    int tabCount = 1;

    /**
     * Timer used for tracking
     */
    Timer logTimer;
    TimerTask logEvent;

    /**
     *Lunches the DS Paint application
     * @param stage The stage of the interface
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("DS Paint");
        logTimer = new Timer();
        // Declare the initial size of the window on start up
        final double[] windowSize = {1500, 750};
        // Declaring array of paintTabs, for save all and save as all
        final paintTab[] paintTabs = new paintTab[25];
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler(shapeUtils.getImageDir() + "\\paintLog.txt");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        logEvent = new TimerTask(){
            //override run method
            @Override
            public void run(){
                Platform.runLater(() -> {
                    for(paintTab pT : paintTabs){
                        if(pT != null){
                            if(pT.isSelected) {
                                    logger.info("  Current Tap: " + pT.tab.getText() + pT.getLastEventLog());
                            }
                        }
                    }
                });
            }
        };
        logTimer.scheduleAtFixedRate(logEvent, 0, 5000);
        // Declaring outermost border pain used to position the menu bar
        BorderPane outsideBorderPane = new BorderPane();
        // Declaring the tab pane
        TabPane tabPane = new TabPane();
        outsideBorderPane.setCenter(tabPane);
        paintTabs[tabCount] = new paintTab("New tab", stage);
        // Create First blank tab
        tabPane.getTabs().add(paintTabs[tabCount].paintTabBlankInstance());
        // Add the plus tab button
        tabPane.getTabs().add(newTabButton(tabPane,stage, paintTabs));
        // set the scene
        Scene scene = new Scene(outsideBorderPane, windowSize[0], windowSize[1]);
        scene.getRoot().setStyle("-fx-accent: #1e74c6;" +
                "    -fx-focus-color: -fx-accent;" +
                "    -fx-base: #373e43;" +
                "    -fx-font-family: \"Comic Sans MS\";" +
                "    -fx-control-inner-background: derive(-fx-base, 35%);" +
                "    -fx-control-inner-background-alt: -fx-control-inner-background;");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main that runs the launch method
     */
    public static class Launcher {
        public static void main(String[] args) {
            launch();
        }
    }

    /**
     * Generate a tab to act as the new tab button
     * every time that this tad is selected generate a new tab
     * Since this tab will be selected when all paint tabs are closed this means that there will always be a paint tab open
     */
    private Tab newTabButton(TabPane tabPane, Stage stage, paintTab[] paintTabs) {
        // make the add tab "button" (is really a tab)
        Tab addTab = new Tab("+");
        // this makes it so the tab will never be closed
        addTab.setClosable(false);
        // If this tab is selected, make a new tab
        // This also means that if the last paint tab is closed then this tab will be selected and make a new tab
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == addTab) {
                // add to the total tab count
                tabCount = tabCount + 1;
                paintTabs[tabCount] = new paintTab("New tab " + Integer.toString(tabCount), stage);
                // Adding new tab before the "button" tab
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, paintTabs[tabCount].paintTabBlankInstance());
                System.out.println(currentTimeMillis());
                // Selecting the tab before the button, which is the newly created one
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
            }
        });
        return addTab;
    }

}