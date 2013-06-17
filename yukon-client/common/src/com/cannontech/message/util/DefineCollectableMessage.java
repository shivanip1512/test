package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableMessage implements com.roguewave.vsj.DefineCollectable {
/**
 * DefineCollectableMessage constructor comment.
 */
public DefineCollectableMessage() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new Message(); //Should this ever occur?
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
	return com.cannontech.message.util.Message.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {

	java.util.Date timeStamp = (java.util.Date) vstr.restoreObject( SimpleMappings.Time );
	int priority = vstr.extractInt();
	int soe_tag = vstr.extractInt();
	String username = (String) (vstr.restoreObject( SimpleMappings.CString ));
	int token = vstr.extractInt();
	String source = (String) (vstr.restoreObject( SimpleMappings.CString ));

	((Message) obj).setTimeStamp( timeStamp );
	((Message) obj).setPriority( priority );
	((Message) obj).setSOE_Tag( soe_tag );
	((Message) obj).setUserName( username );
	((Message) obj).setToken( token );
	((Message) obj).setSource( source );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {

	java.util.Date timeStamp = ((Message) obj).getTimeStamp();
	int priority = ((Message) obj).getPriority();
	int soe_tag = ((Message) obj).getSOE_Tag();
	String username = ((Message) obj).getUserName();
	int token = ((Message) obj).getToken();
	String source = ((Message) obj).getSource();
	
	vstr.saveObject( timeStamp, SimpleMappings.Time );
	vstr.insertInt( priority );
	vstr.insertInt( soe_tag );
	vstr.saveObject( username, SimpleMappings.CString );
	vstr.insertInt( token );
	vstr.saveObject( source, SimpleMappings.CString );
}
}
