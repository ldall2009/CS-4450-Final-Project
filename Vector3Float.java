/***************************************************************
* file: Vector3Float.java
* author: N. Vinjamury, D. Edwards, L. Dall
* class: CS 4450 - Computer Graphics
*
* assignment: Semester Project - Checkpoint 1
* date last modified: 10/5/2020
*
* purpose: This file/class is responsible for holding our camera's
*   position in 3D space.
*
****************************************************************/ 

public class Vector3Float {
    public float x, y, z;

    /***************************************************************
    * method: Vector3Float
    * purpose: constructor used to set values for the camera's position
    *
    ****************************************************************/ 
    public Vector3Float(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}