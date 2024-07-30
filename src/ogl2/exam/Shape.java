package ogl2.exam;

import com.jogamp.opengl.GL2;

public class Shape {
    /* Important shapes: Cube, Cuboid */

    public static void cube(GL2 gl2){
        cube(gl2, 1, true);
    } // draw a cube
    public static void cuboid(GL2 gl2){ // draw a cuboid
        cuboid(gl2, 1, true);
    } // draw a cuboid

    public static void cylinder(GL2 gl){ // draw a cylinder
        cylinder(gl, 0.5, 1, 16, 10, 5, true);
    }

    public static void sphere(GL2 gl){
        sphere(gl, 0.7, 10, 10, true);
    }

    // build a sphere
    private static void sphere(GL2 gl , double radius, int slices, int stacks, boolean makeTextureCoordinates){

        for(int j=0; j<stacks; j++){
            double latitude1 = (Math.PI / stacks) * j - Math.PI / 2;
            double latitude2 = (Math.PI / stacks) * (j + 1) - Math.PI / 2;

            double sinLatitude1 = Math.sin(latitude1);
            double cosLatitude1 = Math.cos(latitude1);
            double sinLatitude2 = Math.sin(latitude2);
            double cosLatitude2 = Math.cos(latitude2);

            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int i = 0; i <= slices; i++) {
                double longitude = (2 * Math.PI / slices) * i;
                double sinLongitude = Math.sin(longitude);
                double cosLongitude = Math.cos(longitude);

                double x1 = cosLongitude * cosLatitude1;
                double y1 = sinLongitude * cosLatitude1;
                double z1 = sinLatitude1;

                double x2 = cosLongitude * cosLatitude2;
                double y2 = sinLongitude * cosLatitude2;
                double z2 = sinLatitude2;

                // draw the lines for the longitude
                gl.glNormal3d(x2, y2, z2);
                if(makeTextureCoordinates){
                    gl.glTexCoord2d(1.0/slices*i, 1.0/stacks*(j+1));
                }
                gl.glVertex3d(radius*x2, radius*y2, radius*z2);

                // draw the lines for the latitude
                gl.glNormal3d(x1, y1, z1);
                if(makeTextureCoordinates){
                    gl.glTexCoord2d(1.0/slices*i, 1.0/stacks*j);
                }
                gl.glVertex3d(radius*x1, radius*y1, radius*z1);
            }
            gl.glEnd();
        }
    }

    // build a cylinder
    private static void cylinder(GL2 gl, double radius, double height, int slices, int stacks, int rings,
                                 boolean makeTextureCoordinates){
        if(radius <= 0) throw new IllegalArgumentException("Radius must be positive");
        if(height <= 0) throw new IllegalArgumentException("Height must be positive");
        if(slices < 3) throw new IllegalArgumentException("Number of slices must be at least 3.");
        if(stacks < 2) throw new IllegalArgumentException("Number of stacks must be at least 2.");

        // body
        for(int j = 0; j < stacks; j++){
            double z1 = (height / stacks) * j;
            double z2 = (height / stacks) * (j+1);
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for(int i = 0; i <= slices; i++){
                double longitude = (2 * Math.PI / slices) * i;
                double sinLongitude = Math.sin(longitude);
                double cosineLongitude = Math.cos(longitude);
                double x = cosineLongitude;
                double y = sinLongitude;
                gl.glNormal3d(x, y, 0);
                if(makeTextureCoordinates){
                    gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * (j+1));
                }
                gl.glVertex3d(radius*x, radius*y, z2);
                if(makeTextureCoordinates){
                    gl.glTexCoord2d(1.0 / slices * i, 1.0 / stacks * j);
                }
                gl.glVertex3d(radius * x, radius*y, z1);
            }
            gl.glEnd();
        }

        // draw the top and bottom
        if(rings > 0){
            gl.glNormal3d(0, 0, 1);
            for (int j=0; j<rings; j++){
                double d1 = (1.0 / rings) * j;
                double d2 = (1.0 / rings) * (j+1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {  // TODO: ADD  "i<= slices"
                    double angle = (2* Math.PI / slices) * i;
                    double sin = Math.sin(angle);
                    double cosine = Math.cos(angle);
                    if(makeTextureCoordinates){
                        gl.glTexCoord2d(1 * (1 + cosine * d1), 0.5 * (1 + sin * d1));
                    }
                    gl.glVertex3d(radius * cosine * d1, radius * sin * d1, height);

                    if(makeTextureCoordinates){
                        gl.glTexCoord2d(1 * (1 + cosine * d2), 0.5 * (1 + sin * d2));
                    }
                    gl.glVertex3d(radius * cosine * d2, radius * sin * d2, height);
                }
                gl.glEnd();
            }
            gl.glNormal3d(0, 0, -1);

            for (int j=0; j<rings; j++){
                double d1 = (1.0 / rings) * j;
                double d2 = (1.0 / rings) * (j+1);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                for (int i = 0; i <= slices; i++) {  // TODO: ADD  "i<= slices"
                    double angle = (2* Math.PI / slices) * i;
                    double sin = Math.sin(angle);
                    double cosine = Math.cos(angle);
                    if(makeTextureCoordinates){
                        gl.glTexCoord2d(0.5 * (1 + cosine * d2), 0.5 * (1 + sin * d2));
                    }
                    gl.glVertex3d(radius * cosine * d2, radius * sin * d2, 0);

                    if(makeTextureCoordinates){
                        gl.glTexCoord2d(0.5 * (1 + cosine * d1), 0.5 * (1 + sin * d1));
                    }
                    gl.glVertex3d(radius * cosine * d1, radius * sin * d1, 0);
                }
                gl.glEnd();
            }
        }
    }


    // build and draw a rectangular pyramid
    public static void rectangularPyramid(GL2 gl){
        gl.glPushMatrix();

            // draw the four triangles
            gl.glBegin(GL2.GL_TRIANGLES);
                // front
                gl.glNormal3f(0, 0, 1);
                gl.glVertex3f(0.0f, 0.5f, 0.0f);
                gl.glNormal3f(0, 0, -1);
                gl.glVertex3f(-0.5f, -0.5f, 0.5f);
                gl.glNormal3f(0, 0, -1);
                gl.glVertex3f(0.5f, -0.5f, 0.5f);

                // right
                gl.glNormal3f(0, 0, 1);
                gl.glVertex3f(0.0f, 0.5f, 0.0f);
                gl.glNormal3f(0, 0, 1);
                gl.glVertex3f(0.5f, -0.5f, 0.5f);
                gl.glNormal3f(0, 0, -1);
                gl.glVertex3f(0.5f, -0.5f, -0.5f);

                // left
                gl.glNormal3f(0, 0, 1);
                gl.glVertex3f(0.0f, 0.5f, 0.0f);
                gl.glNormal3f(0, 0, 1);
                gl.glVertex3f(-0.5f, -0.5f, -0.5f);
                gl.glNormal3f(0, 0, -1);
                gl.glVertex3f(-0.5f, -0.5f, 0.5f);

                // back
                gl.glNormal3f(0, 0, 1);
                gl.glVertex3f(0.0f, 0.5f, 0.0f);
                gl.glNormal3f(0, 0, 1);
                gl.glVertex3f(0.5f, -0.5f, -0.5f);
                gl.glNormal3f(0, 0, -1);
                gl.glVertex3f(-0.5f, -0.5f, -0.5f);

            gl.glEnd();

            // the bottom square
            gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3f(-0.5f, -0.5f, 0.5f);
                gl.glVertex3f(0.5f, -0.5f, 0.5f);
                gl.glVertex3f(0.5f, -0.5f, -0.5f);
                gl.glVertex3f(-0.5f, -0.5f, -0.5f);
            gl.glEnd();
        gl.glPopMatrix();
    }

    // build a cube
    public static void cube(GL2 gl, double side, boolean makeTextureCoordinate){
        // push the current matrix down in the stack
        gl.glPushMatrix();

            gl.glPushMatrix();
                gl.glRotatef(0, 0, 1, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glRotatef(90, 0, 1, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glRotatef(180, 0, 1, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glRotatef(270, 0, 1, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glRotatef(90, 1, 0, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glRotatef(-90, 1, 0, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

        gl.glPopMatrix();
    }

    // build a cuboid
    private static void cuboid(GL2 gl, double side, boolean makeTextureCoordinate){
        gl.glPushMatrix();

            // draw all the sides of the cuboid
            gl.glPushMatrix();
                gl.glScalef(2f, 1f, 1f);
                gl.glRotatef(0, 0, 1, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glScalef(2f, 1f, 1f);
                gl.glRotatef(90, 0, 1, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glScalef(2f, 1f, 1f);
                gl.glRotatef(180, 0, 1, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glScalef(2f, 1f, 1f);
                gl.glRotatef(270, 0, 1, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

            // top and bottom of the cuboid
            gl.glPushMatrix();
                gl.glScalef(2f, 1f, 1f);
                gl.glRotatef(90, 1, 0, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

            gl.glPushMatrix();
                gl.glScalef(2f, 1f, 1f);
                gl.glRotatef(-90, 1, 0, 0);
                gl.glTranslated(0, 0, side/2);
                square(gl, side, makeTextureCoordinate);
            gl.glPopMatrix();

        gl.glPopMatrix();
    }

    // draw a square in the (x,y) plane, with given side length
    public static void square(GL2 gl, double side, boolean makeTextureCoordinate) {

        double radius = side / 2;
        gl.glBegin(GL2.GL_POLYGON);

            // vector for lighting calculation
            gl.glNormal3f(0, 0, 1);

            // top left corner of a square
            if(makeTextureCoordinate){
                gl.glTexCoord2d(0, 1);
            }
            gl.glVertex2d(-radius, radius);

            // bottom left corner of a square
            if(makeTextureCoordinate){
                gl.glTexCoord2d(0, 0);
            }
            gl.glVertex2d(-radius, -radius);

            // bottom right corner of a square
            if(makeTextureCoordinate){
                gl.glTexCoord2d(1, 0);
            }
            gl.glVertex2d(radius, -radius);

            // top right corner of a square
            if(makeTextureCoordinate){
                gl.glTexCoord2d(1, 1);
            }
            gl.glVertex2d(radius, radius);

        gl.glEnd();
    }
}
