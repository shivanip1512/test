package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.*;
import com.roguewave.vsj.streamer.SimpleMappings;
import com.roguewave.tools.v2_0.Comparator;

public class DefineCollectableLMGroupPoint extends DefineCollectableLMDirectGroupBase
{
	//The roguewave class id
	private static int CTILMGROUPPOINT_ID = 625;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefineCollectableLMGroupPoint()
{
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMGroupPoint();
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	return CTILMGROUPPOINT_ID;
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
	return LMGroupPoint.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );

	LMGroupPoint lmGroupPoint = (LMGroupPoint) obj;
	
	Integer devID = new Integer( (int)vstr.extractUnsignedInt() );
	Integer ptID = new Integer( (int)vstr.extractUnsignedInt() );
	Integer startState = new Integer( (int)vstr.extractUnsignedInt() );


	lmGroupPoint.setDeviceIDUsage(devID);
	lmGroupPoint.setPointIDUsage(ptID);
	lmGroupPoint.setStartControlRawState(startState);
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	super.saveGuts( obj, vstr, polystr );

/* This saveGuts isn't implemented because we won't be sending full LMControlAreas
	 to the Server */
}
}
