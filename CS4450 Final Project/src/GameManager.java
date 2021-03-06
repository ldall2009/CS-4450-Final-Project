
/** *************************************************************
 * file: GameManager.java
 * author: N. Vinjamury, D. Edwards, L. Dall
 * class: CS 4450 - Computer Graphics
 *
 * assignment: Semester Project - Final
 * date last modified: 11/30/2020
 *
 * purpose: This class is responsible for updating the game
 * in real time, checking for inputs and passing it along to the
 * relevant objects.
 *
 *************************************************************** */

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

    private static final Vector3f CHUNK_POSITION = new Vector3f(0, 0, 0);
    private static final Vector3f CAMERA_POSITION = new Vector3f(-30, -50, -30);

    // See constructor for key bindings
    private final int EXIT;
    private final int TOGGLE_DEBUG_POSITION;
    private final int JUMP;
    private final int TEXTURES;
	private final int TOGGLE_FLY_MODE;
    private final int LIGHTFORWARD;
    private final int LIGHTBACKWARD;
    private final int DAY;
    private final int NIGHT;

    private final int VERTICAL_AXIS;

    private final int FORWARD_AXIS;
    private final int STRAFE_AXIS;

    private final int FORWARD_AXIS_ALT;
    private final int STRAFE_AXIS_ALT;

    private InputManager input;
    private Chunk chunk;
    private FPCameraController camera;

    FloatBuffer lightPosition;
    
    private int x, y, z;

    
    /***************************************************************
    * method: GameManager
    * purpose: constructor that initializes fields for the GameManager
    * class - mainly controls and light position
    ****************************************************************/
    public GameManager() {
        chunk = new Chunk((int) CHUNK_POSITION.x, (int) CHUNK_POSITION.y, (int) CHUNK_POSITION.z);
        camera = new FPCameraController(CAMERA_POSITION.x, CAMERA_POSITION.y, CAMERA_POSITION.z);

        input = new InputManager();

        EXIT = input.addButton(Keyboard.KEY_ESCAPE);
        TEXTURES = input.addButton(Keyboard.KEY_Q);
        LIGHTFORWARD = input.addButton(Keyboard.KEY_E);
        LIGHTBACKWARD = input.addButton(Keyboard.KEY_R);
        DAY = input.addButton(Keyboard.KEY_T);
        NIGHT = input.addButton(Keyboard.KEY_Y);
        
        TOGGLE_DEBUG_POSITION = input.addButton(Keyboard.KEY_P);
		TOGGLE_FLY_MODE = input.addButton(Keyboard.KEY_F);

        JUMP = input.addButton(Keyboard.KEY_SPACE);

        VERTICAL_AXIS = input.addAxis(Keyboard.KEY_SPACE, Keyboard.KEY_LSHIFT);
        FORWARD_AXIS = input.addAxis(Keyboard.KEY_W, Keyboard.KEY_S);
        STRAFE_AXIS = input.addAxis(Keyboard.KEY_D, Keyboard.KEY_A);
        FORWARD_AXIS_ALT = input.addAxis(Keyboard.KEY_UP, Keyboard.KEY_DOWN);
        STRAFE_AXIS_ALT = input.addAxis(Keyboard.KEY_RIGHT, Keyboard.KEY_LEFT);

        lightPosition = BufferUtils.createFloatBuffer(4);
        x = 30;
        y = 70;
        z = 40;
        lightPosition.put(x).put(y).put(z).put(1.0f).flip();
    }

    /***************************************************************
    * method: currentTime
    * purpose: gets the current system time
    ****************************************************************/
    private long currentTime() {
        //return java.time.Clock.systemUTC().instant().getNano();
        return Sys.getTime();
    }

    /***************************************************************
    * method: timeResolution
    * purpose: gets the current system time resolution
    ****************************************************************/
    private long timeResolution() {
        //return 1000000000;
        return Sys.getTimerResolution();
    }

    /****************************************************************
    * method: gameLoop 
    * purpose: the primary game loop for this program.
    * Responsible for calling relevant methods to respond to things like
    * rendering, user input, etc.
    ****************************************************************/
    public void gameLoop() {
        float dx = 0.0f;
        float dy = 0.0f;

        long lastTime;
        long time;

        float mouseSensitivity = 0.09f;
        float movementSpeed = .35f;
        // hide the mouse
        Mouse.setGrabbed(true);

        chunk.rebuildMesh();
        input.update();

        time = currentTime();

        // keep looping till the display window is closed the ESC key is down
        while (!Display.isCloseRequested() && !input.isHeld(EXIT)) {

            if (input.isDown(TEXTURES)) {
                chunk.changeTextures();
            }
            lastTime = time;
            time = currentTime();
            float dt = (float) ((time - lastTime) / ((double) (timeResolution())));
            //dt = Math.max(0f, dt);

            input.update();

            if (input.isDown(TOGGLE_DEBUG_POSITION)) {
                camera.toggleDebugPosition();
            }

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
            if (input.isDown(JUMP)) {
                camera.jump();
            }
			if (input.isDown(TOGGLE_FLY_MODE)) {
				camera.toggleFlying();
			}

            camera.applyMovement(chunk, dt);

            // set the modelview matrix back to the identity
            glLoadIdentity();

            // look through the camera before you draw anything
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // you would draw your scene here.
            if(input.isHeld(LIGHTFORWARD)){
                lightPosition = camera.moveLightForward(lightPosition);
            }
            
            if(input.isHeld(LIGHTBACKWARD)){
                lightPosition = camera.moveLightBackward(lightPosition);
            }
            
            if(input.isDown(DAY)){
                lightPosition = camera.day(lightPosition);
            }
            
            if(input.isDown(NIGHT)){
                lightPosition = camera.night(lightPosition);
            }
            
            updateLight();
            chunk.render();
            //render();

            // draw the buffer to the screen
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }

    /***************************************************************
    * method: updateLight
    * purpose: updates the light on call to be at a certain position
    ****************************************************************/
    private void updateLight() {
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
}
