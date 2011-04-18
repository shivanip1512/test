package com.cannontech.loadcontrol.data;

import com.cannontech.common.pao.PaoType;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ILMData
{
	String getYukonDescription();
	Integer getYukonID();
	String getYukonName();
	PaoType getYukonType();
		
	void setYukonDescription(String newYukonDescription);
	void setYukonID(Integer newYukonID);
	void setYukonName(String newYukonName);
	void setYukonType(PaoType newYukonType);
}
