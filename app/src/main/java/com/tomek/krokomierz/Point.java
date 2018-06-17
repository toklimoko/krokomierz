package com.tomek.krokomierz;

public class Point {


    private float aX;
    private float aY;
    private float aZ;
    private double aT;

    public Point(float aX, float aY, float aZ, double aT) {
        this.aX = aX;
        this.aY = aY;
        this.aZ = aZ;
        this.aT = aT;
    }

    public float getaX() {
        return aX;
    }

    public void setaX(float aX) {
        this.aX = aX;
    }

    public float getaY() {
        return aY;
    }

    public void setaY(float aY) {
        this.aY = aY;
    }

    public float getaZ() {
        return aZ;
    }

    public void setaZ(float aZ) {
        this.aZ = aZ;
    }

    public double getaT() {
        return aT;
    }

    public void setaT(double aT) {
        this.aT = aT;
    }

    @Override
    public String toString() {
        return "T:" + aT + "\t" +
                "X:" + aX + "\t" +
                "Y:" + aY + "\t" +
                "Z:" + aZ + "\n";
    }
}