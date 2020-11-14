/******************************************************************************
 *       file: Axis.java
 *     author: Daniel Edwards
 *      class: CS 4450
 * assignment: Program2
 * 
 * last modified: 09/20/2020
 * 
 *   purpose:
 * See class's Javadoc, listed just below. With the exception of the main class,
 * standard Java documentation is used so that Netbeans can use it when giving
 * me a summary of the class. 
 * 
 ******************************************************************************/
package Input;

/**
 * Represents an axis in the InputManager.
 * 
 * Note that this is all local to the input package,
 * hence everything is available to the entire package to keep things simple.
 */
class Axis {

	Integer positiveKey;
	Integer negativeKey;

	Axis(Integer positiveKey, Integer negativeKey) {
		this.positiveKey = positiveKey;
		this.negativeKey = negativeKey;
	}
}
