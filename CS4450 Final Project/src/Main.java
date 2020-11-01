/***************************************************************
* file: Main.java
* author: N. Vinjamury, D. Edwards, L. Dall
* class: CS 4450 - Computer Graphics
*
* assignment: Semester Project - Checkpoint 2
* date last modified: 10/30/2020
*
* purpose: This program renders a 3D environment that a player can investigate 
*   by using the mouse to control the camera, and by using keys on the keyboard
*   to move around.  This class sets up LWJGL properties as well as starting the
*   game loop.
*
****************************************************************/ 

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class Main {
    
    private static final int SCREEN_WIDTH = 640;
    private static final int SCREEN_HEIGHT = 480;
    
    private FPCameraController fp = new FPCameraController(0f, 0f, 0f);
    private DisplayMode displayMode;

    /***************************************************************
    * method: start
    * purpose: sets up relevant methods for drawing with OpenGL
    *
    ****************************************************************/ 
    public void start() {
        try {
            createWindow();
            initGL();
            fp.gameLoop();// render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************************************
    * method: createWindow
    * purpose: sets up the window
    *
    ****************************************************************/ 
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == SCREEN_WIDTH && d[i].getHeight() == SCREEN_HEIGHT && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("The Tony Trio - Final Project");
        Display.create();
    }

    /***************************************************************
    * method: createWindow
    * purpose: initializes OpenGL components, such as background
    *   color and camera properties
    *   
    ****************************************************************/ 
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glEnable(GL_DEPTH_TEST);    //Renders the cube so no faces are visible when looking at another face
        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);

	glEnable(GL_TEXTURE_2D);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    }

    /***************************************************************
    * method: main
    * purpose: first method called upon application startup
    * 
    ****************************************************************/ 
    public static void main(String[] args) {
        Main program = new Main();
        program.start();
    }
}
