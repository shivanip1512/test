package com.cannontech.yukon.cbc;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableCBCMessage extends com.cannontech.message.util.DefineCollectableMessage {

	//RogueWave classId
	public static final int CTICCMESSAGE_ID = 500;
/**
 * DefineCollectableMessage constructor comment.
 */
public DefineCollectableCBCMessage() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new CBCMessage();
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
	return DefineCollectable.NO_CLASSID;
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
	return com.cannontech.yukon.cbc.CBCMessage.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {

	super.restoreGuts( obj, vstr, polystr );
	String message = (String) vstr.restoreObject( SimpleMappings.CString );

	((CBCMessage) obj).setMessage( message );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.saveGuts( obj, vstr, polystr );
	String message = ((CBCMessage) obj).getMessage();

	vstr.saveObject( message, SimpleMappings.CString );	
}
}
