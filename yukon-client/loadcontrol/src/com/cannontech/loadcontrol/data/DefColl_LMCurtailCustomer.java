package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge. <-- yay
 */
import java.util.Date;

import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMCurtailCustomer extends DefColl_LMCICustomerBase
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
	return DefColl_LMCurtailCustomer.CTILMCURTAILCUSTOMER_ID;
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

	Boolean reqAck = (vstr.extractUnsignedInt() > 0 ? Boolean.TRUE : Boolean.FALSE);
	Long curtailRefID = new Long(vstr.extractLong());
	String ackStatus = (String) vstr.restoreObject( SimpleMappings.CString );
	Date ackDateTime = (Date)vstr.restoreObject( SimpleMappings.Time );
	String ipAddress = (String) vstr.restoreObject( SimpleMappings.CString );
	String userIDName = (String) vstr.restoreObject( SimpleMappings.CString );
	String nameOfAckPerson = (String) vstr.restoreObject( SimpleMappings.CString );
	String curtNotes = (String) vstr.restoreObject( SimpleMappings.CString );
	Boolean ackLateFlag = (vstr.extractUnsignedInt() > 0 ? Boolean.TRUE : Boolean.FALSE);
	
	lmCurtailCustomer.setRequireAck(reqAck);
	lmCurtailCustomer.setCurtailRefID(curtailRefID);
	lmCurtailCustomer.setAckStatus(ackStatus);
	lmCurtailCustomer.setAckDateTime(ackDateTime);
	lmCurtailCustomer.setIpAddress(ipAddress);
	lmCurtailCustomer.setUserIDname(userIDName);
	lmCurtailCustomer.setNameOfAckPerson(nameOfAckPerson);
	lmCurtailCustomer.setCurtailmentNotes(curtNotes);
	lmCurtailCustomer.setAckLateFlag(ackLateFlag);
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	Exception e = new Exception(this.getClass().getName() + ".saveGuts() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
}
}
