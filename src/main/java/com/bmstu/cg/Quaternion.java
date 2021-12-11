package com.bmstu.cg;

public class Quaternion {
    private float xq;
    private float yq;
    private float zq;
    private float wq;

    public Vector4 getForward() {
        return new Vector4(0, 0, 1, 1).rotate(this);
    }

    public Vector4 getBack() {
        return new Vector4(0, 0, -1, 1).rotate(this);
    }

    public Vector4 getUp() {
        return new Vector4(0, 1, 0, 1).rotate(this);
    }

    public Vector4 getDown() {
        return new Vector4(0, -1, 0, 1).rotate(this);
    }

    public Vector4 getRight() {
        return new Vector4(1, 0, 0, 1).rotate(this);
    }

    public Vector4 getLeft() {
        return new Vector4(-1, 0, 0, 1).rotate(this);
    }

    public float getX() {
        return xq;
    }

    public float getY() {
        return yq;
    }

    public float getZ() {
        return zq;
    }

    public float getW() {
        return wq;
    }

    public Quaternion(float x, float y, float z, float w) {
        this.xq = x;
        this.yq = y;
        this.zq = z;
        this.wq = w;
    }

    public Quaternion(Quaternion quaternion) {
        this.wq = quaternion.wq;
        this.yq = quaternion.yq;
        this.zq = quaternion.zq;
        this.xq = quaternion.xq;
    }

    public Quaternion(Vector4 axis, float angle) {
        float sinHalfAngle = (float) Math.sin(angle / 2);
        float cosHalfAngle = (float) Math.cos(angle / 2);

        this.xq = axis.getX() * sinHalfAngle;
        this.yq = axis.getY() * sinHalfAngle;
        this.zq = axis.getZ() * sinHalfAngle;
        this.wq = cosHalfAngle;
    }

    public float length() {
        return (float) Math.sqrt(xq * xq + yq * yq + zq * zq + wq * wq);
    }

    public Quaternion normalized() {
        float length = length();

        return new Quaternion(xq / length, yq / length, zq / length, wq / length);
    }

    public Quaternion negative() {
        return new Quaternion(-xq, -yq, -zq, wq);
    }

    public Quaternion mul(float r) {
        return new Quaternion(xq * r, yq * r, zq * r, wq * r);
    }

    public Quaternion mul(Quaternion r) {
        float w_ = wq * r.getW() - xq * r.getX() - yq * r.getY() - zq * r.getZ();
        float x_ = xq * r.getW() + wq * r.getX() + yq * r.getZ() - zq * r.getY();
        float y_ = yq * r.getW() + wq * r.getY() + zq * r.getX() - xq * r.getZ();
        float z_ = zq * r.getW() + wq * r.getZ() + xq * r.getY() - yq * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }

    public Quaternion mul(Vector4 r) {
        float w_ = -xq * r.getX() - yq * r.getY() - zq * r.getZ();
        float x_ = wq * r.getX() + yq * r.getZ() - zq * r.getY();
        float y_ = wq * r.getY() + zq * r.getX() - xq * r.getZ();
        float z_ = wq * r.getZ() + xq * r.getY() - yq * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }

    public Quaternion Sub(Quaternion r) {
        return new Quaternion(xq - r.getX(), yq - r.getY(), zq - r.getZ(), wq - r.getW());
    }

    public Quaternion Add(Quaternion r) {
        return new Quaternion(xq + r.getX(), yq + r.getY(), zq + r.getZ(), wq + r.getW());
    }


    public static Quaternion quaternionFromEuler(float ax, float ay, float az) {
        Vector4 vx = new Vector4(1, 0, 0);
        Vector4 vy = new Vector4(0, 1, 0);
        Vector4 vz = new Vector4(0, 0, 1);
        Quaternion qx = new Quaternion(vx, ax);
        Quaternion qy = new Quaternion(vy, ay);
        Quaternion qz = new Quaternion(vz, az);
        Quaternion qt = qx.mul(qy);
        qt = qt.mul(qz);
        return qt;
    }

    public Matrix toRotationMatrix() {
        Vector4 forward = new Vector4(2.0f * (xq * zq - wq * yq), 2.0f * (yq * zq + wq * xq), 1.0f - 2.0f * (xq * xq + yq * yq));
        Vector4 up = new Vector4(2.0f * (xq * yq + wq * zq), 1.0f - 2.0f * (xq * xq + zq * zq), 2.0f * (yq * zq - wq * xq));
        Vector4 right = new Vector4(1.0f - 2.0f * (yq * yq + zq * zq), 2.0f * (xq * yq - wq * zq), 2.0f * (xq * zq + wq * yq));

        return new Matrix().createRotation(forward, up, right);
    }

    public float Dot(Quaternion r) {
        return xq * r.getX() + yq * r.getY() + zq * r.getZ() + wq * r.getW();
    }


    public Quaternion(Matrix rot) {
        float trace = rot.get(0, 0) + rot.get(1, 1) + rot.get(2, 2);

        if (trace > 0) {
            float s = 0.5f / (float) Math.sqrt(trace + 1.0f);
            wq = 0.25f / s;
            xq = (rot.get(1, 2) - rot.get(2, 1)) * s;
            yq = (rot.get(2, 0) - rot.get(0, 2)) * s;
            zq = (rot.get(0, 1) - rot.get(1, 0)) * s;
        } else {
            if (rot.get(0, 0) > rot.get(1, 1) && rot.get(0, 0) > rot.get(2, 2)) {
                float s = 2.0f * (float) Math.sqrt(1.0f + rot.get(0, 0) - rot.get(1, 1) - rot.get(2, 2));
                wq = (rot.get(1, 2) - rot.get(2, 1)) / s;
                xq = 0.25f * s;
                yq = (rot.get(1, 0) + rot.get(0, 1)) / s;
                zq = (rot.get(2, 0) + rot.get(0, 2)) / s;
            } else if (rot.get(1, 1) > rot.get(2, 2)) {
                float s = 2.0f * (float) Math.sqrt(1.0f + rot.get(1, 1) - rot.get(0, 0) - rot.get(2, 2));
                wq = (rot.get(2, 0) - rot.get(0, 2)) / s;
                xq = (rot.get(1, 0) + rot.get(0, 1)) / s;
                yq = 0.25f * s;
                zq = (rot.get(2, 1) + rot.get(1, 2)) / s;
            } else {
                float s = 2.0f * (float) Math.sqrt(1.0f + rot.get(2, 2) - rot.get(0, 0) - rot.get(1, 1));
                wq = (rot.get(0, 1) - rot.get(1, 0)) / s;
                xq = (rot.get(2, 0) + rot.get(0, 2)) / s;
                yq = (rot.get(1, 2) + rot.get(2, 1)) / s;
                zq = 0.25f * s;
            }
        }

        float length = (float) Math.sqrt(xq * xq + yq * yq + zq * zq + wq * wq);
        xq /= length;
        yq /= length;
        zq /= length;
        wq /= length;
    }


}
