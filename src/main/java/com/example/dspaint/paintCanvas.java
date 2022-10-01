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
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.security.cert.PolicyNode;
import java.util.Optional;
import java.util.Stack;

import static java.lang.Math.*;
import static java.sql.DriverManager.println;

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
    Slider lineSizeSlider;

    TextField heightText;
    TextField widthText;
    Button scaleButton;
    double line_size;
    double lastShapeX;
    double lastShapeY;
    double lastShapeW;
    double lastShapeH;
    undoRedoUtils.SizedStack<Image> undoStack;
    undoRedoUtils.SizedStack<Image> redoStack;

    Image startState;
    Image clipBoard;

    paintCanvas() {
        // initialize variables
        canvas = new Canvas();
        graphicsContext = canvas.getGraphicsContext2D();
        undoStack = new undoRedoUtils.SizedStack<>(25);
        redoStack = new undoRedoUtils.SizedStack<>(25);
        // Toolbar items
        fillColorPicker = new ColorPicker();
        clearCanvas = new Button("Clear");
        fillButton = new Button("Fill");
        undoButton = new Button("Undo");
        redoButton = new Button("Redo");
        dashedButton = new ToggleButton("Dashed");
        comboBox = new ComboBox();
        lineColorPicker = new ColorPicker();
        colorDropper = new ToggleButton("Color Grab");
        lineSizeSlider = new Slider(1, 25, 1);
        lineSizeSlider.setShowTickLabels(true);
        lineSizeSlider.setShowTickMarks(true);
        lineSizeSlider.setMajorTickUnit(5);
        lineSizeSlider.setBlockIncrement(5);
        line_size = 0;
        heightText = new TextField("1000");
        heightText.setMaxWidth(50);
        widthText = new TextField("2000");
        widthText.setMaxWidth(50);
        scaleButton = new Button("Scale Canvas");
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
                        // If pen is selected then start making a path at the cursor position
                        if (comboBox.getValue() == "Pen") {
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
                        else if (comboBox.getValue() == "Ellipses" || comboBox.getValue() == "Circle" || comboBox.getValue() == "Square" || comboBox.getValue() == "Rectangle" || comboBox.getValue() == "Line" || comboBox.getValue() == "Triangle" || comboBox.getValue() == "Polygon") {
                            startX[0] = event.getX();
                            startY[0] = event.getY();
                            startState = undoRedoUtils.getSnapshot(canvas);
                        }
                        if (event.getButton() == MouseButton.SECONDARY) {
                            startX[0] = event.getX();
                            startY[0] = event.getY();
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
                        // If pen draw a line along the path to the current cursor position
                        if (comboBox.getValue() == "Pen") {
                            graphicsContext.lineTo(event.getX(), event.getY());
                            graphicsContext.stroke();
                        }
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
                        }

                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>() {

                    public void handle(MouseEvent event) {
                        // If pen do nothing
                        if (comboBox.getValue() == "Pen") {

                        }
                        else if(comboBox.getValue() == "Eraser"){

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
                        }
                        else if (comboBox.getValue() == "Triangle") {
                            shapeUtils.strokeTriangle(startX[0], startY[0], event.getX(),event.getY(),graphicsContext);
                            lastShapeX = startX[0];
                            lastShapeY = startY[0];
                            lastShapeW = event.getX();
                            lastShapeH = event.getY();
                        }
                        else if (comboBox.getValue() == "Polygon") {

                            int sides = shapeUtils.askForSides();
                            double radius = sqrt(pow(abs(startX[0]-event.getX()),2)+pow(abs(startY[0]-event.getY()),2));
                            double[] xPoints = shapeUtils.getPolygonSides(startX[0], startY[0], radius,sides,true);
                            double[] yPoints = shapeUtils.getPolygonSides(startX[0], startY[0], radius,sides,false);
                            graphicsContext.strokePolygon(xPoints, yPoints, sides);

                        }

                        else if (comboBox.getValue() == "Line") {
                            // Line just needs the start X&Y and end X&Y
                            graphicsContext.strokeLine(startX[0], startY[0], event.getX(), event.getY());
                            lastShapeX = startX[0];
                            lastShapeY = startY[0];
                            lastShapeW = event.getX();
                            lastShapeH = event.getY();
                        }
                        else if (comboBox.getValue() == "Paste"){
                            graphicsContext.drawImage(clipBoard, event.getX(), event.getY());
                        }
                        if (event.getButton() == MouseButton.SECONDARY){
                            graphicsContext.drawImage(startState, 0, 0);
                            Rectangle2D bound = new Rectangle2D(lastShapeX, lastShapeY, lastShapeW, lastShapeH);
                            SnapshotParameters params = new SnapshotParameters();
                            params.setViewport(bound);
                            params.setFill(Color.TRANSPARENT);
                            clipBoard = canvas.snapshot(params, null);
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
            }
        });
        undoButton.setOnAction(new EventHandler<ActionEvent>() {
            // Work in progress
            public void handle(ActionEvent e) {
                undoRedoUtils.undo(undoStack, redoStack, canvas, graphicsContext, widthText, heightText);
            }
        });
        redoButton.setOnAction(new EventHandler<ActionEvent>() {
            // Work in progress
            public void handle(ActionEvent e) {
                undoRedoUtils.redo(undoStack, redoStack, canvas, graphicsContext, widthText, heightText);
            }
        });
        fillButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                // Fill the last thing drawn
                fillDrawing(fillColorPicker.getValue());
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

    public void pasteImage(Image image) {
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
                        "Polygon",
                        "Paste"

                );
        comboBox.getItems().addAll(options);
        comboBox.setValue("None");
        fillColorPicker.setValue(Color.BLACK);
        Separator separator1 = new Separator(Orientation.VERTICAL);
        Separator separator2 = new Separator(Orientation.VERTICAL);
        Separator separator3 = new Separator(Orientation.VERTICAL);
        Separator separator4 = new Separator(Orientation.VERTICAL);
        Label drawLabel = new Label("Draw Tools");
        Label fillLabel = new Label("Fill Tools");
        Label hLabel = new Label("Height: ");
        Label wLabel = new Label("Width: ");
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(separator3,
                clearCanvas,
                undoButton,
                redoButton,
                separator1,
                drawLabel,
                comboBox,
                dashedButton,
                lineColorPicker,
                colorDropper,
                lineSizeSlider,
                lineThicknessNum,
                separator2,
                fillLabel,
                fillButton,
                fillColorPicker,
                separator4,
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
                xValues[0] = lastShapeW;
                xValues[1] = lastShapeX;
                xValues[2] = ((lastShapeX - lastShapeW) / 2) + lastShapeW;
                yValues[0] = lastShapeY;
                yValues[1] = lastShapeY;
                yValues[2] = lastShapeH;
            }
            // up and to the right
            else if (lastShapeX < lastShapeW && lastShapeY < lastShapeH) {
                xValues[0] = ((lastShapeW - lastShapeX) / 2) + lastShapeX;
                xValues[1] = lastShapeX;
                xValues[2] = lastShapeW;
                yValues[0] = lastShapeH;
                yValues[1] = lastShapeY;
                yValues[2] = lastShapeY;
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
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                // If the color dropper is selected get the value of the pixel where the cursor is
                if (colorDropper.isSelected()) {
                    Color colorGrabbed = colorReader.getColor((int) event.getX(), (int) event.getY());
                    lineColorPicker.setValue(colorGrabbed);
                }
                // If the color dropper is not selected... don't do this anymore
                else {
                    canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
                }
            }
        });


    }

}


