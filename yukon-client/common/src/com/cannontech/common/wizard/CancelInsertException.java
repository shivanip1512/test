package com.cannontech.common.wizard;

/**
 * Insert the type's description here.
 * Creation date: (4/30/2002 11:31:40 AM)
 * @author: 
 */

/* This exception is most often thrown during the call to getValue()
	and a condition in the getValue() is true
*/

public class CancelInsertException extends RuntimeException {
/**
 * CancelInsertException constructor comment.
 */
public CancelInsertException() {
	this("Insertion of an item was caceled");
}
/**
 * CancelInsertException constructor comment.
 * @param s java.lang.String
 */
public CancelInsertException(String s) {
	super(s);
}
}
