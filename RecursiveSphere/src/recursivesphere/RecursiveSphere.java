package recursivesphere;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import javax.swing.JFrame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class RecursiveSphere implements GLEventListener {

    private final GLU glu = new GLU();
    
    private volatile int subdivisionDepth = 0;
    
    private int gouraudProgram = 0;
    private int phongProgram = 0;
    
    private volatile boolean usePhong = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Recursive Sphere");

        GLCanvas canvas = new GLCanvas();
        RecursiveSphere sphere = new RecursiveSphere();
canvas.addGLEventListener(sphere);

canvas.addKeyListener(new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (sphere.subdivisionDepth < 5) {
                sphere.subdivisionDepth++;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (sphere.subdivisionDepth > 0) {
                sphere.subdivisionDepth--;
            }
        }
        
        if (e.getKeyCode() == KeyEvent.VK_G) {

    sphere.usePhong = false;

    System.out.println(
        "Shading Mode: Gouraud"
    );
}

if (e.getKeyCode() == KeyEvent.VK_P) {

    sphere.usePhong = true;

    System.out.println(
        "Shading Mode: Phong"
    );
}

        System.out.println(
            "Subdivision Depth: "
            + sphere.subdivisionDepth
        );

        canvas.display();
    }
});

canvas.setFocusable(true);


        frame.add(canvas);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        canvas.requestFocusInWindow();
    }

    @Override
public void init(GLAutoDrawable drawable) {

    GL2 gl = drawable.getGL().getGL2();

    gl.glClearColor(0f, 0f, 0f, 1f);
    gl.glEnable(GL.GL_DEPTH_TEST);

    // Enable lighting
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);

    // Ambient lighting
    float[] ambient = {
        0.2f, 0.2f, 0.2f, 1.0f
    };

    // Diffuse lighting
    float[] diffuse = {
        0.7f, 0.7f, 0.7f, 1.0f
    };

    // Specular lighting
    float[] specular = {
        1.0f, 1.0f, 1.0f, 1.0f
    };

    gl.glLightfv(GL2.GL_LIGHT0,
            GL2.GL_AMBIENT, ambient, 0);

    gl.glLightfv(GL2.GL_LIGHT0,
            GL2.GL_DIFFUSE, diffuse, 0);

    gl.glLightfv(GL2.GL_LIGHT0,
            GL2.GL_SPECULAR, specular, 0);

    // Set sphere material color
    float[] materialDiffuse = {
        0.2f, 0.6f, 1.0f, 1.0f
    };

    float[] materialSpecular = {
        1.0f, 1.0f, 1.0f, 1.0f
    };

    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,
            GL2.GL_DIFFUSE, materialDiffuse, 0);

    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,
            GL2.GL_SPECULAR, materialSpecular, 0);

    gl.glMaterialf(GL2.GL_FRONT_AND_BACK,
            GL2.GL_SHININESS, 50.0f);

    // Smooth shading
    gl.glShadeModel(GL2.GL_SMOOTH);
    
        // Create the Gouraud shader program
    gouraudProgram = createShaderProgram(
        gl,
        "gouraud.vert",
        "gouraud.frag"
    );

    // Create the Phong shader program
    phongProgram = createShaderProgram(
        gl,
        "phong.vert",
        "phong.frag"
    );

    System.out.println(
        "Both shading programs initialized."
    );

}

   @Override
public void display(GLAutoDrawable drawable) {

    GL2 gl = drawable.getGL().getGL2();

    gl.glClear(GL.GL_COLOR_BUFFER_BIT
            | GL.GL_DEPTH_BUFFER_BIT);

    gl.glLoadIdentity();
    
    float[] lightPosition = {
    2.0f, 3.0f, 4.0f, 1.0f
};

gl.glLightfv(GL2.GL_LIGHT0,
        GL2.GL_POSITION, lightPosition, 0);

    // Move the camera back to view the sphere
    gl.glTranslated(0.0, 0.0, -4.0);

    // Rotate the sphere for a better 3D view
    gl.glRotated(25.0, 1.0, 0.0, 0.0);
    gl.glRotated(35.0, 0.0, 1.0, 0.0);

       // Select the active shading program
    if (usePhong) {
        gl.glUseProgram(phongProgram);
    } else {
        gl.glUseProgram(gouraudProgram);
    }

    // Draw the sphere using the selected shader
    drawSphere(gl);

    // Deactivate the shader program
    gl.glUseProgram(0);

    gl.glFlush();
}

    @Override
    public void reshape(GLAutoDrawable drawable,
                        int x, int y,
                        int width, int height) {

        GL2 gl = drawable.getGL().getGL2();

        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(
                45.0,
                (double) width / Math.max(height, 1),
                0.1,
                100.0
        );

        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
    
    private void drawSphere(GL2 gl) {

    double[][] vertices = {
        { 1, 0, 0 },
        {-1, 0, 0 },
        { 0, 1, 0 },
        { 0,-1, 0 },
        { 0, 0, 1 },
        { 0, 0,-1 }
    };

    int[][] faces = {
        {0, 2, 4},
        {2, 1, 4},
        {1, 3, 4},
        {3, 0, 4},
        {2, 0, 5},
        {1, 2, 5},
        {3, 1, 5},
        {0, 3, 5}
    };

    gl.glBegin(GL2.GL_TRIANGLES);

    for (int[] face : faces) {
        subdivide(
            gl,
            vertices[face[0]],
            vertices[face[1]],
            vertices[face[2]],
            subdivisionDepth
        );
    }

    gl.glEnd();
}

private void subdivide(GL2 gl,
                       double[] a,
                       double[] b,
                       double[] c,
                       int depth) {

    if (depth == 0) {
        drawTriangle(gl, a, b, c);
        return;
    }

    double[] ab = normalize(midpoint(a, b));
    double[] bc = normalize(midpoint(b, c));
    double[] ca = normalize(midpoint(c, a));

    subdivide(gl, a, ab, ca, depth - 1);
    subdivide(gl, ab, b, bc, depth - 1);
    subdivide(gl, ca, bc, c, depth - 1);
    subdivide(gl, ab, bc, ca, depth - 1);
}

private double[] midpoint(double[] a, double[] b) {
    return new double[] {
        (a[0] + b[0]) / 2.0,
        (a[1] + b[1]) / 2.0,
        (a[2] + b[2]) / 2.0
    };
}

private double[] normalize(double[] v) {

    double length = Math.sqrt(
        v[0] * v[0] +
        v[1] * v[1] +
        v[2] * v[2]
    );

    return new double[] {
        v[0] / length,
        v[1] / length,
        v[2] / length
    };
}

private void drawTriangle(GL2 gl,
                          double[] a,
                          double[] b,
                          double[] c) {

    // Vertex A normal
    gl.glNormal3dv(a, 0);
    gl.glVertex3dv(a, 0);

    // Vertex B normal
    gl.glNormal3dv(b, 0);
    gl.glVertex3dv(b, 0);

    // Vertex C normal
    gl.glNormal3dv(c, 0);
    gl.glVertex3dv(c, 0);
}
private String readShaderFile(String filename) {
    try {
        Path shaderPath = Paths.get(
    "C:/JavaProjects/RecursiveSphere/shaders"
).resolve(filename).toAbsolutePath().normalize();
        
        System.out.println("Shader path: " + shaderPath);
System.out.println("File exists: " + Files.exists(shaderPath));
System.out.println("Regular file: " + Files.isRegularFile(shaderPath));
System.out.println("Readable: " + Files.isReadable(shaderPath));

        System.out.println("Loading shader: " + shaderPath);
        System.out.println("Absolute path: "
        + shaderPath.toAbsolutePath());

System.out.println("File exists: "
        + Files.exists(shaderPath));

System.out.println("Regular file: "
        + Files.isRegularFile(shaderPath));

System.out.println("Readable: "
        + Files.isReadable(shaderPath));

        byte[] bytes = Files.readAllBytes(shaderPath);

        return new String(bytes, StandardCharsets.UTF_8);

    } catch (Exception e) {
        throw new RuntimeException(
            "Unable to load shader: " + filename, e
        );
    }
}

private int compileShader(GL2 gl, int type,
                          String filename) {

    // Create a shader object
    int shader = gl.glCreateShader(type);

    // Read shader source code
    String source = readShaderFile(filename);

    String[] lines = {source};
    int[] lengths = {source.length()};

    // Supply source code to OpenGL
    gl.glShaderSource(
        shader, 1, lines, lengths, 0
    );

    // Compile the shader
    gl.glCompileShader(shader);

    // Check compilation status
    int[] status = new int[1];

    gl.glGetShaderiv(
        shader,
        GL2.GL_COMPILE_STATUS,
        status, 0
    );

    if (status[0] == GL.GL_FALSE) {

        int[] logLength = new int[1];

        gl.glGetShaderiv(
            shader,
            GL2.GL_INFO_LOG_LENGTH,
            logLength, 0
        );

        int size = Math.max(logLength[0], 1);
        byte[] log = new byte[size];
        int[] actualLength = new int[1];

        gl.glGetShaderInfoLog(
            shader, size,
            actualLength, 0,
            log, 0
        );

        String message = new String(
            log, 0, actualLength[0],
            StandardCharsets.UTF_8
        );

        gl.glDeleteShader(shader);

        throw new RuntimeException(
            "Shader compilation failed: "
            + filename + "\n" + message
        );
    }

    System.out.println(
        "Shader compiled successfully: "
        + filename
    );

    return shader;
}
private int createShaderProgram(GL2 gl,
                                String vertexFile,
                                String fragmentFile) {

    // Compile vertex shader
    int vertexShader = compileShader(
        gl,
        GL2.GL_VERTEX_SHADER,
        vertexFile
    );

    // Compile fragment shader
    int fragmentShader = compileShader(
        gl,
        GL2.GL_FRAGMENT_SHADER,
        fragmentFile
    );

    // Create shader program
    int program = gl.glCreateProgram();

    // Attach shaders
    gl.glAttachShader(program, vertexShader);
    gl.glAttachShader(program, fragmentShader);

    // Link program
    gl.glLinkProgram(program);

    // Check linking status
    int[] status = new int[1];

    gl.glGetProgramiv(
        program,
        GL2.GL_LINK_STATUS,
        status, 0
    );

    if (status[0] == GL.GL_FALSE) {

        int[] logLength = new int[1];

        gl.glGetProgramiv(
            program,
            GL2.GL_INFO_LOG_LENGTH,
            logLength, 0
        );

        int size = Math.max(logLength[0], 1);
        byte[] log = new byte[size];
        int[] actualLength = new int[1];

        gl.glGetProgramInfoLog(
            program, size,
            actualLength, 0,
            log, 0
        );

        String message = new String(
            log, 0, actualLength[0],
            StandardCharsets.UTF_8
        );

        gl.glDeleteProgram(program);
        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);

        throw new RuntimeException(
            "Shader linking failed:\n" + message
        );
    }

    // Shaders can be deleted after linking
    gl.glDetachShader(program, vertexShader);
    gl.glDetachShader(program, fragmentShader);

    gl.glDeleteShader(vertexShader);
    gl.glDeleteShader(fragmentShader);

    System.out.println(
        "Shader program linked successfully: "
        + vertexFile + " + " + fragmentFile
    );

    return program;
}
}