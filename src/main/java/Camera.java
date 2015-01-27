import static java.lang.Math.cos;
import static java.lang.Math.sin;

import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

public class Camera {
    public static final float DEFAULT_SPEED = 3.f;
    private static final float RADS = (float) (Math.PI / 180.f);
    private static final float FULL = 360.f;
    private static final float QUARTER = 90.f;
    private static final float ZERO = 0.f;
    private final Tuple3f position;
    private final Tuple3f rotation;
    private final GLU glu;

    public Camera() {
        this.position = new Point3f(50.f, 30.f, 200.f);
        this.rotation = new Point3f(10.5f, 1.0f, 0.f);
        this.glu = GLU.createGLU();
    }

    public void setPerspective(final GL2 gl, final float width, final float height) {
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        this.glu.gluPerspective(45, ratio(width, height), 1, 1000);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private float ratio(final float width, final float height) {
        return height > 0 ? width / height : width;
    }

    public void update(final GL2 gl) {
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glRotatef(this.rotation.y, 1.f, 0.f, 0.f); // up/down
        gl.glRotatef(this.rotation.x, 0.f, 1.f, 0.f); // left/right
        gl.glTranslatef(-this.position.x, -this.position.y, -this.position.z);
    }

    public void moveForward(final float speed) {
        final Vector3f v = viewVector();
        v.scale(speed);
        this.position.add(v);
    }

    public void moveBackward(final float speed) {
        final Vector3f v = viewVector();
        v.scale(-speed);
        this.position.add(v);
    }

    public void moveLeft(final float speed) {
        final Vector3f v = viewVector();
        this.position.x += speed * v.z;
        this.position.z -= speed * v.x;
    }

    public void moveRight(final float speed) {
        final Vector3f v = viewVector();
        this.position.x -= speed * v.z;
        this.position.z += speed * v.x;
    }

    public void moveUp(final float speed) {
        this.position.y += speed;
    }

    public void moveDown(final float speed) {
        this.position.y -= speed;
    }

    public void rotate(final Tuple3f rotation) {
        float x = rotation.x;
        float y = rotation.y;
        float z = rotation.z;
        if(x > FULL || x < -FULL) {
            x = ZERO;
        }
        if(y > QUARTER) {
            y = QUARTER;
        }
        else if(y < -QUARTER) {
            y = -QUARTER;
        }
        if(z != ZERO) {
            z = ZERO;
        }
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }

    private Vector3f viewVector() {
        // rotation round x-axis
        final float xRadTheta = -this.rotation.y * RADS; // -angle * PI / 180
        // rotate the cameras startvector (0,0,-1)
        // view vector y = cos v * y - sin v * z
        // view vector z = sin v * y + cos v * z
        final float y = (float) sin(xRadTheta);
        final float z = (float) -cos(xRadTheta);
        // rotation round y-axis
        final float yRadTheta = -this.rotation.x * RADS; // -angle * PI / 180
        final float sinRadTheta = (float) sin(yRadTheta);
        final float cosRadTheta = (float) cos(yRadTheta);
        // rotate the cameras current view vector (0,y,z)
        // view vector x = cos v * x + sin v * z
        // view vector z = -sin v * x + cos v * z
        final Vector3f viewVector = new Vector3f(sinRadTheta * z, y, cosRadTheta * z);
        viewVector.normalize();
        return viewVector;
    }
}
