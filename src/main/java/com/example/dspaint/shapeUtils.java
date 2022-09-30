package com.example.dspaint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;

public class shapeUtils {

    public static void strokeTriangle(double startX, double startY, double xMouse, double yMouse, GraphicsContext graphicsContext) {
        double[] xValues = {0, 0, 0};
        double[] yValues = {0, 0, 0};
        // Up and to the left
        if (startX < xMouse && startY > yMouse) {
            xValues[0] = startX;
            xValues[1] = xMouse;
            xValues[2] = ((xMouse - startX) / 2)+ startX;
            yValues[0] = startY;
            yValues[1] = startY;
            yValues[2] = yMouse;
        }
        // down and to the right
        else if (startX > xMouse && startY > yMouse) {
            xValues[0] = xMouse;
            xValues[1] = startX;
            xValues[2] = ((startX - xMouse) / 2) + xMouse;
            yValues[0] = startY;
            yValues[1] = startY;
            yValues[2] = yMouse;
        }
        // up and to the right
        else if (startX < xMouse && startY < yMouse) {
            xValues[0] = ((xMouse - startX) / 2) + startX;
            xValues[1] = startX;
            xValues[2] = xMouse;
            yValues[0] = yMouse;
            yValues[1] = startY;
            yValues[2] = startY;
        }
        // Down and to the left
        else {
            xValues[0] = ((startX - xMouse) / 2) + xMouse;
            xValues[1] = xMouse;
            xValues[2] = startX;
            yValues[0] = yMouse;
            yValues[1] = startY;
            yValues[2] = startY;
        }
        graphicsContext.strokePolygon(xValues, yValues, 3);
    }

    public static double[] getPolygonSides(double centerX, double centerY, double radius, int sides, boolean x) {
        double[] returnX = new double[sides];
        double[] returnY = new double[sides];
        final double angleStep = Math.PI * 2 / sides;
        // assumes one point is located directly beneath the center point
        double angle = 0;
        for (int i = 0; i < sides; i++, angle += angleStep) {
            //draws rightside-up; to change, change multiple of angle
            // x coordinate of the corner
            returnX[i] = -1 * Math.sin(angle) * radius + centerX;
            // y coordinate of the corner
            returnY[i] = -1 * Math.cos(angle) * radius + centerY;
        }
        if(x)
            return returnX;
        else
            return returnY;
    }

    public static int askForSides(){
        // default to 3 sides
        final int[] sides = {3};
        TextInputDialog td = new TextInputDialog("3");
        td.setHeaderText("Enter Number of sides");
        // show the text input dialog
        td.showAndWait();
        sides[0] = Integer.parseInt(td.getEditor().getText());
        return sides[0];
    }
}
