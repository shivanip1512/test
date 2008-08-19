package com.cannontech.message.porter.message;

/**
 * Insert the type's description here.
 * Creation date: (5/17/00 1:14:24 PM)
 * @author: 
 */

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableRequest extends com.cannontech.message.util.DefineCollectableMessage {
	private static final int classId = 1585;
/**
 * DefineCollectableRequest constructor comment.
 */
public DefineCollectableRequest() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:27:49 PM)
 * @return java.lang.Object
 * @param vstr com.roguewave.vsj.VirtualInputStream
 * @exception java.io.IOException The exception description.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new Request();
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:28:51 PM)
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
 * Creation date: (5/17/00 1:29:25 PM)
 * @return int
 */
public int getCxxClassId() {
	return classId;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:29:50 PM)
 * @return java.lang.String
 */
public String getCxxStringId() {
	return DefineCollectable.NO_STRINGID;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:30:16 PM)
 * @return java.lang.Class
 */
public Class getJavaClass() {
	return Request.class;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:30:47 PM)
 * @param obj java.lang.Object
 * @param vstr com.roguewave.vsj.VirtualInputStream
 * @param polystr com.roguewave.vsj.CollectableStreamer
 * @exception java.io.IOException The exception description.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	throw new Error("Not implemented!");
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:31:17 PM)
 * @param obj java.lang.Object
 * @param vstr com.roguewave.vsj.VirtualOutputStream
 * @param polystr com.roguewave.vsj.CollectableStreamer
 * @exception java.io.IOException The exception description.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts(obj, vstr, polystr );

	Request req = (Request) obj;

	vstr.insertLong( req.getDeviceID() );
	vstr.saveObject( req.getCommandString(), SimpleMappings.CString );
	vstr.insertLong( req.getRouteID() );
	vstr.insertInt( req.getMacroOffset() );
	vstr.insertInt( req.getAttemptNum() );
	vstr.insertLong( req.getGroupMessageID() );
	vstr.insertLong( req.getUserMessageID() );
	vstr.insertInt( req.getOptionsField() );
}
}
