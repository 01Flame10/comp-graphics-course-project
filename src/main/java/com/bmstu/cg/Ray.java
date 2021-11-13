package com.bmstu.cg;


public class Ray {
    public Vector4 origin;
    public Vector4 direction;

    public Ray() {
        origin = new Vector4(0, 0, 0);
        direction = new Vector4(1, 0, 0);
    }

    public Ray(Vector4 o, Vector4 d) {
        origin = o;
        direction = d;
    }

    public Vector4 getRayOrigin() {
        return origin;
    }

    public Vector4 getRayDirection() {
        return direction;
    }
}
