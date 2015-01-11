import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyActionListener implements KeyListener {

    private final Action onClose;

    public KeyActionListener(final Action onClose) {
        this.onClose = onClose;
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
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

    public interface Action {
        void perform();
    }
}
