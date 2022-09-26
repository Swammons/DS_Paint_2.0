package com.example.dspaint;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.geom.Arc2D;

public class paintCanvas {
     Canvas canvas;
     GraphicsContext graphicsContext;
     ColorPicker fillColorPicker;
     Button clearCanvas;
     Button fillButton;
     Button undoButton;
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
     paintCanvas() {
         // initialize variables
         canvas = new Canvas();
         graphicsContext = canvas.getGraphicsContext2D();
         // Toolbar items
         fillColorPicker = new ColorPicker();
         clearCanvas = new Button("Clear");
         fillButton = new Button("Fill");
         undoButton = new Button("Undo");
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
                  new EventHandler<MouseEvent>(){

                       public void handle(MouseEvent event) {
                           // Set the line size and color to what the toolbar has set
                           graphicsContext.setStroke(lineColorPicker.getValue());
                           graphicsContext.setLineWidth(line_size);
                           // If dash mode is selected then set the dashes
                           if(dashedButton.isSelected()){
                               // If the line size is 0 make it 1 (This is for when we scale dashes the line size)
                               if(line_size == 0){
                                   line_size = 1;
                               }
                               // Set the dash offset and the dashes to a proportion of the line size
                               graphicsContext.setLineDashOffset(2.5*line_size);
                               graphicsContext.setLineDashes(5*line_size);
                           }
                           else{
                               // reset the dash settings to 0 to draw normal
                               graphicsContext.setLineDashOffset(0);
                               graphicsContext.setLineDashes(0);
                           }
                           // If pen is selected then start making a path at the cursor position
                            if(comboBox.getValue()=="Pen") {
                                 graphicsContext.beginPath();
                                 graphicsContext.moveTo(event.getX(), event.getY());
                                 graphicsContext.stroke();
                            }
                            // Any tool other than the pen save the cursor position
                            else if (comboBox.getValue()=="Ellipses"||comboBox.getValue()=="Circle"||comboBox.getValue()=="Square"||comboBox.getValue()=="Rectangle"||comboBox.getValue()=="Line") {
                                startX[0] = event.getX();
                                startY[0] = event.getY();
                            }


                       }
                  });

          canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                  new EventHandler<MouseEvent>(){

                       public void handle(MouseEvent event) {
                           // If pen draw a line along the path to the current cursor position
                            if(comboBox.getValue()=="Pen") {
                                 graphicsContext.lineTo(event.getX(), event.getY());
                                 graphicsContext.stroke();
                            }
                            // Any tool other than the pen do nothing
                            else if (comboBox.getValue()=="Ellipses"||comboBox.getValue()=="Circle"||comboBox.getValue()=="Square"||comboBox.getValue()=="Rectangle"||comboBox.getValue()=="Line") {

                            }

                       }
                  });

          canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                  new EventHandler<MouseEvent>(){

                       public void handle(MouseEvent event) {
                           // If pen do nothing
                            if(comboBox.getValue()=="Pen") {

                            }
                            // For each of the shapes we address 4 different directions that the user could drag
                            else if (comboBox.getValue()=="Ellipses") {
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
                                    graphicsContext.strokeOval(startX[0], event.getY(), event.getX() - startX[0], startY[0]-event.getY());
                                    lastShapeX = startX[0];
                                    lastShapeY = event.getY();
                                    lastShapeW = event.getX() - startX[0];
                                    lastShapeH = startY[0]-event.getY();
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
                            else if (comboBox.getValue()=="Rectangle") {
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
                                    graphicsContext.strokeRect(event.getX(), startY[0], startX[0] - event.getX(), event.getY()-startY[0]);
                                    lastShapeX = event.getX();
                                    lastShapeY = startY[0];
                                    lastShapeW = startX[0] - event.getX();
                                    lastShapeH = event.getY() - startY[0];
                                }
                                // up and to the right
                                else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                    graphicsContext.strokeRect(startX[0], event.getY(), event.getX() - startX[0], startY[0]-event.getY());
                                    lastShapeX = startX[0];
                                    lastShapeY = event.getY();
                                    lastShapeW = event.getX() - startX[0];
                                    lastShapeH = startY[0]-event.getY();
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
                            else if (comboBox.getValue()=="Square") {
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
                            else if (comboBox.getValue()=="Circle") {
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
                            else if (comboBox.getValue()=="Line") {
                                // Line just needs the start X&Y and end X&Y
                                    graphicsContext.strokeLine(startX[0], startY[0], event.getX(), event.getY());
                                    lastShapeX = startX[0];
                                    lastShapeY = startY[0];
                                    lastShapeW = event.getX();
                                    lastShapeH = event.getY();
                            }
                       }
                  });
         clearCanvas.setOnAction(new EventHandler<ActionEvent>() {
            // Clear the canvas
             public void handle(ActionEvent e) {
                 graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
             }
         });
         undoButton.setOnAction(new EventHandler<ActionEvent>() {
            // Work in progress
             public void handle(ActionEvent e) {

             }
         });
         fillButton.setOnAction(new EventHandler<ActionEvent>() {

             public void handle(ActionEvent e) {
                 // Fill the last thing drawn
                 fillDrawing(graphicsContext, fillColorPicker.getValue());
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
             }
         });
     }

     public void pasteImage(Image image){
          // Clear the canvas
          graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
          // set the canvas to the size of the image
          canvas.setWidth(image.getWidth());
          canvas.setHeight(image.getHeight());
          // Set the Height and Width text boxes to reflect the new scale
          widthText.setText(Double.toString(image.getWidth()));
          heightText.setText(Double.toString(image.getHeight()));
          // Put the image in upper left of the canvas
          graphicsContext.drawImage(image,0,0);
     }

    public Canvas makeNewBlankCanvas(){
         // Make a blank canvas
        canvas.setWidth(2000);
        canvas.setHeight(1000);
        return canvas;
    }

     public ToolBar tabToolBar(){
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
                    lineThicknessNum.setText(Integer.toString((int)line_size));
               }
          });
         ObservableList<String> options =
                 FXCollections.observableArrayList(
                         "None",
                         "Pen",
                         "Line",
                         "Ellipses",
                         "Circle",
                         "Square",
                         "Rectangle"

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

     public WritableImage captureCanvas(){
         // take a screenshot of the canvas and return is as a writable image
         WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
         canvas.snapshot(null, writableImage);
         return writableImage;
     }

    private void fillDrawing(GraphicsContext gc, Color color){
        // Set fill to the color of the fill color picker
        gc.setFill(color);
        // based on the value in the combo box preform the corresponding fill action
        if(comboBox.getValue()=="Pen"||comboBox.getValue()=="Dashed Pen") {
            gc.fill();
        }
        else if (comboBox.getValue()=="Ellipses" || comboBox.getValue()=="Circle"){
            gc.fillOval(lastShapeX, lastShapeY, lastShapeW, lastShapeH);
        }
        else if (comboBox.getValue()=="Rectangle" || comboBox.getValue()=="Square") {
            gc.fillRect(lastShapeX, lastShapeY, lastShapeW, lastShapeH);
        }
    }

    private void colorGrabber(){
         // Save the current canvas as a writable image
         WritableImage currentCanvas = captureCanvas();
         // Make a pixel reader for the current canvas
         PixelReader colorReader = currentCanvas.getPixelReader();
         canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,new EventHandler<MouseEvent>(){

             public void handle(MouseEvent event) {
                // If the color dropper is selected get the value of the pixel where the cursor is
                if(colorDropper.isSelected()) {
                    Color colorGrabbed = colorReader.getColor((int) event.getX(), (int) event.getY());
                    lineColorPicker.setValue(colorGrabbed);
                }
                // If the color dropper is not selected... don't do this anymore
                else {
                    canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED,this);
                }
             }
         });


    }




}
