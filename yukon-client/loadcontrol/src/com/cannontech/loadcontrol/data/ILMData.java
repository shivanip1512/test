package com.cannontech.loadcontrol.data;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ILMData
{
	String getYukonCategory();
	String getYukonClass();
	String getYukonDescription();
	Integer getYukonID();
	String getYukonName();
	Integer getYukonType();
		
	void setYukonCategory(String newYukonCategory);
	void setYukonClass(String newYukonClass);
	void setYukonDescription(String newYukonDescription);
	void setYukonID(Integer newYukonID);
	void setYukonName(String newYukonName);
	void setYukonType(Integer newYukonType);
}
