package com.example.dspaint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import java.util.Stack;

/**
 * A class filled with methods that are used in the Undo/Redo features
 */
public class undoRedoUtils {
    /**
     * Clear the redo stack and push a snapshot of the canvas on the undo stack
     * @param undoStack Where the snapshot is pushed
     * @param redoStack Stack that is cleared
     * @param canvas Canvas that the snapshot is taken of
     */
    static void saveState(SizedStack undoStack, SizedStack redoStack, Canvas canvas){
        redoStack.clear();
        undoStack.push(getSnapshot(canvas));
    }

    /**
     * Undoes the users last action
     * Takes a snapshot of the users canvas to push to the redo stack. Then pop from the undo stack make that the canvas
     * @param undoStack Stack to pop from
     * @param redoStack Stack to push to
     * @param canvas Canvas to be snapshot
     * @param graphicsContext Where the popped image will be drawn
     * @param widthText The text displaying the width of the canvas to the user
     * @param heightText The text displaying the height of the canvas to the user
     */
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

    /**
     * Redoes the users last Undo
     * Pushes a snapshot of the canvas to the undo stack and pops an image from the redo stack that become the new canvas
     * @param undoStack Stack to be pushed to
     * @param redoStack Stack to pop from
     * @param canvas Canvas that will be snapshot
     * @param graphicsContext Where the popped image will be drawn
     * @param widthText The text displaying the width of the canvas to the user
     * @param heightText The text displaying the height of the canvas to the user
     */
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

    /**
     * takes a snapshot of the canvas and returns an image
     * @param canvas Canvas that will be snapshot
     * @return Image of the canvas
     */
    public static Image getSnapshot(Canvas canvas) {
        // take a screenshot of the canvas and return is as an image
        return canvas.snapshot(null,null);
    }

    /**
     * Creates a stack with a limited size. If size is exceeded then the item at the bottom of the stack will be deleted.
     */
    public static class SizedStack<T> extends Stack<T> {
        // This class is used to ensure that the Stacks can not get to big and waste RAM
        private int maxSize;

        /**
         * Constructor for SizedStack
         * @param size max size of the stack
         */
        public SizedStack(int size) {
            super();
            this.maxSize = size;
        }

        /**
         * Overrides the push method of Stack
         * Removed bottom of stack if max size is exceeded when pushing a new element.
         * @param object   the item to be pushed onto this stack.
         * @return the original push method with the object
         */
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
