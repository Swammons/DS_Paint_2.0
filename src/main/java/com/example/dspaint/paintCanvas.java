package com.example.dspaint;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
     ToggleButton drawButton;
     ColorPicker lineColorPicker;
     Slider lineSizeSlider;
     double line_size;
     paintCanvas() {
         canvas = new Canvas();
         graphicsContext = canvas.getGraphicsContext2D();
         // Toolbar items
         fillColorPicker = new ColorPicker();
         clearCanvas = new Button("Clear");
         fillButton = new Button("Fill");
         drawButton = new ToggleButton("Draw");
         lineColorPicker = new ColorPicker();
         lineSizeSlider = new Slider(1, 25, 0);
         lineSizeSlider.setShowTickLabels(true);
         lineSizeSlider.setShowTickMarks(true);
         lineSizeSlider.setMajorTickUnit(5);
         lineSizeSlider.setBlockIncrement(5);;
         line_size = 0;



         // Free hand draw controls
          canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                  new EventHandler<MouseEvent>(){

                       public void handle(MouseEvent event) {
                            if(drawButton.isSelected()) {
                                 graphicsContext.setStroke(lineColorPicker.getValue());
                                 graphicsContext.setLineWidth(line_size);
                                 graphicsContext.beginPath();
                                 graphicsContext.moveTo(event.getX(), event.getY());
                                 graphicsContext.stroke();
                            }
                       }
                  });

          canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                  new EventHandler<MouseEvent>(){

                       public void handle(MouseEvent event) {
                            if(drawButton.isSelected()) {
                                 graphicsContext.lineTo(event.getX(), event.getY());
                                 graphicsContext.stroke();
                            }
                       }
                  });

          canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                  new EventHandler<MouseEvent>(){

                       public void handle(MouseEvent event) {
                            if(drawButton.isSelected()) {

                            }

                       }
                  });
         clearCanvas.setOnAction(new EventHandler<ActionEvent>() {

             public void handle(ActionEvent e) {
                 graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
          fillColorPicker.setValue(Color.BLACK);
          Separator separator1 = new Separator(Orientation.VERTICAL);
          Separator separator2 = new Separator(Orientation.VERTICAL);
          Label drawLabel = new Label("Draw Tools");
          Label fillLabel = new Label("Fill Tools");
          ToolBar toolBar = new ToolBar();
          toolBar.getItems().addAll(clearCanvas,
                  separator1,
                  drawLabel,
                  drawButton,
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
        gc.fill();

    }



}
