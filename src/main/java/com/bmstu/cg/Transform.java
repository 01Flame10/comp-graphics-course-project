package com.bmstu.cg;

import lombok.Getter;

@Getter
public class Transform {
    private final Vector4 position;
    private final Quaternion rotation;
    private final Vector4 scale;
    private Vector4 eulerRotation;

    public Transform() {
        this(new Vector4(0, 0, 0, 0), new Vector4(1, 1, 1, 1));
    }

    public Transform(Vector4 pos, Vector4 scale) {
        this(pos, new Quaternion(0, 0, 0, 1), scale, new Vector4(0, 0, 0, 1));
    }

    public Transform(Vector4 position, Quaternion rotation, Vector4 scale, Vector4 eulerRotation) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.eulerRotation = eulerRotation;
    }

    public Transform(Transform transform) {
        this.position = new Vector4(transform.getPosition());
        this.rotation = new Quaternion(transform.getRotation());
        this.scale = new Vector4(transform.getScale());
        this.eulerRotation = new Vector4(transform.getEulerRotation());
    }

    public Vector4 getTransformedPos() {
        return position;
    }

    public Quaternion getTransformedRot() {
        return rotation;
    }

    public Transform setPos(Vector4 pos) {
        return new Transform(pos, rotation, scale, eulerRotation);
    }

    public Transform setScale(Vector4 scale) {
        return new Transform(position, rotation, scale, eulerRotation);
    }

    public Transform rotate(Quaternion rotation) {
        return new Transform(position, rotation.mul(this.rotation).normalized(), scale, eulerRotation);
    }

    public Transform rotateFromNull(float ox, float oy, float oz) {
        eulerRotation = new Vector4(ox, oy, oz, 1);

        float toRad = (float) Math.PI / 180;
        Quaternion rotation = Quaternion.quaternionFromEuler(ox * toRad, oy * toRad, oz * toRad);

        return new Transform(position, rotation.normalized(), scale, eulerRotation);
    }

    public Matrix getTransformation() {
        Matrix translationMatrix = new Matrix()
                .createMovement(position.getX(), position.getY(), position.getZ());
        Matrix rotationMatrix = rotation.toRotationMatrix();
        Matrix scaleMatrix = new Matrix()
                .createScale(scale.getX(), scale.getY(), scale.getZ());

        return translationMatrix.multiply(rotationMatrix.multiply(scaleMatrix));
    }


}
