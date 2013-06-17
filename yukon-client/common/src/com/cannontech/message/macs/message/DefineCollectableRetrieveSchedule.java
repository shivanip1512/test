package com.cannontech.message.macs.message;

/**
 * Insert the type's description here.
 * Creation date: (2/19/2001 1:11:05 PM)
 * @author: 
 */

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;

public class DefineCollectableRetrieveSchedule extends com.cannontech.message.util.DefineCollectableMessage 
{
	//RogueWave classId
	public static final int classId = 143;
/**
 * DefineCollectableInfo constructor comment.
 */
public DefineCollectableRetrieveSchedule() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new RetrieveSchedule();
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
public int getCxxClassId() {
	return classId;
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
	return com.cannontech.message.macs.message.RetrieveSchedule.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	throw new RuntimeException("Not supported");
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.saveGuts(obj, vstr, polystr);

	RetrieveSchedule msg = (RetrieveSchedule) obj;
	vstr.insertLong( msg.getScheduleId() );
}
}
