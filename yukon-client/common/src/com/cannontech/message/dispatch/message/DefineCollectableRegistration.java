package com.cannontech.message.dispatch.message;

/**
 * This type was created in VisualAge.
 */

import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableRegistration extends com.cannontech.message.util.DefineCollectableMessage implements com.roguewave.vsj.DefineCollectable {
	private static final int classId = 1540;
/**
 * DefineCollectableRegistration constructor comment.
 */
public DefineCollectableRegistration() {
	super();
} 
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new Registration();
}
/**
 * getComparator method comment.
 */
public com.roguewave.tools.v2_0.Comparator getComparator() {
	return null;
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
	return Registration.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	//No need to implement this
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.saveGuts( obj, vstr, polystr );

	Registration reg = (Registration) obj;

	vstr.saveObject( reg.getAppName(), SimpleMappings.CString );
	vstr.insertLong( 0L );
	vstr.insertInt( reg.getAppIsUnique() );
	vstr.insertInt( reg.getAppKnownPort() );
	vstr.insertInt( reg.getAppExpirationDelay() );
	
}
}
