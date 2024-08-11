package ogl2.exam;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;

public class TextureLoader {
    private Texture texture;

    public TextureLoader(String filePath) {
        try {
            texture = TextureIO.newTexture(new File(filePath), true);
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

