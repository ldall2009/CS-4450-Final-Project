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

/**
 *
 */
public class GameManager {

	private static final Vector3f CHUNK_POSITION = new Vector3f(0,0,0);
	private static final Vector3f CAMERA_POSITION = new Vector3f(-30,-70,-30);

	private static final int ESCAPE = Keyboard.KEY_ESCAPE;

    private static final int UP = Keyboard.KEY_SPACE;
    private static final int DOWN = Keyboard.KEY_LSHIFT;
    
    private static final int FORWARD = Keyboard.KEY_W;
    private static final int BACKWARD = Keyboard.KEY_S;
    private static final int LEFT = Keyboard.KEY_A;
    private static final int RIGHT = Keyboard.KEY_D;
    
    private static final int FORWARD_ARROW = Keyboard.KEY_UP;
    private static final int BACKWARD_ARROW = Keyboard.KEY_DOWN;
    private static final int LEFT_ARROW = Keyboard.KEY_LEFT;
    private static final int RIGHT_ARROW = Keyboard.KEY_RIGHT;

	private Chunk chunk;
	private FPCameraController camera;


	public GameManager() {
		chunk = new Chunk((int)CHUNK_POSITION.x, (int)CHUNK_POSITION.y, (int)CHUNK_POSITION.z);
		camera = new FPCameraController(CAMERA_POSITION.x, CAMERA_POSITION.y, CAMERA_POSITION.z);
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

        // keep looping till the display window is closed the ESC key is down
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(ESCAPE)) {
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
            if (Keyboard.isKeyDown(FORWARD) || Keyboard.isKeyDown(FORWARD_ARROW))// move forward
            {
                camera.walkForward(movementSpeed);
            }
            
            if (Keyboard.isKeyDown(BACKWARD) || Keyboard.isKeyDown(BACKWARD_ARROW))// move backwards
            {
                camera.walkBackwards(movementSpeed);
            }
            
            if (Keyboard.isKeyDown(LEFT) || Keyboard.isKeyDown(LEFT_ARROW)) {// strafe left {
                camera.strafeLeft(movementSpeed);
            }
            
            if (Keyboard.isKeyDown(RIGHT) || Keyboard.isKeyDown(RIGHT_ARROW))// strafe right {
            {
                camera.strafeRight(movementSpeed);
            }
            
            if (Keyboard.isKeyDown(UP)) // move up
            {
                camera.moveUp(movementSpeed);
            }

            if (Keyboard.isKeyDown(DOWN)) {
                camera.moveDown(movementSpeed);
            }
            
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
        Display.destroy();
    }
}
