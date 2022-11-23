package com.example.dspaint;

import javafx.scene.control.TextInputDialog;
import org.junit.jupiter.api.Test;

import java.util.jar.JarEntry;

import static org.junit.jupiter.api.Assertions.*;

class shapeUtilsTest {

    @Test
    void getPolygonSides() {
         double[] testAnswers = {0, 15, 15};
         double[] testGuess = shapeUtils.getPolygonSides(10, 10, 10,3, false);
         for(int i = 0; i < 3; i++){
             testGuess[i] = Math.round(testGuess[i]);
         }
         assertArrayEquals(testAnswers, testGuess);
    }

    @Test
    void PythagoreanTheorem() {
        double[] testAnswers = {3.61};
        double[] testGuess = {shapeUtils.PythagoreanTheorem(2,3)};
        testGuess[0] = Math.round(testGuess[0] * 100);
        testGuess[0] = testGuess[0]/100;
        System.out.println(testGuess[0]);
        assertArrayEquals(testAnswers, testGuess);
    }

    @Test
    void triangleHalfWayPoint() {
        double[] testAnswers = {4};
        double[] testGuess = {shapeUtils.triangleHalfWayPoint(3,5)};
        assertArrayEquals(testAnswers, testGuess);
    }

}