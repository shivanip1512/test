package com.cannontech.logger.scm;

/*
 * SCMEvent.java	1.0 (5 May 2000)
 *
 */
public class SCMEvent
{
	// Corresponds to value in WINSVC.H
	public static final int SERVICE_STOPPED = 1;
	
	private int id;

public SCMEvent(int code)
{
	id = code;
}
public int getID()
{
	return id;
}
}
