import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyActionListener implements KeyListener {

    private final Action onClose;
    private final Camera camera;

    public KeyActionListener(final Action onClose, final Camera camera) {
        this.onClose = onClose;
        this.camera = camera;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE: // quit
                // Use a dedicate thread to run the stop() to ensure that the
                // animator stops before program exits.
                new Thread() {
                    @Override
                    public void run() {
                        KeyActionListener.this.onClose.perform();
                        System.exit(0);
                    }
                }.start();
                break;
            case KeyEvent.VK_W:
                this.camera.moveForward(Camera.DEFAULT_SPEED);
                break;
            case KeyEvent.VK_S:
                this.camera.moveBackward(Camera.DEFAULT_SPEED);
                break;
            case KeyEvent.VK_A:
                this.camera.moveLeft(Camera.DEFAULT_SPEED);
                break;
            case KeyEvent.VK_D:
                this.camera.moveRight(Camera.DEFAULT_SPEED);
                break;
            case KeyEvent.VK_Q:
                this.camera.moveUp(Camera.DEFAULT_SPEED);
                break;
            case KeyEvent.VK_Z:
                this.camera.moveDown(Camera.DEFAULT_SPEED);
                break;
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

    public interface Action {
        void perform();
    }
}
