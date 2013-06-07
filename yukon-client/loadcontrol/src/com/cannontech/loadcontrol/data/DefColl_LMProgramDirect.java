package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.messaging.message.loadcontrol.data.ProgramDirect;
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
	return new ProgramDirect();
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
	return ProgramDirect.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );

	ProgramDirect lmProgramDirect = (ProgramDirect) obj;
	
	lmProgramDirect.setCurrentGearNumber( new Integer( (int)vstr.extractUnsignedInt() ) );
	lmProgramDirect.setLastGroupControlled( new Integer( (int)vstr.extractUnsignedInt() ) );
	
	GregorianCalendar directStartTime = new java.util.GregorianCalendar();
	directStartTime.setTime((Date)vstr.restoreObject( SimpleMappings.Time ) );
	lmProgramDirect.setDirectStartTime( directStartTime );

	GregorianCalendar directStopTime = new java.util.GregorianCalendar();
	directStopTime.setTime((Date)vstr.restoreObject( SimpleMappings.Time ) );
	lmProgramDirect.setDirectStopTime( directStopTime );	
	
	GregorianCalendar notifyActiveTime = new java.util.GregorianCalendar();
	notifyActiveTime.setTime((Date)vstr.restoreObject( SimpleMappings.Time) );
	lmProgramDirect.setNotifyActiveTime( notifyActiveTime );	
	
	GregorianCalendar notifyInactiveTime = new java.util.GregorianCalendar();
	notifyInactiveTime.setTime((Date)vstr.restoreObject( SimpleMappings.Time) );
	lmProgramDirect.setNotifyInactiveTime( notifyInactiveTime );	
		
	GregorianCalendar startedRampingOutTime = new java.util.GregorianCalendar(); 
	startedRampingOutTime.setTime((Date)vstr.restoreObject( SimpleMappings.Time) );
	lmProgramDirect.setStartedRampingOut( startedRampingOutTime );	
	
	lmProgramDirect.setTriggerOffset( new Integer( (int) vstr.extractUnsignedInt() ) );
	lmProgramDirect.setTriggerRestoreOffset( new Integer( (int) vstr.extractUnsignedInt() ) );

	lmProgramDirect.setConstraintOverride(
		(int)vstr.extractUnsignedInt() == 1 ? true : false );


	//lmProgramDirect.setDirectGearVector( (Vector) vstr.restoreObject( polystr ) );
    lmProgramDirect.setDirectGearVector( CollectionExtracter.extractVector(vstr, polystr) );


	// serialize groups one at a time, using a rwordered/vector is trouble for some reason
	int numGroups = vstr.extractInt();
	Vector groupVector = new Vector(numGroups);	
	for(int i = 0; i < numGroups; i++)
		groupVector.add(vstr.restoreObject(polystr));
	lmProgramDirect.setLoadControlGroupVector( groupVector );

	// restore all the active master programs
	numGroups = vstr.extractInt();
	Vector activeMastersVector = new Vector(numGroups);
	for(int i = 0; i < numGroups; i++)
		activeMastersVector.add(vstr.restoreObject(polystr));
	lmProgramDirect.setActiveMasterPrograms( activeMastersVector );
	
	// restore all the active subordinate programs
	numGroups = vstr.extractInt();
	Vector activeSubordinatesVector = new Vector(numGroups);
	for(int i = 0; i < numGroups; i++)
		activeSubordinatesVector.add(vstr.restoreObject(polystr));
	lmProgramDirect.setActiveSubordinatePrograms(activeSubordinatesVector);
		
}


/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	//I don't think this is ever used!
	super.saveGuts( obj, vstr, polystr );
}
}
