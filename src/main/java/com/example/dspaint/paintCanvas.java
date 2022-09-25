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
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

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
     Slider lineSizeSlider;
     double line_size;
     double lastShapeX;
     double lastShapeY;
     double lastShapeW;
     double lastShapeH;
     paintCanvas() {
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
         lineSizeSlider = new Slider(1, 25, 1);
         lineSizeSlider.setShowTickLabels(true);
         lineSizeSlider.setShowTickMarks(true);
         lineSizeSlider.setMajorTickUnit(5);
         lineSizeSlider.setBlockIncrement(5);;
         line_size = 0;
         final double[] startX = new double[1];
         final double[] startY = new double[1];



         // Free hand draw controls
          canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                  new EventHandler<MouseEvent>(){

                       public void handle(MouseEvent event) {
                           graphicsContext.setStroke(lineColorPicker.getValue());
                           graphicsContext.setLineWidth(line_size);
                           if(dashedButton.isSelected()){
                               if(line_size == 0){
                                   line_size = 1;
                               }
                               graphicsContext.setLineDashOffset(2.5*line_size);
                               graphicsContext.setLineDashes(5*line_size);
                           }
                           else{
                               graphicsContext.setLineDashOffset(0);
                               graphicsContext.setLineDashes(0);
                           }
                            if(comboBox.getValue()=="Pen") {
                                 graphicsContext.beginPath();
                                 graphicsContext.moveTo(event.getX(), event.getY());
                                 graphicsContext.stroke();
                            }
                            else if (comboBox.getValue()=="Ellipses"||comboBox.getValue()=="Circle"||comboBox.getValue()=="Square"||comboBox.getValue()=="Rectangle"||comboBox.getValue()=="Line") {
                                startX[0] = event.getX();
                                startY[0] = event.getY();
                            }


                       }
                  });

          canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                  new EventHandler<MouseEvent>(){

                       public void handle(MouseEvent event) {
                            if(comboBox.getValue()=="Pen") {
                                 graphicsContext.lineTo(event.getX(), event.getY());
                                 graphicsContext.stroke();
                            }
                            else if (comboBox.getValue()=="Ellipses"||comboBox.getValue()=="Circle"||comboBox.getValue()=="Square"||comboBox.getValue()=="Rectangle"||comboBox.getValue()=="Line") {

                            }

                       }
                  });

          canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                  new EventHandler<MouseEvent>(){

                       public void handle(MouseEvent event) {
                            if(comboBox.getValue()=="Pen") {

                            }
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
                                    graphicsContext.strokeRect(event.getX(), startY[0], startX[0] - event.getX(), event.getY()-startX[0]);
                                    lastShapeX = event.getX();
                                    lastShapeY = startY[0];
                                    lastShapeW = startX[0] - event.getX();
                                    lastShapeH = event.getX() - startX[0];
                                }
                                // up and to the right
                                else if (startX[0] < event.getX() && startY[0] > event.getY()) {
                                    graphicsContext.strokeRect(startX[0], event.getY(), event.getX() - startX[0], startX[0]-event.getX());
                                    lastShapeX = startX[0];
                                    lastShapeY = event.getY();
                                    lastShapeW = event.getX() - startX[0];
                                    lastShapeH = startX[0]-event.getX();
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

             public void handle(ActionEvent e) {
                 graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
             }
         });
         undoButton.setOnAction(new EventHandler<ActionEvent>() {

             public void handle(ActionEvent e) {

             }
         });
         fillButton.setOnAction(new EventHandler<ActionEvent>() {

             public void handle(ActionEvent e) {
                 fillDrawing(graphicsContext, fillColorPicker.getValue());
             }
         });
     }

     public Canvas makeNewCanvas(Image image){
          canvas.setWidth(image.getWidth());
          canvas.setHeight(image.getHeight());
          graphicsContext.drawImage(image,0,0);
          return canvas;
     }

    public Canvas makeNewBlankCanvas(){
        canvas.setWidth(2000);
        canvas.setHeight(1000);
        return canvas;
    }

     public ToolBar tabToolBar(){
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
          Label drawLabel = new Label("Draw Tools");
          Label fillLabel = new Label("Fill Tools");
          ToolBar toolBar = new ToolBar();
          toolBar.getItems().addAll(separator3,
                  clearCanvas,
                  undoButton,
                  separator1,
                  drawLabel,
                  comboBox,
                  dashedButton,
                  lineColorPicker,
                  lineSizeSlider,
                  lineThicknessNum,
                  separator2,
                  fillLabel,
                  fillButton,
                  fillColorPicker);
          return toolBar;
     }

     public WritableImage captureCanvas(){
         WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
         canvas.snapshot(null, writableImage);
         return writableImage;
     }

    private void fillDrawing(GraphicsContext gc, Color color){
        gc.setFill(color);
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




}
