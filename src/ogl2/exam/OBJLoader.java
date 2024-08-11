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
        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();

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
                            vertexIndices.add(Integer.parseInt(vertexData[0]) - 1); // Vertex index
                            textureIndices.add(vertexData.length > 1 && !vertexData[1].isEmpty() ? Integer.parseInt(vertexData[1]) - 1 : -1); // Texture index
                            normalIndices.add(vertexData.length > 2 && !vertexData[2].isEmpty() ? Integer.parseInt(vertexData[2]) - 1 : -1); // Normal index
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        float[] verticesArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = vertices.get(i);
        }

        float[] texturesArray = new float[textures.size()];
        for (int i = 0; i < textures.size(); i++) {
            texturesArray[i] = textures.get(i);
        }

        float[] normalsArray = new float[normals.size()];
        for (int i = 0; i < normals.size(); i++) {
            normalsArray[i] = normals.get(i);
        }

        // Create the index arrays for the mesh
        List<Integer> finalIndices = new ArrayList<>();
        for (int i = 0; i < vertexIndices.size(); i += 3) {
            int v0 = vertexIndices.get(i);
            int v1 = vertexIndices.get(i + 1);
            int v2 = vertexIndices.get(i + 2);

            // Check if indices are within bounds
            if (v0 >= 0 && v0 < verticesArray.length / 3 &&
                    v1 >= 0 && v1 < verticesArray.length / 3 &&
                    v2 >= 0 && v2 < verticesArray.length / 3) {
                finalIndices.add(v0);
                finalIndices.add(v1);
                finalIndices.add(v2);
            } else {
                System.out.println("Vertex index out of bounds: " + v0 + ", " + v1 + ", " + v2);
            }
        }

        int[] indicesArray = finalIndices.stream().mapToInt(i -> i).toArray();

        // Debug prints
        System.out.println("Vertices array size: " + verticesArray.length);
        System.out.println("Normals array size: " + normalsArray.length);
        System.out.println("Textures array size: " + texturesArray.length);
        System.out.println("Indices array size: " + indicesArray.length);

        return new Mesh(verticesArray, texturesArray, normalsArray, indicesArray, shader);
    }
}
