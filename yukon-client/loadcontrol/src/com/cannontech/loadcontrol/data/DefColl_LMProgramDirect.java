package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMProgramDirect extends DefColl_LMProgramBase
{
	//The roguewave class id
	private static int CTILMPROGRAMDIRECT_ID = 606;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMProgramDirect()
{
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMProgramDirect();
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	return CTILMPROGRAMDIRECT_ID;
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
	return LMProgramDirect.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );

	LMProgramDirect lmProgramDirect = (LMProgramDirect) obj;
	
	Integer currentGearNumber = new Integer( (int)vstr.extractUnsignedInt() );
	Integer lastGroupControlled = new Integer( (int)vstr.extractUnsignedInt() );
	java.util.GregorianCalendar directStartTime = new java.util.GregorianCalendar();
	directStartTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );

	java.util.GregorianCalendar directStopTime = new java.util.GregorianCalendar();
	directStopTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );	
	
	Integer dailyOps = new Integer( (int) vstr.extractUnsignedInt() );
	
	java.util.GregorianCalendar notifyTime = new java.util.GregorianCalendar();
	notifyTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time) );
	
	java.util.GregorianCalendar startedRampingOutTime = new java.util.GregorianCalendar(); 
	startedRampingOutTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time) );

	
	java.util.Vector directGearVector = (java.util.Vector) vstr.restoreObject( polystr );	
	java.util.Vector groupVector = (java.util.Vector) vstr.restoreObject( polystr );

	
	lmProgramDirect.setCurrentGearNumber(currentGearNumber);
	lmProgramDirect.setLastGroupControlled(lastGroupControlled);
	lmProgramDirect.setDirectStartTime( directStartTime );
	lmProgramDirect.setDirectStopTime( directStopTime );
	lmProgramDirect.setDailyOps( dailyOps );
	lmProgramDirect.setNotifyTime( notifyTime );
	lmProgramDirect.setStartedRampingOut( startedRampingOutTime );
	
	lmProgramDirect.setDirectGearVector(directGearVector);
	lmProgramDirect.setLoadControlGroupVector( groupVector );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	super.saveGuts( obj, vstr, polystr );
}
}
