package ogl2.exam;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.Matrix4f;
import com.jogamp.opengl.math.Vec3f;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Project extends GLCanvas implements GLEventListener, KeyListener, MouseListener,MouseMotionListener {

    private JCheckBox wireFrame;
    private JCheckBox light;

    // create multiple buttons
    private JButton removeButton;
    private JButton addButton;
    private JButton helpButton;
    private JButton quitButton;
    private JButton newGameButton;

    private JLabel label;
    private JLabel gameStateLabel;

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
    private static final String TITLE = "Tower Of Pisa Builder";
    private static final int FPS = 144;

    // initialize a variable to traverse through the blueprint
    private int traverse = 0;

    private String selectedObj = "";
    private static int TOTAL_NUM_OF_SHAPES = 8;

    // indicate if we have finished the game or want to start a new game
    private boolean gameFinished = false;
    private boolean newGame = true;

    // initialize our camera class
    private Camera camera;
    private HashSet<Integer> matchedObj = new HashSet<>();
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
    Shader blueprintShader;
    //Textures
    TextureLoader towerTexture;
    TextureLoader groundTexture;

    HashMap<Integer,Mesh> meshes = new HashMap<>();
    HashMap<Integer,Mesh> blue_meshes = new HashMap<>();
    private Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    //to Log the graphics(opengl) context parameters
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
            //canvas.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
            canvas.setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));

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
            quitButton = new JButton("Quit");
            helpButton = new JButton("Help");
            newGameButton = new JButton("New Game");

            removeButton.setPreferredSize(new Dimension(100, 20));
            addButton.setPreferredSize(new Dimension(100, 20));
            quitButton.setPreferredSize(new Dimension(100, 20));
            helpButton.setPreferredSize(new Dimension(100, 20));
            newGameButton.setPreferredSize(new Dimension(100, 20));

            // initialize the JLabel
            label = new JLabel("Click On The Help Button To Read Game Instructions");
            gameStateLabel = new JLabel("Add appropriate objects to the blueprint!");

            // initialize the JCheckbox
            wireFrame = new JCheckBox("Blueprint Mode", true);
            light = new JCheckBox("Diffuse Light",false);

            imageLabel1 = new JLabel(new ImageIcon("src/textures/bottom.png"));
            imageLabel2 = new JLabel(new ImageIcon("src/textures/middle.png"));
            imageLabel3 = new JLabel(new ImageIcon("src/textures/top.png"));

            imageLabel1.setPreferredSize(new Dimension(100, 100));
            imageLabel2.setPreferredSize(new Dimension(100, 100));
            imageLabel3.setPreferredSize(new Dimension(100, 100));

            imageLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
            gameStateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel windowPanel = new JPanel();
            windowPanel.setLayout(new BoxLayout(windowPanel,BoxLayout.Y_AXIS));

            // create the panel for the first row
            JPanel topPanel = new JPanel();

            topPanel.add(gameStateLabel);
            topPanel.add(imageLabel1);
            topPanel.add(imageLabel2);
            topPanel.add(imageLabel3);
            topPanel.add(removeButton);
            topPanel.add(addButton);
            topPanel.add(light);
            topPanel.add(wireFrame);
            topPanel.setPreferredSize(new Dimension(getWidth(),110));
            windowPanel.add(topPanel);

            JPanel bottomPanel = new JPanel();
            bottomPanel.add(label);
            bottomPanel.add(helpButton);
            bottomPanel.add(newGameButton);
            bottomPanel.add(quitButton);
            bottomPanel.setPreferredSize(new Dimension(getWidth(),30));
            windowPanel.add(bottomPanel);

            frame.add(windowPanel, BorderLayout.SOUTH);

            light.setFocusable(false);
            wireFrame.setFocusable(false);

            imageLabel1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        imageLabel1.setBorder(BorderFactory.createLineBorder(Color.RED,5));
                        imageLabel2.setBorder(null);
                        imageLabel3.setBorder(null);
                        selectedObj = "bottom";
                        System.out.println(selectedObj);
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
                        selectedObj = "middle";
                        System.out.println(selectedObj);
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
                        selectedObj = "top";
                        System.out.println(selectedObj);
                    }
                }
            });
            addButton.addActionListener(e -> {
                if (e.getSource() == addButton) {
                    if(traverse > 0 && traverse <= TOTAL_NUM_OF_SHAPES)
                    {
                        if (selectedObj.equals("bottom")) {
                            taskQueue.add(() -> {
                                try {
                                    Mesh mesh =  OBJLoader.loadMesh("src/models/blueprint_models/bottom.obj", defaultShader);
                                    mesh.setPosition(blue_meshes.get(traverse).getPosition());
                                    mesh.meshId = blue_meshes.get(traverse).meshId;
                                    meshes.put(traverse,mesh);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }
                        else if (selectedObj.equals("middle")) {
                            taskQueue.add(() -> {
                                try {
                                    Mesh mesh =  OBJLoader.loadMesh("src/models/blueprint_models/middle.obj", defaultShader);
                                    mesh.setPosition(blue_meshes.get(traverse).getPosition());
                                    meshes.put(traverse,mesh);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }
                        else if (selectedObj.equals("top")) {
                            taskQueue.add(() -> {
                                try {
                                    Mesh mesh =  OBJLoader.loadMesh("src/models/blueprint_models/top.obj", defaultShader);
                                    mesh.setPosition(blue_meshes.get(traverse).getPosition());
                                    meshes.put(traverse,mesh);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }
                    }
                }
                addButton.setFocusable(false);
            });


            removeButton.addActionListener(e -> {
                meshes.remove(traverse);
                removeButton.setFocusable(false);
            });

            helpButton.addActionListener( e -> {
                if (e.getSource() == helpButton) {

                    JOptionPane.showMessageDialog(frame, "Instructions: \n" +
                                    "T - traverse through the blueprint\n" +
                                    "Add Button - after selecting a shape from the palette, you can add it to the selected blueprint shape by the Add button\n" +
                                    "Remove Button - after selecting a shape from the palette, you can remove it from the selected blueprint shape by the Remove button\n" +
                                    "Finish Button - after the game finished, by pressing on the finish button, you can see your results\n" +
                                    "New Game Button - generate a new game\n" +
                                    "Quit Button - quit from the game\n" +
                                    "Light - you can enable/disable different light models by checking/unchecking the light checkboxes (global ambient light, ambient, diffuse, and specular)\n" +
                                    "W - Move the camera forward\n" +
                                    "S - Move the camera backward\n" +
                                    "A - Move the camera left\n" +
                                    "D - Move the camera right\n" +
                                    "Mouse Movement - Rotate the camera by moving the mouse\n"
                            ,"Help", JOptionPane.INFORMATION_MESSAGE);
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
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setTitle(TITLE);
            frame.setVisible(true);
            animator.start();
        });

    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL4 gl = glAutoDrawable.getGL().getGL4();
        gl.glClearColor(0.2f, 0.3f, 0.4f, 1.0f);

        gl.glEnable(GL4.GL_DEPTH_TEST);

        gl.glClearDepth(1.0f); // set clear depth value to farthest
        gl.glEnable(GL4.GL_DEPTH_TEST); // enable depth testing
        gl.glDepthFunc(GL4.GL_LEQUAL); // the type of depth test to do

        gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_FILL);

        defaultShader = new Shader("src/shaders/default.vert", "src/shaders/default.frag");
        blueprintShader = new Shader("src/shaders/blueprint.vert", "src/shaders/blueprint.frag");
        // Create identity matrices
        Matrix4f modelMatrix = new Matrix4f().loadIdentity();
        Matrix4f viewMatrix = new Matrix4f().loadIdentity();
        Matrix4f projectionMatrix = new Matrix4f().loadIdentity();

        defaultShader.use();
        defaultShader.setUniformMat4f("model", modelMatrix.get(new float[16]));
        defaultShader.setUniformMat4f("view", viewMatrix.get(new float[16]));
        defaultShader.setUniformMat4f("projection", projectionMatrix.get(new float[16]));
        defaultShader.stop();

        blueprintShader.use();
        blueprintShader.setUniformMat4f("model", modelMatrix.get(new float[16]));
        blueprintShader.setUniformMat4f("view", viewMatrix.get(new float[16]));
        blueprintShader.setUniformMat4f("projection", projectionMatrix.get(new float[16]));
        blueprintShader.stop();
        float posY = 3.3f;
        try {
            blue_bottom = OBJLoader.loadMesh("src/models/blueprint_models/bottom.obj", blueprintShader);
            blue_bottom.setPositionY(posY);
            blue_meshes.put(1,blue_bottom);
            blue_mid1 = OBJLoader.loadMesh("src/models/blueprint_models/middle.obj", blueprintShader);
            blue_mid1.setPositionY(posY+=4.5f);
            blue_meshes.put(2,blue_mid1);
            blue_mid2 = OBJLoader.loadMesh("src/models/blueprint_models/middle.obj", blueprintShader);
            blue_mid2.setPositionY(posY+=3.5f);
            blue_meshes.put(3,blue_mid2);
            blue_mid3 = OBJLoader.loadMesh("src/models/blueprint_models/middle.obj", blueprintShader);
            blue_mid3.setPositionY(posY+=3.5f);
            blue_meshes.put(4,blue_mid3);
            blue_mid4 = OBJLoader.loadMesh("src/models/blueprint_models/middle.obj", blueprintShader);
            blue_mid4.setPositionY(posY+=3.5f);
            blue_meshes.put(5,blue_mid4);
            blue_mid5 = OBJLoader.loadMesh("src/models/blueprint_models/middle.obj", blueprintShader);
            blue_mid5.setPositionY(posY+=3.5f);
            blue_meshes.put(6,blue_mid5);
            blue_mid6 = OBJLoader.loadMesh("src/models/blueprint_models/middle.obj", blueprintShader);
            blue_mid6.setPositionY(posY+=3.5f);
            blue_meshes.put(7,blue_mid6);
            blue_top = OBJLoader.loadMesh("src/models/blueprint_models/top.obj", blueprintShader);
            blue_top.setPositionY(posY+=4.3f);
            blue_meshes.put(8,blue_top);

            ground = OBJLoader.loadMesh("src/models/plane.obj",defaultShader);

            towerTexture = new TextureLoader("src/textures/tower.jpg",glAutoDrawable.getGL().getGL4());
            groundTexture = new TextureLoader("src/textures/ground.jpg",glAutoDrawable.getGL().getGL4());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        camera = new Camera(WINDOW_WIDTH, WINDOW_HEIGHT, new Vec3f(0, 40, 70));
        camera.updateMatrix(45.0f, 0.1f, 1000.0f);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) { }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL4 gl = glAutoDrawable.getGL().getGL4();
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        boolean drawBlueprint = wireFrame.isSelected();
        while (!taskQueue.isEmpty()) {
            taskQueue.poll().run();
        }
        if (camera == null) {
            throw new IllegalStateException("Camera has not been initialized.");
        }
        // Update and upload the camera matrices to the shader
        camera.updateMatrix(45.0f, 0.1f, 1000.0f);

        defaultShader.use();
        camera.uploadToShader(gl, defaultShader, "projection", "view");
        defaultShader.stop();
        blueprintShader.use();
        camera.uploadToShader(gl, blueprintShader, "projection", "view");
        blueprintShader.stop();

        defaultShader.use();
        towerTexture.bind(gl);

        int lightUniLoc = gl.glGetUniformLocation(defaultShader.programId, "light");
        gl.glUniform1i(lightUniLoc, light.isSelected()?1:0);

        int diffuseTextureLoc = gl.glGetUniformLocation(defaultShader.programId, "diffuseTexture");
        gl.glUniform1i(diffuseTextureLoc, 0);

        int viewPosLoc = gl.glGetUniformLocation(defaultShader.programId, "viewPos");

        float[] viewPos = {camera.position.x(),camera.position.y(),camera.position.z()};
        gl.glUniform3fv(viewPosLoc, 1, viewPos, 0);

        if(!meshes.isEmpty())
        {
            for (int i = 1;i<=TOTAL_NUM_OF_SHAPES;i++)
            {
                if(meshes.containsKey(i))
                {
                    meshes.get(i).draw(false,false);
                }
            }
        }
        if(drawBlueprint)
        {
            switch (traverse)
            {
                case 1:
                    blue_bottom.draw(true,true);
                    blue_mid1.draw(true,false);
                    blue_mid2.draw(true,false);
                    blue_mid3.draw(true,false);
                    blue_mid4.draw(true,false);
                    blue_mid5.draw(true,false);
                    blue_mid6.draw(true,false);
                    blue_top.draw(true,false);
                    break;
                case 2:
                    blue_bottom.draw(true,false);
                    blue_mid1.draw(true,true);
                    blue_mid2.draw(true,false);
                    blue_mid3.draw(true,false);
                    blue_mid4.draw(true,false);
                    blue_mid5.draw(true,false);
                    blue_mid6.draw(true,false);
                    blue_top.draw(true,false);
                    break;
                case 3:
                    blue_bottom.draw(true,false);
                    blue_mid1.draw(true,false);
                    blue_mid2.draw(true,true);
                    blue_mid3.draw(true,false);
                    blue_mid4.draw(true,false);
                    blue_mid5.draw(true,false);
                    blue_mid6.draw(true,false);
                    blue_top.draw(true,false);
                    break;
                case 4:
                    blue_bottom.draw(true,false);
                    blue_mid1.draw(true,false);
                    blue_mid2.draw(true,false);
                    blue_mid3.draw(true,true);
                    blue_mid4.draw(true,false);
                    blue_mid5.draw(true,false);
                    blue_mid6.draw(true,false);
                    blue_top.draw(true,false);
                    break;
                case 5:
                    blue_bottom.draw(true,false);
                    blue_mid1.draw(true,false);
                    blue_mid2.draw(true,false);
                    blue_mid3.draw(true,false);
                    blue_mid4.draw(true,true);
                    blue_mid5.draw(true,false);
                    blue_mid6.draw(true,false);
                    blue_top.draw(true,false);
                    break;
                case 6:
                    blue_bottom.draw(true,false);
                    blue_mid1.draw(true,false);
                    blue_mid2.draw(true,false);
                    blue_mid3.draw(true,false);
                    blue_mid4.draw(true,false);
                    blue_mid5.draw(true,true);
                    blue_mid6.draw(true,false);
                    blue_top.draw(true,false);
                    break;
                case 7:
                    blue_bottom.draw(true,false);
                    blue_mid1.draw(true,false);
                    blue_mid2.draw(true,false);
                    blue_mid3.draw(true,false);
                    blue_mid4.draw(true,false);
                    blue_mid5.draw(true,false);
                    blue_mid6.draw(true,true);
                    blue_top.draw(true,false);
                    break;
                case 8:
                    blue_bottom.draw(true,false);
                    blue_mid1.draw(true,false);
                    blue_mid2.draw(true,false);
                    blue_mid3.draw(true,false);
                    blue_mid4.draw(true,false);
                    blue_mid5.draw(true,false);
                    blue_mid6.draw(true,false);
                    blue_top.draw(true,true);
                    break;
                default:
                    blue_bottom.draw(true,false);
                    blue_mid1.draw(true,false);
                    blue_mid2.draw(true,false);
                    blue_mid3.draw(true,false);
                    blue_mid4.draw(true,false);
                    blue_mid5.draw(true,false);
                    blue_mid6.draw(true,false);
                    blue_top.draw(true,false);
            }
        }
        towerTexture.unbind(gl);
        defaultShader.stop();

        if(gameFinished){
            printResult();
        }

        if(!gameFinished){
            printMatch();
        }

        if(newGame){
            newGame();
            newGame = false;
        }
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
        WINDOW_HEIGHT = h;
        WINDOW_WIDTH = w;
        camera.height = h;
        camera.width = w;
    }

    private void newGame() {
        writeInfo("Add appropriate objects to the blueprint!",true);
        traverse = 0;
        TOTAL_NUM_OF_SHAPES = 8;
        matchedObj.clear();
        meshes.clear();
    }

    private void printMatch() {
        if(traverse == 1){
            if(meshes.containsKey(traverse))
            {
                boolean isShapeMatched = Objects.equals(blue_meshes.get(traverse).name, meshes.get(traverse).name);
                if(isShapeMatched){
                    writeInfo("Bottom Mesh is Matched",true);
                    matchedObj.add(traverse);
                }
                else
                {
                    matchedObj.remove(traverse);
                }
            }
            else
            {
                matchedObj.remove(traverse);
            }
        }
        else if (traverse == 2) {
            if(meshes.containsKey(traverse))
            {
                boolean isShapeMatched = Objects.equals(blue_meshes.get(traverse).name, meshes.get(traverse).name);

                if(isShapeMatched){
                    writeInfo("Middle Mesh 1 is Matched",true);
                    matchedObj.add(traverse);
                }
                else
                {
                    matchedObj.remove(traverse);
                }
            }
            else
            {
                matchedObj.remove(traverse);
            }
        }
        else if(traverse == 3){
            if(meshes.containsKey(traverse))
            {
                boolean isShapeMatched = Objects.equals(blue_meshes.get(traverse).name, meshes.get(traverse).name);

                if(isShapeMatched){
                    writeInfo("Middle Mesh 2 is Matched",true);
                    matchedObj.add(traverse);
                }
                else
                {
                    matchedObj.remove(traverse);
                }
            }
            else
            {
                matchedObj.remove(traverse);
            }
        }
        else if(traverse == 4){
            if(meshes.containsKey(traverse))
            {
                boolean isShapeMatched = Objects.equals(blue_meshes.get(traverse).name, meshes.get(traverse).name);

                if(isShapeMatched){
                    writeInfo("Middle Mesh 3 is Matched",true);
                    matchedObj.add(traverse);
                }
                else
                {
                    matchedObj.remove(traverse);
                }
            }
            else
            {
                matchedObj.remove(traverse);
            }
        }
        else if(traverse == 5){
            if(meshes.containsKey(traverse))
            {
                boolean isShapeMatched = Objects.equals(blue_meshes.get(traverse).name, meshes.get(traverse).name);

                if(isShapeMatched){
                    writeInfo("Middle Mesh 4 is Matched",true);
                    matchedObj.add(traverse);
                }
                else
                {
                    matchedObj.remove(traverse);
                }
            }
            else
            {
                matchedObj.remove(traverse);
            }
        }
        else if(traverse == 6){
            if(meshes.containsKey(traverse))
            {
                boolean isShapeMatched = Objects.equals(blue_meshes.get(traverse).name, meshes.get(traverse).name);

                if(isShapeMatched){
                    writeInfo("Middle Mesh 5 is Matched",true);
                    matchedObj.add(6);
                }
                else
                {
                    matchedObj.remove(traverse);
                }
            }
            else
            {
                matchedObj.remove(traverse);
            }
        }
        else if(traverse == 7){
            if (meshes.containsKey(traverse)) {
                boolean isShapeMatched = Objects.equals(blue_meshes.get(traverse).name, meshes.get(traverse).name);

                if (isShapeMatched) {
                    writeInfo("Middle Mesh 6 is Matched",true);
                    matchedObj.add(traverse);
                }
                else
                {
                    matchedObj.remove(traverse);
                }
            }
            else
            {
                matchedObj.remove(traverse);
            }
        }
        else if(traverse == 8){
            if (meshes.containsKey(traverse)) {
                boolean isShapeMatched = Objects.equals(blue_meshes.get(traverse).name, meshes.get(traverse).name);

                if (isShapeMatched) {
                    writeInfo("Top Mesh is Matched",true);
                    matchedObj.add(traverse);
                }
                else
                {
                    matchedObj.remove(traverse);
                }
            }
            else
            {
                matchedObj.remove(traverse);
            }
        }

        if(matchedObj.size() >= TOTAL_NUM_OF_SHAPES)
        {
            gameFinished = true;
        }
    }

    private void writeInfo(String msg,boolean isPositive) {
        SwingUtilities.invokeLater(() -> {
            gameStateLabel.setBackground(isPositive?Color.GREEN:Color.RED);
            gameStateLabel.setText(msg);

            //refresh the value on label
            gameStateLabel.revalidate();
            gameStateLabel.repaint();
        });
    }

    private void printResult(){
        writeInfo("GAME OVER! All the shapes are matched successfully!",true);
    }

    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();
        camera.handleKeyInput(e);

        // traverse through the blueprint
        if(key == KeyEvent.VK_T){
            traverse += 1;
            if(traverse == TOTAL_NUM_OF_SHAPES+1){
                traverse = 0;
            }
        }
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
