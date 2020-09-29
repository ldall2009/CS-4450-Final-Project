
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {
    
    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 480;
    
    private static final int ESCAPE = Keyboard.KEY_ESCAPE;
    private static final int FORWARD = Keyboard.KEY_W;
    private static final int BACKWARD = Keyboard.KEY_S;
    private static final int LEFT = Keyboard.KEY_A;
    private static final int RIGHT = Keyboard.KEY_D;
    private static final int UP = Keyboard.KEY_SPACE;
    private static final int DOWN = Keyboard.KEY_LSHIFT;

    public void start() {
        try {
            createWindow();
            initGL();
            render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT));
        Display.setTitle("Final Project");
        Display.create();
    }

    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, WINDOW_WIDTH, 0, WINDOW_HEIGHT, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    private void render() {

        while (!Display.isCloseRequested()) {

            if (Keyboard.isKeyDown(ESCAPE)) {
                System.out.println("Exiting application...");
                System.exit(0);
            }

            try {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                glPointSize(10);

                Display.update();
                Display.sync(60);
            } catch (Exception e) {
            }
        }
        Display.destroy();
    }

    public static void main(String[] args) {
        Main program = new Main();
        program.start();
    }


}
