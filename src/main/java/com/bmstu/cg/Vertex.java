package com.bmstu.cg;

public class Vertex {
    private Vector4 pos;
    private Vector4 texpos;
    private Vector4 normal;

    public Vertex(Vector4 pos_v, Vector4 texCoords_v, Vector4 normal_v) {
        pos = pos_v;
        texpos = texCoords_v;
        normal = normal_v;
    }

    public float getX() {
        return pos.getX();
    }

    public float getY() {
        return pos.getY();
    }

    public float getZ() {
        return pos.getZ();
    }

    public Vector4 getPosition() {
        return pos;
    }

    public Vector4 getTexCoords() {
        return texpos;
    }

    public Vector4 getNormal() {
        return normal;
    }

    public Vertex Transform(Matrix transform, Matrix normalTransform) {
        return new Vertex(transform.multiply(pos), texpos,
                normalTransform.multiply(normal).normalized());
    }

    public Vertex TransformPos(Matrix transform) {
        return new Vertex(transform.multiply(pos), texpos, normal);
    }

    public Vertex PerspectiveDivide() {
        //System.out.println(pos.getZ() + " " + pos.getW());
        //float z_on_w =  pos.getZ()/pos.getW();
        return new Vertex(new Vector4((pos.getX() / pos.getW()), (pos.getY() / pos.getW()),
                (pos.getZ() / pos.getW()), pos.getW()),
                texpos, normal);
    }

    public float TriangleAreaTimesTwo(Vertex b, Vertex c) {
        float x1 = b.getX() - pos.getX();
        float y1 = b.getY() - pos.getY();

        float x2 = c.getX() - pos.getX();
        float y2 = c.getY() - pos.getY();

        return (x1 * y2 - x2 * y1);
    }

    public Vertex Lerp(Vertex other, float lerpAmt) {
        return new Vertex(
                pos.lerp(other.getPosition(), lerpAmt),
                texpos.lerp(other.getTexCoords(), lerpAmt),
                normal.lerp(other.getNormal(), lerpAmt)
        );
    }

    public boolean IsInsideView() {
        return
                Math.abs(pos.getX()) <= Math.abs(pos.getW()) &&
                        Math.abs(pos.getY()) <= Math.abs(pos.getW()) &&
                        Math.abs(pos.getZ()) <= Math.abs(pos.getW());
    }

    public float get(int index) {
        switch (index) {
            case 0:
                return pos.getX();
            case 1:
                return pos.getY();
            case 2:
                return pos.getZ();
            case 3:
                return pos.getW();
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
