package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMProgramBase implements com.roguewave.vsj.DefineCollectable 
{
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMProgramBase() {
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	throw new RuntimeException();
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
			return (int) (((LMProgramBase)x).getYukonID().intValue() - ((LMProgramBase)y).getYukonID().intValue() );
		}
	};
	
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	Exception e = new Exception("com.cannontech.loadcontrol.data.DefineCollectableLMProgramBase.getCxxClassId() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return -1;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId()
{
	Exception e = new Exception("com.cannontech.loadcontrol.data.DefineCollectableLMProgramBase.getCxxStringId() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass()
{
	Exception e = new Exception("com.cannontech.loadcontrol.data.DefineCollectableLMProgramBase.getJavaClass() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return LMProgramBase.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	LMProgramBase lmProgramBase = (LMProgramBase) obj;
	
	Integer yukonID = new Integer( (int)vstr.extractUnsignedInt() );
	String yukonCategory = (String) vstr.restoreObject( SimpleMappings.CString );
	String yukonClass = (String) vstr.restoreObject( SimpleMappings.CString );
	String yukonName = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer yukonType = new Integer( (int)vstr.extractUnsignedInt() );
	String yukonDescription = (String) vstr.restoreObject( SimpleMappings.CString );
	int disableFlag = (int)vstr.extractUnsignedInt();
	Integer userOrder = new Integer( (int)vstr.extractUnsignedInt() );
	Integer stopOrder = new Integer( (int)vstr.extractUnsignedInt() );
	Integer defaultPriority = new Integer( (int)vstr.extractUnsignedInt() );
	String controlType = (String) vstr.restoreObject( SimpleMappings.CString );
	String availableWeekDays = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer maxHoursDaily = new Integer( (int)vstr.extractUnsignedInt() );
	Integer maxHoursMonthly = new Integer( (int)vstr.extractUnsignedInt() );
	Integer maxHoursSeasonal = new Integer( (int)vstr.extractUnsignedInt() );
	Integer maxHoursAnnually = new Integer( (int)vstr.extractUnsignedInt() );
	Integer minActivateTime = new Integer( (int)vstr.extractUnsignedInt() );
	Integer minRestartTime = new Integer( (int)vstr.extractUnsignedInt() );
	Integer programStatusPointId = new Integer( (int)vstr.extractUnsignedInt() );
	Integer programStatus = new Integer( (int)vstr.extractUnsignedInt() );
	Integer reductionAnalogPointId = new Integer( (int)vstr.extractUnsignedInt() );
	Double reductionTotal = new Double( vstr.extractDouble() );
	java.util.GregorianCalendar startedControlling = new java.util.GregorianCalendar();
	startedControlling.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	java.util.GregorianCalendar lastControlSent = new java.util.GregorianCalendar();
	lastControlSent.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	int manualControlReceivedFlag = (int)vstr.extractUnsignedInt();
	java.util.Vector controlWindowVector = (java.util.Vector) vstr.restoreObject( polystr );

	lmProgramBase.setYukonID(yukonID);
	lmProgramBase.setYukonCategory(yukonCategory);
	lmProgramBase.setYukonClass(yukonClass);
	lmProgramBase.setYukonName(yukonName);
	lmProgramBase.setYukonType(yukonType);
	lmProgramBase.setYukonDescription(yukonDescription);
	lmProgramBase.setDisableFlag(new Boolean(disableFlag>0));
	lmProgramBase.setUserOrder(userOrder);
	lmProgramBase.setStopOrder(stopOrder);
	lmProgramBase.setDefaultPriority(defaultPriority);
	lmProgramBase.setControlType(controlType);
	lmProgramBase.setAvailableWeekDays(availableWeekDays);
	lmProgramBase.setMaxHoursDaily(maxHoursDaily);
	lmProgramBase.setMaxHoursMonthly(maxHoursMonthly);
	lmProgramBase.setMaxHoursSeasonal(maxHoursSeasonal);
	lmProgramBase.setMaxHoursAnnually(maxHoursAnnually);
	lmProgramBase.setMinActivateTime(minActivateTime);
	lmProgramBase.setMinResponseTime(minRestartTime);
	lmProgramBase.setProgramStatusPointID(programStatusPointId);
	lmProgramBase.setProgramStatus(programStatus);
	lmProgramBase.setReductionAnalogPointId(reductionAnalogPointId);
	lmProgramBase.setReductionTotal(reductionTotal);
	lmProgramBase.setStartedControlling(startedControlling);
	lmProgramBase.setLastControlSent(lastControlSent);
	lmProgramBase.setManualControlReceivedFlag(new Boolean(manualControlReceivedFlag>0));
	lmProgramBase.setControlWindowVector(controlWindowVector);
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {

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
