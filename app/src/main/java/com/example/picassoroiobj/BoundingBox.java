package com.example.picassoroiobj;

public class BoundingBox {
    private float left;
    private float top;
    private float right;
    private float bottom;

    public BoundingBox(float left, float top, float right, float bottom) {

        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

    }

    public float getLeft() {
        return left;
    }


    public float getTop() {
        return top;
    }


    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }
}
