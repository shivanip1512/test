package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableLMGroupVersacom extends DefineCollectableLMDirectGroupBase
{
	//The roguewave class id
	private static int CTILMGROUPVERSACOM_ID = 610;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefineCollectableLMGroupVersacom()
{
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMGroupVersacom();
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	return CTILMGROUPVERSACOM_ID;
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
	return LMGroupVersacom.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );

	LMGroupVersacom lmGroupVersacom = (LMGroupVersacom) obj;	
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
