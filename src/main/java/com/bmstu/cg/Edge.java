package com.bmstu.cg;

public class Edge {
    private float x;
    private float xStep;
    private int yStart;
    private int yEnd;
    private float texCoordX;
    private float texCoordXStep;
    private float texCoordY;
    private float texCoordYStep;
    private float oneOverZ;
    private float oneOverZStep;
    private float depth;
    private float depthStep;
    private float lightAmt;
    private float lightAmtStep;

    public Edge(Interpolation interp, Vertex minYVert, Vertex maxYVert, int minYVertIndex) {
        yStart = (int) Math.ceil(minYVert.getY());
        yEnd = (int) Math.ceil(maxYVert.getY());

        float yDist = maxYVert.getY() - minYVert.getY();
        float xDist = maxYVert.getX() - minYVert.getX();

        float yPrestep = yStart - minYVert.getY();
        xStep = xDist / yDist;
        x = minYVert.getX() + yPrestep * xStep;
        float xPrestep = x - minYVert.getX();

        texCoordX = interp.getTexCoordX(minYVertIndex) +
                interp.getTexCoordXXStep() * xPrestep +
                interp.getTexCoordXYStep() * yPrestep;
        texCoordXStep = interp.getTexCoordXYStep() + interp.getTexCoordXXStep() * xStep;

        texCoordY = interp.getTexCoordY(minYVertIndex) +
                interp.getTexCoordYXStep() * xPrestep +
                interp.getTexCoordYYStep() * yPrestep;
        texCoordYStep = interp.getTexCoordYYStep() + interp.getTexCoordYXStep() * xStep;

        oneOverZ = interp.getOneOverZ(minYVertIndex) +
                interp.getOneOverZXStep() * xPrestep +
                interp.getOneOverZYStep() * yPrestep;
        oneOverZStep = interp.getOneOverZYStep() + interp.getOneOverZXStep() * xStep;

        depth = interp.getDepth(minYVertIndex) +
                interp.getDepthXStep() * xPrestep +
                interp.getDepthYStep() * yPrestep;
        depthStep = interp.getDepthYStep() + interp.getDepthXStep() * xStep;

        lightAmt = interp.getLightAmt(minYVertIndex) +
                interp.getLightAmtXStep() * xPrestep +
                interp.getLightAmtYStep() * yPrestep;
        lightAmtStep = interp.getLightAmtYStep() + interp.getLightAmtXStep() * xStep;
    }

    public float getX() {
        return x;
    }

    public int getYStart() {
        return yStart;
    }

    public int getYEnd() {
        return yEnd;
    }

    public float getTexCoordX() {
        return texCoordX;
    }

    public float getTexCoordY() {
        return texCoordY;
    }

    public float getOneOverZ() {
        return oneOverZ;
    }

    public float getDepth() {
        return depth;
    }

    public float getLightAmt() {
        return lightAmt;
    }

    public void step() {
        x += xStep;
        texCoordX += texCoordXStep;
        texCoordY += texCoordYStep;
        oneOverZ += oneOverZStep;
        depth += depthStep;
        lightAmt += lightAmtStep;
    }
}
