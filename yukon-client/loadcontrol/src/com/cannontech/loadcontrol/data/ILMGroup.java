package com.cannontech.loadcontrol.data;

import java.util.Date;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface ILMGroup 
{
	public Integer getYukonID();
	
	public String getName();
	
	public Boolean getDisableFlag();

	public String getGroupControlStateString();

	public Date getGroupTime();

	public String getStatistics();
		
	public Double getReduction();

	public Integer getOrder();

	public boolean isRampingIn();

	public boolean isRampingOut();
}