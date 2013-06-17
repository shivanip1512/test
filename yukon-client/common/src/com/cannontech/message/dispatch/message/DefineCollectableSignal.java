package com.cannontech.message.dispatch.message;

/**
 * Insert the type's description here.
 * Creation date: (1/28/00 11:24:57 AM)
 * @author: 
 */

import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableSignal extends com.cannontech.message.util.DefineCollectableMessage implements com.roguewave.vsj.DefineCollectable {
	private static final int classId = 1596; 
/**
 * DefineCollectableSignal constructor comment.
 */
public DefineCollectableSignal() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new Signal();
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
	return Signal.class;
}
/**
 * restoreGuts method comment. 
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.restoreGuts( obj, vstr, polystr );
	
	Signal signal = (Signal) obj;

	signal.setPointID(vstr.extractInt());
	signal.setLogType( vstr.extractInt());
	signal.setCategoryID( vstr.extractUnsignedInt() );
	signal.setDescription( (String) vstr.restoreObject( SimpleMappings.CString ) );
	signal.setAction( (String) vstr.restoreObject( SimpleMappings.CString ));
	signal.setTags( vstr.extractUnsignedInt() );
	signal.setCondition(vstr.extractInt());	
	signal.setMillis( vstr.extractUnsignedInt() );
	
	vstr.extractDouble();
}

/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.saveGuts( obj, vstr, polystr );

	Signal signal = (Signal) obj;

	vstr.insertLong( signal.getPointID() );
	vstr.insertInt( signal.getLogType() );
	vstr.insertUnsignedInt( signal.getCategoryID() );	
	vstr.saveObject( signal.getDescription(), SimpleMappings.CString );
	vstr.saveObject( signal.getAction(), SimpleMappings.CString );
	vstr.insertUnsignedInt( signal.getTags() );
	vstr.insertLong( signal.getCondition() );
	vstr.insertUnsignedInt( signal.getMillis() );
	
	vstr.insertDouble( 0 );
	
}
}
