package com.cannontech.message.dispatchmessage;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.messaging.message.dispatch.RegistrationMessage;
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
	return new RegistrationMessage();
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
	return RegistrationMessage.class;
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

	RegistrationMessage reg = (RegistrationMessage) obj;

	vstr.saveObject( reg.getAppName(), SimpleMappings.CString );
	vstr.insertInt( (int)reg.getRegId() );
	vstr.insertInt( reg.getAppIsUnique() );
	vstr.insertInt( reg.getAppKnownPort() );
	vstr.insertInt( reg.getAppExpirationDelay() );
	
}
}
