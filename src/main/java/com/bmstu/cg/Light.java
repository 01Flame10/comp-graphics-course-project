package com.bmstu.cg;


public class Light extends Source {
    public Vector4 position;
    public ColorCG color;
    public float intensive;

    public Light() {
        position = new Vector4(0, 0, 0);
        color = new ColorCG(1, 1, 1);
        intensive = 1;
    }

    public Light(Vector4 p, ColorCG c, float intensive_v) {
        position = p;
        color = c;
        intensive = intensive_v;
    }

    @Override
    public void setLightPosition(Vector4 pos) {
        position = pos;
    }

    @Override
    public void setLightIntensive(float intens) {
        intensive = intens;
    }

    @Override
    public void setLightColor(ColorCG col) {
        color = col;
    }

    @Override
    public Vector4 getLightPosition() {
        return position;
    }

    @Override
    public float getLightIntensive() {
        return intensive;
    }

    @Override
    public ColorCG getLightColor() {
        return color;
    }
}
