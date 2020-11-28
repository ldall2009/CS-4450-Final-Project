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
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class FPCameraController {
    
	private static float SIZE = 1.2f;
	private static float HEIGHT = 3.5f;

    // 3d vector to store the camera's position in
    private Vector3f position = null;
    private Vector3f lookPosition = null;
	private Vector3f deltaPosition = null;
    
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

		deltaPosition = new Vector3f();

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
        deltaPosition.x -= xOffset;
        deltaPosition.z += zOffset;
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
        deltaPosition.x += xOffset;
        deltaPosition.z -= zOffset;
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
        deltaPosition.x -= xOffset;
        deltaPosition.z += zOffset;
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
        deltaPosition.x -= xOffset;
        deltaPosition.z += zOffset;
    }

    /***************************************************************
    * method: moveUp
    * purpose: moves the camera up
    *
    ****************************************************************/ 
    public void moveUp(float distance) {
        deltaPosition.y -= distance;
    }

    /***************************************************************
    * method: moveDown
    * purpose: moves the camera down
    *
    ****************************************************************/ 
    public void moveDown(float distance) {
        deltaPosition.y += distance;
    }

	private Vector3f[] getCorners(Vector3f center) {
		//System.out.println(center);
		float height = HEIGHT;
		float extent = SIZE/2;

		float xSize = extent;
		float ySize = height;
		float zSize = extent;

		return new Vector3f[] {
			new Vector3f(center.x+xSize,center.y+ySize,center.z+zSize),
			new Vector3f(center.x+xSize,center.y+ySize,center.z-zSize),
			new Vector3f(center.x-xSize,center.y+ySize,center.z+zSize),
			new Vector3f(center.x-xSize,center.y+ySize,center.z-zSize),

			new Vector3f(center.x+xSize,center.y,center.z+zSize),
			new Vector3f(center.x+xSize,center.y,center.z-zSize),
			new Vector3f(center.x-xSize,center.y,center.z+zSize),
			new Vector3f(center.x-xSize,center.y,center.z-zSize),
		};
	}

	private Block[] getIntersections(Chunk chunk, Vector3f center) {
		Vector3f[] corners = getCorners(center);
		ArrayList<Block> blocks = new ArrayList<>(corners.length);

		for(Vector3f corner : corners) {
			Block b = chunk.getBlockAtPoint(corner);
			if(b != null) {
				blocks.add(b);
			}
		}

		Block[] blockArray = new Block[blocks.size()];
		return blocks.toArray(blockArray);
	}

	/**
	 * Attempts to move by the given delta.
	 * @param chunk Chunk with collision data.
	 * @param delta Amount to move.
	 * @return True if entire move was executed; false otherwise.
	 */
	public boolean move(Chunk chunk, Vector3f delta) {
		Vector3f newPosition = new Vector3f();
		Vector3f.add(position, delta, newPosition);
		
		if(getIntersections(chunk, newPosition).length == 0 ) {
			position = newPosition;
			return true;
		}
		else {
			return false;
		}

	}

	public void applyMovement(Chunk chunk) {
		System.out.println(position);
		move(chunk, new Vector3f(deltaPosition.x, 0, 0));
		move(chunk, new Vector3f(0, deltaPosition.y, 0));
		move(chunk, new Vector3f(0, 0, deltaPosition.z));
		
		deltaPosition = new Vector3f();

	}

    /***************************************************************
    * method: lookThrough
    * purpose: translates and rotates the matrix so that it looks through
    *   the camera.  This does basically what gluLookAt() does
    *
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

	private void glVertex(Vector3f p) {
		glVertex3f(p.x, p.y, p.z);
	}
	
	public void renderBoundary() {
		Vector3f[] corners = getCorners(position);

		/*
		glPointSize(10);
		glBegin(GL_LINES);
		glColor3f(1.0f, 1.0f, 1.0f);
		for(Vector3f corner : corners) {
			glVertex3f(corner.x, corner.y, corner.z);
		}
		glEnd();
*/

		try {
            glBegin(GL_QUADS);
            glColor3f(1.0f, 0.0f, 1.0f);
            glVertex(corners[0]);
            glVertex(corners[1]);
            glVertex(corners[2]);
            glVertex(corners[3]);
            glEnd();
            
            glBegin(GL_QUADS);
            glColor3f(0.7f, 0.2f, 1.0f);
            glVertex(corners[4]);
            glVertex(corners[5]);
            glVertex(corners[6]);
            glVertex(corners[7]);
            glEnd();
            
			/*
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
*/

            
        } catch (Exception e) { }
	}

}
