/******************************************************************************
 * file: KeyInfo.java
 * author: D. Edwards
 * class: CS 4450 - Computer Graphics
 * assignment: Semester Project - Final
 * 
 * last modified: 11/30/2020
 * 
 *   purpose:
 * See class's Javadoc, listed just below. With the exception of the main class,
 * standard Java documentation is used so that Netbeans can use it when giving
 * me a summary of the class. 
 * 
 ******************************************************************************/
package Input;

import org.lwjgl.input.Keyboard;

/**
 * This is a container for each of our keys.
 * 
 * Methods mirror the InputManager's methods, and should be called in the
 * same circumstances.
 */
class KeyInfo {
	private int keyCode;
	private boolean heldLastFrame;
	private boolean heldThisFrame;

	public KeyInfo(int keyCode) {
		this.keyCode = keyCode;
		heldLastFrame = false;
		heldThisFrame = false;
	}

	public int getKeyCode() { return keyCode; }

	public void update() {
		heldLastFrame = heldThisFrame;
		heldThisFrame = Keyboard.isKeyDown(keyCode);
	}	

	public boolean isDown() {
		return heldThisFrame && !heldLastFrame;
	}

	public boolean isHeld() {
		return heldThisFrame;
	}

	public boolean isUp() {
		return !heldThisFrame && heldLastFrame;
	}
}