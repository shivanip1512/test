package com.cannontech.database.data.pao;

/**
 * @author rneuharth
 * Sep 6, 2002 at 12:42:10 PM
 * 
 * Used to keep all subclasses from conflicting with int types
 */
public interface TypeBase
{
	public final static int DEVICE_OFFSET      = 1000;
	public final static int ROUTE_OFFSET       = 2000;
	public final static int PORT_OFFSET        = 3000;
	public final static int CAPCONTROL_OFFSET  = 4000;
}
