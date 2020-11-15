/***************************************************************
* file: FPCameraController.java
* author: N. Vinjamury, D. Edwards, L. Dall
* class: CS 4450 - Computer Graphics
*
* assignment: Semester Project - Checkpoint 2
* date last modified: 11/14/2020
*
* purpose: This class is responsible for updating the game
* in real time, checking for inputs and passing it along to the
* relevant objects.
*
****************************************************************/ 

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import Input.InputManager;

import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

public class GameManager {
	
	private static final Vector3f CHUNK_POSITION = new Vector3f(0,0,0);
	private static final Vector3f CAMERA_POSITION = new Vector3f(-30,-70,-30);
	
	// See constructor for key bindings
	private static final int ESCAPE = Keyboard.KEY_ESCAPE;
	
	private static final int VERTICAL_AXIS = 0;
	
	private static final int FORWARD_AXIS  = 1;
	private static final int STRAFE_AXIS   = 2;
	
	private static final int FORWARD_AXIS_ALT  = 3;
	private static final int STRAFE_AXIS_ALT   = 4;
	
	private InputManager input;
	private Chunk chunk;
	private FPCameraController camera;

	FloatBuffer lightPosition;
	
	
	public GameManager() {
		chunk = new Chunk((int)CHUNK_POSITION.x, (int)CHUNK_POSITION.y, (int)CHUNK_POSITION.z);
		camera = new FPCameraController(CAMERA_POSITION.x, CAMERA_POSITION.y, CAMERA_POSITION.z);
		
		input = new InputManager();
		input.addButton(ESCAPE, ESCAPE);
		input.addAxis(VERTICAL_AXIS,    Keyboard.KEY_SPACE, Keyboard.KEY_LSHIFT);
		input.addAxis(FORWARD_AXIS,     Keyboard.KEY_W,     Keyboard.KEY_S);
		input.addAxis(STRAFE_AXIS,      Keyboard.KEY_D,     Keyboard.KEY_A);
		input.addAxis(FORWARD_AXIS_ALT, Keyboard.KEY_UP,    Keyboard.KEY_DOWN);
		input.addAxis(STRAFE_AXIS_ALT,  Keyboard.KEY_RIGHT, Keyboard.KEY_LEFT);

		lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(30).put(70).put(40).put(1.0f).flip();
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
		
		chunk.rebuildMesh();
		input.update();
		
		// keep looping till the display window is closed the ESC key is down
		while (!Display.isCloseRequested() && !Keyboard.isKeyDown(ESCAPE)) {
			time = Sys.getTime();
			lastTime = time;
			input.update();
			
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
			camera.walkForward(
				input.getCombinedAxis(new int[]{FORWARD_AXIS, FORWARD_AXIS_ALT})
				* movementSpeed
			);
			camera.strafeRight(
				input.getCombinedAxis(new int[]{STRAFE_AXIS, STRAFE_AXIS_ALT})
				* movementSpeed
			);
			camera.moveUp(input.getAxis(VERTICAL_AXIS) * movementSpeed);
			
			// set the modelview matrix back to the identity
			glLoadIdentity();
			
			// look through the camera before you draw anything
			camera.lookThrough();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			// you would draw your scene here.
			updateLight();
			chunk.render();
			//render();
			
			// draw the buffer to the screen
			Display.update();
			Display.sync(60);
		}
		Display.destroy();
	}

	private void updateLight() {
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
	}
}
