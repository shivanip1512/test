package com.cannontech.message.porter.message;

/**
 * Insert the type's description here.
 * Creation date: (5/17/00 1:15:05 PM)
 * @author: 
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableReturn extends com.cannontech.message.util.DefineCollectableMessage {
	private static final int classId = 1590;
/**
 * DefineCollectableReturn constructor comment.
 */
public DefineCollectableReturn() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:28:05 PM)
 * @return java.lang.Object
 * @param vstr com.roguewave.vsj.VirtualInputStream
 * @exception java.io.IOException The exception description.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new Return();
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:28:56 PM)
 * @return com.roguewave.tools.v2_0.Comparator
 */
public Comparator getComparator() {
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
 * Insert the method's description here.
 * Creation date: (5/17/00 1:29:36 PM)
 * @return int
 */
public int getCxxClassId() {
	return classId;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:29:57 PM)
 * @return java.lang.String
 */
public String getCxxStringId() {
	return DefineCollectable.NO_STRINGID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:30:20 PM)
 * @return java.lang.Class
 */
public Class getJavaClass() {
	return Return.class;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:30:51 PM)
 * @param obj java.lang.Object
 * @param vstr com.roguewave.vsj.VirtualInputStream
 * @param polystr com.roguewave.vsj.CollectableStreamer
 * @exception java.io.IOException The exception description.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );
 
	Return ret = (Return) obj;

	ret.setVector( (java.util.Vector)vstr.restoreObject( polystr ) );

	ret.setDeviceID( vstr.extractInt() );
	ret.setCommandString( (String) vstr.restoreObject(SimpleMappings.CString));
	ret.setResultString( (String) vstr.restoreObject( SimpleMappings.CString));
	ret.setStatus( vstr.extractInt() );
	ret.setRouteOffset( vstr.extractInt() );
	ret.setMacroOffset( vstr.extractInt() );
	ret.setAttemptNum( vstr.extractInt() );
	ret.setTransmissionID( vstr.extractLong() );
	ret.setUserMessageID( vstr.extractLong() );
	ret.setExpectMore( vstr.extractInt() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:31:04 PM)
 * @param obj java.lang.Object
 * @param vstr com.roguewave.vsj.VirtualOutputStream
 * @param polystr com.roguewave.vsj.CollectableStreamer
 * @exception java.io.IOException The exception description.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	throw new Error("Not implemented!");
}
}
