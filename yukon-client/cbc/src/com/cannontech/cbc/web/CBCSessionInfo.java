/*
 * Created on May 12, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.cbc.web;

import com.cannontech.cbc.gui.SubBusTableModel;

/**
 * @author rneuharth
 *
 * Represents a data structure used to store session info about a CBC connection.
 * One of these will exist per user.
 */
public class CBCSessionInfo
{
	private String lastArea = SubBusTableModel.ALL_FILTER;
	private int lastSubRowNum = 0;
	
	private String refreshRate = CapControlWebAnnex.REF_SECONDS_DEF;


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
		if( area_ == null )
			lastArea = SubBusTableModel.ALL_FILTER;
		else
			lastArea = area_;
	}

	/**
	 * @return
	 */
	public String getRefreshRate()
	{
		return refreshRate;
	}

	/**
	 * @param string
	 */
	public void setRefreshRate(String string)
	{
		refreshRate = string;
	}

	/**
	 * @return
	 */
	public int getLastSubRowNum()
	{
		return lastSubRowNum;
	}

	/**
	 * @param int
	 */
	public void setLastSubRowNum(int rowNum_)
	{
		lastSubRowNum = rowNum_;
	}

}
