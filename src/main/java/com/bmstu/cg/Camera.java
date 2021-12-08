package com.bmstu.cg;

import java.awt.event.KeyEvent;

public class Camera {
    private static final Vector4 Y_AXIS = new Vector4(0, 1, 0);

    private Transform transform;
    private Matrix projection;
    private Vector4 cameraRight;
    private Vector4 cameraDown;


    public Camera(Matrix projection) {
        this.projection = projection;
        this.transform = new Transform();
        this.cameraRight = Y_AXIS.cross(getCameraDirection()).normalized();
        this.cameraDown = this.cameraRight.cross(getCameraDirection()).normalized();
    }

    public Vector4 getCameraPosition() {
        return transform.getTransformedPos();
    }

    public Vector4 getCameraDirection() {
        return transform.getRotation().getForward();
    }

    public Vector4 getCameraRight() {
        return cameraRight;
    }

    public Vector4 getCameraDown() {
        return cameraDown;
    }


    public Matrix getViewProjection() {
        Matrix cameraRotation = transform.getTransformedRot().Negative().toRotationMatrix();
        Vector4 cameraPos = transform.getTransformedPos().multiply(-1);

        Matrix cameraTranslation = new Matrix().createMovement(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());

        return projection.multiply(cameraRotation.multiply(cameraTranslation));
    }


    public void update(KeyEvent action, double delta) {
        final float sensitivityX = (float) (1.66f * delta);
        final float sensitivityY = (float) (1.0f * delta);
        final float movAmt = (float) (5.0f * delta);
        if (action != null) {
            switch (action.getKeyCode()) {
                case KeyEvent.VK_W:
                    move(transform.getRotation().getForward(), movAmt);
                    break;
                case KeyEvent.VK_S:
                    move(transform.getRotation().getForward(), -movAmt);
                    break;
                case KeyEvent.VK_SPACE:
                    move(transform.getRotation().getUp(), movAmt);
                    break;
                case KeyEvent.VK_CONTROL:
                    move(transform.getRotation().getDown(), movAmt);
                    break;
                case KeyEvent.VK_A:
                    move(transform.getRotation().getLeft(), movAmt);
                    break;
                case KeyEvent.VK_D:
                    move(transform.getRotation().getRight(), movAmt);
                    break;

                case KeyEvent.VK_RIGHT:
                    rotate(Y_AXIS, sensitivityX);
                    break;
                case KeyEvent.VK_LEFT:
                    rotate(Y_AXIS, -sensitivityX);
                    break;
                case KeyEvent.VK_DOWN:
                    rotate(transform.getRotation().getRight(), sensitivityY);
                    break;
                case KeyEvent.VK_UP:
                    rotate(transform.getRotation().getRight(), -sensitivityY);
                    break;
            }
        }

    }


    public void move(Vector4 dir, float amt) {
        transform = transform.setPos(transform.getPosition().add(dir.multiply(amt)));
        this.cameraRight = Y_AXIS.cross(getCameraDirection()).normalized();
        this.cameraDown = this.cameraRight.cross(getCameraDirection());
    }

    public void rotate(Vector4 axis, float angle) {
        transform = transform.rotate(new Quaternion(axis, angle));
        this.cameraRight = Y_AXIS.cross(getCameraDirection()).normalized();
        this.cameraDown = this.cameraRight.cross(getCameraDirection());
    }
}
