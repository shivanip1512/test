package com.cannontech.message.dispatch.message;

/**
 * Insert the type's description here.
 * Creation date: (1/28/00 11:53:13 AM)
 * @author: 
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableEmailMsg extends com.cannontech.message.util.DefineCollectableMessage implements com.roguewave.vsj.DefineCollectable {
	private static final int classId = 1597;
/**
 * DefineCollectablePointData constructor comment.
 */
public DefineCollectableEmailMsg() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new EmailMsg();
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
	return EmailMsg.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	super.restoreGuts(obj, vstr, polystr);
	
	EmailMsg email = (EmailMsg) obj;

	email.setId( vstr.extractLong() );
	email.setType( vstr.extractInt() );
	email.setSubject( (String) vstr.restoreObject(SimpleMappings.CString));
	email.setText( (String) vstr.restoreObject(SimpleMappings.CString));
	email.setSender( (String) vstr.restoreObject(SimpleMappings.CString));
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	EmailMsg email = (EmailMsg) obj;

	vstr.insertLong( email.getId() );
	vstr.insertInt( email.getType() );
	vstr.saveObject( email.getSubject(), SimpleMappings.CString );
	vstr.saveObject( email.getText(), SimpleMappings.CString );
	vstr.saveObject( email.getSender(), SimpleMappings.CString );
}
}
