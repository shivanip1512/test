package com.cannontech.common.util;

/**
 * Insert the type's description here.
 * Creation date: (1/21/2002 3:24:05 PM)
 * @author: 
 */
public class MethodNotImplementedException extends RuntimeException 
{
	private static String DEFAULT_STRING = "The method called has not been implemented by CTI";
/**
 * MethodNotImplementedException constructor comment.
 */
public MethodNotImplementedException() {
	super(DEFAULT_STRING);
}
/**
 * MethodNotImplementedException constructor comment.
 * @param s java.lang.String
 */
public MethodNotImplementedException(String s) {
	super(s);
}
}
