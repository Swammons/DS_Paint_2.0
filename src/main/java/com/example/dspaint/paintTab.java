package com.example.dspaint;

import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

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

public class paintTab {
    Tab tab;
    BorderPane borderPane;
    ScrollPane scrollPane;
    StackPane stackPane;

    paintCanvas canvas;

    String filePath;
    paintTab(String name){
        tab = new Tab(name);
        borderPane = new BorderPane();
        scrollPane = new ScrollPane();
        stackPane = new StackPane();
        canvas = new paintCanvas();
    }
    public Tab paintTabInstance(File file){
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        Image image = new Image(fileInputStream);
        borderPane.setTop(canvas.tabToolBar());
        borderPane.setCenter(scrollPane);
        scrollPane.setContent(stackPane);
        stackPane.getChildren().add(canvas.makeNewCanvas(image));
        tab.setContent(borderPane);
        filePath = file.getAbsolutePath();
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


}

