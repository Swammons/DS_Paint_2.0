package com.example.dspaint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;

/**
 * A class filled with methods that are used in cut-and-paste features
 */
public class cutPasteUtils {
    /**
     * Rotates a selected section of the canvas
     * @param gc graphics context of the canvas that part will be rotated
     * @param angle what angle that the selected area will be rotated to
     * @param px x coordinate of the point that area will be rotated around
     * @param py y coordinate of the point that area will be rotated around
     */
    static void rotateSection(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }
}
