package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

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
	GregorianCalendar directStartTime = new java.util.GregorianCalendar();
	directStartTime.setTime((Date)vstr.restoreObject( SimpleMappings.Time ) );

	GregorianCalendar directStopTime = new java.util.GregorianCalendar();
	directStopTime.setTime((Date)vstr.restoreObject( SimpleMappings.Time ) );	
	
	Integer dailyOps = new Integer( (int) vstr.extractUnsignedInt() );
	
	GregorianCalendar notifyTime = new java.util.GregorianCalendar();
	notifyTime.setTime((Date)vstr.restoreObject( SimpleMappings.Time) );
	
	GregorianCalendar startedRampingOutTime = new java.util.GregorianCalendar(); 
	startedRampingOutTime.setTime((Date)vstr.restoreObject( SimpleMappings.Time) );

	Vector directGearVector = (Vector) vstr.restoreObject( polystr );
	
	// serialize groups one at a time, using a rwordered/vector is trouble for some reason
	int numGroups = vstr.extractInt();
	Vector groupVector = new Vector(numGroups);
	
	for(int i = 0; i < numGroups; i++) {
		groupVector.add(vstr.restoreObject(polystr));
	}

	Vector activeMastersVector = (Vector) vstr.restoreObject(polystr);
	Vector activeSubordinatesVector = (Vector) vstr.restoreObject(polystr);

	lmProgramDirect.setCurrentGearNumber(currentGearNumber);
	lmProgramDirect.setLastGroupControlled(lastGroupControlled);
	lmProgramDirect.setDirectStartTime( directStartTime );
	lmProgramDirect.setDirectStopTime( directStopTime );
	lmProgramDirect.setDailyOps( dailyOps );
	lmProgramDirect.setNotifyTime( notifyTime );
	lmProgramDirect.setStartedRampingOut( startedRampingOutTime );
	
	lmProgramDirect.setDirectGearVector(directGearVector);
	lmProgramDirect.setLoadControlGroupVector( groupVector );
	lmProgramDirect.setActiveMasterPrograms(activeMastersVector);
	lmProgramDirect.setActiveSubordinatePrograms(activeSubordinatesVector);
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	super.saveGuts( obj, vstr, polystr );
}
}
