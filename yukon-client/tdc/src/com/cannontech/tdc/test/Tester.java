package com.cannontech.tdc.test;

/**
 * Insert the type's description here.
 * Creation date: (6/1/00 1:34:39 PM)
 * @author: 
 * @Version: <version>
 */
import com.cannontech.tdc.utils.DataBaseInteraction;

public class Tester {
/**
 * Tester constructor comment.
 */
public Tester() 
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/00 1:34:52 PM)
 * Version: <version>
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	//Object[] a = DataBaseInteraction.queryResults( "select * from rawpointhistory", null );
	Object[] a = DataBaseInteraction.queryResults( "", null );

com.cannontech.clientutils.CTILogger.info(a.length + " ");
}
}
