package com.bmstu.cg;

import com.bmstu.cg.enums.ObjectConnectionType;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Accessors(chain = true)
@Getter
public class ComplexObject {
    public ImageCG texture;
    public ColorCG color;
    public boolean texPaint;
    public Transform trans;
    public String type;
    private List<Vertex> vertexes;
    private List<Integer> indexes;
    private Vector4 minimalCoordinatesOfSource;
    private boolean isPhantom;
    private ComplexObject parent;
    private ObjectConnectionType parentConnectionType;
    private Map<ObjectConnectionType, ComplexObject> connections;

    @SneakyThrows
    public ComplexObject(String fileName, Transform trans_v, String type_v, ColorCG color_v) {
        Model model = new OBJModel(fileName).toIndexedModel();
        vertexes = new ArrayList<>();
        for (int i = 0; i < model.getPositions().size(); i++) {
            vertexes.add(new Vertex(
                    model.getPositions().get(i),
                    model.getTexCoords().get(i),
                    model.getNormals().get(i)));
        }

        minimalCoordinatesOfSource = extractMinimalCoordinatesVector(vertexes);

        indexes = model.getIndices();
        color = color_v;
        texPaint = false;
        trans = trans_v;
        type = type_v;
        isPhantom = false;
        connections = new HashMap<>();
    }

    private ComplexObject() {

    }

    public Vector4 extractMinimalCoordinatesVector() {
        return extractMinimalCoordinatesVector(this.vertexes);
    }

    private Vector4 extractMinimalCoordinatesVector(List<Vertex> vertexes) {
        return new Vector4(vertexes.stream()
                .map(Vertex::getX).min(Float::compareTo).orElseThrow(RuntimeException::new),
                vertexes.stream()
                        .map(Vertex::getY).min(Float::compareTo).orElseThrow(RuntimeException::new),
                vertexes.stream()
                        .map(Vertex::getZ).min(Float::compareTo).orElseThrow(RuntimeException::new)
        );
    }

    public void setShiftedVertexes(Vector4 vector) {
        this.vertexes = shiftVertexes(vector.getX(), vector.getY(), vector.getZ());
    }

    public List<Vertex> shiftVertexes(float xShift, float yShift, float zShift) {
        return this.vertexes.stream()
                .map(v -> new Vertex(
                        new Vector4(v.getPosition())
                                .add(new Vector4(xShift, yShift, zShift, 0)),
                        new Vector4(v.getTexCoords()),
                        new Vector4(v.getNormal())))
                .collect(Collectors.toList());
    }

    public void addToObjects(List<PrimitiveObject> scene_objects) {
        Matrix transform = trans.getTransformation();

        if (texPaint) {
            for (int i = 0; i < indexes.size(); i += 3) {
                Triangle tmp = new Triangle(vertexes.get(indexes.get(i)).transform(transform, transform),//.Transform(screenSpaceTransform, identity).PerspectiveDivide(),
                        vertexes.get(indexes.get(i + 1)).transform(transform, transform),//.Transform(screenSpaceTransform, identity).PerspectiveDivide(),
                        vertexes.get(indexes.get(i + 2)).transform(transform, transform),//.Transform(screenSpaceTransform, identity).PerspectiveDivide(),
                        texture);
                scene_objects.add(tmp);

            }
        } else {
            for (int i = 0; i < indexes.size(); i += 3) {
                Triangle tmp = new Triangle(vertexes.get(indexes.get(i)).transform(transform, transform),
                        vertexes.get(indexes.get(i + 1)).transform(transform, transform),
                        vertexes.get(indexes.get(i + 2)).transform(transform, transform),
                        color);
                scene_objects.add(tmp);

            }
        }
    }

    public void draw(RenderSceneTriangle context, Matrix viewProjection, List<Source> lightsArray) {
        Matrix transform = trans.getTransformation();
        Matrix mvp = viewProjection.multiply(transform);

        for (int i = 0; i < indexes.size(); i += 3) {
            context.drawTriangle(
                    vertexes.get(indexes.get(i)).transform(mvp, transform),
                    vertexes.get(indexes.get(i + 1)).transform(mvp, transform),
                    vertexes.get(indexes.get(i + 2)).transform(mvp, transform),
                    texture,
                    color,
                    texPaint,
                    lightsArray,
                    isPhantom);
        }
    }

    public void setPhantom(boolean phantom) {
        isPhantom = phantom;
    }

    public Map<ObjectConnectionType, ComplexObject> getConnections() {
        return connections;
    }

    public List<ComplexObject> createAvailablePhantoms(ComplexObject phantomObject) {
        List<ComplexObject> availablePhantoms = new LinkedList<>();
        Supplier<Stream<Vertex>> vertexesSupplier = vertexes::stream;
        Arrays.stream(ObjectConnectionType.values())
                .filter(v -> !connections.containsKey(v))
                .forEach(key -> {
                    switch (key) {
                        case UP:
                            vertexesSupplier.get()
                                    .min(Comparator.comparingDouble(Vertex::getZ))
                                    .ifPresent(min -> {
                                        vertexesSupplier.get()
                                                .max(Comparator.comparingDouble(Vertex::getZ))
                                                .ifPresent(max -> {
                                                    ComplexObject phantom = createPhantom(phantomObject, 0, 0, max.getZ() - min.getZ(), ObjectConnectionType.UP);
                                                    connections.put(ObjectConnectionType.UP, phantom);
                                                    availablePhantoms.add(phantom);
                                                });
                                    });
                            break;

                        case DOWN:
                            vertexesSupplier.get()
                                    .min(Comparator.comparingDouble(Vertex::getZ))
                                    .ifPresent(min -> {
                                        vertexesSupplier.get()
                                                .max(Comparator.comparingDouble(Vertex::getZ))
                                                .ifPresent(max -> {
                                                    ComplexObject phantom = createPhantom(phantomObject, 0, 0, min.getZ() - max.getZ(), ObjectConnectionType.DOWN);
                                                    connections.put(ObjectConnectionType.DOWN, phantom);
                                                    availablePhantoms.add(phantom);
                                                });
                                    });
                            break;

                        case RIGHT:
                            vertexesSupplier.get()
                                    .min(Comparator.comparingDouble(Vertex::getY))
                                    .ifPresent(min -> {
                                        vertexesSupplier.get()
                                                .max(Comparator.comparingDouble(Vertex::getY))
                                                .ifPresent(max -> {
                                                    ComplexObject phantom = createPhantom(phantomObject, 0, max.getY() - min.getY(), 0, ObjectConnectionType.RIGHT);
                                                    connections.put(ObjectConnectionType.RIGHT, phantom);
                                                    availablePhantoms.add(phantom);
                                                });
                                    });
                            break;

                        case LEFT:
                            vertexesSupplier.get()
                                    .min(Comparator.comparingDouble(Vertex::getY))
                                    .ifPresent(min -> {
                                        vertexesSupplier.get()
                                                .max(Comparator.comparingDouble(Vertex::getY))
                                                .ifPresent(max -> {
                                                    ComplexObject phantom = createPhantom(phantomObject, 0, min.getY() - max.getY(), 0, ObjectConnectionType.LEFT);
                                                    connections.put(ObjectConnectionType.LEFT, phantom);
                                                    availablePhantoms.add(phantom);
                                                });
                                    });
                            break;

                        case FRONT:
                            vertexesSupplier.get()
                                    .min(Comparator.comparingDouble(Vertex::getX))
                                    .ifPresent(min -> {
                                        vertexesSupplier.get()
                                                .max(Comparator.comparingDouble(Vertex::getX))
                                                .ifPresent(max -> {
                                                    ComplexObject phantom = createPhantom(phantomObject, max.getX() - min.getX(), 0, 0, ObjectConnectionType.FRONT);
                                                    connections.put(ObjectConnectionType.FRONT, phantom);
                                                    availablePhantoms.add(phantom);
                                                });
                                    });
                            break;

                        case BACK:
                            vertexesSupplier.get()
                                    .min(Comparator.comparingDouble(Vertex::getX))
                                    .ifPresent(min -> {
                                        vertexesSupplier.get()
                                                .max(Comparator.comparingDouble(Vertex::getX))
                                                .ifPresent(max -> {
                                                    ComplexObject phantom = createPhantom(phantomObject, min.getX() - max.getX(), 0, 0, ObjectConnectionType.BACK);
                                                    connections.put(ObjectConnectionType.BACK, phantom);
                                                    availablePhantoms.add(phantom);
                                                });
                                    });
                            break;
                    }
                });
        return availablePhantoms;
    }

    private ComplexObject createPhantom(ComplexObject phantomObject, float xShift, float yShift, float zShift, ObjectConnectionType mountType) { // clone doesn't make deep copying
        ComplexObject copy = new ComplexObject();
        copy.vertexes = phantomObject.shiftVertexes(xShift, yShift, zShift);
        copy.connections = new HashMap<>();
        copy.indexes = new ArrayList<>(phantomObject.indexes);
        copy.texture = phantomObject.texture;
        copy.color = phantomObject.color.setOpacity(0.5f);//new ColorCG(0, 0, 1);
        copy.texPaint = texPaint;
        copy.trans = new Transform(phantomObject.getTrans());
        copy.type = phantomObject.type;
        copy.parent = this;
        copy.isPhantom = true;
        copy.parentConnectionType = mountType;
        copy.minimalCoordinatesOfSource = this.minimalCoordinatesOfSource;
        return copy;
    }
}
