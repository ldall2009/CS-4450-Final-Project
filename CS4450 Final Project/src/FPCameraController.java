/***************************************************************
* file: FPCameraController.java
* author: N. Vinjamury, D. Edwards, L. Dall
* class: CS 4450 - Computer Graphics
*
* assignment: Semester Project - Checkpoint 2
* date last modified: 10/5/2020
*
* purpose: This file/class is responsible for allowing the user to move 
*   and modify the first-person camera in order to interact within the
*   3D environment.
*
****************************************************************/ 

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import Input.*;

public class FPCameraController {
    
    private static final int ESCAPE = Keyboard.KEY_ESCAPE;

	// See constructor for key bindings
	private static final int VERTICAL_AXIS = 0;

	private static final int FORWARD_AXIS  = 1;
	private static final int STRAFE_AXIS   = 2;

	private static final int FORWARD_AXIS_ALT  = 3;
	private static final int STRAFE_AXIS_ALT   = 4;

	private InputManager input;

    
    // 3d vector to store the camera's position in
    private Vector3f position = null;
    private Vector3f lookPosition = null;
    
    // the rotation around the Y axis of the camera
    private float yaw = 0.0f;
    
    // the rotation around the X axis of the camera
    private float pitch = 0.0f;
    
    private Vector3Float me;

	private Chunk chunk;

    /***************************************************************
    * method: FPCameraController
    * purpose: constructor used to instantiate the position Vector3f.
    *   It instantiates the position of the user as well as their initial
    *   "look" position
    *
    ****************************************************************/ 
    public FPCameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        lookPosition = new Vector3f(x, y, z);
        lookPosition.x = 0f;
        lookPosition.y = 15f;
        lookPosition.z = 0f;

		input = new InputManager();
		input.addButton(ESCAPE, ESCAPE);
		input.addAxis(VERTICAL_AXIS,    Keyboard.KEY_SPACE, Keyboard.KEY_LSHIFT);
		input.addAxis(FORWARD_AXIS,     Keyboard.KEY_W,     Keyboard.KEY_S);
		input.addAxis(STRAFE_AXIS,      Keyboard.KEY_D,     Keyboard.KEY_A);
		input.addAxis(FORWARD_AXIS_ALT, Keyboard.KEY_UP,    Keyboard.KEY_DOWN);
		input.addAxis(STRAFE_AXIS_ALT,  Keyboard.KEY_RIGHT, Keyboard.KEY_LEFT);

		chunk = new Chunk((int)x, (int)y, (int)z);
    }

    /***************************************************************
    * method: yaw
    * purpose: increments the camera's current yaw rotation
    *
    ****************************************************************/ 
    public void yaw(float amount) {
        // increment the yaw by the amount param
        yaw += amount;
    }

    /***************************************************************
    * method: pitch
    * purpose: decrements the camera's current pitch rotation
    *
    ****************************************************************/ 
    public void pitch(float amount) {
        // decrement the pitch by the amount param
        pitch -= amount;
    }

    /***************************************************************
    * method: walkForward
    * purpose: moves the camera forward relative to its current rotation
    *   (yaw)
    *
    ****************************************************************/ 
    public void walkForward(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }

    /***************************************************************
    * method: strafeRight
    * purpose: strafes the camera right relative to its current rotation
    *   (yaw)
    *
    ****************************************************************/ 
    public void strafeRight(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    /***************************************************************
    * method: moveUp
    * purpose: moves the camera up
    *
    ****************************************************************/ 
    public void moveUp(float distance) {
        position.y -= distance;
    }

    /***************************************************************
    * method: lookThrough
    * purpose: translates and rotates the matrix so that it looks through
    *   the camera.  This does basically what gluLookAt() does
    *
    ****************************************************************/ 
    public void lookThrough() {
        // rotate the pitch around the X axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        // rotate the yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        // translate to the position vector's location
        glTranslatef(position.x, position.y, position.z);
    }

    /***************************************************************
    * method: gameLoop
    * purpose: the primary game loop for this program.  Responsible for
    *   calling relevant methods to respond to things like rendering, user
    *   input, etc.
    *
    ****************************************************************/ 
    public void gameLoop() {
        FPCameraController camera = new FPCameraController(-30, -70, -30);
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
			camera.walkForward(input.getCombinedAxis(new int[]{FORWARD_AXIS, FORWARD_AXIS_ALT}) * movementSpeed);
			camera.strafeRight(input.getCombinedAxis(new int[]{STRAFE_AXIS, STRAFE_AXIS_ALT}) * movementSpeed);
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
        Display.destroy();
    }

    /***************************************************************
    * method: render
    * purpose: renders shapes into the 3D space.
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
