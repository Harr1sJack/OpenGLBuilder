package ogl2.exam;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

    public static Mesh loadMesh(String filename, Shader shader) throws IOException {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Float> finalVertices = new ArrayList<>();
        List<Float> finalTextures = new ArrayList<>();
        List<Float> finalNormals = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                switch (tokens[0]) {
                    case "v":
                        vertices.add(Float.parseFloat(tokens[1]));
                        vertices.add(Float.parseFloat(tokens[2]));
                        vertices.add(Float.parseFloat(tokens[3]));
                        break;
                    case "vt":
                        textures.add(Float.parseFloat(tokens[1]));
                        textures.add(Float.parseFloat(tokens[2]));
                        break;
                    case "vn":
                        normals.add(Float.parseFloat(tokens[1]));
                        normals.add(Float.parseFloat(tokens[2]));
                        normals.add(Float.parseFloat(tokens[3]));
                        break;
                    case "f":
                        for (int i = 1; i <= 3; i++) {
                            String[] vertexData = tokens[i].split("/");
                            int vertexIndex = Integer.parseInt(vertexData[0]) - 1;
                            int textureIndex = vertexData.length > 1 && !vertexData[1].isEmpty() ? Integer.parseInt(vertexData[1]) - 1 : -1;
                            int normalIndex = vertexData.length > 2 && !vertexData[2].isEmpty() ? Integer.parseInt(vertexData[2]) - 1 : -1;

                            indices.add(finalVertices.size() / 3);

                            // Add vertex data
                            finalVertices.add(vertices.get(vertexIndex * 3));
                            finalVertices.add(vertices.get(vertexIndex * 3 + 1));
                            finalVertices.add(vertices.get(vertexIndex * 3 + 2));

                            // Add texture data
                            if (textureIndex != -1) {
                                finalTextures.add(textures.get(textureIndex * 2));
                                finalTextures.add(textures.get(textureIndex * 2 + 1));
                            } else {
                                finalTextures.add(0.0f);
                                finalTextures.add(0.0f);
                            }

                            // Add normal data
                            if (normalIndex != -1) {
                                finalNormals.add(normals.get(normalIndex * 3));
                                finalNormals.add(normals.get(normalIndex * 3 + 1));
                                finalNormals.add(normals.get(normalIndex * 3 + 2));
                            } else {
                                finalNormals.add(0.0f);
                                finalNormals.add(0.0f);
                                finalNormals.add(0.0f);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        float[] verticesArray = new float[finalVertices.size()];
        for (int i = 0; i < finalVertices.size(); i++) {
            verticesArray[i] = finalVertices.get(i);
        }

        float[] texturesArray = new float[finalTextures.size()];
        for (int i = 0; i < finalTextures.size(); i++) {
            texturesArray[i] = finalTextures.get(i);
        }

        float[] normalsArray = new float[finalNormals.size()];
        for (int i = 0; i < finalNormals.size(); i++) {
            normalsArray[i] = finalNormals.get(i);
        }

        int[] indicesArray = indices.stream().mapToInt(i -> i).toArray();

        System.out.println("Vertices array size: " + verticesArray.length);
        System.out.println("Normals array size: " + normalsArray.length);
        System.out.println("Textures array size: " + texturesArray.length);
        System.out.println("Indices array size: " + indicesArray.length);

        return new Mesh(verticesArray, texturesArray, normalsArray, indicesArray, shader);
    }

    public static QuadMesh loadQuadMesh(String filename, Shader shader) throws IOException {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Float> finalVertices = new ArrayList<>();
        List<Float> finalTextures = new ArrayList<>();
        List<Float> finalNormals = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                switch (tokens[0]) {
                    case "v":
                        vertices.add(Float.parseFloat(tokens[1]));
                        vertices.add(Float.parseFloat(tokens[2]));
                        vertices.add(Float.parseFloat(tokens[3]));
                        break;
                    case "vt":
                        textures.add(Float.parseFloat(tokens[1]));
                        textures.add(Float.parseFloat(tokens[2]));
                        break;
                    case "vn":
                        normals.add(Float.parseFloat(tokens[1]));
                        normals.add(Float.parseFloat(tokens[2]));
                        normals.add(Float.parseFloat(tokens[3]));
                        break;
                    case "f":
                        for (int i = 1; i <= 3; i++) {
                            String[] vertexData = tokens[i].split("/");
                            int vertexIndex = Integer.parseInt(vertexData[0]) - 1;
                            int textureIndex = vertexData.length > 1 && !vertexData[1].isEmpty() ? Integer.parseInt(vertexData[1]) - 1 : -1;
                            int normalIndex = vertexData.length > 2 && !vertexData[2].isEmpty() ? Integer.parseInt(vertexData[2]) - 1 : -1;

                            indices.add(finalVertices.size() / 3);

                            // Add vertex data
                            finalVertices.add(vertices.get(vertexIndex * 3));
                            finalVertices.add(vertices.get(vertexIndex * 3 + 1));
                            finalVertices.add(vertices.get(vertexIndex * 3 + 2));

                            // Add texture data
                            if (textureIndex != -1) {
                                finalTextures.add(textures.get(textureIndex * 2));
                                finalTextures.add(textures.get(textureIndex * 2 + 1));
                            } else {
                                finalTextures.add(0.0f);
                                finalTextures.add(0.0f);
                            }

                            // Add normal data
                            if (normalIndex != -1) {
                                finalNormals.add(normals.get(normalIndex * 3));
                                finalNormals.add(normals.get(normalIndex * 3 + 1));
                                finalNormals.add(normals.get(normalIndex * 3 + 2));
                            } else {
                                finalNormals.add(0.0f);
                                finalNormals.add(0.0f);
                                finalNormals.add(0.0f);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        float[] verticesArray = new float[finalVertices.size()];
        for (int i = 0; i < finalVertices.size(); i++) {
            verticesArray[i] = finalVertices.get(i);
        }

        float[] texturesArray = new float[finalTextures.size()];
        for (int i = 0; i < finalTextures.size(); i++) {
            texturesArray[i] = finalTextures.get(i);
        }

        float[] normalsArray = new float[finalNormals.size()];
        for (int i = 0; i < finalNormals.size(); i++) {
            normalsArray[i] = finalNormals.get(i);
        }

        int[] indicesArray = indices.stream().mapToInt(i -> i).toArray();

        System.out.println("Vertices array size: " + verticesArray.length);
        System.out.println("Normals array size: " + normalsArray.length);
        System.out.println("Textures array size: " + texturesArray.length);
        System.out.println("Indices array size: " + indicesArray.length);

        return new QuadMesh(verticesArray, texturesArray, normalsArray, indicesArray, shader);
    }
}
