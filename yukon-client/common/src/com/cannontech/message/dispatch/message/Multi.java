package com.cannontech.message.dispatch.message;

/**
 * This type was created in VisualAge.
 */

public final class Multi extends com.cannontech.message.util.Message 
{	
	private java.util.Vector vector = null;
/**
 * MultiPointChange constructor comment.
 */
public Multi() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:50:21 AM)
 * @return java.util.Vector
 */
public java.util.Vector getVector() {
	if( vector == null )
	{
		vector = new java.util.Vector();
	}
	
	return vector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/28/00 11:50:21 AM)
 * @param newVector java.util.Vector
 */
public void setVector(java.util.Vector newVector) {
	vector = newVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/00 4:40:27 PM)
 * @return java.lang.String
 */
public String toString() {
	String retStr =  "com.cannontech.message.dispatch.message.Multi:\n  ";

	java.util.Vector v = getVector();

	if( v != null )
	{
		for( int i = 0; i < v.size(); i++ )
		{
			retStr += v.elementAt(i) + "\n";
		}
	}
	else
	{
		retStr += "Empty multi";
	}

	return retStr;
}
}
