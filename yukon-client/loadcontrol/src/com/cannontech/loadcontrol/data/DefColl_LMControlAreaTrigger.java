package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import java.util.Date;

import com.cannontech.dr.controlarea.model.TriggerType;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMControlAreaTrigger implements com.roguewave.vsj.DefineCollectable
{
	//The roguewave class id
	private static int CTILMCONTROLAREATRIGGER_ID = 605;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMControlAreaTrigger()
{
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMControlAreaTrigger();
}
/**
 * getComparator method comment.
 */
public com.roguewave.tools.v2_0.Comparator getComparator() 
{
	return new Comparator() 
	{
		public int compare(Object x, Object y) 
		{
			return (int) (((LMControlAreaTrigger)x).getYukonID().intValue() - ((LMControlAreaTrigger)y).getYukonID().intValue() );
		}
	};
	
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	return CTILMCONTROLAREATRIGGER_ID;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId()
{
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class<?> getJavaClass()
{
	return LMControlAreaTrigger.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	LMControlAreaTrigger lmTrigger = (LMControlAreaTrigger) obj;

	Integer yukonID = new Integer( (int)vstr.extractUnsignedInt() );
	Integer triggerNumber = new Integer( (int)vstr.extractUnsignedInt() );
	String triggerType = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer pointID = new Integer( (int)vstr.extractUnsignedInt() );
	Double pointValue = new Double( vstr.extractDouble() );
	Date lastPointValueTimeStamp = (Date) vstr.restoreObject( SimpleMappings.Time );
	Integer normalState = new Integer( (int)vstr.extractUnsignedInt() );
	Double threshold = new Double( vstr.extractDouble() );
	String projectionType = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer projectionPoints = new Integer( (int)vstr.extractUnsignedInt() );
	Integer projectAheadDuration = new Integer( (int)vstr.extractUnsignedInt() );
	Integer thresholdKickPercent = new Integer( (int)vstr.extractUnsignedInt() );
	Double minRestoreOffset = new Double( vstr.extractDouble() );
	Integer peakPointID = new Integer( (int)vstr.extractUnsignedInt() );
	Double peakPointValue = new Double( vstr.extractDouble() );
	Date lastPeakPointValueTimeStamp = (Date) vstr.restoreObject( SimpleMappings.Time );
 	Double projectedPointValue = new Double( vstr.extractDouble() );
 	
	lmTrigger.setYukonID(yukonID);
	lmTrigger.setTriggerNumber(triggerNumber);
	lmTrigger.setTriggerType(TriggerType.getForDbString(triggerType));
	lmTrigger.setPointId(pointID);
	lmTrigger.setPointValue(pointValue);
	lmTrigger.setLastPointValueTimeStamp(lastPointValueTimeStamp);
	lmTrigger.setNormalState(normalState);
	lmTrigger.setThreshold(threshold);
	lmTrigger.setProjectionType(projectionType);
	lmTrigger.setProjectionPoints(projectionPoints);
	lmTrigger.setProjectAheadDuration(projectAheadDuration);
	lmTrigger.setThresholdKickPercent(thresholdKickPercent);
	lmTrigger.setMinRestoreOffset(minRestoreOffset);
	lmTrigger.setPeakPointId(peakPointID);
	lmTrigger.setPeakPointValue(peakPointValue);
	lmTrigger.setLastPeakPointValueTimeStamp(lastPeakPointValueTimeStamp);
	lmTrigger.setProjectedPointValue(projectedPointValue);
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
/* This saveGuts isn't implemented because we won't be sending full LMControlAreas
	 to the Server */

	/*LMControlArea lmControlArea = (LMControlArea) obj;
	
	vstr.insertUnsignedInt( strategy.getCapStrategyID().intValue() );
	vstr.saveObject( strategy.getStrategyName(), SimpleMappings.CString );
	vstr.saveObject( strategy.getAreaName(), SimpleMappings.CString );
	vstr.insertUnsignedInt( strategy.getActualVarPointID().intValue() );
	vstr.insertDouble( strategy.getActualVarPointValue().doubleValue() );
	vstr.insertUnsignedInt( strategy.getMaxDailyOperation().intValue() );
	vstr.insertDouble( strategy.getPeakSetPoint().doubleValue() );
	vstr.insertDouble( strategy.getOffPeakSetPoint().doubleValue() );
	vstr.saveObject( strategy.getPeakStartTime().getTime(), SimpleMappings.Time );
	vstr.saveObject( strategy.getPeakStopTime().getTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( strategy.getCalculatedVarPointID().intValue() );
	vstr.insertDouble( strategy.getCalculatedVarPointValue().doubleValue() );
	vstr.insertDouble( strategy.getBandwidth().doubleValue() );
	vstr.insertUnsignedInt( strategy.getControlInterval().intValue() );
	vstr.insertUnsignedInt( strategy.getMinResponseTime().intValue() );
	vstr.insertUnsignedInt( strategy.getMinConfirmPercent().intValue() );
	vstr.insertUnsignedInt( strategy.getFailurePercent().intValue() );
	vstr.saveObject( strategy.getStatus(), SimpleMappings.CString );
	vstr.insertUnsignedInt( strategy.getOperations().intValue() );
	vstr.saveObject( strategy.getLastOperationTime().getTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( strategy.getLastCapBankControlled().intValue() );
	vstr.saveObject( strategy.getDaysOfWeek(), SimpleMappings.CString );
	vstr.insertUnsignedInt( strategy.getPeakOrOffPeak().intValue() );
	vstr.insertUnsignedInt( strategy.getRecentlyControlled().intValue() );
	vstr.insertDouble( strategy.getCalculatedValueBeforeControl().doubleValue() );
	vstr.saveObject( strategy.getLastControlTime().getTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( strategy.getDecimalPlaces().intValue() );
	vstr.saveObject( ((java.util.Vector)strategy.getCapBankDeviceVector()), polystr );*/
}
}
