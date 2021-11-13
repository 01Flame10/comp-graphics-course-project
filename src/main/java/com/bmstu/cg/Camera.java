package com.bmstu.cg;

import java.awt.event.KeyEvent;

public class Camera {
    private static final Vector4 Y_AXIS = new Vector4(0, 1, 0);

    private Transform transform;
    private Matrix projection;
    private Vector4 CameraRight;
    private Vector4 CameraDown;


    public Camera(Matrix projection) {
        this.projection = projection;
        this.transform = new Transform();
        this.CameraRight = Y_AXIS.cross(getCameraDirection()).normalized();
        this.CameraDown = this.CameraRight.cross(getCameraDirection()).normalized();
    }

    public Vector4 getCameraPosition() {
        return transform.getTransformedPos();
    }

    public Vector4 getCameraDirection() {
        return transform.getRot().getForward();
    }

    public Vector4 getCameraRight() {
        return CameraRight;
    }

    public Vector4 getCameraDown() {
        return CameraDown;
    }


    public Matrix getViewProjection() {
        Matrix cameraRotation = transform.getTransformedRot().Negative().ToRotationMatrix();
        Vector4 cameraPos = transform.getTransformedPos().multiply(-1);

        Matrix cameraTranslation = new Matrix().createMovement(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());

        return projection.multiply(cameraRotation.multiply(cameraTranslation));
    }


    public void update(KeyEvent action, double delta) {
        final float sensitivityX = (float) (1.66f * delta);
        final float sensitivityY = (float) (1.0f * delta);
        final float movAmt = (float) (5.0f * delta);
        System.out.println("MOV " + movAmt);
        if (action != null) {
            System.out.println("ACTION " + action.getKeyCode());
            switch (action.getKeyCode()) {
                case KeyEvent.VK_W:
                    move(transform.getRot().getForward(), movAmt);
                    break;
                case KeyEvent.VK_S:
                    move(transform.getRot().getForward(), -movAmt);
                    break;
                case KeyEvent.VK_A:
                    move(transform.getRot().getLeft(), movAmt);
                    break;
                case KeyEvent.VK_D:
                    move(transform.getRot().getRight(), movAmt);
                    break;

                case KeyEvent.VK_RIGHT:
                    rotate(Y_AXIS, sensitivityX);
                    break;
                case KeyEvent.VK_LEFT:
                    rotate(Y_AXIS, -sensitivityX);
                    break;
                case KeyEvent.VK_DOWN:
                    rotate(transform.getRot().getRight(), sensitivityY);
                    break;
                case KeyEvent.VK_UP:
                    rotate(transform.getRot().getRight(), -sensitivityY);
                    break;
            }
        }

    }


    public void move(Vector4 dir, float amt) {
        transform = transform.setPos(transform.getPos().add(dir.multiply(amt)));
        this.CameraRight = Y_AXIS.cross(getCameraDirection()).normalized();
        this.CameraDown = this.CameraRight.cross(getCameraDirection());
    }

    public void rotate(Vector4 axis, float angle) {
        transform = transform.rotate(new Quaternion(axis, angle));
        this.CameraRight = Y_AXIS.cross(getCameraDirection()).normalized();
        this.CameraDown = this.CameraRight.cross(getCameraDirection());
    }
}
