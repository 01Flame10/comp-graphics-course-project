package com.bmstu.cg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class OBJModel {
    private class OBJIndex {
        private int vertexIndex;
        private int texCoordIndex;
        private int normalIndex;

        public int getVertexIndex() {
            return vertexIndex;
        }

        public int getTexCoordIndex() {
            return texCoordIndex;
        }

        public int getNormalIndex() {
            return normalIndex;
        }

        public void setVertexIndex(int val) {
            vertexIndex = val;
        }

        public void setTexCoordIndex(int val) {
            texCoordIndex = val;
        }

        public void setNormalIndex(int val) {
            normalIndex = val;
        }

        @Override
        public int hashCode() {
            final int BASE = 17;
            final int MULTIPLIER = 31;

            int result = BASE;

            result = MULTIPLIER * result + vertexIndex;
            result = MULTIPLIER * result + texCoordIndex;
            result = MULTIPLIER * result + normalIndex;

            return result;
        }
    }

    private List<Vector4> positions;
    private List<Vector4> texCoords;
    private List<Vector4> normals;
    private List<OBJIndex> indices;
    private boolean hasTexCoords;
    private boolean hasNormals;

    private static String[] RemoveEmptyStrings(String[] data) {
        List<String> result = new ArrayList<String>();

        for (int i = 0; i < data.length; i++)
            if (!data[i].equals(""))
                result.add(data[i]);

        String[] res = new String[result.size()];
        result.toArray(res);

        return res;
    }

    public OBJModel(String fileName) throws IOException {
        positions = new ArrayList<>();
        texCoords = new ArrayList<>();
        normals = new ArrayList<>();
        indices = new ArrayList<>();
        hasTexCoords = false;
        hasNormals = false;

        BufferedReader meshReader = null;

        meshReader = new BufferedReader(new FileReader(fileName));
        String line;

        while ((line = meshReader.readLine()) != null) {
            String[] tokens = line.split(" ");
            tokens = RemoveEmptyStrings(tokens);

            if (tokens.length != 0 && !tokens[0].equals("#")) {
                switch (tokens[0]) {
                    case "v":
                        positions.add(new Vector4(Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3]), 1));
                        break;
                    case "vt":
                        texCoords.add(new Vector4(Float.parseFloat(tokens[1]),
                                1.0f - Float.parseFloat(tokens[2]), 0, 0));
                        break;
                    case "vn":
                        normals.add(new Vector4(Float.parseFloat(tokens[1]),
                                Float.parseFloat(tokens[2]),
                                Float.parseFloat(tokens[3]), 0));
                        break;
                    case "f":
                        for (int i = 0; i < tokens.length - 3; i++) {
                            indices.add(ParseOBJIndex(tokens[1]));
                            indices.add(ParseOBJIndex(tokens[2 + i]));
                            indices.add(ParseOBJIndex(tokens[3 + i]));
                        }
                        break;
                }
            }
        }


        meshReader.close();
    }

    public Model toIndexedModel() {
        Model result = new Model();
        Model normalModel = new Model();
        Map<OBJIndex, Integer> resultIndexMap = new HashMap<OBJIndex, Integer>();
        Map<Integer, Integer> normalIndexMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();

        for (OBJIndex currentIndex : indices) {
            Vector4 currentPosition = positions.get(currentIndex.getVertexIndex());
            Vector4 currentTexCoord;
            Vector4 currentNormal;

            if (hasTexCoords)
                currentTexCoord = texCoords.get(currentIndex.getTexCoordIndex());
            else
                currentTexCoord = new Vector4(0, 0, 0, 0);

            if (hasNormals)
                currentNormal = normals.get(currentIndex.getNormalIndex());
            else
                currentNormal = new Vector4(0, 0, 0, 0);

            Integer modelVertexIndex = resultIndexMap.get(currentIndex);

            if (modelVertexIndex == null) {
                modelVertexIndex = result.getPositions().size();
                resultIndexMap.put(currentIndex, modelVertexIndex);

                result.getPositions().add(currentPosition);
                result.getTexCoords().add(currentTexCoord);
                if (hasNormals)
                    result.getNormals().add(currentNormal);
            }

            Integer normalModelIndex = normalIndexMap.get(currentIndex.getVertexIndex());

            if (normalModelIndex == null) {
                normalModelIndex = normalModel.getPositions().size();
                normalIndexMap.put(currentIndex.getVertexIndex(), normalModelIndex);

                normalModel.getPositions().add(currentPosition);
                normalModel.getTexCoords().add(currentTexCoord);
                normalModel.getNormals().add(currentNormal);
                normalModel.getTangents().add(new Vector4(0, 0, 0, 0));
            }

            result.getIndices().add(modelVertexIndex);
            normalModel.getIndices().add(normalModelIndex);
            indexMap.put(modelVertexIndex, normalModelIndex);
        }

        if (!hasNormals) {
            normalModel.calculateNormals();

            for (int i = 0; i < result.getPositions().size(); i++)
                result.getNormals().add(normalModel.getNormals().get(indexMap.get(i)));
        }

        normalModel.calculateTangents();

        for (int i = 0; i < result.getPositions().size(); i++)
            result.getTangents().add(normalModel.getTangents().get(indexMap.get(i)));

        return result;
    }

    private OBJIndex ParseOBJIndex(String token) {
        String[] values = token.split("/");

        OBJIndex result = new OBJIndex();
        result.setVertexIndex(Integer.parseInt(values[0]) - 1);

        if (values.length > 1) {
            if (!values[1].isEmpty()) {
                hasTexCoords = true;
                result.setTexCoordIndex(Integer.parseInt(values[1]) - 1);
            }

            if (values.length > 2) {
                hasNormals = true;
                result.setNormalIndex(Integer.parseInt(values[2]) - 1);
            }
        }

        return result;
    }
}
