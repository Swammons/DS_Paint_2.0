package com.example.dspaint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import java.util.Stack;

public class undoRedoUtils {
    static void saveState(SizedStack undoStack, SizedStack redoStack, Canvas canvas){
        redoStack.clear();
        undoStack.push(getSnapshot(canvas));
    }

    public static void undo(SizedStack undoStack, SizedStack redoStack, Canvas canvas, GraphicsContext graphicsContext, TextField widthText, TextField heightText){
        if(!undoStack.isEmpty()) {
            // get an image of the canvas to add to the redo array
            Image redo = getSnapshot(canvas);
            // pop the first image of the undo array
            redoStack.push(redo);
            Image undoImage = (Image) undoStack.pop();
            // scale the canvas to the size of the new image
            canvas.setHeight(undoImage.getHeight());
            canvas.setWidth(undoImage.getWidth());
            // also set the height width text fields
            widthText.setText(Double.toString(undoImage.getWidth()));
            heightText.setText(Double.toString(undoImage.getHeight()));
            // add the popped image
            graphicsContext.drawImage(undoImage, 0, 0);
        }
    }

    public static void redo(SizedStack undoStack, SizedStack redoStack, Canvas canvas, GraphicsContext graphicsContext, TextField widthText, TextField heightText) {
        if (!redoStack.isEmpty()) {
            // get an image of the canvas to add to the undo array
            Image undo = getSnapshot(canvas);
            undoStack.push(undo);
            // pop the first image of the redo array
            Image redoImage = (Image) redoStack.pop();
            // scale the canvas to the size of the new image
            canvas.setHeight(redoImage.getHeight());
            canvas.setWidth(redoImage.getWidth());
            // also set the height width text fields
            widthText.setText(Double.toString(redoImage.getWidth()));
            heightText.setText(Double.toString(redoImage.getHeight()));
            // add the popped image
            graphicsContext.drawImage(redoImage, 0, 0);
        }
    }

    public static Image getSnapshot(Canvas canvas) {
        // take a screenshot of the canvas and return is as an image
        return canvas.snapshot(null,null);
    }

    public static class SizedStack<T> extends Stack<T> {
        // This class is used to ensure that the Stacks can not get to big and waste RAM
        private int maxSize;

        public SizedStack(int size) {
            super();
            this.maxSize = size;
        }

        @Override
        public T push(T object) {
            //If the stack is too big, remove elements until it's the right size.
            while (this.size() >= maxSize) {
                this.remove(0);
            }
            return super.push(object);
        }
    }

}
