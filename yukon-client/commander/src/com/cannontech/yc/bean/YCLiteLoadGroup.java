/*
 * Created on Aug 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yc.bean;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YCLiteLoadGroup{
	private int groupID = -1;
	private int routeID = -1;
	private double kwCapacity = 0; 
	private String serial = null;
	
	/**
	 * 
	 */
	public YCLiteLoadGroup(int groupID_, double kwCapacity_, int routeID_, String serial_) {
		super();
		groupID = groupID_;
		kwCapacity = kwCapacity_;
		routeID = routeID_;
		serial = serial_;
	}
	public int getGroupID()
	{
		return groupID;
	}
	public String getSerial()
	{
		return serial;
	}
    public double getKwCapacity()
    {
        return kwCapacity;
    }
    public int getRouteID()
    {
        return routeID;
    }
}