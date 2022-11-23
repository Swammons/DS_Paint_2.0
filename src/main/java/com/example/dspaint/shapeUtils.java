package com.example.dspaint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;

import java.nio.file.Paths;

import static java.lang.Math.*;

/**
 * A class filled with methods that are used shape drawing features
 */
public class shapeUtils {

    /**
     * Strokes a triangle on the canvas based on users mouse position
     * @param startX X coordinate for where the mouse was initially pressed
     * @param startY Y coordinate for where the mouse was initially pressed
     * @param xMouse X coordinate for the current location of the cursor
     * @param yMouse Y coordinate for the current location of the cursor
     * @param graphicsContext Where the Triangle will be drawn
     */
    public static void strokeTriangle(double startX, double startY, double xMouse, double yMouse, GraphicsContext graphicsContext) {
        double[] xValues = {0, 0, 0};
        double[] yValues = {0, 0, 0};
        // down and to the left
        if (startX < xMouse && startY > yMouse) {
            xValues[0] = startX;
            xValues[1] = xMouse;
            xValues[2] = triangleHalfWayPoint(xMouse, startX);
            yValues[0] = startY;
            yValues[1] = startY;
            yValues[2] = yMouse;
        }
        // up and to the left
        else if (startX > xMouse && startY > yMouse) {
            xValues[0] = startX;
            xValues[1] = startX;
            xValues[2] = xMouse;
            yValues[0] = startY;
            yValues[1] = yMouse;
            yValues[2] = ((yMouse - startY) / 2) + startY;
        }
        // down and to the right
        else if (startX < xMouse && startY < yMouse) {
            xValues[0] = startX;
            xValues[1] = startX;
            xValues[2] = xMouse;
            yValues[0] = startY;
            yValues[1] = yMouse;
            yValues[2] = ((yMouse - startY) / 2) + startY;
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

    /**
     * Take in a numbered of sides and the center coordinates and returns an array of either the X or Y coordinates
     * of the vertexes of a polygon with the given number of sides depending on the value of the bool X
     * @param centerX X coordinate of the center of the polygon
     * @param centerY Y coordinate of the center of the polygon
     * @param radius distance from the center that the points need to be
     * @param sides number of sides that the polygon will have
     * @param x Is it returning X(true) or Y(false)
     * @return Array of either the X or Y coordinates for the points of the vertexes of the polygon
     */
    public static double[] getPolygonSides(double centerX, double centerY, double radius, int sides, boolean x) {
        double[] returnX = new double[sides];
        double[] returnY = new double[sides];
        final double angleStep = Math.PI * 2 / sides;
        // assumes one point is located directly beneath the center point
        double angle = 0;
        for (int i = 0; i < sides; i++, angle += angleStep) {
            //draws right-side-up; to change, change multiple of angle
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

    /**
     * Create a TextInputDialog box that the users input a number in to.
     * @return The number the user entered into the TextInputDialog box
     */
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

    /**
     * Takes in a X and Y length and returns the length of the hypotenuse
     * @param xLength length of the x dimension
     * @param yLength length of the y detention
     * @return length of the hypotenuse
     */
    public static double PythagoreanTheorem(double xLength, double yLength){
        // default to 3 sides
        return sqrt(pow(abs(xLength), 2)+ pow(abs(yLength), 2));
    }

    /**
     * Returns the value halfway between X and Y
     * @param x1 larger point
     * @param x2 smaller point
     * @return the value halfway between x and y
     */
    public static double triangleHalfWayPoint(double x1, double x2){
        return ((x1 - x2) / 2) + x2;
    }

    /**
     * Returns the path to where this project is stored up to src
     * @return the path to where this project is stored up to src
     */
    public static String getImageDir(){
        String currDir = Paths.get("").toAbsolutePath().toString();
        currDir = currDir.replaceAll("file:/","");
        return currDir;
    }
}

