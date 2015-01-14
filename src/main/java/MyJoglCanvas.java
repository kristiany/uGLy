import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.FPSAnimator;

public class MyJoglCanvas extends GLJPanel implements GLEventListener {
    private static final int FPS = 60;
    private GLU glu;
    private FPSAnimator animator;
    private float angle;
    private int listId;
    private int program;

    public MyJoglCanvas(final int width, final int height, final GLCapabilities capabilities) {
        super(capabilities);
        setPreferredSize(new Dimension(width, height));
        this.addGLEventListener(this);
        this.addKeyListener(new KeyActionListener(this::stop));
    }

    @Override
    public void init(final GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        //drawable.setGL(new DebugGL2(gl));
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        //gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glClearDepth(1f);
        this.animator = new FPSAnimator(this, FPS, true);
        this.animator.start();
        this.glu = GLU.createGLU();
        this.listId = gl.glGenLists(1);
        gl.glNewList(this.listId, GL2.GL_COMPILE);
            triangle(gl);
        gl.glEndList();
        setupShader(gl);
        System.out.println("Init ready");
    }

    private void setupShader(final GL2 gl) {
        final int shader = ShaderUtils.loadVertexShaderFromFile(gl, "src/main/resource/shaders/3lights.vs");
        final FloatBuffer tLightPos0 = DirectBufferUtils.createDirectFloatBuffer(new float[]{0.0f, 30.0f, 30.0f,0.0f});
        final FloatBuffer tLightPos1 = DirectBufferUtils.createDirectFloatBuffer(new float[]{30.0f, 30.0f, 0.0f,0.0f});
        final FloatBuffer tLightPos2 = DirectBufferUtils.createDirectFloatBuffer(new float[]{0.0f,-30.0f,-30.0f,0.0f});
        final FloatBuffer tLightCol0 = DirectBufferUtils.createDirectFloatBuffer(new float[]{1.0f, 0.25f, 0.25f, 1.0f});
        final FloatBuffer tLightCol1 = DirectBufferUtils.createDirectFloatBuffer(new float[]{0.25f, 1.0f, 0.25f, 1.0f});
        final FloatBuffer tLightCol2 = DirectBufferUtils.createDirectFloatBuffer(new float[]{0.25f, 0.25f, 1.0f, 1.0f});
        this.program = ShaderUtils.generateSimple_1xFS_OR_1xVS_ShaderProgramm(gl, shader);
        gl.glUseProgram(this.program);
        ShaderUtils.setUniform3fv(gl, this.program, "lightPos[0]", tLightPos0);
        ShaderUtils.setUniform3fv(gl, this.program, "lightPos[1]", tLightPos1);
        ShaderUtils.setUniform3fv(gl, this.program, "lightPos[2]", tLightPos2);
        ShaderUtils.setUniform4fv(gl, this.program, "lightCol[0]", tLightCol0);
        ShaderUtils.setUniform4fv(gl, this.program, "lightCol[1]", tLightCol1);
        ShaderUtils.setUniform4fv(gl, this.program, "lightCol[2]", tLightCol2);
        gl.glUseProgram(0);
    }

    @Override
    public void dispose(final GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glDeleteLists(this.listId, 1);
        gl.glDeleteProgram(this.program);
    }

    public void stop() {
        if(this.animator.isStarted()) {
            System.out.println("Closing animator");
            this.animator.stop();
        }
    }

    @Override
    public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
        drawable.getGL().glViewport(0, 0, width, height);
        final GL2 gl = drawable.getGL().getGL2();
        final float aspect = height > 0 ? (float)width / height : width;
        gl.glViewport(0, 0, width, height);
        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        this.glu.gluPerspective(45.0, aspect, 0.1, 100.0);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void display(final GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        setCamera(gl, this.glu, 100);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -6.0f);
        gl.glRotatef(this.angle, 0.0f, 1.0f, 0.0f);
        gl.glUseProgram(this.program);
        gl.glCallList(this.listId);
        gl.glUseProgram(0);
        this.angle += 0.2f;
    }

    private void triangle(final GL2 gl) {
        gl.glColor3f(0.9f, 0.5f, 0.2f);
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glVertex3f(-20, -20, 0);
        gl.glVertex3f(+20, -20, 0);
        gl.glColor3f(0.7f, 0.3f, 0.1f);
        gl.glVertex3f(0, 20, 0);
        gl.glEnd();
    }

    private void setCamera(final GL gl, final GLU glu, final float distance) {
        gl.getGL2().glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.getGL2().glLoadIdentity();
        final float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);
        gl.getGL2().glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.getGL2().glLoadIdentity();
    }

    public static void main(final String[] args) {
        final GLCapabilities capabilities = new GLCapabilities(GLProfile.getDefault());
        capabilities.setRedBits(8);
        capabilities.setBlueBits(8);
        capabilities.setGreenBits(8);
        capabilities.setAlphaBits(8);
        SwingUtilities.invokeLater(() -> {
            final MyJoglCanvas canvas = new MyJoglCanvas(500, 500, capabilities);
            final JFrame frame = new JFrame();
            frame.getContentPane().add(canvas);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent e) {
                    // Use a dedicate thread to run the stop() to ensure that the
                    // animator stops before program exits.
                    new Thread() {
                        @Override
                        public void run() {
                            canvas.stop();
                            System.exit(0);
                        }
                    }.start();
                }
            });
            // Fullscreen
            //frame.setUndecorated(true);
            //frame.setExtendedState(Frame.MAXIMIZED_BOTH);  // full screen mode
            // Window
            frame.pack();
            frame.setVisible(true);
        });
    }
}