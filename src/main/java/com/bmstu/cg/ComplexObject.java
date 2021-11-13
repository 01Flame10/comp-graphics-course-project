package com.bmstu.cg;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class ComplexObject {
    public ImageCG texture;
    public ColorCG color;
    public boolean tex_paint;
    public Transform trans;
    public String type;
    private List<Vertex> vertexes;
    private List<Integer> indexes;

    public ComplexObject(String fileName, Transform trans_v, String type_v, ColorCG color_v) throws IOException {
        Model model = new OBJModel(fileName).ToIndexedModel();

        vertexes = new ArrayList<>();
        for (int i = 0; i < model.getPositions().size(); i++) {
            vertexes.add(new Vertex(
                    model.getPositions().get(i),
                    model.getTexCoords().get(i),
                    model.getNormals().get(i)));
        }

        indexes = model.getIndices();
        color = color_v;
        tex_paint = false;
        trans = trans_v;
        type = type_v;
    }

    public void addToObjects(List<PrimitiveObject> scene_objects) {
        Matrix transform = trans.getTransformation();

        if (tex_paint) {
            for (int i = 0; i < indexes.size(); i += 3) {
                Triangle tmp = new Triangle(vertexes.get(indexes.get(i)).Transform(transform, transform),//.Transform(screenSpaceTransform, identity).PerspectiveDivide(),
                        vertexes.get(indexes.get(i + 1)).Transform(transform, transform),//.Transform(screenSpaceTransform, identity).PerspectiveDivide(),
                        vertexes.get(indexes.get(i + 2)).Transform(transform, transform),//.Transform(screenSpaceTransform, identity).PerspectiveDivide(),
                        texture);
                scene_objects.add(tmp);

            }
        } else {
            for (int i = 0; i < indexes.size(); i += 3) {
                Triangle tmp = new Triangle(vertexes.get(indexes.get(i)).Transform(transform, transform),
                        vertexes.get(indexes.get(i + 1)).Transform(transform, transform),
                        vertexes.get(indexes.get(i + 2)).Transform(transform, transform),
                        color);
                scene_objects.add(tmp);

            }
        }
    }

    public void draw(RenderSceneTriangle context, Matrix viewProjection, List<Source> light_array) {
        Matrix transform = trans.getTransformation();
        Matrix mvp = viewProjection.multiply(transform);
        for (int i = 0; i < indexes.size(); i += 3) {
            context.DrawTriangle(
                    vertexes.get(indexes.get(i)).Transform(mvp, transform),
                    vertexes.get(indexes.get(i + 1)).Transform(mvp, transform),
                    vertexes.get(indexes.get(i + 2)).Transform(mvp, transform),
                    texture,
                    color,
                    tex_paint,
                    light_array);
        }
    }
}
