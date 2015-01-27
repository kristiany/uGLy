import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2ES2;
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

public class WorldOfWaves extends GLJPanel implements GLEventListener {
    private static final int FPS = 60;
    private GLU glu;
    private FPSAnimator animator;
    private float angle;
    private int timeRef;
    private Land land;

    public WorldOfWaves(final int width, final int height, final GLCapabilities capabilities) {
        super(capabilities);
        setPreferredSize(new Dimension(width, height));
        this.addGLEventListener(this);
        this.addKeyListener(new KeyActionListener(this::stop));
    }

    @Override
    public void init(final GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        System.out.println("GLSL version: " + gl.glGetString(GL2ES2.GL_SHADING_LANGUAGE_VERSION));
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glClearDepth(1f);
        setLight(gl);
        this.animator = new FPSAnimator(this, FPS, true);
        this.animator.start();
        this.glu = GLU.createGLU();
        this.land = new Land(gl, "src/main/resource/maps/land4X64-1024.bmp", 1024, 1024, 100.f, 100.f);
        System.out.println("Init ready");
    }

    private void setLight(final GL2 gl) {
        final FloatBuffer lightPos0 = DirectBufferUtils.createDirectFloatBuffer(new float[]{0.0f, 100.0f, 100.0f, 0.0f}); // direction light
        gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, lightPos0);
        gl.glEnable(GLLightingFunc.GL_LIGHTING);
        gl.glEnable(GLLightingFunc.GL_LIGHT0);
    }

    @Override
    public void dispose(final GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();
        this.land.dispose(gl);
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
        setCamera(gl, this.glu, 20);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(-50.0f, -30.0f, -200.0f);
        gl.glRotatef(10.5f, 1.0f, 0.0f, 0.0f);
        //gl.glRotatef(this.angle, 0.0f, 1.0f, 0.0f);
        //gl.glUniform1f(this.timeRef, this.angle);
        //this.angle += 0.2f;
        this.land.draw(gl);
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
            final WorldOfWaves canvas = new WorldOfWaves(500, 500, capabilities);
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