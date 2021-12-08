package com.bmstu.cg;

import com.bmstu.cg.exception.RenderChosenObjectException;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class RenderSceneTriangle extends ImageCG {
    private float[] zBuffer;

    public RenderSceneTriangle(int width, int height) {
        super(width, height);
        zBuffer = new float[width * height];
    }

    public void newZBuffer() {
        Arrays.fill(zBuffer, Float.MAX_VALUE);
    }

    public void drawTriangle(Vertex v1, Vertex v2, Vertex v3, ImageCG texture, ColorCG color,
                             boolean texPaint, List<Source> lightsArray, boolean isPhantom) {
        if (v1.isInsideView() && v2.isInsideView() && v3.isInsideView()) {
            fillTriangle(v1, v2, v3, texture, color, texPaint, lightsArray, isPhantom);
            return;
        }

        List<Vertex> vertices = new ArrayList<>();
        List<Vertex> additionalList = new ArrayList<>();

        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);

        if (clipPolygon(vertices, additionalList, 0) &&
                clipPolygon(vertices, additionalList, 1) &&
                clipPolygon(vertices, additionalList, 2)) {
            Vertex initialVertex = vertices.get(0);

            for (int i = 1; i < vertices.size() - 1; i++) {
                fillTriangle(initialVertex, vertices.get(i), vertices.get(i + 1),
                        texture, color, texPaint, lightsArray, isPhantom);
            }
        }
    }

    private boolean clipPolygon(List<Vertex> vertices, List<Vertex> addList,
                                int componentIndex) {
        clipPolygonComponent(vertices, componentIndex, 1.0f, addList);
        vertices.clear();

        if (addList.isEmpty()) {
            return false;
        }

        clipPolygonComponent(addList, componentIndex, -1.0f, vertices);
        addList.clear();

        return !vertices.isEmpty();
    }

    private void clipPolygonComponent(List<Vertex> vertices, int componentIndex,
                                      float componentFactor, List<Vertex> result) {
        Vertex previousVertex = vertices.get(vertices.size() - 1);
        float previousComponent = previousVertex.get(componentIndex) * componentFactor;
        boolean previousInside = previousComponent <= previousVertex.getPosition().getW();

        for (Vertex currentVertex : vertices) {
            float currentComponent = currentVertex.get(componentIndex) * componentFactor;
            boolean currentInside = currentComponent <= currentVertex.getPosition().getW();

            if (currentInside ^ previousInside) {
                float lerpAmt = (previousVertex.getPosition().getW() - previousComponent) /
                        ((previousVertex.getPosition().getW() - previousComponent) -
                                (currentVertex.getPosition().getW() - currentComponent));

                result.add(previousVertex.lerp(currentVertex, lerpAmt));
            }

            if (currentInside) {
                result.add(currentVertex);
            }

            previousVertex = currentVertex;
            previousComponent = currentComponent;
            previousInside = currentInside;
        }
    }

    private void fillTriangle(Vertex v1, Vertex v2, Vertex v3, ImageCG texture, ColorCG color,
                              boolean texPaint, List<Source> lightsArray, boolean isPhantom) {
        Matrix screenSpaceTransform =
                new Matrix().createScreenSpace(getWidth() >> 1, getHeight() >> 1);
        Matrix identity = new Matrix().createIdentity();
        Vertex minYVert = v1.transform(screenSpaceTransform, identity).perspectiveDivide();
        Vertex midYVert = v2.transform(screenSpaceTransform, identity).perspectiveDivide();
        Vertex maxYVert = v3.transform(screenSpaceTransform, identity).perspectiveDivide();

        if (minYVert.triangleAreaTimesTwo(maxYVert, midYVert) >= 0) {
            return;
        }

        if (maxYVert.getY() < midYVert.getY()) {
            Vertex temp = maxYVert;
            maxYVert = midYVert;
            midYVert = temp;
        }

        if (midYVert.getY() < minYVert.getY()) {
            Vertex temp = midYVert;
            midYVert = minYVert;
            minYVert = temp;
        }

        if (maxYVert.getY() < midYVert.getY()) {
            Vertex temp = maxYVert;
            maxYVert = midYVert;
            midYVert = temp;
        }

        scanTriangle(minYVert, midYVert, maxYVert,
                minYVert.triangleAreaTimesTwo(maxYVert, midYVert) >= 0,
                texture, color, texPaint, lightsArray, isPhantom);
    }

    private void scanTriangle(Vertex minYVert, Vertex midYVert,
                              Vertex maxYVert, boolean handedness,
                              ImageCG texture, ColorCG color, boolean texPaint,
                              List<Source> lightsArray, boolean isPhantom) {
        Interpolation gradients = new Interpolation(minYVert, midYVert, maxYVert, lightsArray.get(0).getLightPosition());
        Edge topToBottom = new Edge(gradients, minYVert, maxYVert, 0);
        Edge topToMiddle = new Edge(gradients, minYVert, midYVert, 0);
        Edge middleToBottom = new Edge(gradients, midYVert, maxYVert, 1);

        scanEdges(gradients, topToBottom, topToMiddle, handedness, texture, color, texPaint, isPhantom);
        scanEdges(gradients, topToBottom, middleToBottom, handedness, texture, color, texPaint, isPhantom);
    }

    private void scanEdges(Interpolation gradients, Edge a, Edge b,
                           boolean handedness, ImageCG texture, ColorCG color, boolean texPaint, boolean isPhantom) {
        Edge left = a;
        Edge right = b;
        if (handedness) {
            Edge temp = left;
            left = right;
            right = temp;
        }

        int yStart = b.getYStart();
        int yEnd = b.getYEnd();

        for (int j = yStart; j < yEnd; j++) {
            drawLine(gradients, left, right, j, texture, color, texPaint, isPhantom);
            left.step();
            right.step();
        }
    }

    private void drawLine(Interpolation gradients, Edge left, Edge right,
                          int j, ImageCG texture, ColorCG color, boolean texPaint, boolean isPhantom) {
        int xMin = (int) Math.ceil(left.getX());
        int xMax = (int) Math.ceil(right.getX());
        float xDopStep = xMin - left.getX();
        float texCoordXXStep = gradients.getTexCoordXXStep();
        float texCoordYXStep = gradients.getTexCoordYXStep();
        float oneOverZXStep = gradients.getOneOverZXStep();
        float depthXStep = gradients.getDepthXStep();
        float lightAmtXStep = gradients.getLightAmtXStep();

        float texCoordX = left.getTexCoordX() + texCoordXXStep * xDopStep;
        float texCoordY = left.getTexCoordY() + texCoordYXStep * xDopStep;
        float oneOnZ = left.getOneOverZ() + oneOverZXStep * xDopStep;
        float depth = left.getDepth() + depthXStep * xDopStep;
        float lightAmt = left.getLightAmt() + lightAmtXStep * xDopStep;
        boolean isColumnPhantom = Launcher.mouseY == j && Launcher.phantomChooseMode && Launcher.mouseClickedOnPhantomMode && isPhantom;
        for (int i = xMin; i < xMax; i++) {
            if (Launcher.mouseX == i && isColumnPhantom) {
                throw new RenderChosenObjectException();
            }
            int index = i + j * getWidth();
            if (depth < zBuffer[index]) {
                zBuffer[index] = depth;
                if (texPaint) {
                    float z = 1.0f / oneOnZ;
                    int srcX = (int) ((texCoordX * z) * (float) (texture.getWidth() - 1) + 0.5f);
                    int srcY = (int) ((texCoordY * z) * (float) (texture.getHeight() - 1) + 0.5f);
                    copyPixel(i, j, srcX, srcY, texture, lightAmt);
                } else {
                    drawPixelLight(i, j, (byte) 0xFF,
                            (byte) ((int) (color.blue * 255) & 0xFF),
                            (byte) ((int) (color.green * 255) & 0xFF),
                            (byte) ((int) (color.red * 255) & 0xFF),
                            lightAmt);
                }
            }

            oneOnZ += oneOverZXStep;
            texCoordX += texCoordXXStep;
            texCoordY += texCoordYXStep;
            depth += depthXStep;
            lightAmt += lightAmtXStep;
        }
    }
}
