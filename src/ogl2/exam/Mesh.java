package ogl2.exam;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

    private int vaoId;
    private int[] vboIds = new int[3]; // For vertices, textures, and normals
    private int eboId;
    private int vertexCount;

    private float[] vertices;
    private float[] textures;
    private float[] normals;
    private int[] indices;

    private Shader shader;

    public Mesh(float[] vertices, float[] textures, float[] normals, int[] indices, Shader shader) {
        this.vertices = vertices;
        this.textures = textures;
        this.normals = normals;
        this.indices = indices;
        this.shader = shader;
        this.vertexCount = indices.length;
        initMesh();
        System.out.println("Verts");
        for(var i:vertices)
        {
            System.out.println(i);
        }
        System.out.println("Texture");
        for(var i:textures)
        {
            System.out.println(i);
        }
        System.out.println("Normals");
        for(var i:normals)
        {
            System.out.println(i);
        }
        System.out.println("Indicies");
        for(var i:indices)
        {
            System.out.println(i);
        }
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

    public void draw() {
        GL4 gl = GLContext.getCurrentGL().getGL4();

        shader.use();  // Ensure your Shader class has a use() method

        gl.glBindVertexArray(vaoId);
        gl.glDrawElements(GL4.GL_TRIANGLES, vertexCount, GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
    }
}
