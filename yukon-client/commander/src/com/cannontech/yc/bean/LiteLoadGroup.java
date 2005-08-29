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
public class LiteLoadGroup{
	private int groupID = -1;
	private String serial = null;
	
	/**
	 * 
	 */
	public LiteLoadGroup(int groupID_, String serial_) {
		super();
		groupID = groupID_;
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
}