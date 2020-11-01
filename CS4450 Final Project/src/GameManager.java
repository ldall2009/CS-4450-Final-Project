/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import Input.*;

/**
 *
 */
public class GameManager {

    private static final int ESCAPE = Keyboard.KEY_ESCAPE;

	// See constructor for key bindings
	private static final int VERTICAL_AXIS = 0;

	private static final int FORWARD_AXIS  = 1;
	private static final int STRAFE_AXIS   = 2;

	private static final int FORWARD_AXIS_ALT  = 3;
	private static final int STRAFE_AXIS_ALT   = 4;

	private InputManager input;
	private FPCameraController camera;
	private Chunk chunk;

	public GameManager() {

		input = new InputManager();
		input.addButton(ESCAPE, ESCAPE);
		input.addAxis(VERTICAL_AXIS,    Keyboard.KEY_SPACE, Keyboard.KEY_LSHIFT);
		input.addAxis(FORWARD_AXIS,     Keyboard.KEY_W,     Keyboard.KEY_S);
		input.addAxis(STRAFE_AXIS,      Keyboard.KEY_D,     Keyboard.KEY_A);
		input.addAxis(FORWARD_AXIS_ALT, Keyboard.KEY_UP,    Keyboard.KEY_DOWN);
		input.addAxis(STRAFE_AXIS_ALT,  Keyboard.KEY_RIGHT, Keyboard.KEY_LEFT);

        camera = new FPCameraController(-30, -70, -30);
		chunk = new Chunk(0, 0, 0);
	}
	
    /***************************************************************
    * method: gameLoop
    * purpose: the primary game loop for this program.  Responsible for
    *   calling relevant methods to respond to things like rendering, user
    *   input, etc.
    *
    ****************************************************************/ 
    public void gameLoop() {
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f; // length of frame
        float lastTime = 0.0f; // when the last frame was
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = .35f;
        // hide the mouse
        Mouse.setGrabbed(true);

		input.update();
		chunk.rebuildMesh();

        // keep looping till the display window is closed the ESC key is down
        //while (!Display.isCloseRequested() && !Keyboard.isKeyDown(ESCAPE)) {
        while (!Display.isCloseRequested() && !input.isDown(ESCAPE)) {
			input.update();
            time = Sys.getTime();
            lastTime = time;
            
            // distance in mouse movement
            // from the last getDX() call.
            dx = Mouse.getDX();
            
            // distance in mouse movement
            // from the last getDY() call.
            dy = Mouse.getDY();
            
            // control camera yaw from x movement front the mouse
            camera.yaw(dx * mouseSensitivity);
            
            // control camera pitch from y movement front the mouse
            camera.pitch(dy * mouseSensitivity);
            
            // when passing in the distance to move
            // we times the movementSpeed with dt this is a time scale
            // so if its a slow frame u move more then a fast frame
            // so on a slow computer you move just as fast as on a fast computer
			camera.walkForward(input.getCombinedAxis(
				new int[]{FORWARD_AXIS, FORWARD_AXIS_ALT}) * movementSpeed
			);
			camera.strafeRight(
				input.getCombinedAxis(new int[]{STRAFE_AXIS, STRAFE_AXIS_ALT}) * movementSpeed
			);
			camera.moveUp(input.getAxis(VERTICAL_AXIS) * movementSpeed);
            
            // set the modelview matrix back to the identity
            glLoadIdentity();
            
            // look through the camera before you draw anything
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            // you would draw your scene here.
			chunk.render();
            //render();
            
            // draw the buffer to the screen
            Display.update();
            Display.sync(60);
        }
    }

    /***************************************************************
    * method: render
    * purpose: renders a 3D cube. (Used for testing)
    *
    ****************************************************************/ 
    private void render() {
        try {
            glBegin(GL_QUADS);
            glColor3f(1.0f, 0.0f, 1.0f);
            
            glVertex3f(1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            glColor3f(0.7f, 0.2f, 1.0f);
            
            glVertex3f(1.0f, -1.0f, -3.0f);
            glVertex3f(-1.0f, -1.0f, -3.0f);
            glVertex3f(-1.0f, 1.0f, -3.0f);
            glVertex3f(1.0f, 1.0f, -3.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            glColor3f(0.0f, 0.0f, 1.0f);
            
            glVertex3f(1.0f, -1.0f, -1.0f);
            glVertex3f(1.0f, -1.0f, -3.0f);
            glVertex3f(1.0f, 1.0f, -3.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            glColor3f(1.0f, 0.0f, 0.0f);
            
            glVertex3f(-1.0f, -1.0f, -3.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -3.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            glColor3f(1.0f, 0.5f, 0.5f);
            
            glVertex3f(1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -3.0f);
            glVertex3f(1.0f, -1.0f, -3.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            glColor3f(0.0f, 1.0f, 0.0f);
            
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -3.0f);
            glVertex3f(1.0f, 1.0f, -3.0f);
            glEnd();

            
        } catch (Exception e) { }
    }
}
