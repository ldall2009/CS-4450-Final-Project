/***************************************************************
* file: FPCameraController.java
* author: N. Vinjamury, D. Edwards, L. Dall
* class: CS 4450 - Computer Graphics
*
* assignment: Semester Project - Checkpoint 3
* date last modified: 11/14/2020
*
* purpose: This file/class is responsible for allowing the user to move 
*   and modify the first-person camera in order to interact within the
*   3D environment.
*
****************************************************************/ 

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class FPCameraController {
    
    // 3d vector to store the camera's position in
    private Vector3f position = null;
    private Vector3f lookPosition = null;
    
    // the rotation around the Y axis of the camera
    private float yaw = 0.0f;
    
    // the rotation around the X axis of the camera
    private float pitch = 0.0f;
    
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

        // if user is looking within a normal pitch range for a human
        if (pitch - amount >= -90 && pitch - amount <= 90) {

          // decrement the pitch by the amount param
            pitch -= amount;
        } 
        //if the played would go past looking straight up
        else if (pitch - amount < -90) {
            pitch = -90;
        } 
        //if the player would go past looking straight down
        else {
            pitch = 90;
        }
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
    * method: walkBackwards
    * purpose: moves the camera backward relative to its current rotation
    *   (yaw)
    *
    ****************************************************************/
    public void walkBackwards(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }

    /***************************************************************
    * method: strafeLeft
    * purpose: strafes the camera left relative to its current rotation
    *   (yaw)
    *
    ****************************************************************/ 
    public void strafeLeft(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
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
    * method: moveDown
    * purpose: moves the camera down
    *
    ****************************************************************/ 
    public void moveDown(float distance) {
        position.y += distance;
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

}
