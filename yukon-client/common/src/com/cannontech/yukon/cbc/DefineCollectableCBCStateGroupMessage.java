package com.cannontech.yukon.cbc;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;

public class DefineCollectableCBCStateGroupMessage extends DefineCollectableCBCMessage
{
	//RogueWave classId
	public static final int CTISTATEGROUP_MESSAGE_ID = 508;
/**
 * DefineCollectableMessage constructor comment.
 */
public DefineCollectableCBCStateGroupMessage() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new CBCStates();
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
	return DefineCollectableCBCStateGroupMessage.CTISTATEGROUP_MESSAGE_ID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass() {
	return com.cannontech.yukon.cbc.CBCStates.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {

	super.restoreGuts( obj, vstr, polystr );
	java.util.Vector stateStore = (java.util.Vector)vstr.restoreObject( polystr );

	((CBCStates) obj).setStates( stateStore );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	com.cannontech.clientutils.CTILogger.info("**********************************************************************************");
	com.cannontech.clientutils.CTILogger.info("com.cannontech.cbc.DefineCollectableCBCStateGroupMessage.saveGuts() should not be called");
	com.cannontech.clientutils.CTILogger.info("**********************************************************************************");
	
	//super.saveGuts( obj, vstr, polystr );
	//java.util.Vector stateStore = ((CBCAlarmStates) obj).getStates();
	//vstr.saveObject( stateStore, com.roguewave.vsj.streamer.CollectableMappings.allCollectables );
}
}
