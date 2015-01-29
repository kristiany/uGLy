import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseActionListener implements MouseMotionListener {
    private static final float MOVEMENT_SENSITIVITY = 0.3f;
    private static final float MAX_MOVEMENT = 10.f;
    private Camera camera;
    private float lastx;
    private float lasty;

    public MouseActionListener(final Camera camera) {
        this.camera = camera;
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
    }

    private float clamp(final float v) {
        if(Math.abs(v) <= MAX_MOVEMENT) {
            return v;
        }
        return v < 0.f ? -MAX_MOVEMENT : MAX_MOVEMENT;
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if(e.isShiftDown()) {
            this.camera.rotate(
                    -MOVEMENT_SENSITIVITY * clamp(this.lastx - e.getX()),
                    -MOVEMENT_SENSITIVITY * clamp(this.lasty - e.getY()),
                    0.f);
        }
        this.lastx = e.getX();
        this.lasty = e.getY();
    }
}
