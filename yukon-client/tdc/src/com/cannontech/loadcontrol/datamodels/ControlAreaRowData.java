package com.cannontech.loadcontrol.datamodels;

/**
 * Insert the type's description here.
 * Creation date: (7/25/2001 11:52:00 AM)
 * @author: 
 */
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.point.PointType;
import com.cannontech.dr.controlarea.model.TriggerType;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.spring.YukonSpringHook;

public class ControlAreaRowData 
{
	private LMControlAreaTrigger trigger = null;
	private com.cannontech.database.data.lite.LitePoint litePoint = null;
	private String currentValue = null;
	private String triggerValue = null;
/**
 * ControlAreaRowData constructor comment.
 */
public ControlAreaRowData() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 12:13:55 PM)
 * @return java.lang.String
 */
public java.lang.String getCurrentValue() 
{
	if( currentValue == null )
	{
		//Returns the current value of the point
		if( getLitePoint().getPointTypeEnum() == PointType.Status)
		{
			currentValue = YukonSpringHook.getBean(StateGroupDao.class).findLiteState(
					 			getLitePoint().getStateGroupID(), getTrigger().getPointValue().intValue() ).getStateText();
		}
		else
		{
			currentValue = String.valueOf(getTrigger().getPointValue().doubleValue());
		}

	}
	
	return currentValue;
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/2001 1:55:47 PM)
 * @return com.cannontech.database.data.lite.LitePoint
 */
public com.cannontech.database.data.lite.LitePoint getLitePoint() {
	return litePoint;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 12:08:09 PM)
 * @return com.cannontech.loadcontrol.data.LMControlAreaTrigger
 */
public com.cannontech.loadcontrol.data.LMControlAreaTrigger getTrigger() {
	return trigger;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 12:13:55 PM)
 * @return java.lang.String
 */
public java.lang.String getTriggerValue() 
{
	if( triggerValue == null )
	{
		//Returns the value of the trigger
		if( getTrigger().getTriggerType() == TriggerType.STATUS )
		{
			triggerValue = YukonSpringHook.getBean(StateGroupDao.class).findLiteState( getLitePoint().getStateGroupID(), 
					getTrigger().getNormalState().intValue()).getStateText();
		}
		else if( getTrigger().getTriggerType() == TriggerType.THRESHOLD ||
		         getTrigger().getTriggerType() == TriggerType.THRESHOLD_POINT )
		{
			triggerValue = getTrigger().getThreshold().toString();
		}
		else
			triggerValue = "  ---";

	}

	return triggerValue;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 12:11:54 PM)
 * @return boolean
 */
public boolean isFiring() 
{
	if( isValidTrigger() )
	{
		if( getTrigger().getTriggerType() == TriggerType.STATUS )
		{
			return getCurrentValue().equalsIgnoreCase(getTriggerValue());
		}
		else
			return getTrigger().getPointValue().doubleValue() > getTrigger().getThreshold().doubleValue();
	}
	else
		return false;
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/2001 2:15:24 PM)
 * @return boolean
 */
private boolean isValidTrigger() 
{
	//we only can have a true TRIGGER if we have a trigger object
	//  and a litepoint object
	return (getLitePoint() != null && getTrigger() != null);
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/2001 1:55:47 PM)
 * @param newLitePoint com.cannontech.database.data.lite.LitePoint
 */
public void setLitePoint(com.cannontech.database.data.lite.LitePoint newLitePoint) 
{
	//Any time the litepoint changes, we must re-evaluate our other values
	currentValue = null;
	triggerValue = null;

	litePoint = newLitePoint;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 12:08:09 PM)
 * @param newTrigger com.cannontech.loadcontrol.data.LMControlAreaTrigger
 */
public void setTrigger(com.cannontech.loadcontrol.data.LMControlAreaTrigger newTrigger) 
{
	//Any time the trigger changes, we must re-evaluate our other values
	currentValue = null;
	triggerValue = null;

	
	trigger = newTrigger;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 11:53:54 AM)
 * @return java.lang.String
 */
@Override
public String toString() 
{
	if( !isValidTrigger() )
		return "(null)";
	else if( getTrigger().getTriggerType() == TriggerType.STATUS )
	{
		return getLitePoint().getPointName().toUpperCase() + " : " + getCurrentValue() + " [Normal = " + getTriggerValue() + "]";
	}
	else if( getTrigger().getTriggerType() == TriggerType.THRESHOLD ||
	        getTrigger().getTriggerType() == TriggerType.THRESHOLD_POINT)
	{
		return getLitePoint().getPointName().toUpperCase() + " : " + getCurrentValue() + " [Threshold = " + getTriggerValue() + "]";
	}
	else
		return getLitePoint().getPointName().toUpperCase() + " : " + getCurrentValue() + " [" + getTriggerValue() + "]";
}
}
