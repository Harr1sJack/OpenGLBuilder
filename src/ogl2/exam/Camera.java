package ogl2.exam;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Vec3f;

import java.awt.event.*;

public class Camera {

    private int width;
    private int height;
    public Vec3f position;
    private Vec3f orientation;
    private Vec3f up;
    private float moveSpeed = 0.7f;
    private float rotationSpeed = 0.01f;
    public Matrix4f viewMatrix;
    public Matrix4f projMatrix;

    private boolean isDragging = false;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private float sensitivity = 0.4f;

    public Camera(int width, int height, Vec3f position) {
        this.width = width;
        this.height = height;
        this.position = position;
        this.orientation = new Vec3f(0, 0, -1);
        this.up = new Vec3f(0, 1, 0);
        this.viewMatrix = new Matrix4f();
        this.projMatrix = new Matrix4f();
    }

    public void updateMatrix(float FOVdeg, float nearPlane, float farPlane) {
        // Set view matrix
        viewMatrix.loadIdentity();
        Vec3f target = new Vec3f(position).add(orientation);
        viewMatrix.setToLookAt(position, target, up, new Matrix4f());

        // Set projection matrix
        projMatrix.loadIdentity();
        projMatrix.setToPerspective(FOVdeg, (float) width / height, nearPlane, farPlane);
    }


    public void moveForward() {
        Vec3f forward = new Vec3f(orientation).scale(moveSpeed);
        position.add(forward);
    }

    public void moveBackward() {
        Vec3f backward = new Vec3f(orientation).scale(-moveSpeed);
        position.add(backward);
    }

    public void moveLeft() {
        Vec3f left = new Vec3f();
        left.cross(up,orientation);
        left.normalize();
        left.scale(moveSpeed);
        position.add(left);
    }

    public void moveRight() {
        Vec3f right = new Vec3f();
        right.cross(orientation,up);
        right.normalize();
        right.scale(moveSpeed);
        position.add(right);
    }


    public void rotateYaw(float angle) {
        Matrix4f rotationMatrix = new Matrix4f().setToRotationEuler(new Vec3f(0,-angle * rotationSpeed,0));
        rotationMatrix.mulVec3f(orientation, orientation);
    }

    public void rotatePitch(float angle) {
        Vec3f right = new Vec3f();
        right.cross(orientation,up);
        right.normalize();
        Matrix4f rotationMatrix = new Matrix4f().setToRotationEuler(new Vec3f(-angle * rotationSpeed,0,0));
        rotationMatrix.mulVec3f(orientation, orientation);
    }

    public void uploadToShader(GL4 gl, Shader shader, String projectionUniform, String viewUniform) {
        // Use the shader
        shader.use();

        // Send matrices to shaders
        int projectionMatrixLocation = gl.glGetUniformLocation(shader.programId, projectionUniform);
        int viewMatrixLocation = gl.glGetUniformLocation(shader.programId, viewUniform);

        float[] projectionMatrixArray = new float[16];
        float[] viewMatrixArray = new float[16];

        projMatrix.get(projectionMatrixArray);
        viewMatrix.get(viewMatrixArray);

        gl.glUniformMatrix4fv(projectionMatrixLocation, 1, false, projectionMatrixArray, 0);
        gl.glUniformMatrix4fv(viewMatrixLocation, 1, false, viewMatrixArray, 0);
    }


    public void handleKeyInput(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_W:
                moveForward();
                break;
            case KeyEvent.VK_S:
                moveBackward();
                break;
            case KeyEvent.VK_A:
                moveLeft();
                break;
            case KeyEvent.VK_D:
                moveRight();
                break;
            case KeyEvent.VK_LEFT:
                rotateYaw(-1);
                break;
            case KeyEvent.VK_RIGHT:
                rotateYaw(1);
                break;
            case KeyEvent.VK_UP:
                rotatePitch(-1);
                break;
            case KeyEvent.VK_DOWN:
                rotatePitch(1);
                break;
        }
    }

    public void MousePressed(MouseEvent mouseEvent) {
        System.out.println("mouse pressed");
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
            isDragging = true;
            lastMouseX = mouseEvent.getX();
            lastMouseY = mouseEvent.getY();
        }
    }

    public void MouseReleased(MouseEvent mouseEvent) {
        System.out.println("mouse released");
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
            isDragging = false;
        }
    }

    public void MouseDragged(MouseEvent mouseEvent) {
        System.out.println("mouse dragged");
        if (isDragging) {
            int deltaX = mouseEvent.getX() - lastMouseX;
            int deltaY = mouseEvent.getY() - lastMouseY;

            // Apply sensitivity to the delta values
            rotateYaw(deltaX * sensitivity);
            rotatePitch(deltaY * sensitivity);

            // Update last mouse positions
            lastMouseX = mouseEvent.getX();
            lastMouseY = mouseEvent.getY();
        }
    }
}
