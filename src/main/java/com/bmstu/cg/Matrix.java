package com.bmstu.cg;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Matrix {
    private float[][] matrix;

    public Matrix() {
        matrix = new float[4][4];
    }

    public float[][] getM() {
        float[][] res = new float[4][4];

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                res[i][j] = matrix[i][j];

        return res;
    }

    public float get(int x, int y) {
        return matrix[x][y];
    }

    public void setM(float[][] m) {
        this.matrix = m;
    }

    public void set(int x, int y, float value) {
        matrix[x][y] = value;
    }

    public Matrix createIdentity() {
        matrix[0][0] = 1;
        matrix[0][1] = 0;
        matrix[0][2] = 0;
        matrix[0][3] = 0;
        matrix[1][0] = 0;
        matrix[1][1] = 1;
        matrix[1][2] = 0;
        matrix[1][3] = 0;
        matrix[2][0] = 0;
        matrix[2][1] = 0;
        matrix[2][2] = 1;
        matrix[2][3] = 0;
        matrix[3][0] = 0;
        matrix[3][1] = 0;
        matrix[3][2] = 0;
        matrix[3][3] = 1;

        return this;
    }

    public Matrix createScreenSpace(float halfWidth, float halfHeight) {
        matrix[0][0] = halfWidth;
        matrix[0][1] = 0;
        matrix[0][2] = 0;
        matrix[0][3] = halfWidth - 0.5f;
        matrix[1][0] = 0;
        matrix[1][1] = -halfHeight;
        matrix[1][2] = 0;
        matrix[1][3] = halfHeight - 0.5f;
        matrix[2][0] = 0;
        matrix[2][1] = 0;
        matrix[2][2] = 1;
        matrix[2][3] = 0;
        matrix[3][0] = 0;
        matrix[3][1] = 0;
        matrix[3][2] = 0;
        matrix[3][3] = 1;

        return this;
    }

    public Matrix createMovement(float x, float y, float z) {
        matrix[0][0] = 1;
        matrix[0][1] = 0;
        matrix[0][2] = 0;
        matrix[0][3] = x;
        matrix[1][0] = 0;
        matrix[1][1] = 1;
        matrix[1][2] = 0;
        matrix[1][3] = y;
        matrix[2][0] = 0;
        matrix[2][1] = 0;
        matrix[2][2] = 1;
        matrix[2][3] = z;
        matrix[3][0] = 0;
        matrix[3][1] = 0;
        matrix[3][2] = 0;
        matrix[3][3] = 1;

        return this;
    }

    public Matrix createRotation(float x, float y, float z, float angle) {
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);

        matrix[0][0] = cos + x * x * (1 - cos);
        matrix[0][1] = x * y * (1 - cos) - z * sin;
        matrix[0][2] = x * z * (1 - cos) + y * sin;
        matrix[0][3] = 0;
        matrix[1][0] = y * x * (1 - cos) + z * sin;
        matrix[1][1] = cos + y * y * (1 - cos);
        matrix[1][2] = y * z * (1 - cos) - x * sin;
        matrix[1][3] = 0;
        matrix[2][0] = z * x * (1 - cos) - y * sin;
        matrix[2][1] = z * y * (1 - cos) + x * sin;
        matrix[2][2] = cos + z * z * (1 - cos);
        matrix[2][3] = 0;
        matrix[3][0] = 0;
        matrix[3][1] = 0;
        matrix[3][2] = 0;
        matrix[3][3] = 1;

        return this;
    }


    public Matrix createScale(float x, float y, float z) {
        matrix[0][0] = x;
        matrix[0][1] = 0;
        matrix[0][2] = 0;
        matrix[0][3] = 0;
        matrix[1][0] = 0;
        matrix[1][1] = y;
        matrix[1][2] = 0;
        matrix[1][3] = 0;
        matrix[2][0] = 0;
        matrix[2][1] = 0;
        matrix[2][2] = z;
        matrix[2][3] = 0;
        matrix[3][0] = 0;
        matrix[3][1] = 0;
        matrix[3][2] = 0;
        matrix[3][3] = 1;

        return this;
    }

    public Matrix createPerspective(float fov, float aspectRatio, float zNear, float zFar) {
        float tanHalfFOV = (float) Math.tan(fov / 2);
        float zRange = zNear - zFar;

        matrix[0][0] = 1.0f / (tanHalfFOV * aspectRatio);
        matrix[0][1] = 0;
        matrix[0][2] = 0;
        matrix[0][3] = 0;
        matrix[1][0] = 0;
        matrix[1][1] = 1.0f / tanHalfFOV;
        matrix[1][2] = 0;
        matrix[1][3] = 0;
        matrix[2][0] = 0;
        matrix[2][1] = 0;
        matrix[2][2] = (-zNear - zFar) / zRange;
        matrix[2][3] = 2 * zFar * zNear / zRange;
        matrix[3][0] = 0;
        matrix[3][1] = 0;
        matrix[3][2] = 1;
        matrix[3][3] = 0;


        return this;
    }

    public Matrix createRotation(Vector4 forward, Vector4 up) {
        Vector4 f = forward.normalized();

        Vector4 r = up.normalized();
        r = r.cross(f);

        Vector4 u = f.cross(r);

        return createRotation(f, u, r);
    }

    public Matrix createRotation(Vector4 forward, Vector4 up, Vector4 right) {
        Vector4 f = forward;
        Vector4 r = right;
        Vector4 u = up;

        matrix[0][0] = r.getX();
        matrix[0][1] = r.getY();
        matrix[0][2] = r.getZ();
        matrix[0][3] = 0;
        matrix[1][0] = u.getX();
        matrix[1][1] = u.getY();
        matrix[1][2] = u.getZ();
        matrix[1][3] = 0;
        matrix[2][0] = f.getX();
        matrix[2][1] = f.getY();
        matrix[2][2] = f.getZ();
        matrix[2][3] = 0;
        matrix[3][0] = 0;
        matrix[3][1] = 0;
        matrix[3][2] = 0;
        matrix[3][3] = 1;

        return this;
    }

    public Vector4 multiply(Vector4 r) {
        return new Vector4(matrix[0][0] * r.getX() + matrix[0][1] * r.getY() + matrix[0][2] * r.getZ() + matrix[0][3] * r.getW(),
                matrix[1][0] * r.getX() + matrix[1][1] * r.getY() + matrix[1][2] * r.getZ() + matrix[1][3] * r.getW(),
                matrix[2][0] * r.getX() + matrix[2][1] * r.getY() + matrix[2][2] * r.getZ() + matrix[2][3] * r.getW(),
                matrix[3][0] * r.getX() + matrix[3][1] * r.getY() + matrix[3][2] * r.getZ() + matrix[3][3] * r.getW());
    }

    public Matrix multiply(Matrix r) {
        Matrix res = new Matrix();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                res.set(i, j, matrix[i][0] * r.get(0, j) +
                        matrix[i][1] * r.get(1, j) +
                        matrix[i][2] * r.get(2, j) +
                        matrix[i][3] * r.get(3, j));
            }
        }

        return res;
    }

    @Override
    public String toString() {
        return "Matrix{" +
                "matrix=\n" + Arrays.stream(matrix)
                .map(a -> "x: " + a[0] + "\ty: " + a[1] + "\tz: " + a[2] + "\tw: " + a[3])
                .collect(Collectors.joining("\n"))+
                '}';
    }
}
