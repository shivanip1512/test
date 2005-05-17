package com.cannontech.database.data.point;

import java.util.Date;

/**
 * @author ryan
 *
 */
public interface SystemLogData
{
	public String getAction();
	public Date getDateTime();
	public String getDescription();
	public Integer getLogID();
	public Integer getPointID();
	public Integer getPriority();
	public Integer getSoe_tag();
	public Integer getType();
	public String getUserName();


}
