package com.cannontech.loadcontrol.messages;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;

public class DefineCollectableLMManualControlResponse extends DefineCollectableLMMessage
{
	//RogueWave classId
	public static final int CTILMMANUALCONTROLRESPONSE_ID = 613;
/**
 * DefineCollectableScheduleCommand constructor comment.
 */
public DefineCollectableLMManualControlResponse()
{
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMManualControlResponse();
}
/**
 * getComparator method comment.
 */
public com.roguewave.tools.v2_0.Comparator getComparator() {
	return new Comparator() {
	  public int compare(Object x, Object y) {
			if( x == y )
				return 0;
			else
				return -1;
	  }
	};
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	return CTILMMANUALCONTROLRESPONSE_ID;
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
	return LMManualControlResponse.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	super.restoreGuts( obj, vstr, polystr );
	LMManualControlResponse lmManualControlResponse = (LMManualControlResponse) obj;
	Vector v = (Vector) vstr.restoreObject(polystr);
	lmManualControlResponse.setConstraintViolations(v);								
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );
	//	Should never be called
	  throw new java.lang.Error("LMManualControlResponse restoreGuts should never be receieved by the client" );
}
}
