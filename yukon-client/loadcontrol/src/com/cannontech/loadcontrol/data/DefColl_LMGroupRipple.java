package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;

public class DefColl_LMGroupRipple extends DefColl_LMDirectGroupBase
{
	//The roguewave class id
	private static int CTILMGROUPRIPPLE_ID = 625;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMGroupRipple()
{
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMGroupRipple();
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	return CTILMGROUPRIPPLE_ID;
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
	return LMGroupRipple.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );

	LMGroupRipple lmGroupRipple = (LMGroupRipple) obj;
	lmGroupRipple.setShedTime( new Integer( (int)vstr.extractUnsignedInt() ) );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	super.saveGuts( obj, vstr, polystr );

/* This saveGuts isn't implemented because we won't be sending full LMGroupRipples
	 to the Server */
}
}
