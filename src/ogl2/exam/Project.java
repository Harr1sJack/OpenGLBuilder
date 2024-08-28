package ogl2.exam;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Vec3f;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Project extends GLCanvas implements GLEventListener, KeyListener, MouseListener,MouseMotionListener {

    // create multiple checkboxes
    private JCheckBox lightOnOff;
    private JCheckBox globalAmbientLight;
    private JCheckBox diffuseLight;
    private JCheckBox specularLight;
    private JCheckBox ambientLight;

    // create multiple buttons
    private JButton removeButton;
    private JButton addButton;
    private JButton finishButton;
    private JButton helpButton;
    private JButton quitButton;
    private JButton newGameButton;

    // placeholder to display text
    private JLabel label;

    //palette UI's
    private JLabel imageLabel1;
    private JLabel imageLabel2;
    private JLabel imageLabel3;

    // the main gui where all the components like buttons,
    // checkboxes, etc.. are added
    private JFrame frame;

    // initialize the canvas for the window
    private GLCanvas canvas;
    private FPSAnimator animator;
    private int WINDOW_WIDTH = 700;
    private int WINDOW_HEIGHT = 700;
    private static final String TITLE = "The Shapes";
    private static final int FPS = 144;

    // provide access to opengl utility libraries like scaling, projections, etc..
    private GLU glu;

    // render bitmapped java 2d text unto our opengl window
    //private TextRenderer textRenderer;
    //private TextRenderer textMatch;

    // initialize some global variables to randomly position our
    // shapes on the blueprint
    private int randomTop;
    private int randomTopTwo;
    private int randomLeft;
    private int randomRight;
    private int randomBottom;

    // initialize a nameId for picking the shapes
    private int nameId = 0;

    // id for the palette shapes insert to the blueprint
    private int top_idn = 0;
    private int top_two_idn = 0;
    private int left_idn = 0;
    private int right_idn = 0;
    private int bottom_idn = 0;

    // color of the palette shape inserted into the blueprint
    private float addShapeRed = 0f;
    private float addShapeGreen = 0f;
    private float addShapeBlue = 0f;

    // default color for the shapes to be drawn
    private float defaultRed = 0.5f;
    private float defaultGreen = 0.5f;
    private float defaultBlue = 0.5f;

    private float redTop = defaultRed;
    private float greenTop = defaultGreen;
    private float blueTop = defaultBlue;

    private float redTopTwo = defaultRed;
    private float greenTopTwo = defaultGreen;
    private float blueTopTwo = defaultBlue;

    private float redLeft = defaultRed;
    private float greenLeft = defaultGreen;
    private float blueLeft = defaultBlue;

    private float redRight = defaultRed;
    private float greenRight = defaultGreen;
    private float blueRight = defaultBlue;

    private float redBottom = defaultRed;
    private float greenBottom = defaultGreen;
    private float blueBottom = defaultBlue;

    // the position of the shape on the blueprint
    private double topX;
    private double topY;
    private double topZ;
    private double topTwoX;
    private double topTwoY;
    private double topTwoZ;
    private double leftX;
    private double leftY;
    private double leftZ;
    private double rightX;
    private double rightY;
    private double rightZ;

    private double bottomX;
    private double bottomY;
    private double bottomZ;

    // initialize a variable to traverse through the blueprint
    private int traverse = 0;

    // a constant value for scaling the shapes in the blueprint (increase/decrease)
    private final float scaleDelta = 0.1f;

    // initialize the scale of the shapes inserted into the blueprint
    private float scaleTop = 0.5f;
    private float scaleTopTwo = 0.5f;
    private float scaleLeft = 0.5f;
    private float scaleRight = 0.5f;
    private float scaleBottom = 0.5f;

    // set the angle of the shapes inserted into the blueprint
    private int angleTopX = 90;
    private int angleTopY = 90;
    private int angleTopZ = 90;
    private int angleTopTwoX = 90;
    private int angleTopTwoY = 90;
    private int angleTopTwoZ = 90;
    private int angleLeftX = 90;
    private int angleLeftY = 90;
    private int angleLeftZ = 90;
    private int angleRightX = 90;
    private int angleRightY = 90;
    private int angleRightZ = 90;

    private int angleBottomX = 90;
    private int angleBottomY = 90;
    private int angleBottomZ = 90;

    // a constant to rotate the shapes inserted into the blueprint (at an angle)
    private float rotate = 1;

    // initialize the colors of the shape drawn on the palette
    private float paletteRed = 0.45f;
    private float paletteGreen = 0.20f;
    private float paletteBlue = 0.75f;

    // initialize the id for where the shapes will be placed
    private static final int TOP_ID = 1;
    private static final int LEFT_ID = 2;
    private static final int RIGHT_ID = 3;
    private static final int BOTTOM_ID = 4;
    private static final int TOP_TWO_ID = 5;


    // initialize variables for the shapes on the palette & blueprint
    Map<Integer, String> shapes = new HashMap<>();

    private static final int CUBE_ID = 1;
    private static final int CUBOID_ID = 2;
    private static final int RECTANGULAR_PYRAMID_ID = 3;
    private static final int CYLINDER_ID = 4;
    private static final int SPHERE_ID = 5;


    // total number of shapes (n+1)
    private static int TOTAL_NUM_OF_SHAPES = 0;

    // size of the buffer to store in memory, information about the selected shape
    private static final int BUFSIZE = 512;
    private IntBuffer selectBuffer;

    // used in selecting the object we want to draw on the blueprint
    // based on the (X,Y) coordinate
    private boolean inSelectionMode = false;
    private int xCursor = 0;
    private int yCursor = 0;

    // the current angle of the blueprint
    private int currentAngleOfRotationX = 0;
    private int currentAngleOfRotationY = 0;
    private int currentAngleOfVisibleField = 55; // camera

    // holds the value at which we want to rotate the shape
    private int angleDelta = 5;

    private float aspect; // calculate the aspect ratio of the background
    private float aspectP; // calculate the aspect ratio of the palette

    // indicate if we have finished the game or want to start a new game
    private boolean gameFinished = false;
    private boolean newGame = true;

    // translate the blueprint
    private float translateX;
    private float translateY;
    private float translateZ;

    // scale the shape added into the blueprint
    private float scale;
    private float scaleTopShape;
    private float scaleTopTwoShape;
    private float scaleLeftShape;
    private float scaleRightShape;
    private float scaleBottomShape;

    // manipulate the blueprint
    private double blueprintConstant = 0.1;

    // initialize our camera class
    private Camera camera;

    Mesh tower;
    Mesh ground;
    //Meshes
    Mesh blue_bottom;
    Mesh blue_mid1;
    Mesh blue_mid2;
    Mesh blue_mid3;
    Mesh blue_mid4;
    Mesh blue_mid5;
    Mesh blue_mid6;
    Mesh blue_top;

    //Shaders
    Shader defaultShader;
    //Textures
    TextureLoader textureLoader;

    public static void logGLCapabilities(GLCapabilities capabilities) {
        System.out.println("Chosen GLCapabilities: ");
        System.out.println("Red bits: " + capabilities.getRedBits());
        System.out.println("Green bits: " + capabilities.getGreenBits());
        System.out.println("Blue bits: " + capabilities.getBlueBits());
        System.out.println("Alpha bits: " + capabilities.getAlphaBits());
        System.out.println("Depth bits: " + capabilities.getDepthBits());
        System.out.println("Stencil bits: " + capabilities.getStencilBits());
        System.out.println("Double buffered: " + capabilities.getDoubleBuffered());
        System.out.println("Hardware accelerated: " + capabilities.getHardwareAccelerated());
    }

    public static void logGLProfile(GLProfile profile) {
        System.out.println("GL Profile: " + profile);
    }
    public Project(){

        // gathers information about the current hardware & software configuration
        // to allow us render graphics to our screen
        GLProfile profile = GLProfile.get(GLProfile.GL4);
        logGLProfile(profile);
        // specify a set of capabilities that our rendering should support
        GLCapabilities caps = new GLCapabilities(profile);
        logGLCapabilities(caps);
        caps.setAlphaBits(8); // set the number of bits for the color buffer alpha component
        caps.setDepthBits(24); // set the number of bits for the depth buffer
        caps.setDoubleBuffered(true); // reduce flicking and provide smooth animation
        caps.setStencilBits(8); // mask pixels in an image to product special effects

        SwingUtilities.invokeLater(() -> {

            // create the openGL rendering canvas
            canvas = new GLCanvas(caps);

            // set the desired frame size upon launch
            canvas.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            // listen for functions to be called when a specific event happens
            canvas.addGLEventListener(this);
            canvas.addKeyListener(this); //receive keyboard events
            canvas.addMouseListener(this); // notify when the mouse state changes
            canvas.addMouseMotionListener(this);
            canvas.setFocusable(true); // get's the focus state of the component
            canvas.requestFocus(); // allow user input via the keyboard
            canvas.requestFocusInWindow(); // ensures the window gains focus once launched

            // initialize the FPSAnimator
            animator = new FPSAnimator(canvas, FPS, true);

            // initialize the jFrame constructor
            frame = new JFrame();

            // initialize the buttons, checkbox, labels and
            // set a preferred dimensions
            removeButton = new JButton("Remove");
            addButton = new JButton("Add");
            finishButton =  new JButton("Finish");
            quitButton = new JButton("Quit");
            helpButton = new JButton("Help");
            newGameButton = new JButton("New Game");

            removeButton.setPreferredSize(new Dimension(100, 20));
            addButton.setPreferredSize(new Dimension(100, 20));
            finishButton.setPreferredSize(new Dimension(100, 20));
            quitButton.setPreferredSize(new Dimension(100, 20));
            helpButton.setPreferredSize(new Dimension(100, 20));
            newGameButton.setPreferredSize(new Dimension(100, 20));

            // initialize the JLabel
            label = new JLabel("Click On The Help Button To Read Game Instructions");

            // initialize the JCheckbox
            lightOnOff = new JCheckBox("Turn Light On/Off", true);
            ambientLight = new JCheckBox("Ambient Light", false);
            globalAmbientLight = new JCheckBox("Global Ambient Light", false);
            specularLight = new JCheckBox("Specular Light", false);
            diffuseLight = new JCheckBox("Diffuse Light", false);


            JPanel windowPanel = new JPanel();
            windowPanel.setLayout(new GridLayout(2, 1));

            JPanel palettePanel = new JPanel();
            palettePanel.setLayout(new BoxLayout(palettePanel, BoxLayout.Y_AXIS));

            imageLabel1 = new JLabel(new ImageIcon("src/textures/bottom.png"));
            imageLabel2 = new JLabel(new ImageIcon("src/textures/middle.png"));
            imageLabel3 = new JLabel(new ImageIcon("src/textures/top.png"));

            imageLabel1.setPreferredSize(new Dimension(100, 100));
            imageLabel2.setPreferredSize(new Dimension(100, 100));
            imageLabel3.setPreferredSize(new Dimension(100, 100));

            imageLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);

            palettePanel.add(imageLabel1);
            palettePanel.add(imageLabel2);
            palettePanel.add(imageLabel3);
            palettePanel.setPreferredSize(new Dimension(110,300));
            //frame.add(palettePanel, BorderLayout.WEST);

            // create the panel for the first row
            JPanel topPanel = new JPanel();
            topPanel.add(removeButton);
            topPanel.add(addButton);
            topPanel.add(globalAmbientLight);
            topPanel.add(lightOnOff);
            topPanel.add(ambientLight);
            topPanel.add(diffuseLight);
            topPanel.add(specularLight);

            windowPanel.add(topPanel);

            // create the panel for the second row
            JPanel bottomPanel = new JPanel();
            bottomPanel.add(label);
            bottomPanel.add(helpButton);
            bottomPanel.add(finishButton);
            bottomPanel.add(newGameButton);
            bottomPanel.add(quitButton);

            windowPanel.add(bottomPanel);

            frame.add(windowPanel, BorderLayout.SOUTH);

            ambientLight.setFocusable(false);
            lightOnOff.setFocusable(false);
            globalAmbientLight.setFocusable(false);
            diffuseLight.setFocusable(false);
            specularLight.setFocusable(false);

            imageLabel1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        imageLabel1.setBorder(BorderFactory.createLineBorder(Color.RED,5));
                        imageLabel2.setBorder(null);
                        imageLabel3.setBorder(null);
                    }
                }
            });

            imageLabel2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        imageLabel1.setBorder(null);
                        imageLabel2.setBorder(BorderFactory.createLineBorder(Color.RED,5));
                        imageLabel3.setBorder(null);
                    }
                }
            });

            imageLabel3.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        imageLabel1.setBorder(null);
                        imageLabel2.setBorder(null);
                        imageLabel3.setBorder(BorderFactory.createLineBorder(Color.RED,5));
                    }
                }
            });
            addButton.addActionListener( e -> {
                // TODO: Implement to Pick Shapes from the Palette And Show Them on The Screen
                if(e.getSource() == addButton){
                    if(traverse == 1){
                        top_idn = nameId;
                    } else if (traverse == 2) {
                        left_idn = nameId;
                    } else if (traverse == 3) {
                        right_idn = nameId;
                    } else if (traverse == 4) {
                        bottom_idn = nameId;
                    } else if (traverse == 5){
                        top_two_idn = nameId;
                    }
                }
                addButton.setFocusable(false);
            });

            removeButton.addActionListener(e -> {
                // TODO: Implement to Remove a Shapes from the Blueprint
                if(traverse == 1){
                    top_idn = 0;
                } else if (traverse == 2) {
                    left_idn = 0;
                } else if (traverse == 3) {
                    right_idn = 0;
                } else if (traverse == 4) {
                    bottom_idn = 0;
                } else if(traverse == 5){
                    top_two_idn = 0;
                }
                removeButton.setFocusable(false);
            });

            finishButton.addActionListener(e -> {
                if(e.getSource() == finishButton){
                    gameFinished = true;
                    addShapeRed = 0;
                    addShapeGreen = 0.7f;
                    addShapeBlue = 1;
                    currentAngleOfVisibleField = 80;
                    translateY = -1;
                }
                finishButton.setFocusable(false);
            });

            helpButton.addActionListener( e -> {
                if (e.getSource() == helpButton) {

                    JOptionPane.showMessageDialog(frame, "Instructions: \n" +
                                    "W - traverse through the blueprint\n" +
                                    "A - reduce the scale of the shape inserted into the blueprint\n" +
                                    "S - increase the scale of the shape inserted into the blueprint\n" +
                                    "Z - increase the scale of the blueprint\n" +
                                    "X - reduce the scale of the blueprint\n" +
                                    "I - move the blueprint (translate) on the z-axis in positive direction\n" +
                                    "O - move the blueprint (translate) on the z-axis in negative direction\n" +
                                    "J - move the blueprint (translate) on the x-axis in positive direction \n" +
                                    "K - move the blueprint (translate) on the x-axis in negative direction\n" +
                                    "N - move the blueprint (translate) on the y-axis in positive direction\n" +
                                    "M - move the blueprint (translate) on the y-axis in negative direction\n" +


                                    "Add Button - after selecting a shape from the palette, you can add it to the selected blueprint shape by the Add button\n" +
                                    "Remove Button - after selecting a shape from the palette, you can remove it from the selected blueprint shape by the Remove button\n" +

                                    "Finish Button - after the game finished, by pressing on the finish button, you can see your results\n" +
                                    "New Game Button - generate a new game\n" +
                                    "Quit Button - quit from the game \n" +
                                    "Light - you can enable/disable different light models by checking/unchecking  the light chekboxes (global ambient light, ambient, diffuse and specular)\n" +


                                    "+ (Numerical Keypad 9)- zoom in\n" +
                                    "- (Numerical Keypad 9)- zoom out\n" +
                                    "Left arrow - negative rotation around the x-axis of the blueprint\n" +
                                    "Right arrow - positive rotation around the x-axis of the blueprint \n" +
                                    "Up arrow - negative rotation around the y-axis of the blueprint\n" +
                                    "Down arrow - positive rotation around the y-axis of the blueprint\n" +
                                    "1 (Numerical Keypad 1) - positive rotation around the x-axis of the shape inserted into the blueprint\n" +
                                    "3 (Numerical Keypad 3)- negative rotation around the x-axis of the shape inserted into the blueprint\n" +
                                    "4 (Numerical Keypad 4)- positive rotation around the y-axis of the shape inserted into the blueprint\n" +
                                    "6 (Numerical Keypad 6)- negative rotation around the y-axis of the shape inserted into the blueprint\n" +
                                    "7 (Numerical Keypad 7)- positive rotation around the z-axis of the shape inserted into the blueprint\n" +
                                    "9 (Numerical Keypad 9)- negative rotation around the z-axis of the shape inserted into the blueprint\n"
                            , "Help", JOptionPane.INFORMATION_MESSAGE);
                }
                helpButton.setFocusable(false);
            });

            quitButton.addActionListener(e -> {
                if(e.getSource() == quitButton){
                    animator.stop();
                    System.exit(0);
                }
                quitButton.setFocusable(false);
            });

            newGameButton.addActionListener(e -> {
                if(e.getSource() == newGameButton){
                    newGame = true;
                    gameFinished = false;
                }
                newGameButton.setFocusable(false);
            });

            frame.getContentPane().add(canvas);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread(() -> {
                        if(animator.isStarted()){
                            animator.stop();
                            System.exit(0);
                        }
                    }).start();
                }
            });

            frame.pack();
            frame.setTitle(TITLE);
            frame.setVisible(true);
            animator.start();
        });

    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL4 gl = glAutoDrawable.getGL().getGL4();
        gl.glClearColor(0.2f, 0.3f, 0.4f, 1.0f);
        // enable the depth buffer to allow us represent depth information in 3d space
        gl.glEnable(GL4.GL_DEPTH_TEST);

        gl.glClearDepth(1.0f); // set clear depth value to farthest
        gl.glEnable(GL4.GL_DEPTH_TEST); // enable depth testing
        gl.glDepthFunc(GL4.GL_LEQUAL); // the type of depth test to do

        gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);

        // initialize the textures to use
        glu = GLU.createGLU(gl); // get Gl utilities

        // initialize the font to use when rendering our text
        //textRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 12));
        //textMatch = new TextRenderer(new Font("SansSerif", Font.BOLD, 20));

        defaultShader = new Shader("src/shaders/default.vert", "src/shaders/default.frag");

        // Create identity matrices
        Matrix4f modelMatrix = new Matrix4f().loadIdentity();
        Matrix4f viewMatrix = new Matrix4f().loadIdentity();
        Matrix4f projectionMatrix = new Matrix4f().loadIdentity();

        defaultShader.use();
        defaultShader.setUniformMat4f("model", modelMatrix.get(new float[16]));       // Convert to float array
        defaultShader.setUniformMat4f("view", viewMatrix.get(new float[16]));         // Convert to float array
        defaultShader.setUniformMat4f("projection", projectionMatrix.get(new float[16])); // Convert to float array
        defaultShader.stop();

        try {
            blue_bottom = OBJLoader.loadMesh("src/models/blueprint_models/bottom.obj", defaultShader);
            blue_mid1 = OBJLoader.loadMesh("src/models/blueprint_models/middle1.obj", defaultShader);
            blue_mid2 = OBJLoader.loadMesh("src/models/blueprint_models/middle2.obj", defaultShader);
            blue_mid3 = OBJLoader.loadMesh("src/models/blueprint_models/middle3.obj", defaultShader);
            blue_mid4 = OBJLoader.loadMesh("src/models/blueprint_models/middle4.obj", defaultShader);
            blue_mid5 = OBJLoader.loadMesh("src/models/blueprint_models/middle5.obj", defaultShader);
            blue_mid6 = OBJLoader.loadMesh("src/models/blueprint_models/middle6.obj", defaultShader);
            blue_top = OBJLoader.loadMesh("src/models/blueprint_models/top.obj", defaultShader);
            tower = OBJLoader.loadMesh("src/models/tower.obj",defaultShader);
            ground = OBJLoader.loadMesh("src/models/plane.obj",defaultShader);

            textureLoader = new TextureLoader("C:/Users/Jack/Downloads/tower.jpg",glAutoDrawable.getGL().getGL4());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        camera = new Camera(WINDOW_WIDTH, WINDOW_HEIGHT, new Vec3f(0, 0, 10));
        camera.updateMatrix(45.0f, 0.1f, 1000.0f);
    }
    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) { }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL4 gl = glAutoDrawable.getGL().getGL4();

        // clears both the color and depth buffer before rendering
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        // check if we are in selection model
        //if(inSelectionMode){
            //pickModels(glAutoDrawable);
        //}else{
            //palette(glAutoDrawable); // add the palette (left side)
            //drawBlueprint(glAutoDrawable); // draws the blueprint (right side)
            //drawBackground(glAutoDrawable); // draws the background (rainbow)
        //}

        if (camera == null) {
            throw new IllegalStateException("Camera has not been initialized.");
        }
        // Update and upload the camera matrices to the shader
        camera.updateMatrix(45.0f, 0.1f, 1000.0f);

        defaultShader.use();
        camera.uploadToShader(gl, defaultShader, "projection", "view");
        defaultShader.stop();

        defaultShader.use();
        textureLoader.bind(gl);

        int diffuseTextureLoc = gl.glGetUniformLocation(defaultShader.programId, "diffuseTexture");
        gl.glUniform1i(diffuseTextureLoc, 0); // Texture unit 0

        int lightPosLoc = gl.glGetUniformLocation(defaultShader.programId, "lightPos");
        int viewPosLoc = gl.glGetUniformLocation(defaultShader.programId, "viewPos");

        float[] lightPos = {0.0f, 70.0f, 0.0f};
        gl.glUniform3fv(lightPosLoc, 1, lightPos, 0);

        float[] viewPos = {camera.position.x(),camera.position.y(),camera.position.z()};
        gl.glUniform3fv(viewPosLoc, 1, viewPos, 0);
        tower.draw();
        //ground.draw();
        /*
        gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_LINE);
        blue_bottom.draw();
        blue_mid1.draw();
        blue_mid2.draw();
        blue_mid3.draw();
        blue_mid4.draw();
        blue_mid5.draw();
        blue_mid6.draw();
        blue_top.draw();
        gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);
         */
        //draw
        textureLoader.unbind(gl);
        defaultShader.stop();
        // check if the game is finished, print the total matched shapes
        if(gameFinished){
            printResult();
        }

        // print the match shapes, once we've inserted it correctly
        if(!gameFinished){
            printMatch();
        }

        // reset the game if the user decides
        if(newGame){
            newGame();
            newGame = false;
        }

    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
        WINDOW_HEIGHT = h;
        WINDOW_WIDTH = w;
    }

    private void newGame() {

        // initialize the shapes to use & grab the total number
        shapes.put(0, " ");
        shapes.put(1, "CUBE");
        shapes.put(2, "CUBOID");
        shapes.put(3, "RECTANGULAR_PYRAMID");
        shapes.put(4, "CYLINDER");
        shapes.put(5, "SPHERE");

        TOTAL_NUM_OF_SHAPES = shapes.size()-1;

        // initialize for the shapes that we want to draw
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 1; i <= TOTAL_NUM_OF_SHAPES; i++) {
            list.add(i);
        }

        // shuffle the list randomly
        Collections.shuffle(list);

        // Add the shapes that needs to be drawn on our blueprint
        randomTop = list.get(0);
        randomLeft = list.get(1);
        randomRight = list.get(2);
        randomBottom = list.get(3);
        randomTopTwo = list.get(4);

        currentAngleOfVisibleField = 55;
        currentAngleOfRotationX = 0;
        currentAngleOfRotationY = 0;

        // reset the value for the blueprint
        translateX = 0;
        translateY = 0;
        translateZ = 0;

        scale = 1;
        nameId = 0;
        top_idn = 0;
        left_idn = 0;
        right_idn = 0;
        bottom_idn = 0;
        top_two_idn = 0;

        // reset/initialize the colors of the shapes inserted into the blueprint
        // as it changes once the user finishes the game
        addShapeRed = 1f;
        addShapeGreen = 0.7f;
        addShapeBlue = 0f;

        // remove the current selection
        redTop = defaultRed;
        greenTop = defaultGreen;
        blueTop = defaultBlue;

        redTopTwo = defaultRed;
        greenTopTwo = defaultGreen;
        blueTopTwo = defaultBlue;

        redLeft = defaultRed;
        greenLeft = defaultGreen;
        blueLeft = defaultBlue;

        redRight = defaultRed;
        greenRight = defaultGreen;
        blueRight = defaultBlue;

        redBottom = defaultRed;
        greenBottom = defaultGreen;
        blueBottom = defaultBlue;

        // position and scale for the template shapes on the blueprint
        topX = -0.5;
        topY = 1.5;
        topZ = 0.5;
        scaleTopShape = 1f;

        topTwoX = 0.5;
        topTwoY = 1.5;
        topTwoZ = -0.5;
        scaleTopTwoShape = 1f;

        leftX = -1.5;
        leftY = 0.5;
        leftZ = 0;
        scaleLeftShape = 1f;

        rightX = 1.4;
        rightY = 0;
        rightZ = 0.5;
        scaleRightShape = 1f;

        bottomX = 1;
        bottomY = -1.6;
        bottomZ = 0.5;
        scaleBottomShape = 1f;

        // reset the pointer
        traverse = 0;

        // scale for the added shape on the blueprint (template)
        scaleTop = 0.5f;
        scaleTopTwo = 0.5f;
        scaleLeft = 0.4f;
        scaleRight = 0.6f;
        scaleBottom = 0.5f;

        // angle of the added shape on the blueprint (top, left....)
        angleTopX = 90;
        angleTopY = 90;
        angleTopZ = 90;

        angleTopTwoX = 90;
        angleTopTwoY = 90;
        angleTopTwoZ = 90;

        angleLeftX = 90;
        angleLeftY = 90;
        angleLeftZ = 90;

        angleRightX = 90;
        angleRightY = 90;
        angleRightZ = 90;

        angleBottomX = 90;
        angleBottomY = 90;
        angleBottomZ = 90;
    }
    /*
    private void lights(GL4 gl, float [] zero) {
        gl.glColor3d(0.5, 0.5, 0.5);
        gl.glMaterialfv(GL4.GL_FRONT_AND_BACK, GL4.GL_SPECULAR, zero, 0);

        if(lightOnOff.isSelected()){
            gl.glDisable(GL4.GL_LIGHTING);
        }else{
            gl.glEnable(GL4.GL_LIGHTING);
        }

        float [] ambient = {0.1f, 0.1f, 0.1f, 1};
        float [] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        float [] specular = {1.0f, 1.0f, 1.0f, 1.0f};

        // ambient light
        if(ambientLight.isSelected()){
            gl.glMaterialfv(GL4.GL_FRONT_AND_BACK, GL4.GL_EMISSION, ambient, 0);
            gl.glEnable(GL4.GL_LIGHT0);
        }else{
            gl.glMaterialfv(GL4.GL_FRONT_AND_BACK, GL4.GL_EMISSION, zero, 0);
            gl.glDisable(GL4.GL_LIGHT0);
        }

        // diffuse light
        if(diffuseLight.isSelected()){
            gl.glMaterialfv(GL4.GL_FRONT_AND_BACK, GL4.GL_EMISSION, diffuse, 0);
            gl.glEnable(GL4.GL_LIGHT1);
        }else{
            gl.glMaterialfv(GL4.GL_FRONT_AND_BACK, GL4.GL_EMISSION, zero, 0);
            gl.glDisable(GL4.GL_LIGHT1);
        }

        // specular light
        if(specularLight.isSelected()){
            float [] shininess = {0.1f, 0.1f, 0.1f, 1};
            gl.glMaterialfv(GL4.GL_FRONT_AND_BACK, GL4.GL_EMISSION, specular, 0);
            gl.glMaterialfv(GL4.GL_FRONT_AND_BACK, GL4.GL_SHININESS, shininess, 0);
            gl.glEnable(GL4.GL_LIGHT2);
        }else{
            gl.glMaterialfv(GL4.GL_FRONT_AND_BACK, GL4.GL_EMISSION, zero, 0);
            gl.glDisable(GL4.GL_LIGHT2);
        }

        gl.glMaterialfv(GL4.GL_FRONT_AND_BACK, GL4.GL_EMISSION, zero, 0);
    }
    */

    /*
    private void drawBackground(GLAutoDrawable glAutoDrawable){
        try {
            GL4 gl = glAutoDrawable.getGL().getGL4();

            // define the characteristics of our camera such as clipping, point of view...
            gl.glMatrixMode(GL4.GL_PROJECTION);
            gl.glLoadIdentity(); // reset the current matrix

            // set the window screen
            gl.glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

            // calculate the aspect ratio
            aspect = (float) WINDOW_HEIGHT / ((float) WINDOW_WIDTH);

            // define the orthographic view
            gl.glOrtho(
                    (float) -10/2, // left vertical clipping plane
                    (float) 10/2, // right vertical clipping plane
                    (-10*aspect) / 2, // bottom horizontal clipping plane
                    (10*aspect) / 2, // top horizontal clipping plane
                    0, // near depth clipping plane
                    100 // near farther clipping plane
            );

            // define the position orientation of the camera
            gl.glMatrixMode(GL4.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);

            gl.glPushMatrix();
            gl.glEnable(GL4.GL_TEXTURE_2D);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
            gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);

            textures[3].bind(gl); // specify the texture to use

            gl.glTranslated(0, 0, -100);
            gl.glScalef(1.75f, 1, 1);
            gl.glColor3f(1f, 1f, 1f);

            double radius = 5;

            // add the texture to our background
            gl.glBegin(GL4.GL_POLYGON);
            gl.glNormal3f(0, 0, 1); // lighting calculation

            // top left corner of a square
            gl.glTexCoord2d(0, 1);
            gl.glVertex2d(-radius, radius);

            // bottom left corner of a square
            gl.glTexCoord2d(0, 0);
            gl.glVertex2d(-radius, -radius);

            // bottom right corner of a square
            gl.glTexCoord2d(1, 0);
            gl.glVertex2d(radius, -radius);

            // top right corner of a square
            gl.glTexCoord2d(1, 1);
            gl.glVertex2d(radius, radius);

            gl.glDisable(GL4.GL_TEXTURE_2D);
            gl.glEnd();
            gl.glPopMatrix();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
     */

    private void drawBlueprint(GLAutoDrawable glAutoDrawable){
        GL4 gl = glAutoDrawable.getGL().getGL4();

        // define the point of view of the blueprint
        gl.glViewport(WINDOW_WIDTH/8, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        //gl.glMatrixMode(GL4.GL_PROJECTION);
        //gl.glLoadIdentity();
        //glu.gluPerspective(currentAngleOfVisibleField,
        //       1.f*WINDOW_WIDTH/WINDOW_HEIGHT, 1, 100);
        //gl.glMatrixMode(GL4.GL_MODELVIEW);
        //gl.glLoadIdentity();
        //setObserver();

        // draw the blueprint
        //gl.glPushMatrix();

        // change the orientation of the blueprint
        //gl.glTranslated(translateX, translateY, translateZ);
        //gl.glScalef(scale, scale, scale);
        //gl.glRotated(currentAngleOfRotationX, 1, 0, 0);
        //gl.glRotated(currentAngleOfRotationY, 0, 1, 0);

        // add some texture on the blueprint
        //gl.glColor3f(1, 1, 1);
        //gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);
        //textures[1].bind(gl);
        //gl.glEnable(GL.GL_TEXTURE_2D);

        //Shape.cube(gl, 2, true); // #TODO: Uncomment later
//            Shape.cuboid(gl, 1, true); // #TODO: Delete once done testing
//            Shape.rectangularPyramid(gl); // #TODO: Delete once you're done testing
//            Shape.cylinder(gl); // #TODO: Delete once you're done testing
//            Shape.sphere(gl); // #TODO: Delete once you're done testing

        //gl.glDisable(GL.GL_TEXTURE_2D);

        // Draw random template shapes to be filled at different parts of the blueprint
        //drawRandomShapeOnBlueprint(glAutoDrawable, 1); // draw at on top
        //drawRandomShapeOnBlueprint(glAutoDrawable, 2); // draw at on left
        //drawRandomShapeOnBlueprint(glAutoDrawable, 3); // draw at on right
        //drawRandomShapeOnBlueprint(glAutoDrawable, 4); // draw at the bottom
        //drawRandomShapeOnBlueprint(glAutoDrawable, 5); // draw the second top shape

        // Allows the user to select a shape from the palette and deploy it unto the blueprint
        //deployShapeFromPaletteToBlueprint(glAutoDrawable, 1); // top side
        //deployShapeFromPaletteToBlueprint(glAutoDrawable, 2); // left side
        //deployShapeFromPaletteToBlueprint(glAutoDrawable, 3); // right side
        //deployShapeFromPaletteToBlueprint(glAutoDrawable, 4); // bottom side
        //deployShapeFromPaletteToBlueprint(glAutoDrawable, 5); // top second side

        //gl.glPopMatrix();
    }

    private void deployShapeFromPaletteToBlueprint(GLAutoDrawable drawable, int choice){
        //  Allows the user to select a shape from the palette and deploy it unto the
        switch (choice){
            //#TODO: Add the condition for a new shape here that's to be displayed on the palette
            case 1: // top side
                addShapeToBlueprint(drawable,
                        addShapeRed, addShapeGreen, addShapeBlue, // colors
                        topX, topY, topZ, // translation
                        angleTopX, angleTopY, angleTopZ, // angle
                        scaleTop, rotate, // scale & rotate
                        top_idn); // shape to be drawn
                break;
            case 2: // left side
                addShapeToBlueprint(drawable,
                        addShapeRed, addShapeGreen, addShapeBlue, // colors
                        leftX, leftY, leftZ,  // translation
                        angleLeftX, angleLeftY, angleLeftZ, // angle
                        scaleLeft, rotate, // scale & rotate
                        left_idn); // shape to be drawn
                break;
            case 3: // right side
                addShapeToBlueprint(drawable,
                        addShapeRed, addShapeGreen, addShapeBlue, // colors
                        rightX, rightY, rightZ, // translation
                        angleRightX, angleRightY, angleRightZ, // angle
                        scaleRight, rotate, // scale & rotate
                        right_idn); // shape to be drawn
                break;
            case 4: // the bottom side
                addShapeToBlueprint(drawable,
                        addShapeRed, addShapeGreen, addShapeBlue, // colors
                        bottomX, bottomY, bottomZ, // translation
                        angleBottomX, angleBottomY, angleBottomZ, // angle
                        scaleBottom, rotate, // scale & rotate
                        bottom_idn); // shape to be drawn
                break;
            case 5:
                addShapeToBlueprint(drawable,
                        addShapeRed, addShapeGreen, addShapeBlue, // colors
                        topTwoX, topTwoY, topTwoZ, // translation
                        angleTopTwoX, angleTopTwoY, angleTopTwoZ, // angle
                        scaleTopTwo, rotate, // scale & rotate
                        top_two_idn); // shape to be drawn
                break;
            default:
                break;
        }
    }

    private void addShapeToBlueprint(GLAutoDrawable glAutoDrawable,
                                     float colorRed, float colorGreen, float colorBlue, // colors
                                     double tX, double tY, double tZ, // translation
                                     float angleX, float angleY, float angleZ, // angle
                                     float randomScale, float randomRotate, // scale & rotate
                                     int shapeId){ // shape to be drawn

        GL4 gl = glAutoDrawable.getGL().getGL4();
        //gl.glPushMatrix();
        //gl.glColor3f(colorRed, colorGreen, colorBlue);
        //gl.glTranslated(tX, tY, tZ);
        //gl.glScalef(randomScale, randomScale, randomScale);
        //gl.glRotatef(angleZ, 0, 0, randomRotate);
        //gl.glRotatef(angleY, 0, randomRotate, 0);
        //gl.glRotatef(angleX, randomRotate, 0, 0);
        gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);
        pickShape(glAutoDrawable, shapeId);
        //gl.glPopMatrix();
    }

    private void pickShape(GLAutoDrawable glAutoDrawable, int nameID){
        GL4 gl = glAutoDrawable.getGL().getGL4();
        switch (nameID){ //#TODO: We will add other shapes here
            case CUBE_ID:
                //Shape.cube(gl,defaultShader);
                break;
            case CUBOID_ID:
                //Shape.cuboid(gl);
                break;
            case RECTANGULAR_PYRAMID_ID:
                //Shape.rectangularPyramid(gl);
                break;
            case CYLINDER_ID:
                //Shape.cylinder(gl);
                break;
            case SPHERE_ID:
                //Shape.sphere(gl);
                break;
        }
    }

    private void drawRandomShapeOnBlueprint(GLAutoDrawable drawable, int choice) {
        switch (choice){
            // TODO: Add another random shape template placeholder
            case 1: // draw random shape at the top
                drawRandomShapeOnBlueprint(drawable,
                        redTop, greenTop, blueTop, // the color
                        topX, topY, topZ, // translation
                        scaleTopShape, // the scale of the shape
                        randomTop); // the random shape to be drawn
                break;
            case 2: // draw random shape on the left side
                drawRandomShapeOnBlueprint(drawable,
                        redLeft, greenLeft, blueLeft, // the color
                        leftX, leftY, leftZ, // translation
                        scaleLeftShape, // the scale of the shape
                        randomLeft); // the random shape to be drawn
                break;
            case 3:
                drawRandomShapeOnBlueprint(drawable,
                        redRight, greenRight, blueRight, // the color
                        rightX, rightY, rightZ,  // translation
                        scaleRightShape, // the scale of the shape
                        randomRight); // the random shape to be drawn
                break;
            case 4:
                drawRandomShapeOnBlueprint(drawable,
                        redBottom, greenBottom, blueBottom, // the color
                        bottomX, bottomY, bottomZ, // translation
                        scaleBottomShape, // the scale of the shape
                        randomBottom); // the random shape to be drawn
                break;
            case 5:
                drawRandomShapeOnBlueprint(drawable,
                        redTopTwo, greenTopTwo, blueTopTwo, // the color
                        topTwoX, topTwoY, topTwoZ, // translation
                        scaleTopTwoShape, // the scale of the shape
                        randomTopTwo); // the random shape to be drawn
                break;
        }
    }

    private void drawRandomShapeOnBlueprint(GLAutoDrawable drawable,
                                            float colorRed, float colorGreen, float colorBlue, // color
                                            double tX, double tY, double tZ,  // translation
                                            float randomScale, // scale
                                            int randomShape // random shape to be drawn
    ){
        GL4 gl = drawable.getGL().getGL4();

        if(randomRight == CUBOID_ID){
            rightX = 2.0f;
        }

        // apply a consecutive series of transformations
        //gl.glPushMatrix();
        //gl.glColor3f(colorRed, colorGreen, colorBlue);
        gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_LINE);
        gl.glLineWidth(2); // define the width of the line
        //gl.glTranslated(tX, tY, tZ);  // translate the shape
        //gl.glScalef(randomScale, randomScale, randomScale); // scale it
        // draw a random shape at the top of the blueprint
        pickShape(drawable, randomShape);
        //gl.glPopMatrix();
    }

    // set the position of the camera
    //private void setObserver() {
    //    glu.gluLookAt(-1, 2, 10.0, // look from camera XYZ
    //            0.0, 0.0, 0.0, // look at the origin
    //            0.0, 1.0, 0.0); // positive Y up vector
    //}

    // the palette drawn on the left side of the screen
    private void palette(GLAutoDrawable glAutoDrawable) {
        GL4 gl = glAutoDrawable.getGL().getGL4();

        // apply the subsequent matrix operation to the modelview matrix stack
        //gl.glMatrixMode(GL4.GL_MODELVIEW); // convert local coordinates to world space
        //gl.glLoadIdentity(); // reset the value

        // apply the subsequent matrix operation to the projection matrix stack
        //gl.glMatrixMode(GL4.GL_PROJECTION); // add perspective to the current operation
        //gl.glLoadIdentity();

        // specify the lower left of the viewport rectangle in pixel (0,0), width and height
        gl.glViewport(0, 0, WINDOW_WIDTH/3, WINDOW_HEIGHT);

        aspectP = (float) WINDOW_HEIGHT / ((float) WINDOW_WIDTH /3);

        // multiply the current matrix with an orthographic matrix
        //gl.glOrtho(
        //        (float) -10/2, // left vertical clipping plane
        //        (float) 10 / 2, // right vertical clipping plane
        //        (-10 * aspectP) / 2, // bottom horizontal clipping plane
        //        (10 * aspectP) / 2, // top horizontal clipping plane
        //        1, // near depth clipping plane
        //        11 // near farther clipping plane
        //);
        //gl.glMatrixMode(GL4.GL_MODELVIEW);

        // draw the background of the palette
        //paletteBackground(glAutoDrawable);
        //gl.glLoadIdentity();

        // set the camera for the palette
        glu.gluLookAt(-1, 2, 10.0, // look from camera XYZ
                0.0, 0.0, 0.0, // look at the origin
                0.0, 1.0, 0.0); // positive Y up vector

        gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);

        // draw the shape on top of the background palette
        drawPaletteShape(glAutoDrawable, 1); // draws a cube
        drawPaletteShape(glAutoDrawable, 2); // draws a cuboid
        drawPaletteShape(glAutoDrawable, 3); // draws a rectangular pyramid
        drawPaletteShape(glAutoDrawable, 4); // draw a cylinder
        drawPaletteShape(glAutoDrawable, 5); // draw a sphere
    }
    private void paletteBackground(GLAutoDrawable glAutoDrawable) {
        try {
            GL4 gl = glAutoDrawable.getGL().getGL4(); // get the openGL graphics context
            //gl.glPushMatrix();

            // enable the server-side GL capabilities for texture
            gl.glEnable(GL4.GL_TEXTURE_2D);

            // set different texture parameters
            gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
            gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
            gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);

            //gl.glTranslated(-1.35f, -2f, -10f);
            //gl.glScalef(3.5f, 5f, 0f);
            //gl.glColor3f(1f, 1f, 1f);

            //Shape.square(gl, 2, true);

            gl.glDisable(GL4.GL_TEXTURE_2D);

            //gl.glPopMatrix();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void drawPaletteShape(GLAutoDrawable drawable, int shapeChoice){
        switch (shapeChoice){
            case CUBE_ID: // draw a cube
                paletteShape(drawable, paletteRed, paletteGreen, paletteBlue,
                        -3.5f, -0.5f, 0f, 1f, 1);
                break;
            case CUBOID_ID: // draw a cuboid
                paletteShape(drawable, paletteRed, paletteGreen, paletteBlue,
                        -0.5f, 1f, 0f, 0.8f,2);
                break;
            case RECTANGULAR_PYRAMID_ID: // draw a rectangular pyramid
                paletteShape(drawable, paletteRed, paletteGreen, paletteBlue,
                        -3.5f, 1.5f, 0f, 1f,3);
                break;
            case CYLINDER_ID: // draw a cylinder
                paletteShape(drawable, paletteRed, paletteGreen, paletteBlue,
                        -1.0f, -1.0f, 0f, 1.25f, 4);
                break;
            case SPHERE_ID: // draw a sphere
                paletteShape(drawable, paletteRed, paletteGreen, paletteBlue,
                        -3.5f, -2.2f, 0f, 0.9f, 5);
                break;
        }
    }
    private void paletteShape(GLAutoDrawable drawable,
                              float colorRed, float colorGreen, float colorBlue, // color to display
                              float tX, float tY, float tZ, // translation
                              float randomScale, // the size of the shape
                              int shapeId // the choice of shape
    ){
        GL4 gl = drawable.getGL().getGL4();
        //gl.glColor3f(colorRed, colorGreen, colorBlue);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL4.GL_FILL);

        // control the transformation applied to the object
        //gl.glPushMatrix();
        //gl.glTranslated(tX, tY, tZ); // add the translation matrix
        //gl.glScalef(randomScale, randomScale, randomScale);
        pickShape(drawable, shapeId);
        //gl.glPopMatrix();
    }

    /* Allows the user to picks object from the palette and draw it on the blueprint */
    private void pickModels(GLAutoDrawable glAutoDrawable) {
        GL4 gl = glAutoDrawable.getGL().getGL4();

        // start picking objects from the screen
        startPicking(glAutoDrawable);

        // enables the user pick objects from the palette and
        // deploy them to the blueprint
        palettePicking(glAutoDrawable);

        // TODO: For any new shape, you'll need to register them here
        //gl.glPushName(CUBE_ID);
        drawPaletteShape(glAutoDrawable, 1); // draws a cube
        //gl.glPopName();

        //gl.glPushName(CUBOID_ID);
        drawPaletteShape(glAutoDrawable, 2); // draws a cuboid
        //gl.glPopName();

        //gl.glPushName(RECTANGULAR_PYRAMID_ID);
        drawPaletteShape(glAutoDrawable, 3); // draws a rectangular pyramid
        //gl.glPopName();

        //gl.glPushName(CYLINDER_ID);
        drawPaletteShape(glAutoDrawable, 4); // draws a cylinder
        //gl.glPopName();

        //gl.glPushName(SPHERE_ID);
        drawPaletteShape(glAutoDrawable, 5); // draws a sphere
        //gl.glPopName();

        // we are done picking
        endPicking(glAutoDrawable);
    }
    private void startPicking(GLAutoDrawable glAutoDrawable) {
        GL4 gl = glAutoDrawable.getGL().getGL4();
        // Determine which shape are to be drawn on the blueprint
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BUFSIZE * Integer.BYTES);
        byteBuffer.order(ByteOrder.nativeOrder());
        selectBuffer = byteBuffer.asIntBuffer();

        //gl.glSelectBuffer(BUFSIZE, selectBuffer);
        //gl.glRenderMode(GL4.GL_SELECT);
        //gl.glInitNames();
        //gl.glMatrixMode(GL4.GL_MODELVIEW);
    }

    private void palettePicking(GLAutoDrawable glAutoDrawable){
        GL4 gl = glAutoDrawable.getGL().getGL4();

        //gl.glMatrixMode(GL4.GL_PROJECTION);
        //gl.glPushMatrix();

        //gl.glLoadIdentity();
        int [] viewport = new int[4];
        float [] projectionMatrix = new float[16];

        gl.glGetIntegerv(GL4.GL_VIEWPORT, viewport, 0);
        viewport[0] = 0;
        viewport[1] = 0;
        viewport[2] = WINDOW_WIDTH / 3;
        viewport[3] = WINDOW_HEIGHT;

        //gl.glGetFloatv(GL4.GL_PROJECTION_MATRIX, projectionMatrix, 0);

        // define the picking region
        glu.gluPickMatrix((double) xCursor,
                (double) (viewport[3] - yCursor),
                1.0,
                1.0,
                viewport,
                0);
        //gl.glMultMatrixf(projectionMatrix, 0);
        //gl.glOrtho((float) -10/2,
        //        (float) 10/2,
        //        (-10*aspectP) / 2,
        //        (10*aspectP) / 2,
        //        1,
        //        11);
        //gl.glMatrixMode(GL4.GL_MODELVIEW);
        //gl.glLoadIdentity();
        //glu.gluLookAt(-1, 2, 10.0,
        //        0.0, 0.0, 0.0,
        //        0.0, 1.0, 0.0);
        //gl.glPopMatrix();
    }
    private void endPicking(GLAutoDrawable glAutoDrawable){
        GL4 gl = glAutoDrawable.getGL().getGL4();

        //gl.glMatrixMode(GL4.GL_PROJECTION);
        //gl.glPopMatrix();
        //gl.glMatrixMode(GL4.GL_MODELVIEW);
        gl.glFlush();

        //int numHits = gl.glRenderMode(GL4.GL_RENDER);
        //processHits(glAutoDrawable, numHits);
        inSelectionMode = false;
    }
    private void processHits(GLAutoDrawable glAutoDrawable, int numHits) {
        GL4 gl = glAutoDrawable.getGL().getGL4();
        if(numHits == 0)  return;

        // store the Id's for what was selected
        int selectedNameId = 0;
        float smallestZ = -1.0f;
        boolean isFirstLoop = true;
        int offset = 0;

        for (int i = 0; i < numHits; i++) {
            int numNames = selectBuffer.get(offset);
            offset++;

            float minZ = getDepth(offset);
            offset++;

            // store the smallest z value
            if(isFirstLoop){
                smallestZ = minZ;
                isFirstLoop = false;
            }else{
                if(minZ < smallestZ){
                    smallestZ = minZ;
                }
            }

            float maxZ = getDepth(offset);
            offset++;

            for (int j = 0; j < numNames; j++) {
                nameId = selectBuffer.get(offset);
                System.out.println(idToString(nameId)+" \n");
                if(j == (numNames - 1)){
                    if(smallestZ == minZ){
                        selectedNameId = nameId;
                    }
                }
                offset++;
            }
        }
    }
    private String idToString(int nameId){
        // TODO: Update for new shapes
        if(nameId == CUBE_ID){
            return "palette_cube";
        }else if(nameId == CUBOID_ID){
            return "palette_cuboid";
        }else if(nameId == RECTANGULAR_PYRAMID_ID){
            return "palette_rectangular_pyramid";
        } else if (nameId == CYLINDER_ID) {
            return "palette_cylinder";
        } else if (nameId == SPHERE_ID) {
            return "palette_sphere";
        } else{
            return "nameId: "+nameId;
        }
    }
    private float getDepth(int offset){
        long depth = (long) selectBuffer.get(offset);
        return (1.0f + ((float) depth / 0x7fffffff)); // 7'fs
    }
    private void colorShape(int traverse){
        // TODO: Ensure to update for selecting different templates when the key 'W' is pressed
        switch (traverse){
            case TOP_ID:
                redTop = 1;
                redTopTwo = 0;
                redLeft = 0;
                redRight = 0;
                redBottom = 0;
                break;
            case TOP_TWO_ID:
                redTop = 0;
                redTopTwo = 1;
                redLeft = 0;
                redRight = 0;
                redBottom = 0;
                break;
            case LEFT_ID:
                redTop = 0;
                redTopTwo = 0;
                redLeft = 1;
                redRight = 0;
                redBottom = 0;
                break;
            case RIGHT_ID:
                redTop = 0;
                redTopTwo = 0;
                redLeft = 0;
                redRight = 1;
                redBottom = 0;
                break;
            case BOTTOM_ID:
                redTop = 0;
                redTopTwo = 0;
                redLeft = 0;
                redRight = 0;
                redBottom = 1;
                break;
            default:
                redLeft = 0;
                redTopTwo = 0;
                redTop = 0;
                redRight = 0;
                redBottom = 0;
                break;
        }
    }


    /* Valid the matched shapes and display to screen*/

    // check if the user has correctly added a shape from the palette to the blueprint
    // the scale, rotation, etc... is valid
    private void printMatch() {
        if(traverse == 1){
            // check if the shape matched
            boolean isShapeMatched = shapes.get(randomTop).equals(shapes.get(top_idn));

            // check if the scale for the top shape matches
            String isScaleMatched = scaleCheck(scaleTop);

            // check if the rotation matches
            String isRotationMatched = rotationCheck(randomTop, angleTopX, angleTopY, angleTopZ);

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
            }
        }
        else if (traverse == 2) {
            boolean isShapeMatched = shapes.get(randomLeft).equals(shapes.get(randomLeft)); // check if the shape matched
            String isRotationMatched = rotationCheck(randomLeft, angleLeftX, angleLeftY, angleLeftZ); // is rotation correct
            String isScaleMatched = scaleCheck(scaleLeft); // is scale appropriate

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
            }
        }else if(traverse == 3){
            boolean isShapeMatched = shapes.get(randomRight).equals(shapes.get(randomRight)); // check if the shape matches
            String isRotationMatched = rotationCheck(randomRight,
                    angleRightX,
                    angleRightY,
                    angleRightZ); // is rotation correct
            String isScaleMatched = scaleCheck(scaleRight); // is scale appropriate

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
            }
        }else if(traverse == 4){
            boolean isShapeMatched = shapes.get(randomBottom).equals(shapes.get(bottom_idn)); // check if the shape matches
            String isRotationMatched = rotationCheck(randomBottom,  // is rotation appropriate
                    angleBottomX,
                    angleBottomY,
                    angleBottomZ);
            String isScaleMatched = scaleCheck(scaleBottom); // is scale correct

//            System.out.printf("[INFO] Shape check: %d, X: %d,  Y: %d,   Z: %d\n",
//                    randomBottom, angleBottomX, angleBottomY, angleBottomZ);
//            System.out.println("Is Shape Matched: "+isShapeMatched);
//            System.out.println("Is Scale Matched: "+isScaleMatched);
//            System.out.println("Is Rotation Matched: "+isRotationMatched);
//            System.out.println("============================\n\n");

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
//                System.out.println("[INFO] YAHHHHHHHH!!!!!!!!!!!!!!! CORRECT MATCH!!!");
                writeMatch((int) (WINDOW_WIDTH / 4f), WINDOW_HEIGHT-40);
            }
        } else if(traverse == 5){
            boolean isShapeMatched = shapes.get(randomTopTwo).equals(shapes.get(randomTopTwo)); // check if the shape matches
            String isRotationMatched = rotationCheck(randomTopTwo,
                    angleTopTwoX,
                    angleTopTwoY,
                    angleTopTwoZ); // is rotation correct
            String isScaleMatched = scaleCheck(scaleTopTwo); // is scale appropriate

            if(isShapeMatched & isScaleMatched.equals("appropriate") & isRotationMatched.equals("correct")){
                writeMatch(
                        (int) (WINDOW_WIDTH / 4f),
                        WINDOW_HEIGHT - 40);
            }
        }
    }
    private void writeMatch(int x, int y){
        //textMatch.beginRendering(WINDOW_WIDTH, WINDOW_HEIGHT);
        //textMatch.setColor(0.3f, 0.3f, 0.3f, 1);
        //textMatch.draw("Well done! Correct Shape, Rotation & Scaling", x, y);
        //textMatch.endRendering();
    }
    private String scaleCheck(float value) {
        double scaling = Math.round(value * 100.0) / 100.0;
        String text = "";
        if(scaling == 1.0){
            text = "appropriate";
        }else{
            text = "not appropriate";
        }
        return text;
    }
    private String rotationCheck(int shape, int angleX, int angleY, int angleZ) {
        String text = "";
        if(shape == CUBE_ID){ // cube
            // rotation: x, y, z
            boolean checkX = (angleX == 0 || angleX == 90 || angleX == 180 || angleX == 270 || angleX == 360);
            boolean checkY = (angleY == 0 || angleY == 90 || angleY == 180 || angleY == 270 || angleY == 360);
            boolean checkZ = (angleZ == 0 || angleZ == 90 || angleZ == 180 || angleZ == 270 || angleZ == 360);

            if(checkX & checkY & checkZ){
                text = "correct";
            }else{
                text = "incorrect";
            }
        } else if (shape == CUBOID_ID) { // check for cuboid
            // rotation: x, y, z
            boolean checkX = (angleX == 0 || angleX == 90 || angleX == 180 || angleX == 270 || angleX == 360);
            boolean checkY = (angleY == 0 || angleY == 180 || angleY == 360);
            boolean checkZ = (angleZ == 0 || angleZ == 180 || angleZ == 360);

            if(checkX & checkY & checkZ){
                text = "correct";
            }else{
                text = "incorrect";
            }
        }else if(shape == RECTANGULAR_PYRAMID_ID){ // check for rectangular pyramid
            boolean checkX = (angleX == 0);
            boolean checkY = (angleY == 0 || angleY == 90 || angleY == 180 || angleY == 270 || angleY == 360);
            boolean checkZ = (angleZ == 0 || angleZ == 180 || angleZ == 360);

            boolean firstCheck = checkX && checkY && checkZ;
            boolean secondCheck = (angleX == 90 && angleY == 90 && angleZ == 90);
            boolean thirdCheck = (angleX == 90 && angleY == 270 || angleZ == 270);
            boolean fourthCheck = (angleX == 180 && checkY && angleZ == 180);

            if(firstCheck || secondCheck || thirdCheck || fourthCheck){
                text = "correct";
            }else{
                text = "incorrect";
            }
        }else if(shape == CYLINDER_ID){ // check for cylinder
            boolean checkX = (angleX == 0  ||  angleX == 180 || angleX == 360);
            boolean checkY = (angleY == 0 || angleY == 180 || angleY == 360);
            if(checkX && checkY){
                text = "correct";
            }else{
                text = "incorrect";
            }
        } else if(shape == SPHERE_ID){ // check for sphere
            text = "correct";
        }
        return text;
    }
    private void printResult(){
        String text = String.format("RESULT: %d/%d shapes matched correctly", matchedShape(), TOTAL_NUM_OF_SHAPES);
        writeText(text, (int) (WINDOW_WIDTH/3.5f), WINDOW_HEIGHT-40);
    }
    private void writeText(String text, int x, int y){
        //textRenderer.beginRendering(WINDOW_WIDTH, WINDOW_HEIGHT);
        //textRenderer.setColor(0.3f, 0.3f, 0.5f, 1);
        //textRenderer.draw(text, x, y);
        //textRenderer.endRendering();
    }
    private int matchedShape(){
        // TODO: Add a check for a particular placeholder to see if the correct shape was matched or not
        int match = 0;
        boolean isTopMatched = shapes.get(randomTop).equals(shapes.get(top_idn));
        boolean isTopTwoMatched = shapes.get(randomTopTwo).equals(shapes.get(top_two_idn));
        boolean isLeftMatched = shapes.get(randomLeft).equals(shapes.get(left_idn));
        boolean isRightMatched = shapes.get(randomRight).equals(shapes.get(right_idn));
        boolean isBottomMatched = shapes.get(randomBottom).equals(shapes.get(bottom_idn));
        if(isTopMatched) match++;
        if(isTopTwoMatched) match++;
        if(isLeftMatched) match++;
        if(isRightMatched) match++;
        if(isBottomMatched) match++;
        return match;
    }

    /* Event listeners for keyboard, mouse clicks, etc..*/
    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        camera.handleKeyInput(e);
        // traverse through the blueprint
        if(key == KeyEvent.VK_W){
            traverse = traverse + 1;
            colorShape(traverse);
            if(traverse == TOTAL_NUM_OF_SHAPES+1){
                traverse = 0;
            }
        }
        // ============== BLUEPRINT CONTROLS =================
        else if(key == KeyEvent.VK_Z){ // increase the scale of the blueprint
            if(scale <= 2.1){
                scale += blueprintConstant;
            }
        }
        else if(key == KeyEvent.VK_X){ // decrease the scale of the blueprint
            if(scale >= 0.1){
                scale -= blueprintConstant;
            }
        }
        // move the blueprint (translate) on the z-axis in the positive direction
        else if(key == KeyEvent.VK_I){
            translateZ += blueprintConstant;
        }
        // move the blueprint (translate) on the z-axis in the negative direction
        else if (key == KeyEvent.VK_O) {
            translateZ -= blueprintConstant;
        }
        // move the blueprint (translate) on the x-axis in the positive direction
        else if (key == KeyEvent.VK_J) {
            translateX += blueprintConstant;
        }
        // move the blueprint (translate) on the x-axis in the negative direction
        else if (key == KeyEvent.VK_K) {
            translateX -= blueprintConstant;
        }
        // move the blueprint (translate) on the y-axis in the positive direction
        else if(key == KeyEvent.VK_N){
            translateY += blueprintConstant;
        }
        // move the blueprint (translate) on the y-axis in the negative direction
        else if(key == KeyEvent.VK_M){
            translateY -= blueprintConstant;
        }
        // zoom into our blueprint (The Z axis contains the point of view of the object)
        else if(key == KeyEvent.VK_ADD){
            if(currentAngleOfVisibleField > 10){
                currentAngleOfVisibleField--;
            }
        }
        // zoom out of our blueprint
        else if(key == KeyEvent.VK_SUBTRACT){
            if(currentAngleOfVisibleField < 175){
                currentAngleOfVisibleField++;
            }
        }
        // negative rotation around the x-axis of the blueprint
        else if(key == KeyEvent.VK_LEFT){
            currentAngleOfRotationX++;
        }
        // positive rotation around the x-axis of the blueprint
        else if(key == KeyEvent.VK_RIGHT){
            currentAngleOfRotationX--;
        }
        // negative rotation around the y-axis of the blueprint
        else if(key == KeyEvent.VK_UP){
            currentAngleOfRotationY--;
        }
        // positive rotation around the y-axis of the blueprint
        else if(key == KeyEvent.VK_DOWN){
            currentAngleOfRotationY++;
        }

        // =============== SHAPES ADDED INTO THE BLUEPRINT =====================
        // TODO: Ensure to provide support to scale, rotate the new template shape added to your blueprint
        else if(key == KeyEvent.VK_A){ // reduce the scale of the shape inserted into the blueprint
            if(traverse == 1){
                scaleTop -= scaleDelta;
            } else if (traverse == 2) {
                scaleLeft -= scaleDelta;
            } else if (traverse == 3) {
                scaleRight -= scaleDelta;
            } else if (traverse == 4){
                scaleBottom -= scaleDelta;
            } else if (traverse == 5){
                scaleTopTwo -= scaleDelta;
            }
        }
        else if(key == KeyEvent.VK_S){ // increase the scale of the shape inserted into the blueprint
            if(traverse == 1){
                scaleTop += scaleDelta;
            } else if (traverse == 2) {
                scaleLeft += scaleDelta;
            } else if (traverse == 3) {
                scaleRight += scaleDelta;
            } else if (traverse == 4){
                scaleBottom += scaleDelta;
            } else if (traverse == 5){
                scaleTopTwo += scaleDelta;
            }
        }

        // (Numerical Keypad 1) - positive rotation around the x-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD1){
            if(traverse == 1){
                if(angleTopX <= 360){
                    angleTopX += angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftX <= 360){
                    angleLeftX += angleDelta;
                }
            }else if(traverse == 3){
                if(angleRightX <= 360){
                    angleRightX += angleDelta;
                }
            } else if (traverse == 4){
                if(angleBottomX <= 360){
                    angleBottomX += angleDelta;
                }
            } else if (traverse == 5){
                if(angleTopTwoX <= 360){
                    angleTopTwoX += angleDelta;
                }
            }
        }

        // (Numerical Keypad 3) - negative rotation around the x-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD3){
            if(traverse == 1){
                if(angleTopX >= 0){
                    angleTopX -= angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftX >= 0){
                    angleLeftX -= angleDelta;
                }
            }else if(traverse == 3){
                if(angleRightX >= 0){
                    angleRightX -= angleDelta;
                }
            }else if(traverse == 4){
                if(angleBottomX >= 0){
                    angleBottomX -= angleDelta;
                }
            }else if(traverse == 5){
                if(angleTopTwoX >= 0){
                    angleTopTwoX -= angleDelta;
                }
            }
        }

        // (Numerical Keypad 4) - positive rotate around the y-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD4){
            if(traverse == 1){
                if(angleTopY <= 360){
                    angleTopY += angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftY <= 360){
                    angleLeftY += angleDelta;
                }
            }else if(traverse == 3){
                if(angleRightY <= 360){
                    angleRightY += angleDelta;
                }
            }else if(traverse == 4){
                if(angleBottomY <= 360){
                    angleBottomY += angleDelta;
                }
            }else if(traverse == 5){
                if(angleTopTwoY <= 360){
                    angleTopTwoY += angleDelta;
                }
            }
        }

        // (Numerical Keypad 6) - negative rotation around the y-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD6){
            if(traverse == 1){
                if(angleTopY >= 0){
                    angleTopY -= angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftY >= 0){
                    angleLeftY -= angleDelta;
                }
            } else if (traverse == 3){
                if(angleRightY >= 0){
                    angleRightY -= angleDelta;
                }
            } else if(traverse == 4){
                if(angleBottomY >= 0){
                    angleBottomY -= angleDelta;
                }
            } else if(traverse == 5){
                if(angleTopTwoY >= 0){
                    angleTopTwoY -= angleDelta;
                }
            }
        }

        // (Numerical Keypad 7) - positive rotation around the z-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD7){
            if(traverse == 1){
                if(angleTopZ <= 360){
                    angleTopZ += angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftZ <= 360){
                    angleLeftZ += angleDelta;
                }
            } else if(traverse == 3){
                if(angleRightZ <= 360){
                    angleRightZ += angleDelta;
                }
            } else if(traverse == 4){
                if(angleBottomZ <= 360){
                    angleBottomZ += angleDelta;
                }
            } else if(traverse == 5){
                if(angleTopTwoZ <= 360){
                    angleTopTwoZ += angleDelta;
                }
            }
        }

        // (Numerical Keypad 9) - negative rotation around the z-axis of the shape inserted into the blueprint
        else if(key == KeyEvent.VK_NUMPAD9){
            if(traverse == 1){
                if(angleTopZ >= 0){
                    angleTopZ -= angleDelta;
                }
            } else if (traverse == 2) {
                if(angleLeftZ >= 0){
                    angleLeftZ -= angleDelta;
                }
            } else if (traverse == 3) {
                if(angleRightZ >= 0){
                    angleRightZ -= angleDelta;
                }
            }else if(traverse == 4){
                if(angleBottomZ >= 0){
                    angleBottomZ -= angleDelta;
                }
            }else if(traverse == 5){
                if(angleTopTwoZ >= 0){
                    angleTopTwoZ -= angleDelta;
                }
            }
        }

        // Escape key - stop the animator and exit the game
        else if(key == KeyEvent.VK_ESCAPE){
            animator.stop();
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }
    @Override
    public void mouseClicked(MouseEvent mouseEvent) { }
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        /*
        switch (mouseEvent.getButton()){
            case MouseEvent.BUTTON1: { // left click
                xCursor = mouseEvent.getX();
                yCursor = mouseEvent.getY();
                inSelectionMode = true;
                break;
            }
        }
         */
        camera.MousePressed(mouseEvent);
    }
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        camera.MouseReleased(mouseEvent);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent)
    {
        camera.MouseDragged(mouseEvent);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) { }
    @Override
    public void mouseExited(MouseEvent mouseEvent) { }

    public static void main(String[] args) {
        new Project();
    }
}
