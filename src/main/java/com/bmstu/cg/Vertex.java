package com.bmstu.cg;

public class Vertex {
    private Vector4 position;
    private Vector4 texPosition;
    private Vector4 normal;

    public Vertex(Vector4 pos_v, Vector4 texCoords_v, Vector4 normal_v) {
        position = pos_v;
        texPosition = texCoords_v;
        normal = normal_v;
    }

    public float getX() {
        return position.getX();
    }

    public float getY() {
        return position.getY();
    }

    public float getZ() {
        return position.getZ();
    }

    public Vector4 getPosition() {
        return position;
    }

    public void setPosition(Vector4 pos) {
        this.position = pos;
    }

    public Vector4 getTexCoords() {
        return texPosition;
    }

    public Vector4 getNormal() {
        return normal;
    }

    public Vertex transform(Matrix transform, Matrix normalTransform) {
        return new Vertex(transform.multiply(position), texPosition,
                normalTransform.multiply(normal).normalized());
    }

    public Vertex transformPos(Matrix transform) {
        return new Vertex(transform.multiply(position), texPosition, normal);
    }

    public Vertex perspectiveDivide() {
        //System.out.println(pos.getZ() + " " + pos.getW());
        //float z_on_w =  pos.getZ()/pos.getW();
        return new Vertex(new Vector4((position.getX() / position.getW()), (position.getY() / position.getW()),
                (position.getZ() / position.getW()), position.getW()),
                texPosition, normal);
    }

    public float triangleAreaTimesTwo(Vertex b, Vertex c) {
        float x1 = b.getX() - position.getX();
        float y1 = b.getY() - position.getY();

        float x2 = c.getX() - position.getX();
        float y2 = c.getY() - position.getY();

        return (x1 * y2 - x2 * y1);
    }

    public Vertex lerp(Vertex other, float lerpAmt) {
        return new Vertex(
                position.lerp(other.getPosition(), lerpAmt),
                texPosition.lerp(other.getTexCoords(), lerpAmt),
                normal.lerp(other.getNormal(), lerpAmt)
        );
    }

    public boolean isInsideView() {
        return
                Math.abs(position.getX()) <= Math.abs(position.getW()) &&
                        Math.abs(position.getY()) <= Math.abs(position.getW()) &&
                        Math.abs(position.getZ()) <= Math.abs(position.getW());
    }

    public float get(int index) {
        switch (index) {
            case 0:
                return position.getX();
            case 1:
                return position.getY();
            case 2:
                return position.getZ();
            case 3:
                return position.getW();
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
