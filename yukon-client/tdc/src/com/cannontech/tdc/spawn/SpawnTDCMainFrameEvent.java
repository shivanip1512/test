package com.cannontech.tdc.spawn;

/**
 * Insert the type's description here.
 * Creation date: (10/10/00 4:08:52 PM)
 * @author: 
 */
public class SpawnTDCMainFrameEvent extends java.util.EventObject 
{
	private int id = 0;
	public static final int DISPOSE_TDCMAINFRAME = 1001;
	public static final int CREATE_TDCMAINFRAME = 1002;	
	public static final int LOG_TOGGLE = 1003;
	public static final int EXIT_TDC = 1004;
/**
 * TDCMainFrameEvent constructor comment.
 * @param source java.lang.Object
 */
public SpawnTDCMainFrameEvent(Object source) {
	super(source);
}
/**
 * TDCMainFrameEvent constructor comment.
 * @param source java.lang.Object
 */
public SpawnTDCMainFrameEvent(Object source, int ID) 
{
	super(source);

	id = ID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/00 10:33:02 AM)
 * @return int
 */
public int getId() 
{
	return id;
}
}
