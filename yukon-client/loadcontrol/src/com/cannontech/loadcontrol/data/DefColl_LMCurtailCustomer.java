package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMCurtailCustomer extends DefColl_LMGroupBase
{
	//The roguewave class id
	private static int CTILMCURTAILCUSTOMER_ID = 608;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMCurtailCustomer() {
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMCurtailCustomer();
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId() {
	return this.CTILMCURTAILCUSTOMER_ID;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId() {
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass() {
	return LMCurtailCustomer.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );
	LMCurtailCustomer lmCurtailCustomer = (LMCurtailCustomer) obj;
	
	String custTimeZone = (String) vstr.restoreObject( SimpleMappings.CString );
	int reqAck = (int)vstr.extractUnsignedInt();
	int curtailRefID = (int)vstr.extractUnsignedInt();
	String ackStatus = (String) vstr.restoreObject( SimpleMappings.CString );
	java.util.Date ackDateTime = (java.util.Date)vstr.restoreObject( SimpleMappings.Time );
	String ipAddress = (String) vstr.restoreObject( SimpleMappings.CString );
	String userIDName = (String) vstr.restoreObject( SimpleMappings.CString );
	String nameOfAckPerson = (String) vstr.restoreObject( SimpleMappings.CString );
	String curtNotes = (String) vstr.restoreObject( SimpleMappings.CString );
	int ackLateFlag = (int)vstr.extractUnsignedInt();
	
	
	lmCurtailCustomer.setCustTimeZone(custTimeZone);
	lmCurtailCustomer.setRequireAck(new Boolean(reqAck>0));
	lmCurtailCustomer.setCurtailRefID(curtailRefID);
	lmCurtailCustomer.setAckStatus(ackStatus);
	lmCurtailCustomer.setAckDateTime(ackDateTime);
	lmCurtailCustomer.setIpAddress(ipAddress);
	lmCurtailCustomer.setUserIDname(userIDName);
	lmCurtailCustomer.setNameOfAckPerson(nameOfAckPerson);
	lmCurtailCustomer.setCurtailmentNotes(curtNotes);
	lmCurtailCustomer.setAckLateFlag(new Boolean(ackLateFlag>0));

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
