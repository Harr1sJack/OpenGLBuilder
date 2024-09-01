package ogl2.exam;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;

public class TextureLoader {
    private Texture texture;

    public TextureLoader(String filePath, GL4 gl) {
        try {
            texture = TextureIO.newTexture(new File(filePath), true);  // Load texture with mipmaps
            bind(gl);  // Bind the texture to configure it

            // Set texture filtering and mipmap options
            gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR_MIPMAP_LINEAR);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);

            // Generate mipmaps (optional if TextureIO already generates them)
            gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);

            unbind(gl);  // Unbind the texture after setting parameters
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load texture: " + filePath);
        }
    }

    public void bind(GL4 gl) {
        texture.bind(gl);
    }

    public void unbind(GL4 gl) {
        gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
    }

    public int getTextureId() {
        return texture.getTextureObject();
    }

    public Texture getTexture() {
        return texture;
    }
}
