package com.cannontech.message.dispatchmessage;

/**
 * This type was created by Cannon Technologies Inc.
 */
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableDBChangeMsg extends com.cannontech.message.util.DefineCollectableMessage {
	private static final int classId = 1580;
/**
 * DefineDBChangeMsg constructor comment.
 */
public DefineCollectableDBChangeMsg() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new DBChangeMessage();
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
	return DBChangeMessage.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.restoreGuts( obj, vstr, polystr );

	DBChangeMessage dbChange = (DBChangeMessage) obj;

	dbChange.setId( vstr.extractInt() );
	dbChange.setDatabase( vstr.extractInt() );
	dbChange.setCategory( (String)vstr.restoreObject(SimpleMappings.CString) );
	dbChange.setObjectType( (String)vstr.restoreObject(SimpleMappings.CString) );
	dbChange.setDbChangeType( DbChangeType.getForType(vstr.extractInt()) );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts(obj, vstr, polystr );

	DBChangeMessage dbChange = (DBChangeMessage) obj;

	vstr.insertInt( dbChange.getId() );
	vstr.insertInt( dbChange.getDatabase() );
	vstr.saveObject( dbChange.getCategory(), SimpleMappings.CString );
	vstr.saveObject( dbChange.getObjectType(), SimpleMappings.CString );
	vstr.insertInt( dbChange.getDbChangeType().getTypeOfChange() );
}
}
