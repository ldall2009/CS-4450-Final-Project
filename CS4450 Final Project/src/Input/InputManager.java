/******************************************************************************
 * file: InputManager.java
 * author: D. Edwards
 * class: CS 4450 - Computer Graphics
 * assignment: Semester Project - Final
 * 
 * last modified: 11/30/2020
 * 
 * purpose:
 * See class's Javadoc, listed just below. With the exception of the main class,
 * standard Java documentation is used so that Netbeans can use it when giving
 * me a summary of the class. 
 * 
 ******************************************************************************/
package Input;

import java.util.HashMap;

/**
 * Processes input, allowing for checking when something just gets pressed,
 * held, and depressed.
 * 
 * For this to work correctly, the update method must be called every frame.
 */
public class InputManager {

	// If performance is ever a concern, this should be turned into a set of
	// parallel arrays. Will it make a big difference? Maybe not.
	// But we definitely lose on performance by doing a hash map for input.
	private HashMap<Integer, KeyInfo> watchList;
	private HashMap<Integer, Axis> axisList;

	/**
	 * Constructs a new input manager.
	 */
	public InputManager() {
		watchList = new HashMap<>();
		axisList = new HashMap<>();
	}

	/**
	 * Adds a new button which the InputManager will watch on future updates.
	 * This does nothing if the key was already being watched.
	 * 
	 * @param key Key mapped to button.
	 * @return Label which button can be referenced with.
	 */
	public int addButton(Integer key) {
		int label = key;

		if(!watchList.containsKey(key)) {
			watchList.put(label, new KeyInfo(key));
		}

		return label;
	}

	/**
	 * Creates a new virtual axis.
	 * 
	 * This isn't a real axis, but rather a pair of opposite keys. Calling this
	 * repeatedly with the same pair of keys WILL create new axis.
	 * 
	 * @param label Identifier for the axis. Is arbitrary.
	 * @param positiveKey Key which yields 1.
	 * @param negativeKey Key which yields -1.
	 * @return A unique label which the axis may be referenced by.
	 */
	public int addAxis(Integer positiveKey, Integer negativeKey) {
		int label = axisList.size();

		axisList.put(label, new Axis(addButton(positiveKey), addButton(negativeKey)));

		return label;
	}

	/**
	 * Updates the info on our keys.
	 * Call this every frame to keep it up to date.
	 */
	public void update() {
		watchList.values().forEach(ki -> {
			ki.update();
		});
	}

	/**
	 * Checks if a key has just been pressed this frame.
	 * @param keyCode Key value from the Keyboard class.
	 * @return True if the key has just been pressed; false otherwise.
	 */
	public boolean isDown(int keyCode) {
		return watchList.get(keyCode).isDown();
	}

	/**
	 * Checks if a key is currently being pressed.
	 * @param keyCode Key value from the Keyboard class.
	 * @return True if the key is being pressed; false otherwise.
	 */
	public boolean isHeld(int keyCode) {
		return watchList.get(keyCode).isHeld();
	}

	/**
	 * Checks if a key is currently being pressed.
	 * @param keyCode Key value from the Keyboard class.
	 * @return True if the key is being pressed; false otherwise.
	 */
	public boolean isUp(int keyCode) {
		return watchList.get(keyCode).isUp();
	}

	/**
	 * Returns position of the given axis.
	 * @param axisLabel Axis to refer to.
	 * @return -1, 0, or 1, depending on the axis.
	 */
	public int getAxis(int axisLabel) {
		int result = 0;
		Axis a = axisList.get(axisLabel);

		if(isHeld(a.positiveKey)) result++;
		if(isHeld(a.negativeKey)) result--;

		return result;		
	}

	/**
	 * Combines the results of multiple virtual axis.
	 * 
	 * This result is constrained between -1 and 1. Use this when there are
	 * multiple virtual axis which do the same thing.
	 * 
	 * @param labels Labels of axis to combine.
	 * @return A value between -1 and 1, inclusive.
	 */
	public int getCombinedAxis(int[] labels) {
		int result = 0;

		for(int label : labels) {
			result += getAxis(label);
		}
		
		return Math.max(-1, Math.min(result, 1));
	}

}
