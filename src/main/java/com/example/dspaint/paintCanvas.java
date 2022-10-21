package com.example.dspaint;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;


/**
 * Represents the canvas on a tab.
 * This also includes the toolbar in the tab.
 */
public class paintCanvas {
    Canvas canvas;
    GraphicsContext graphicsContext;
    ColorPicker fillColorPicker;
    Button clearCanvas;
    Button fillButton;
    Button undoButton;
    Button redoButton;
    ToggleButton dashedButton;
    ComboBox comboBox;
    ColorPicker lineColorPicker;
    ToggleButton colorDropper;
    ToggleButton pasteButton;
    ToggleButton cutButton;
    Button rotateButton;
    Button rotateAllButton;
    double rotationAngel;
    Button flipButton;
    Slider lineSizeSlider;
    TextField heightText;
    TextField widthText;
    Button scaleButton;
    double line_size;
    double lastShapeX;
    double lastShapeY;
    double lastShapeW;
    double lastShapeH;
    double lastCopyX;
    double lastCopyY;
    double lastCopyW;
    double lastCopyH;
    undoRedoUtils.SizedStack<Image> undoStack;
    undoRedoUtils.SizedStack<Image> redoStack;

    Image startState;
    Image clipBoard;

    String canvasEventLog;
    Collection<ChangeListener> listeners;

    paintCanvas() {
        // initialize variables
        canvas = new Canvas();
        graphicsContext = canvas.getGraphicsContext2D();
        undoStack = new undoRedoUtils.SizedStack<>(25);
        redoStack = new undoRedoUtils.SizedStack<>(25);
        // Toolbar items
        fillColorPicker = new ColorPicker();
        // Clear button set up
        clearCanvas = new Button();
        clearCanvas.setTooltip(new Tooltip("Clear the canvas"));
        Image clearImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\eraser.png");
        ImageView clearImageView = new ImageView(clearImage);
        clearCanvas.setGraphic(clearImageView);
        // Paste button set up
        pasteButton = new ToggleButton();
        pasteButton.setTooltip(new Tooltip("Pastes last image copied to the next place you click"));
        Image pasteImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\clipboard2-plus.png");
        ImageView pasteImageView = new ImageView(pasteImage);
        pasteButton.setGraphic(pasteImageView);
        // Cut button set up
        cutButton = new ToggleButton();
        Image cutImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\scissors.png");
        ImageView cutImageView = new ImageView(cutImage);
        cutButton.setGraphic(cutImageView);
        cutButton.setTooltip(new Tooltip("Cuts out and pastes last image copied to the next place you click"));
        // Rotate button set up
        rotateButton = new Button();
        rotationAngel = 90;
        Image rotateImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\arrow-clockwise.png");
        ImageView rotateImageView = new ImageView(rotateImage);
        rotateButton.setGraphic(rotateImageView);
        rotateButton.setTooltip(new Tooltip("Rotates the last selected area 90 degrees clockwise"));
        // Rotate all button set up
        rotateAllButton = new Button("All");
        Image rotateAllImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\arrow-clockwise.png");
        ImageView rotateAllImageView = new ImageView(rotateAllImage);
        rotateAllButton.setGraphic(rotateAllImageView);
        rotateAllButton.setTooltip(new Tooltip("Rotates the Canvas 90 degrees clockwise"));
        // Flip button set up
        flipButton = new Button();
        Image flipImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\arrows-expand.png");
        ImageView flipImageView = new ImageView(flipImage);
        flipButton.setGraphic(flipImageView);
        flipButton.setTooltip(new Tooltip("Flips the last selected area"));
        // Fill button set up
        fillButton = new Button();
        fillButton.setTooltip(new Tooltip("Fill last shape drawn"));
        Image fillImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\paint-bucket.png");
        ImageView fillImageView = new ImageView(fillImage);
        fillButton.setGraphic(fillImageView);
        // Undo button set up
        undoButton = new Button();
        undoButton.setTooltip(new Tooltip("Undo last action"));
        Image undoImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\arrow-bar-left.png");
        ImageView undoImageView = new ImageView(undoImage);
        undoButton.setGraphic(undoImageView);
        // Redo button set up
        redoButton = new Button();
        redoButton.setTooltip(new Tooltip("Redo last thing Undo"));
        Image redoImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\arrow-bar-right.png");
        ImageView redoImageView = new ImageView(redoImage);
        redoButton.setGraphic(redoImageView);
        // Dashed button set up
        dashedButton = new ToggleButton("- - -");
        dashedButton.setTooltip(new Tooltip("Makes all lines and shapes dashed"));
        // Combo box set up
        comboBox = new ComboBox();
        lineColorPicker = new ColorPicker();
        // Color dropper button set up
        colorDropper = new ToggleButton();
        colorDropper.setTooltip(new Tooltip("Set the color picker to a value from the canvas"));
        Image colorDropperImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\eyedropper.png");
        ImageView colorDropperImageView = new ImageView(colorDropperImage);
        colorDropper.setGraphic(colorDropperImageView);
        // Set up line thickness slider
        lineSizeSlider = new Slider(1, 25, 1);
        lineSizeSlider.setShowTickLabels(true);
        lineSizeSlider.setShowTickMarks(true);
        lineSizeSlider.setMajorTickUnit(5);
        lineSizeSlider.setBlockIncrement(5);
        line_size = 0;
        // Set up scale tools
        heightText = new TextField("500");
        heightText.setMaxWidth(50);
        widthText = new TextField("1000");
        widthText.setMaxWidth(50);
        scaleButton = new Button();
        Image scaleImage = new Image("C:\\Users\\Drew Simmons\\IdeaProjects\\DSPaint\\src\\main\\resources\\Icons\\crop.png");
        ImageView scaleImageView = new ImageView(scaleImage);
        scaleButton.setGraphic(scaleImageView);
        scaleButton.setTooltip(new Tooltip("Scale the canvas to these number entered to the left"));
        // set up Canvas Log tool
        canvasEventLog = "Tab Created";
        listeners = new LinkedList<ChangeListener>();

        // for saving the initial coordinates of the click and drag draw options
        final double[] startX = new double[1];
        final double[] startY = new double[1];


        // When the mouse is clicked in
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {

                    public void handle(MouseEvent event) {
                        undoRedoUtils.saveState(undoStack, redoStack, canvas);
                        // Set the line size and color to what the toolbar has set
                        graphicsContext.setStroke(lineColorPicker.getValue());
                        graphicsContext.setLineWidth(line_size);
                        // If dash mode is selected then set the dashes
                        if (dashedButton.isSelected()) {
                            // If the line size is 0 make it 1 (This is for when we scale dashes the line size)
                            if (line_size == 0) {
                                line_size = 1;
                            }
                            // Set the dash offset and the dashes to a proportion of the line size
                            graphicsContext.setLineDashOffset(2.5 * line_size);
                            graphicsContext.setLineDashes(5 * line_size);
                        }
                        else {
                            // reset the dash settings to 0 to draw normal
                            graphicsContext.setLineDashOffset(0);
                            graphicsContext.setLineDashes(0);
                        }
                        if (pasteButton.isSelected()){
                            startState = undoRedoUtils.getSnapshot(canvas);
                        }
                        else if (cutButton.isSelected()){
                            startState = undoRedoUtils.getSnapshot(canvas);
                        }
                        // If pen is selected then start making a path at the cursor position
                        else if (comboBox.getValue() == "Pen") {
                            graphicsContext.beginPath();
                            graphicsContext.moveTo(event.getX(), event.getY());
                            graphicsContext.stroke();
                        }
                        else if (comboBox.getValue() == "Eraser") {
                            graphicsContext.setStroke(Color.WHITE);
                            graphicsContext.setLineWidth(line_size*3);
                            graphicsContext.setLineDashOffset(0);
                            graphicsContext.setLineDashes(0);
                            graphicsContext.beginPath();
                            graphicsContext.moveTo(event.getX(), event.getY());
                            graphicsContext.stroke();
                        }
                        // Any tool other than the pen save the cursor position
                        else if (comboBox.getValue() == "Ellipses" || comboBox.getValue() == "Circle"
                                || comboBox.getValue() == "Square" || comboBox.getValue() == "Rectangle"
                                || comboBox.getValue() == "Line" || comboBox.getValue() == "Triangle"
                                || comboBox.getValue() == "Polygon") {
                            startX[0] = event.getX();
                            startY[0] = event.getY();
                            startState = undoRedoUtils.getSnapshot(canvas);
                        }
                        // Selecting tool
                        if (event.getButton() == MouseButton.SECONDARY) {
                            startX[0] = event.getX();
                            startY[0] = event.getY();
                            rotationAngel = 0;
                            graphicsContext.setLineDashOffset(2.5);
                            graphicsContext.setLineDashes(7.5);
                            graphicsContext.setStroke(Color.LIGHTBLUE);
                            graphicsContext.setLineWidth(4);
                            startState = undoRedoUtils.getSnapshot(canvas);
                        }
                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>() {

                    public void handle(MouseEvent event) {
                        if (pasteButton.isSelected()){
                            graphicsContext.drawImage(startState, 0, 0);
                            graphicsContext.drawImage(clipBoard, event.getX(), event.getY());
                        }
                        if (cutButton.isSelected()){
                            graphicsContext.drawImage(startState, 0, 0);
                            graphicsContext.drawImage(clipBoard, event.getX(), event.getY());
                        }
                        // If pen draw a line along the path to the current cursor position
                        else if (comboBox.getValue() == "Pen") {
                            graphicsContext.lineTo(event.getX(), event.getY());
                            graphicsContext.stroke();
                        }
                        // If eraser is selected erase the area around the cursor
                        else if(comboBox.getValue() == "Eraser"){
                            graphicsContext.lineTo(event.getX(), event.getY());
                            graphicsContext.stroke();
                        }
                        // Any tool other than the pen do nothing
                        else if (comboBox.getValue() == "Ellipses" || comboBox.getValue() == "Circle" || comboBox.getValue() == "Square" || comboBox.getValue() == "Rectangle" || comboBox.getValue() == "Triangle" || comboBox.getValue() == "Line" || comboBox.getValue() == "Polygon") {
                            graphicsContext.drawImage(startState, 0, 0);
                            if (comboBox.getValue() == "Ellipses") {
                                // down and to the right
                                if (startX[0] < event.getX() && startY[0] < event.getY()) {
                                    graphicsContext.strokeOval(startX[0], startY[0], event.getX() - startX[0], event.getY() - startY[0]);
                                }
                                // down and to the left
                                else if (startX[0] > event.getX() && startY[0] < event.getY()) {
                                    graphicsContext.strokeOval(event.getX(), startY[0], startX[0] - event.getX(), event.getY() - startY[0]);
                                }
                                // up and to the right
                                else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                    graphicsContext.strokeOval(startX[0], event.getY(), event.getX() - startX[0], startY[0] - event.getY());
                                }
                                // up and to the left
                                else {
                                    graphicsContext.strokeOval(event.getX(), event.getY(), startX[0] - event.getX(), startY[0] - event.getY());
                                }
                            }
                            else if (comboBox.getValue() == "Rectangle") {
                                // down and to the right
                                if (startX[0] < event.getX() && startY[0] < event.getY()) {
                                    graphicsContext.strokeRect(startX[0], startY[0], event.getX() - startX[0], event.getY() - startY[0]);
                                }
                                // down and to the left
                                else if (startX[0] > event.getX() && startY[0] < event.getY()) {
                                    graphicsContext.strokeRect(event.getX(), startY[0], startX[0] - event.getX(), event.getY() - startY[0]);
                                }
                                // up and to the right
                                else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                    graphicsContext.strokeRect(startX[0], event.getY(), event.getX() - startX[0], startY[0] - event.getY());
                                }
                                // up and to the left
                                else {
                                    graphicsContext.strokeRect(event.getX(), event.getY(), startX[0] - event.getX(), startY[0] - event.getY());
                                }
                            }
                            else if (comboBox.getValue() == "Square") {
                                // down and to the right
                                if (startX[0] < event.getX() && startY[0] < event.getY()) {
                                    graphicsContext.strokeRect(startX[0], startY[0], event.getX() - startX[0], event.getX() - startX[0]);
                                }
                                // down and to the left
                                else if (startX[0] > event.getX() && startY[0] < event.getY()) {
                                    graphicsContext.strokeRect(event.getX(), startY[0], startX[0] - event.getX(), startX[0] - event.getX());
                                }
                                // up and to the right
                                else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                    graphicsContext.strokeRect(startX[0], event.getY(), event.getX() - startX[0], event.getX() - startX[0]);
                                }
                                // up and to the left
                                else {
                                    graphicsContext.strokeRect(event.getX(), event.getY(), startX[0] - event.getX(), startX[0] - event.getX());
                                }
                            }
                            else if (comboBox.getValue() == "Circle") {
                                // down and to the right
                                if (startX[0] < event.getX() && startY[0] < event.getY()) {
                                    graphicsContext.strokeOval(startX[0], startY[0], event.getX() - startX[0], event.getX() - startX[0]);
                                }
                                // down and to the left
                                else if (startX[0] > event.getX() && startY[0] < event.getY()) {
                                    graphicsContext.strokeOval(event.getX(), startY[0], startX[0] - event.getX(), startX[0] - event.getX());
                                }
                                // up and to the right
                                else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                    graphicsContext.strokeOval(startX[0], event.getY(), event.getX() - startX[0], event.getX() - startX[0]);
                                }
                                // up and to the left
                                else {
                                    graphicsContext.strokeOval(event.getX(), event.getY(), startX[0] - event.getX(), startX[0] - event.getX());
                                }
                            }
                            else if (comboBox.getValue() == "Triangle") {
                                // down and to the right
                                shapeUtils.strokeTriangle(startX[0], startY[0], event.getX(),event.getY(),graphicsContext);
                            }
                            else if (comboBox.getValue() == "Square") {
                                // down and to the right
                                if (startX[0] < event.getX() && startY[0] < event.getY()) {
                                    graphicsContext.strokeRect(startX[0], startY[0], event.getX() - startX[0], event.getX() - startX[0]);
                                }
                                // down and to the left
                                else if (startX[0] > event.getX() && startY[0] < event.getY()) {
                                    graphicsContext.strokeRect(event.getX(), startY[0], startX[0] - event.getX(), startX[0] - event.getX());

                                }
                                // up and to the right
                                else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                    graphicsContext.strokeRect(startX[0], event.getY(), event.getX() - startX[0], event.getX() - startX[0]);
                                }
                                // up and to the left
                                else {
                                    graphicsContext.strokeRect(event.getX(), event.getY(), startX[0] - event.getX(), startX[0] - event.getX());
                                }
                            }
                            else if (comboBox.getValue() == "Line") {
                                // Line just needs the start X&Y and end X&Y
                                graphicsContext.strokeLine(startX[0], startY[0], event.getX(), event.getY());
                            }
                        }
                        if (event.getButton() == MouseButton.SECONDARY) {
                            graphicsContext.drawImage(startState, 0, 0);
                            if (startX[0] < event.getX() && startY[0] < event.getY()) {
                                graphicsContext.strokeRect(startX[0], startY[0], event.getX() - startX[0], event.getY() - startY[0]);
                                lastCopyX = startX[0];
                                lastCopyY = startY[0];
                                lastCopyW = event.getX() - startX[0];
                                lastCopyH = event.getY() - startY[0];
                            }
                            // down and to the left
                            else if (startX[0] > event.getX() && startY[0] < event.getY()) {
                                graphicsContext.strokeRect(event.getX(), startY[0], startX[0] - event.getX(), event.getY() - startY[0]);
                                lastCopyX = event.getX();
                                lastCopyY = startY[0];
                                lastCopyW = startX[0] - event.getX();
                                lastCopyH = event.getY() - startY[0];
                            }
                            // up and to the right
                            else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                graphicsContext.strokeRect(startX[0], event.getY(), event.getX() - startX[0], startY[0] - event.getY());
                                lastCopyX = startX[0];
                                lastCopyY = event.getY();
                                lastCopyW = event.getX() - startX[0];
                                lastCopyH = startY[0] - event.getY();
                            }
                            // up and to the left
                            else {
                                graphicsContext.strokeRect(event.getX(), event.getY(), startX[0] - event.getX(), startY[0] - event.getY());
                                lastCopyX = event.getX();
                                lastCopyY = event.getY();
                                lastCopyW = startX[0] - event.getX();
                                lastCopyH = startY[0] - event.getY();
                            }
                        }
                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>() {

                    public void handle(MouseEvent event) {
                        if (pasteButton.isSelected()){
                            graphicsContext.drawImage(clipBoard, event.getX(), event.getY());
                            pasteButton.setSelected(false);
                            canvasEventLog = "Image pasted at " + Double.toString(event.getX()) + "," + Double.toString(event.getY());
                        }
                        else if (cutButton.isSelected()){
                            graphicsContext.drawImage(clipBoard, event.getX(), event.getY());
                            cutButton.setSelected(false);
                            canvasEventLog = "Image cut and pasted at " + Integer.toString((int)event.getX()) + "," + Integer.toString((int)event.getY());
                        }
                        // If pen do nothing
                        else if (comboBox.getValue() == "Pen") {
                            canvasEventLog = "Line drawn ending at " + Integer.toString((int)event.getX()) + "," + Integer.toString((int)event.getY());
                        }
                        else if(comboBox.getValue() == "Eraser"){
                            canvasEventLog = "Eraser used ending at " + Integer.toString((int)event.getX()) + "," + Integer.toString((int)event.getY());
                        }
                        // For each of the shapes we address 4 different directions that the user could drag
                        else if (comboBox.getValue() == "Ellipses") {
                            // down and to the right
                            if (startX[0] < event.getX() && startY[0] < event.getY()) {
                                graphicsContext.strokeOval(startX[0], startY[0], event.getX() - startX[0], event.getY() - startY[0]);
                                lastShapeX = startX[0];
                                lastShapeY = startY[0];
                                lastShapeW = event.getX() - startX[0];
                                lastShapeH = event.getY() - startY[0];
                            }
                            // down and to the left
                            else if (startX[0] > event.getX() && startY[0] < event.getY()) {
                                graphicsContext.strokeOval(event.getX(), startY[0], startX[0] - event.getX(), event.getY() - startY[0]);
                                lastShapeX = event.getX();
                                lastShapeY = startY[0];
                                lastShapeW = startX[0] - event.getX();
                                lastShapeH = event.getY() - startY[0];
                            }
                            // up and to the right
                            else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                graphicsContext.strokeOval(startX[0], event.getY(), event.getX() - startX[0], startY[0] - event.getY());
                                lastShapeX = startX[0];
                                lastShapeY = event.getY();
                                lastShapeW = event.getX() - startX[0];
                                lastShapeH = startY[0] - event.getY();
                            }
                            // up and to the left
                            else {
                                graphicsContext.strokeOval(event.getX(), event.getY(), startX[0] - event.getX(), startY[0] - event.getY());
                                lastShapeX = event.getX();
                                lastShapeY = event.getY();
                                lastShapeW = startX[0] - event.getX();
                                lastShapeH = startY[0] - event.getY();
                            }
                            canvasEventLog = "Ellipses drawn starting at " + Integer.toString((int)startX[0]) + "," + Integer.toString((int)startY[0])
                                    + " and ending at " + Integer.toString((int)event.getX()) + "," + Integer.toString((int)event.getY());
                        }
                        else if (comboBox.getValue() == "Rectangle") {
                            // down and to the right
                            if (startX[0] < event.getX() && startY[0] < event.getY()) {
                                graphicsContext.strokeRect(startX[0], startY[0], event.getX() - startX[0], event.getY() - startY[0]);
                                lastShapeX = startX[0];
                                lastShapeY = startY[0];
                                lastShapeW = event.getX() - startX[0];
                                lastShapeH = event.getY() - startY[0];
                            }
                            // down and to the left
                            else if (startX[0] > event.getX() && startY[0] < event.getY()) {
                                graphicsContext.strokeRect(event.getX(), startY[0], startX[0] - event.getX(), event.getY() - startY[0]);
                                lastShapeX = event.getX();
                                lastShapeY = startY[0];
                                lastShapeW = startX[0] - event.getX();
                                lastShapeH = event.getY() - startY[0];
                            }
                            // up and to the right
                            else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                graphicsContext.strokeRect(startX[0], event.getY(), event.getX() - startX[0], startY[0] - event.getY());
                                lastShapeX = startX[0];
                                lastShapeY = event.getY();
                                lastShapeW = event.getX() - startX[0];
                                lastShapeH = startY[0] - event.getY();
                            }
                            // up and to the left
                            else {
                                graphicsContext.strokeRect(event.getX(), event.getY(), startX[0] - event.getX(), startY[0] - event.getY());
                                lastShapeX = event.getX();
                                lastShapeY = event.getY();
                                lastShapeW = startX[0] - event.getX();
                                lastShapeH = startY[0] - event.getY();
                            }
                            canvasEventLog = "Rectangle drawn starting at " + Integer.toString((int)startX[0]) + "," + Integer.toString((int)startY[0])
                                    + " and ending at " + Integer.toString((int)event.getX()) + "," + Integer.toString((int)event.getY());
                        }
                        else if (comboBox.getValue() == "Square") {
                            // down and to the right
                            if (startX[0] < event.getX() && startY[0] < event.getY()) {
                                graphicsContext.strokeRect(startX[0], startY[0], event.getX() - startX[0], event.getX() - startX[0]);
                                lastShapeX = startX[0];
                                lastShapeY = startY[0];
                                lastShapeW = event.getX() - startX[0];
                                lastShapeH = event.getX() - startX[0];
                            }
                            // down and to the left
                            else if (startX[0] > event.getX() && startY[0] < event.getY()) {
                                graphicsContext.strokeRect(event.getX(), startY[0], startX[0] - event.getX(), startX[0] - event.getX());
                                lastShapeX = event.getX();
                                lastShapeY = startY[0];
                                lastShapeW = startX[0] - event.getX();
                                lastShapeH = startX[0] - event.getX();
                            }
                            // up and to the right
                            else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                graphicsContext.strokeRect(startX[0], event.getY(), event.getX() - startX[0], event.getX() - startX[0]);
                                lastShapeX = startX[0];
                                lastShapeY = event.getY();
                                lastShapeW = event.getX() - startX[0];
                                lastShapeH = event.getX() - startX[0];
                            }
                            // up and to the left
                            else {
                                graphicsContext.strokeRect(event.getX(), event.getY(), startX[0] - event.getX(), startX[0] - event.getX());
                                lastShapeX = event.getX();
                                lastShapeY = event.getY();
                                lastShapeW = startX[0] - event.getX();
                                lastShapeH = startX[0] - event.getX();
                            }
                            canvasEventLog = "Square drawn starting at " + Integer.toString((int)startX[0]) + "," + Integer.toString((int)startY[0])
                                    + " and ending at " + Integer.toString((int)event.getX()) + "," + Integer.toString((int)event.getY());
                        }
                        else if (comboBox.getValue() == "Circle") {
                            // down and to the right
                            if (startX[0] < event.getX() && startY[0] < event.getY()) {
                                graphicsContext.strokeOval(startX[0], startY[0], event.getX() - startX[0], event.getX() - startX[0]);
                                lastShapeX = startX[0];
                                lastShapeY = startY[0];
                                lastShapeW = event.getX() - startX[0];
                                lastShapeH = event.getX() - startX[0];
                            }
                            // down and to the left
                            else if (startX[0] > event.getX() && startY[0] < event.getY()) {
                                graphicsContext.strokeOval(event.getX(), startY[0], startX[0] - event.getX(), startX[0] - event.getX());
                                lastShapeX = event.getX();
                                lastShapeY = startY[0];
                                lastShapeW = startX[0] - event.getX();
                                lastShapeH = startX[0] - event.getX();
                            }
                            // up and to the right
                            else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                graphicsContext.strokeOval(startX[0], event.getY(), event.getX() - startX[0], event.getX() - startX[0]);
                                lastShapeX = startX[0];
                                lastShapeY = event.getY();
                                lastShapeW = event.getX() - startX[0];
                                lastShapeH = event.getX() - startX[0];
                            }
                            // up and to the left
                            else {
                                graphicsContext.strokeOval(event.getX(), event.getY(), startX[0] - event.getX(), startX[0] - event.getX());
                                lastShapeX = event.getX();
                                lastShapeY = event.getY();
                                lastShapeW = startX[0] - event.getX();
                                lastShapeH = startX[0] - event.getX();
                            }
                            canvasEventLog = "Circle drawn starting at " + Integer.toString((int)startX[0]) + "," + Integer.toString((int)startY[0])
                                    + " and ending at " + Integer.toString((int)event.getX()) + "," + Integer.toString((int)event.getY());

                        }
                        else if (comboBox.getValue() == "Triangle") {
                            shapeUtils.strokeTriangle(startX[0], startY[0], event.getX(),event.getY(),graphicsContext);
                            lastShapeX = startX[0];
                            lastShapeY = startY[0];
                            lastShapeW = event.getX();
                            lastShapeH = event.getY();
                            canvasEventLog = "Triangle drawn starting at " + Double.toString(startX[0]) + "," + Double.toString(startY[0])
                                    + " and ending at " + Integer.toString((int)event.getX()) + "," + Integer.toString((int)event.getY());
                        }
                        else if (comboBox.getValue() == "Polygon") {

                            int sides = shapeUtils.askForSides();
                            double radius = shapeUtils.PythagoreanTheorem(startX[0]-event.getX(), startY[0]-event.getY());
                            double[] xPoints = shapeUtils.getPolygonSides(startX[0], startY[0], radius,sides,true);
                            double[] yPoints = shapeUtils.getPolygonSides(startX[0], startY[0], radius,sides,false);
                            graphicsContext.strokePolygon(xPoints, yPoints, sides);
                            canvasEventLog = "Polygon with " + Integer.toString(sides) + " and a center at " + Integer.toString((int)startX[0]) + "," + Integer.toString((int)startY[0]) + " drawn";
                        }

                        else if (comboBox.getValue() == "Line") {
                            // Line just needs the start X&Y and end X&Y
                            graphicsContext.strokeLine(startX[0], startY[0], event.getX(), event.getY());
                            lastShapeX = startX[0];
                            lastShapeY = startY[0];
                            lastShapeW = event.getX();
                            lastShapeH = event.getY();
                            canvasEventLog = "Line drawn from " + Integer.toString((int)startX[0]) + "," + Integer.toString((int)startY[0]) + " to "
                                    + Integer.toString((int)event.getX()) + "," + Integer.toString((int)event.getY());
                        }
                        if (event.getButton() == MouseButton.SECONDARY){
                            graphicsContext.drawImage(startState, 0, 0);
                            Rectangle2D bound = new Rectangle2D(lastCopyX, lastCopyY, lastCopyW, lastCopyH);
                            SnapshotParameters params = new SnapshotParameters();
                            params.setViewport(bound);
                            params.setFill(Color.TRANSPARENT);
                            clipBoard = canvas.snapshot(params, null);
                            canvasEventLog = "Area selected from " + Integer.toString((int)startX[0]) + "," + Integer.toString((int)startY[0]) + " to "
                                    + Integer.toString((int)event.getX()) + "," + Integer.toString((int)event.getY());
                        }
                    }
                });
        clearCanvas.setOnAction(new EventHandler<ActionEvent>() {
            // Clear the canvas
            public void handle(ActionEvent e) {
                // Make an alert asking the user to confirm they want to clear
                Alert areYouSureAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to clear this canvas?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = areYouSureAlert.showAndWait();
                if (areYouSureAlert.getResult() == ButtonType.YES) {
                    // If they said yes clear the canvas
                    graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    Image newBlank = undoRedoUtils.getSnapshot(canvas);
                    graphicsContext.drawImage(newBlank, 0, 0);
                } else {
                    // if they said no close the dialog box
                    e.consume();
                }
                canvasEventLog = "Canvas cleared";
            }
        });
        undoButton.setOnAction(new EventHandler<ActionEvent>() {
            // Work in progress
            public void handle(ActionEvent e) {
                undoRedoUtils.undo(undoStack, redoStack, canvas, graphicsContext, widthText, heightText);
                canvasEventLog = "Undo used";
            }
        });
        redoButton.setOnAction(new EventHandler<ActionEvent>() {
            // Work in progress
            public void handle(ActionEvent e) {
                undoRedoUtils.redo(undoStack, redoStack, canvas, graphicsContext, widthText, heightText);
                canvasEventLog = "Redo used";
            }
        });
        fillButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                // Fill the last thing drawn
                fillDrawing(fillColorPicker.getValue());
                canvasEventLog = "Fill tool used with the color" + fillColorPicker.toString();
            }
        });
        pasteButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                comboBox.setValue("None");
                cutButton.setSelected(false);
            }
        });
        cutButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                comboBox.setValue("None");
                pasteButton.setSelected(false);
                graphicsContext.setLineWidth(0);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.setLineDashes(0);
                graphicsContext.setStroke(Color.WHITE);
                graphicsContext.setFill(Color.WHITE);
                graphicsContext.strokeRect(lastCopyX, lastCopyY, lastCopyW, lastCopyH);
                graphicsContext.fillRect(lastCopyX, lastCopyY, lastCopyW, lastCopyH);
                undoRedoUtils.saveState(undoStack, redoStack, canvas);
            }
        });
        rotateButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                comboBox.setValue("None");
                undoRedoUtils.saveState(undoStack, redoStack, canvas);
                graphicsContext.setLineWidth(0);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.setLineDashes(0);
                graphicsContext.setStroke(Color.WHITE);
                graphicsContext.setFill(Color.WHITE);
                graphicsContext.strokeRect(lastCopyX, lastCopyY, lastCopyW, lastCopyH);
                graphicsContext.fillRect(lastCopyX, lastCopyY, lastCopyW, lastCopyH);
                graphicsContext.drawImage(startState, 0, 0);
                startState = undoRedoUtils.getSnapshot(canvas);
                graphicsContext.save();
                cutPasteUtils.rotateSection(graphicsContext, rotationAngel, lastCopyX + clipBoard.getWidth() / 2, lastCopyY + clipBoard.getHeight() / 2);
                graphicsContext.drawImage(clipBoard, lastCopyX, lastCopyY);
                graphicsContext.restore();
                canvasEventLog = "Selected area rotated " + Integer.toString((int)rotationAngel)+ " degrees";
                rotationAngel = rotationAngel + 90;
                if (rotationAngel > 270) rotationAngel = 0;
                undoRedoUtils.saveState(undoStack, redoStack, canvas);
            }
        });

        flipButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                undoRedoUtils.saveState(undoStack, redoStack, canvas);
                graphicsContext.setLineWidth(0);
                graphicsContext.setLineDashOffset(0);
                graphicsContext.setLineDashes(0);
                graphicsContext.setStroke(Color.WHITE);
                graphicsContext.setFill(Color.WHITE);
                graphicsContext.strokeRect(lastCopyX, lastCopyY, lastCopyW, lastCopyH);
                graphicsContext.fillRect(lastCopyX, lastCopyY, lastCopyW, lastCopyH);
                graphicsContext.drawImage(startState, 0, 0);
                startState = undoRedoUtils.getSnapshot(canvas);
                graphicsContext.save();
                graphicsContext.drawImage(clipBoard, 0, 0, clipBoard.getWidth(), clipBoard.getHeight(), lastCopyX + lastCopyW,lastCopyY,-clipBoard.getWidth(),clipBoard.getHeight());
                graphicsContext.restore();
                canvasEventLog = "Selected area flipped";
                undoRedoUtils.saveState(undoStack, redoStack, canvas);
            }
        });

        rotateAllButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                comboBox.setValue("None");
                canvas.setRotate(canvas.getRotate()+90);
                widthText.setText(Double.toString(canvas.getHeight()));
                heightText.setText(Double.toString(canvas.getWidth()));
                canvasEventLog = "Canvas flipped";
                undoRedoUtils.saveState(undoStack, redoStack, canvas);
            }
        });

        colorDropper.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                // We Set the tool to none, so we don't accidentally draw when picking a color
                comboBox.setValue("None");
                // run the color grabber function
                colorGrabber();
            }
        });

        scaleButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // take the values in the Height and Width text boxes and scale the canvas to those
                canvas.setHeight(Double.parseDouble(heightText.getText()));
                canvas.setWidth(Double.parseDouble(widthText.getText()));
                undoRedoUtils.saveState(undoStack, redoStack, canvas);
            }
        });
    }

    public void addImage(Image image) {
        undoRedoUtils.saveState(undoStack, redoStack, canvas);
        // Clear the canvas
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        // set the canvas to the size of the image
        canvas.setWidth(image.getWidth());
        canvas.setHeight(image.getHeight());
        // Set the Height and Width text boxes to reflect the new scale
        widthText.setText(Double.toString(image.getWidth()));
        heightText.setText(Double.toString(image.getHeight()));
        // Put the image in upper left of the canvas
        graphicsContext.drawImage(image, 0, 0);
    }


    public Canvas makeNewBlankCanvas() {
        // Make a blank canvas
        canvas.setWidth(1000);
        canvas.setHeight(500);
        Image newBlank = undoRedoUtils.getSnapshot(canvas);
        graphicsContext.drawImage(newBlank, 0, 0);
        return canvas;
    }

    public ToolBar tabToolBar() {
        // Construct the toolbar
        lineColorPicker.setValue(Color.BLACK);
        lineSizeSlider.setShowTickLabels(true);
        lineSizeSlider.setShowTickMarks(true);
        lineSizeSlider.setMajorTickUnit(5);
        lineSizeSlider.setBlockIncrement(5);
        Label lineThicknessNum = new Label();
        lineSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                line_size = lineSizeSlider.getValue();
                lineThicknessNum.setText(Integer.toString((int) line_size));
            }
        });
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "None",
                        "Pen",
                        "Eraser",
                        "Line",
                        "Ellipses",
                        "Circle",
                        "Square",
                        "Rectangle",
                        "Triangle",
                        "Polygon"
                );
        comboBox.getItems().addAll(options);
        comboBox.setValue("None");
        fillColorPicker.setValue(Color.BLACK);
        Separator separator1 = new Separator(Orientation.VERTICAL);
        Separator separator2 = new Separator(Orientation.VERTICAL);
        Separator separator3 = new Separator(Orientation.VERTICAL);
        Separator separator4 = new Separator(Orientation.VERTICAL);
        Separator separator5 = new Separator(Orientation.VERTICAL);
        GridPane selectorTools = new GridPane();
        selectorTools.setHgap(5);
        selectorTools.setVgap(5);
        selectorTools.add(pasteButton,0,0);
        selectorTools.add(cutButton,1,0);
        selectorTools.add(rotateButton,0,1);
        selectorTools.add(flipButton,1,1);
        selectorTools.add(rotateAllButton,2,0);
        Label drawLabel = new Label("Draw Tools");
        Label fillLabel = new Label("Fill Tools");
        Label hLabel = new Label("Height: ");
        Label wLabel = new Label("Width: ");
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(separator1,
                clearCanvas,
                undoButton,
                redoButton,
                separator2,
                selectorTools,
                separator3,
                drawLabel,
                comboBox,
                dashedButton,
                lineColorPicker,
                colorDropper,
                lineSizeSlider,
                lineThicknessNum,
                separator4,
                fillLabel,
                fillButton,
                fillColorPicker,
                separator5,
                hLabel,
                heightText,
                wLabel,
                widthText,
                scaleButton
        );
        return toolBar;
    }

    public WritableImage captureCanvas() {
        // take a screenshot of the canvas and return is as a writable image
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        return writableImage;
    }

    private void fillDrawing(Color color) {
        undoRedoUtils.saveState(undoStack, redoStack, canvas);
        // Set fill to the color of the fill color picker
        graphicsContext.setFill(color);
        // based on the value in the combo box preform the corresponding fill action
        if (comboBox.getValue() == "Pen") {
            graphicsContext.fill();
        }
        else if (comboBox.getValue() == "Ellipses" || comboBox.getValue() == "Circle") {
            graphicsContext.fillOval(lastShapeX, lastShapeY, lastShapeW, lastShapeH);
        }
        else if (comboBox.getValue() == "Rectangle" || comboBox.getValue() == "Square") {
            graphicsContext.fillRect(lastShapeX, lastShapeY, lastShapeW, lastShapeH);
        }
        else if (comboBox.getValue() == "Triangle") {
            double[] xValues = {0, 0, 0};
            double[] yValues = {0, 0, 0};
            // down and to the right
            if (lastShapeX < lastShapeW && lastShapeY > lastShapeH) {
                xValues[0] = lastShapeX;
                xValues[1] = lastShapeW;
                xValues[2] = ((lastShapeW - lastShapeX) / 2)+ lastShapeX;
                yValues[0] = lastShapeY;
                yValues[1] = lastShapeY;
                yValues[2] = lastShapeH;
            }
            // down and to the left
            else if (lastShapeX > lastShapeW && lastShapeY > lastShapeH) {
                xValues[0] = lastShapeX;
                xValues[1] = lastShapeX;
                xValues[2] = lastShapeW;
                yValues[0] = lastShapeY;
                yValues[1] = lastShapeH;
                yValues[2] = ((lastShapeY - lastShapeH) / 2) + lastShapeH;
            }
            // up and to the right
            else if (lastShapeX < lastShapeW && lastShapeY < lastShapeH) {
                xValues[0] = lastShapeX;
                xValues[1] = lastShapeX;
                xValues[2] = lastShapeW;
                yValues[0] = lastShapeY;
                yValues[1] = lastShapeH;
                yValues[2] = ((lastShapeY - lastShapeH) / 2) + lastShapeH;
            }
            // up and to the left
            else {
                xValues[0] = ((lastShapeX - lastShapeW) / 2) + lastShapeW;
                xValues[1] = lastShapeW;
                xValues[2] = lastShapeX;
                yValues[0] = lastShapeH;
                yValues[1] = lastShapeY;
                yValues[2] = lastShapeY;
            }
            graphicsContext.fillPolygon(xValues, yValues, 3);
        }
    }

    private void colorGrabber() {
        // Save the current canvas as a writable image
        WritableImage currentCanvas = captureCanvas();
        // Make a pixel reader for the current canvas
        PixelReader colorReader = currentCanvas.getPixelReader();
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                // If the color dropper is selected get the value of the pixel where the cursor is
                if (colorDropper.isSelected()) {
                    Color colorGrabbed = colorReader.getColor((int) event.getX(), (int) event.getY());
                    lineColorPicker.setValue(colorGrabbed);
                    canvasEventLog = "The color " + colorGrabbed.toString() + " was grabbed from " + Double.toString(event.getX()) + "," + Double.toString(event.getY());
                }
                // If the color dropper is not selected... don't do this anymore
                else {
                    canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
                }
            }
        });
    }

}


