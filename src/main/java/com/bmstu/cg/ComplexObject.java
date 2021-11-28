package com.bmstu.cg;

import com.bmstu.cg.enums.ObjectConnectionType;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Accessors(chain = true)
@Getter
public class ComplexObject {
//        public String uuid;
    public ImageCG texture;
    public ColorCG color;
    public boolean texPaint;
    public Transform trans;
    public String type;
    private List<Vertex> vertexes;
    private List<Integer> indexes;
    private boolean isPhantom;
    private ComplexObject parent;
    private ObjectConnectionType parentConnectionType;
    private Map<ObjectConnectionType, ComplexObject> connections;

    @SneakyThrows
    public ComplexObject(String fileName, Transform trans_v, String type_v, ColorCG color_v) {
//        uuid = UUID.randomUUID().toString();
        Model model = new OBJModel(fileName).toIndexedModel();
//        model.get
        vertexes = new ArrayList<>();
        for (int i = 0; i < model.getPositions().size(); i++) {
            vertexes.add(new Vertex(
                    model.getPositions().get(i),
                    model.getTexCoords().get(i),
                    model.getNormals().get(i)));
        }

        indexes = model.getIndices();
        color = color_v;
        texPaint = false;
        trans = trans_v;
        type = type_v;
        isPhantom = false;
        connections = new HashMap<>();
    }

    public ComplexObject(ComplexObject object) {
//        this.vertexes = object.getVertexes().stream()
//                .map(v -> new Vertex(
//                        new Vector4(v.getPosition()),
//                        new Vector4(v.getTexCoords()),
//                        new Vector4(v.getNormal())))
//                .collect(Collectors.toList());
//        this.indexes = new ArrayList<>(phantomObject.indexes);
//        this.texture = phantomObject.texture;
//        this.color = phantomObject.color.setOpacity(0.5f);//new ColorCG(0, 0, 1);
//        this.texPaint = texPaint;
//        this.trans = new Transform(phantomObject.getTrans());
//        this.type = phantomObject.type;
//        this.parent = this;
//        this.isPhantom = true;
//        this.parentConnectionType = mountType;
    }

    private ComplexObject() {

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
//        System.out.println("v1: " + vertexes.get(indexes.get(0)).transform(mvp, transform).getPosition()
//        + " v2: " + vertexes.get(indexes.get(1)).transform(mvp, transform).getPosition()
//        + " v3: " + vertexes.get(indexes.get(2)).transform(mvp, transform).getPosition());
        ColorCG originalColor = color;
//        System.out.println("OPACITY " + color.opacity);
        if (isPhantom) {
            color = new ColorCG(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
        }
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
        color = originalColor;
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
        System.out.println("Creating phantpms for " + this.getType() + " y: " + this.getVertexes().stream()
                .map(v -> String.valueOf(v.getPosition().getY())).collect(Collectors.joining(", ")));
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
                                                    System.out.println("UP CREATING");
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
                                                    System.out.println("DOWN CREATING");
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
                                                    System.out.println("RIGHT CREATING");
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
                                                    System.out.println("LEFT CREATING");
                                                    ComplexObject phantom = createPhantom(phantomObject,0, min.getY() - max.getY(), 0, ObjectConnectionType.LEFT);
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
                                                    System.out.println("FRONT CREATING");
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
                                                    System.out.println("BACK CREATING");
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
        copy.vertexes = phantomObject.getVertexes().stream()
                .map(v -> new Vertex(
                        new Vector4(v.getPosition())
                                .add(new Vector4(xShift, yShift, zShift, 0)),
                        new Vector4(v.getTexCoords()),
                        new Vector4(v.getNormal())))
                .collect(Collectors.toList());
        System.out.println("Creating phantom on " + mountType + " y " + copy.getVertexes().stream()
                .map(v -> String.valueOf(v.getPosition().getY())).collect(Collectors.joining(", ")));

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
//        copy.uuid = "Phantom-" + UUID.randomUUID().toString();
        return copy;
    }
}
