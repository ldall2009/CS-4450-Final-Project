
/** *************************************************************
 * file: FPCameraController.java
 * author: N. Vinjamury, D. Edwards, L. Dall
 * class: CS 4450 - Computer Graphics
 *
 * assignment: Semester Project - Final
 * date last modified: 11/30/2020
 *
 * purpose: This file/class is responsible for allowing the user to move
 *   and modify the first-person camera in order to interact within the
 *   3D environment.
 *
 *************************************************************** */

import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class FPCameraController {

    private static float SIZE = 1.2f;
    private static float HEIGHT = 3.5f;

    private static float WALK_SPEED = 60f;
    private static float GRAVITY = 40f;
    private static float JUMP_IMPULSE = -20f;

    // 3d vector to store the camera's position in
    private Vector3f position = null;
    private Vector3f deltaPosition = null;
    private Vector3f velocity = null;
    private Vector3f acceleration = null;

    private Vector3f lookPosition = null;

    // the rotation around the Y axis of the camera
    private float yaw = 0.0f;

    // the rotation around the X axis of the camera
    private float pitch = 0.0f;

    // While true, dump position data to console
    private boolean debugPosition = false;

	private boolean flying = false;
    private boolean hasHitGround = false;

    /****************************************************************
    * method: FPCameraController 
    * purpose: constructor used to instantiate the
    * position Vector3f. It instantiates the position of the user 
    * as well as their initial "look" position
    ****************************************************************/
    public FPCameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        lookPosition = new Vector3f(x, y, z);
        lookPosition.x = 30f;
        lookPosition.y = 70f;
        lookPosition.z = 40f;

        deltaPosition = new Vector3f();
        velocity = new Vector3f();
        acceleration = new Vector3f(0f, GRAVITY, 0f);

    }

    /***************************************************************
    * method: toggleDebugPosition
    * purpose: sets the debugPosition to be the opposite of what it is
    ****************************************************************/
    public void toggleDebugPosition() {
        debugPosition = !debugPosition;
    }

    /***************************************************************
    * method: setDebugPosiition
    * purpose: sets the Debug position to true or false
    ****************************************************************/
    public void setDebugPosition(boolean value) {
        debugPosition = value;
    }
    
    /***************************************************************
    * method: toggleFlying
    * purpose: toggles between flying mode and regular mode
    ****************************************************************/
    public void toggleFlying() {
	flying = !flying;
        
	if(flying) {
            velocity = new Vector3f();
	}
    }

    /****************************************************************
    * method: yaw 
    * purpose: increments the camera's current yaw rotation
    *
    ****************************************************************/
    public void yaw(float amount) {
        // increment the yaw by the amount param
        yaw += amount;
    }

    /****************************************************************
    * method: pitch 
    * purpose: decrements the camera's current pitch rotation
    *
    ****************************************************************/
    public void pitch(float amount) {

        // if user is looking within a normal pitch range for a human
        if (pitch - amount >= -90 && pitch - amount <= 90) {

            // decrement the pitch by the amount param
            pitch -= amount;
        } //if the played would go past looking straight up
        else if (pitch - amount < -90) {
            pitch = -90;
        } //if the player would go past looking straight down
        else {
            pitch = 90;
        }
    }

    /****************************************************************
    * method: walkForward 
    * purpose: moves the camera forward relative to its
    * current rotation (yaw)
    ****************************************************************/
    public void walkForward(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        deltaPosition.x -= xOffset;
        deltaPosition.z += zOffset;
    }

    /****************************************************************
    * method: walkBackwards 
    * purpose: moves the camera backwards relative to its
    * current rotation (yaw)
    ****************************************************************/
    public void walkBackwards(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        deltaPosition.x += xOffset;
        deltaPosition.z -= zOffset;
    }

    /****************************************************************
    * method: strafeLeft
    * purpose: strafes the camera left relative to its
    * current rotation (yaw)
    ****************************************************************/
    public void strafeLeft(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        deltaPosition.x -= xOffset;
        deltaPosition.z += zOffset;
    }

    /****************************************************************
    * method: strafeRight
    * purpose: strafes the camera right relative to its
    * current rotation (yaw)
    ****************************************************************/
    public void strafeRight(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        deltaPosition.x -= xOffset;
        deltaPosition.z += zOffset;
    }

    /****************************************************************
    * method: moveUp
    * purpose: moves the camera up
    ****************************************************************/
    public void moveUp(float distance) {
	if(flying){
            deltaPosition.y -= distance;
	}
    }

    /****************************************************************
    * method: moveDown
    * purpose: moves the camera down
    ****************************************************************/
    public void moveDown(float distance) {
	if(flying){
            deltaPosition.y += distance;
	}
    }

    /****************************************************************
    * method: moveLightForward
    * purpose: moves the lightposition forward on keypress
    ****************************************************************/
    public FloatBuffer moveLightForward(FloatBuffer lightPosition){
        if(lookPosition.x > 90){
            return lightPosition;
        }
        if(lookPosition.z > 90){
            return lightPosition;
        }
        lightPosition.put(lookPosition.x+=1).put(
            lookPosition.y).put(lookPosition.z+=1).put(1.0f).flip();
        
        return lightPosition;
    }
    
    /****************************************************************
    * method: day
    * purpose: moves the lightposition to (90,y,90) for day effect
    ****************************************************************/
    public FloatBuffer day(FloatBuffer lightPosition){
        lightPosition.put(lookPosition.x = 90).put(
            lookPosition.y).put(lookPosition.z = 90).put(1.0f).flip();
        return lightPosition;
    }
    
    /****************************************************************
    * method: night
    * purpose: moves the lightposition to (-15,y,-15) for night effect
    ****************************************************************/
    public FloatBuffer night(FloatBuffer lightPosition){
        lightPosition.put(lookPosition.x = -15).put(
            lookPosition.y).put(lookPosition.z = -15).put(1.0f).flip();
        return lightPosition;
    }
    
    /****************************************************************
    * method: moveLightBackward
    * purpose: moves the lightposition backward on keypress
    ****************************************************************/
    public FloatBuffer moveLightBackward(FloatBuffer lightPosition){
        if(lookPosition.x < -15){
            return lightPosition;
        }
        if(lookPosition.z < -15){
            return lightPosition;
        }

        lightPosition.put(lookPosition.x-=1).put(
            lookPosition.y).put(lookPosition.z-=1).put(1.0f).flip();
        return lightPosition;
    }
    
    /****************************************************************
    * method: jump
    * purpose: checks if the user can jump and then applies jump force
    ****************************************************************/
    public void jump() {
        if (!flying && hasHitGround) {
            velocity.y += JUMP_IMPULSE;
            hasHitGround = false;
        }
    }

    /****************************************************************
    * method: getCorners
    * purpose: gets the corners of a block with vector3f center
    ****************************************************************/
    private Vector3f[] getCorners(Vector3f center) {
        //System.out.println(center);
        float height = HEIGHT;
        float extent = SIZE / 2;

        float xSize = extent;
        float ySize = height;
        float zSize = extent;

        return new Vector3f[]{
            new Vector3f(center.x + xSize, center.y + ySize, center.z + zSize),
            new Vector3f(center.x + xSize, center.y + ySize, center.z - zSize),
            new Vector3f(center.x - xSize, center.y + ySize, center.z + zSize),
            new Vector3f(center.x - xSize, center.y + ySize, center.z - zSize),
            new Vector3f(center.x + xSize, center.y, center.z + zSize),
            new Vector3f(center.x + xSize, center.y, center.z - zSize),
            new Vector3f(center.x - xSize, center.y, center.z + zSize),
            new Vector3f(center.x - xSize, center.y, center.z - zSize),};
    }

    /****************************************************************
    * method: getIntersections
    * purpose: gets the intersections of a block with vector3f center
    * in chunk chunk
    ****************************************************************/
    private Block[] getIntersections(Chunk chunk, Vector3f center) {
        Vector3f[] corners = getCorners(center);
        ArrayList<Block> blocks = new ArrayList<>(corners.length);

        for (Vector3f corner : corners) {
            Block b = chunk.getBlockAtPoint(corner);
            if (b != null) {
                blocks.add(b);
            }
        }

        Block[] blockArray = new Block[blocks.size()];
        return blocks.toArray(blockArray);
    }

    /*****************************************************************
    * method: move
    * purpose: attempts to move by the given delta
    *
    * @param chunk Chunk with collision data.
    * @param delta Amount to move.
    * @return True if entire move was executed; false otherwise.
    ****************************************************************/
    private boolean move(Chunk chunk, Vector3f delta) {
        Vector3f newPosition = new Vector3f();
        Vector3f.add(position, delta, newPosition);

        if (getIntersections(chunk, newPosition).length == 0) {
            position = newPosition;
            return true;
        } else {
            return false;
        }

    }

    /****************************************************************
    * method: applyMovement
    * purpose: applies movement based on location and time
    ****************************************************************/
    public void applyMovement(Chunk chunk, float deltaTime) {
        if (debugPosition) {
            System.out.println(position);
        }

        deltaPosition.scale(deltaTime * WALK_SPEED);

		if(!flying) {
			Vector3f accelStep = new Vector3f(acceleration);
			accelStep.scale(deltaTime);
			Vector3f.add(velocity, accelStep, velocity);

			Vector3f velStep = new Vector3f(velocity);
			velStep.scale(deltaTime);
			Vector3f.add(deltaPosition, velStep, deltaPosition);
		}

        if (deltaPosition.y != 0) {
            hasHitGround = false;
        }

        move(chunk, new Vector3f(deltaPosition.x, 0, 0));
        move(chunk, new Vector3f(0, 0, deltaPosition.z));

        if (!move(chunk, new Vector3f(0, deltaPosition.y, 0))) {
            // When we land on something, reset the velocity
            velocity = new Vector3f();
            hasHitGround = true;
        }

        deltaPosition = new Vector3f();

    }

    /****************************************************************
    * method: lookThrough 
    * purpose: translates and rotates the matrix so that it
    * looks through the camera. This does what gluLookAt() does
    ****************************************************************/
    public void lookThrough() {
        //glTranslatef(0f, 0f, -0.1f);

        // rotate the pitch around the X axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        // rotate the yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        // translate to the position vector's location
        glTranslatef(position.x, position.y, position.z);
    }

    /****************************************************************
    * method: glVertex
    * purpose: prints with glvertex with a given vector3f
    ****************************************************************/
    private void glVertex(Vector3f p) {
        glVertex3f(p.x, p.y, p.z);
    }

}
