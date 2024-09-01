package ogl2.exam;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.math.Vec3f;
import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Quaternion;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.random.RandomGenerator;

public class Mesh {

    public String name;
    public int meshId;
    private int vaoId;
    private int[] vboIds = new int[3]; // For vertices, textures, and normals
    private int eboId;
    private int vertexCount;

    private float[] vertices;
    private float[] textures;
    private float[] normals;
    private int[] indices;

    private Shader shader;

    // Transformation attributes
    private Vec3f position;
    private Quaternion rotation;
    private Vec3f scale;

    public Mesh(String name, float[] vertices, float[] textures, float[] normals, int[] indices, Shader shader) {
        this.name = name;
        this.meshId = RandomGenerator.getDefault().nextInt();
        System.out.println(name + Math.abs(meshId));
        this.vertices = vertices;
        this.textures = textures;
        this.normals = normals;
        this.indices = indices;
        this.shader = shader;
        this.vertexCount = indices.length;

        // Initialize transformation attributes
        this.position = new Vec3f(0.0f, 0.0f, 0.0f);
        this.rotation = new Quaternion().setIdentity();
        this.scale = new Vec3f(1.0f, 1.0f, 1.0f);

        initMesh();
    }

    private void initMesh() {
        GL4 gl = GLContext.getCurrentGL().getGL4();

        // Generate and bind VAO
        int[] vao = new int[1];
        gl.glGenVertexArrays(1, vao, 0);
        vaoId = vao[0];
        gl.glBindVertexArray(vaoId);

        // Generate and bind VBOs
        gl.glGenBuffers(3, vboIds, 0);

        // VBO for vertices
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vboIds[0]);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, FloatBuffer.wrap(vertices), GL4.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 3 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(0);

        // VBO for textures
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vboIds[1]);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, (long) textures.length * Float.BYTES, FloatBuffer.wrap(textures), GL4.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 2 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(1);

        // VBO for normals
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vboIds[2]);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, (long) normals.length * Float.BYTES, FloatBuffer.wrap(normals), GL4.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(2, 3, GL4.GL_FLOAT, false, 3 * Float.BYTES, 0);
        gl.glEnableVertexAttribArray(2);

        // Generate and bind EBO
        int[] ebo = new int[1];
        gl.glGenBuffers(1, ebo, 0);
        eboId = ebo[0];
        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, eboId);
        gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, (long) indices.length * Integer.BYTES,
                IntBuffer.wrap(indices), GL4.GL_STATIC_DRAW);

        // Unbind VAO
        gl.glBindVertexArray(0);
    }

    public void draw(boolean wireframe, boolean isSelected) {
        GL4 gl = GLContext.getCurrentGL().getGL4();
        if (wireframe) {
            gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_LINE);
        } else {
            gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);
        }

        // Create transformation matrix
        Matrix4f modelMatrix = new Matrix4f().loadIdentity();

        modelMatrix.translate(position,modelMatrix);
        float[] modelMatrixArray = new float[16];
        modelMatrix.get(modelMatrixArray);

        // Send the model matrix to the shader
        shader.use();
        shader.setUniformMat4f("model", modelMatrixArray);

        int isSelectedLoc = shader.getUniformLocation("isSelected");
        gl.glUniform1i(isSelectedLoc, isSelected ? 1 : 0);

        // Draw the mesh
        gl.glBindVertexArray(vaoId);
        gl.glDrawElements(GL4.GL_TRIANGLES, vertexCount, GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);

        shader.stop();
    }

    // Getters and setters for position, rotation, and scale
    public Vec3f getPosition() {
        return position;
    }

    public void setPosition(Vec3f position) {
        this.position = position;
    }
    public void setPositionY(float posY) {
        this.position = new Vec3f(position.x(),posY,position.z());
        System.out.println(position);
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public Vec3f getScale() {
        return scale;
    }

    public void setScale(Vec3f scale) {
        this.scale = scale;
    }
}
