package ogl2.exam;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Shader {
    public int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public Shader(String vertexFilePath, String fragmentFilePath) {
        vertexShaderId = createShader(vertexFilePath, GL4.GL_VERTEX_SHADER);
        fragmentShaderId = createShader(fragmentFilePath, GL4.GL_FRAGMENT_SHADER);
        programId = createProgram(vertexShaderId, fragmentShaderId);
    }

    private int createShader(String filePath, int shaderType) {
        GL4 gl = GLContext.getCurrentGL().getGL4();

        String shaderSource = readFile(filePath);
        int shaderId = gl.glCreateShader(shaderType);
        gl.glShaderSource(shaderId, 1, new String[]{ shaderSource }, null);
        gl.glCompileShader(shaderId);

        int[] compileStatus = new int[1];
        gl.glGetShaderiv(shaderId, GL4.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == GL4.GL_FALSE) {
            int[] logLength = new int[1];
            gl.glGetShaderiv(shaderId, GL4.GL_INFO_LOG_LENGTH, logLength, 0);
            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(shaderId, logLength[0], (int[]) null, 0, log, 0);
            System.err.println("Error compiling shader: " + new String(log, StandardCharsets.UTF_8));
            System.exit(1);
        }

        return shaderId;
    }

    private int createProgram(int vertexShaderId, int fragmentShaderId) {
        GL4 gl = GLContext.getCurrentGL().getGL4();

        int programId = gl.glCreateProgram();
        gl.glAttachShader(programId, vertexShaderId);
        gl.glAttachShader(programId, fragmentShaderId);
        gl.glLinkProgram(programId);

        int[] linkStatus = new int[1];
        gl.glGetProgramiv(programId, GL4.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == GL4.GL_FALSE) {
            int[] logLength = new int[1];
            gl.glGetProgramiv(programId, GL4.GL_INFO_LOG_LENGTH, logLength, 0);
            byte[] log = new byte[logLength[0]];
            gl.glGetProgramInfoLog(programId, logLength[0], (int[]) null, 0, log, 0);
            System.err.println("Error linking program: " + new String(log, StandardCharsets.UTF_8));
            System.exit(1);
        }

        return programId;
    }

    private String readFile(String filePath) {
        StringBuilder shaderSource = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shaderSource.toString();
    }

    public void use() {
        GL4 gl = GLContext.getCurrentGL().getGL4();
        gl.glUseProgram(programId);
    }

    public int getUniformLocation(String name) {
        GL4 gl = GLContext.getCurrentGL().getGL4();
        return gl.glGetUniformLocation(programId, name);
    }
    public void setUniformMat4f(String name, float[] matrix) {
        GL4 gl = GLContext.getCurrentGL().getGL4();
        int location = getUniformLocation(name);
        gl.glUniformMatrix4fv(location, 1, false, matrix, 0);
    }
    public void setUniformSampler(String name, int value) {
        GL4 gl = GLContext.getCurrentGL().getGL4();
        int location = gl.glGetUniformLocation(programId, name);
        gl.glUniform1i(location, value);
    }
    public void stop() {
        GL4 gl = GLContext.getCurrentGL().getGL4();
        gl.glUseProgram(0);
    }

    public void cleanUp() {
        GL4 gl = GLContext.getCurrentGL().getGL4();
        gl.glDeleteShader(vertexShaderId);
        gl.glDeleteShader(fragmentShaderId);
        gl.glDeleteProgram(programId);
    }
}

