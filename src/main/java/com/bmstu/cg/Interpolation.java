package com.bmstu.cg;


public class Interpolation {
    private float[] texCoordX;
    private float[] texCoordY;
    private float[] oneOverZ;
    private float[] depth;
    private float[] lightAmt;

    private float texCoordXXStep;
    private float texCoordXYStep;
    private float texCoordYXStep;
    private float texCoordYYStep;
    private float oneOverZXStep;
    private float oneOverZYStep;
    private float depthXStep;
    private float depthYStep;
    private float lightAmtXStep;
    private float lightAmtYStep;

    public Interpolation(Vertex minYVert, Vertex midYVert, Vertex maxYVert, Vector4 lightDir) {
        float oneOverdX = 1.0f /
                (((midYVert.getX() - maxYVert.getX()) *
                        (minYVert.getY() - maxYVert.getY())) -
                        ((minYVert.getX() - maxYVert.getX()) *
                                (midYVert.getY() - maxYVert.getY())));

        float oneOverdY = -oneOverdX;

        oneOverZ = new float[3];
        texCoordX = new float[3];
        texCoordY = new float[3];
        depth = new float[3];
        lightAmt = new float[3];

        depth[0] = minYVert.getPosition().getZ();
        depth[1] = midYVert.getPosition().getZ();
        depth[2] = maxYVert.getPosition().getZ();

        lightAmt[0] = limit(minYVert.getNormal().dot(lightDir)) * 0.5f + 0.1f;
        lightAmt[1] = limit(midYVert.getNormal().dot(lightDir)) * 0.5f + 0.1f;
        lightAmt[2] = limit(maxYVert.getNormal().dot(lightDir)) * 0.5f + 0.1f;

        oneOverZ[0] = 1.0f / minYVert.getPosition().getW();
        oneOverZ[1] = 1.0f / midYVert.getPosition().getW();
        oneOverZ[2] = 1.0f / maxYVert.getPosition().getW();

        texCoordX[0] = minYVert.getTexCoords().getX() * oneOverZ[0];
        texCoordX[1] = midYVert.getTexCoords().getX() * oneOverZ[1];
        texCoordX[2] = maxYVert.getTexCoords().getX() * oneOverZ[2];

        texCoordY[0] = minYVert.getTexCoords().getY() * oneOverZ[0];
        texCoordY[1] = midYVert.getTexCoords().getY() * oneOverZ[1];
        texCoordY[2] = maxYVert.getTexCoords().getY() * oneOverZ[2];

        texCoordXXStep = findStepX(texCoordX, minYVert, midYVert, maxYVert, oneOverdX);
        texCoordXYStep = findStepY(texCoordX, minYVert, midYVert, maxYVert, oneOverdY);
        texCoordYXStep = findStepX(texCoordY, minYVert, midYVert, maxYVert, oneOverdX);
        texCoordYYStep = findStepY(texCoordY, minYVert, midYVert, maxYVert, oneOverdY);
        oneOverZXStep = findStepX(oneOverZ, minYVert, midYVert, maxYVert, oneOverdX);
        oneOverZYStep = findStepY(oneOverZ, minYVert, midYVert, maxYVert, oneOverdY);
        depthXStep = findStepX(depth, minYVert, midYVert, maxYVert, oneOverdX);
        depthYStep = findStepY(depth, minYVert, midYVert, maxYVert, oneOverdY);
        lightAmtXStep = findStepX(lightAmt, minYVert, midYVert, maxYVert, oneOverdX);
        lightAmtYStep = findStepY(lightAmt, minYVert, midYVert, maxYVert, oneOverdY);
    }

    public float getTexCoordX(int index) {
        return texCoordX[index];
    }

    public float getTexCoordY(int index) {
        return texCoordY[index];
    }

    public float getOneOverZ(int index) {
        return oneOverZ[index];
    }

    public float getDepth(int index) {
        return depth[index];
    }

    public float getLightAmt(int index) {
        return lightAmt[index];
    }

    public float getTexCoordXXStep() {
        return texCoordXXStep;
    }

    public float getTexCoordXYStep() {
        return texCoordXYStep;
    }

    public float getTexCoordYXStep() {
        return texCoordYXStep;
    }

    public float getTexCoordYYStep() {
        return texCoordYYStep;
    }

    public float getOneOverZXStep() {
        return oneOverZXStep;
    }

    public float getOneOverZYStep() {
        return oneOverZYStep;
    }

    public float getDepthXStep() {
        return depthXStep;
    }

    public float getDepthYStep() {
        return depthYStep;
    }

    public float getLightAmtXStep() {
        return lightAmtXStep;
    }

    public float getLightAmtYStep() {
        return lightAmtYStep;
    }

    private float findStepX(float[] values, Vertex minYVert, Vertex midYVert,
                            Vertex maxYVert, float oneOverdX) {
        return
                (((values[1] - values[2]) *
                        (minYVert.getY() - maxYVert.getY())) -
                        ((values[0] - values[2]) *
                                (midYVert.getY() - maxYVert.getY()))) * oneOverdX;
    }

    private float findStepY(float[] values, Vertex minYVert, Vertex midYVert,
                            Vertex maxYVert, float oneOverdY) {
        return
                (((values[1] - values[2]) *
                        (minYVert.getX() - maxYVert.getX())) -
                        ((values[0] - values[2]) *
                                (midYVert.getX() - maxYVert.getX()))) * oneOverdY;
    }

    private float limit(float val) {
        if (val > 1.0f) {
            return 1.0f;
        }
        if (val < 0.0f) {
            return 0.0f;
        }
        return val;
    }
}
