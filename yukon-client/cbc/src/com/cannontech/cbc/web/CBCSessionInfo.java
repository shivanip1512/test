/*
 * Created on May 12, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.cbc.web;

/**
 * @author rneuharth
 *
 * Represents a data structure used to store session info about a CBC connection.
 * One of these will exist per user.
 */
public class CBCSessionInfo
{
	private String lastArea = null; //SubBusTableModel.ALL_FILTER;
	private int _subID = 0;
	
	public CBCSessionInfo()
	{
		super();
	}


	/**
	 * @return
	 */
	public String getLastArea()
	{
		return lastArea;
	}

	/**
	 * @param string
	 */
	public void setLastArea(String area_ )
	{
		lastArea = area_;
	}

	/**
	 * @return
	 */
	public int getLastSubID()
	{
		return _subID;
	}

	/**
	 * @param int
	 */
	public void setLastSubID(int subID )
	{
        _subID = subID;
	}

}
