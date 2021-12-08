package com.bmstu.cg;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Vector4 {
    private float x;
    private float y;
    private float w;
    private float z;

    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4(float x, float y, float z) {
        this(x, y, z, 1.0f);
    }

    public Vector4(Vector4 vector) {
        this(vector.x, vector.y, vector.z, vector.w);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public float dot3(Vector4 r) {
        return x * r.getX() + y * r.getY() + z * r.getZ();// + w * r.getW();
    }

    public float dot(Vector4 r) {
        return x * r.getX() + y * r.getY() + z * r.getZ() + w * r.getW();
    }

    public Vector4 cross(Vector4 r) {
        float x_ = y * r.getZ() - z * r.getY();
        float y_ = z * r.getX() - x * r.getZ();
        float z_ = x * r.getY() - y * r.getX();

        return new Vector4(x_, y_, z_, 0);
    }

    public Vector4 normalized() {
        float length = length3();

        return new Vector4(x / length, y / length, z / length, w / length);
    }

    public Vector4 rotate(Vector4 axis, float angle) {
        float sinAngle = (float) Math.sin(-angle);
        float cosAngle = (float) Math.cos(-angle);

        return this.cross(axis.multiply(sinAngle)).add(
                (this.multiply(cosAngle)).add(
                        axis.multiply(this.dot(axis.multiply(1 - cosAngle)))));
    }

    public Vector4 rotate(Quaternion rotation) {
        Quaternion w = rotation.mul(this).mul(rotation.Negative());

        return new Vector4(w.getX(), w.getY(), w.getZ(), 1.0f);
    }

    public Vector4 lerp(Vector4 dest, float lerpFactor) {
        return dest.substitute(this).multiply(lerpFactor).add(this);
    }

    public Vector4 add(Vector4 r) {
        return new Vector4(x + r.getX(), y + r.getY(), z + r.getZ(), w + r.getW());
    }

    public Vector4 add(float r) {
        return new Vector4(x + r, y + r, z + r, w);
    }

    public Vector4 substitute(Vector4 r) {
        return new Vector4(x - r.getX(), y - r.getY(), z - r.getZ(), w - r.getW());
    }

    public Vector4 substitute(float r) {
        return new Vector4(x - r, y - r, z - r, w - r);
    }

    public Vector4 multiply(Vector4 r) {
        return new Vector4(x * r.getX(), y * r.getY(), z * r.getZ(), w * r.getW());
    }

    public Vector4 multiply(float r) {
        return new Vector4(x * r, y * r, z * r, w * r);
    }


    public Vector4 negative() {
        return new Vector4(-x, -y, -z, 1);
    }

    public float length3() {
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
    }

    @Override
    public String toString() {
        return "Vector4{" +
                "x=" + x +
                ",y=" + y +
                ",z=" + z +
                ",w=" + w +
                '}';
    }
}
