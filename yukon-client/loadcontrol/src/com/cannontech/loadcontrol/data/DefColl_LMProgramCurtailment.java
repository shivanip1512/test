package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMProgramCurtailment extends DefColl_LMProgramBase
{
	//The roguewave class id
	private static int CTILMPROGRAMCURTAILMENT_ID = 614;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMProgramCurtailment()
{
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMProgramCurtailment();
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	return CTILMPROGRAMCURTAILMENT_ID;
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
public Class getJavaClass()
{
	return LMProgramCurtailment.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );

	LMProgramCurtailment lmProgramCurtailment = (LMProgramCurtailment) obj;
	
	Integer minNotifyTime = new Integer( (int)vstr.extractUnsignedInt() );
	String heading = (String) vstr.restoreObject( SimpleMappings.CString );
	String messageHeader = (String) vstr.restoreObject( SimpleMappings.CString );
	String messageFooter = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer ackTimeLimit = new Integer( (int)vstr.extractUnsignedInt() );	
	String cancelMsg = (String) vstr.restoreObject( SimpleMappings.CString );
	String stopEarlyMsg = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer curtailReferenceId = new Integer( (int)vstr.extractUnsignedInt() );
	java.util.GregorianCalendar actionDateTime = new java.util.GregorianCalendar();
	actionDateTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	java.util.GregorianCalendar notificationDateTime = new java.util.GregorianCalendar();
	notificationDateTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	java.util.GregorianCalendar curtailmentStartTime = new java.util.GregorianCalendar();
	curtailmentStartTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	java.util.GregorianCalendar curtailmentStopTime = new java.util.GregorianCalendar();
	curtailmentStopTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	String runStatus = (String) vstr.restoreObject( SimpleMappings.CString );
	String additionalInfo = (String) vstr.restoreObject( SimpleMappings.CString );
	java.util.Vector groupVector = (java.util.Vector) vstr.restoreObject( polystr );

	lmProgramCurtailment.setMinNotifyTime(minNotifyTime);
	lmProgramCurtailment.setHeading(heading);
	lmProgramCurtailment.setMessageHeader(messageHeader);
	lmProgramCurtailment.setMessageFooter(messageFooter);
	lmProgramCurtailment.setAckTimeLimit(ackTimeLimit);
	lmProgramCurtailment.setCanceledMsg( cancelMsg );
	lmProgramCurtailment.setStoppedEarlyMsg( stopEarlyMsg );
	lmProgramCurtailment.setCurtailReferenceId(curtailReferenceId);
	lmProgramCurtailment.setActionDateTime(actionDateTime);
	lmProgramCurtailment.setNotificationDateTime(notificationDateTime);
	lmProgramCurtailment.setCurtailmentStartTime(curtailmentStartTime);
	lmProgramCurtailment.setCurtailmentStopTime(curtailmentStopTime);
	lmProgramCurtailment.setRunStatus(runStatus);
	lmProgramCurtailment.setAdditionalInfo(additionalInfo);
	lmProgramCurtailment.setLoadControlGroupVector( groupVector );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	super.saveGuts( obj, vstr, polystr );

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
