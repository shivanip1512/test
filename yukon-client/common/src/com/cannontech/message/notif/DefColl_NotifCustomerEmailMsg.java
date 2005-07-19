package com.cannontech.message.notif;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_NotifCustomerEmailMsg extends com.cannontech.message.util.DefineCollectableMessage 
{
	//RogueWave classId
	public static final int NOTIF_CUSTOMER_EMAIL_MSG_ID = 701;


	/**
	 * DefColl_NotifResultMsg constructor comment.
	 */
	public DefColl_NotifCustomerEmailMsg() {
		super();
	}
	/**
	 * create method comment.
	 */
	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new NotifCustomerEmailMsg();
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
	public int getCxxClassId()
	{
		return NOTIF_CUSTOMER_EMAIL_MSG_ID;
	}
	/**
	 * getCxxStringId method comment.
	 */
	public String getCxxStringId()
	{
		return DefineCollectable.NO_STRINGID;
	}
	/**
	 * getJavaClass method comment.
	 */
	public Class getJavaClass() {
		return NotifCustomerEmailMsg.class;
	}
	/**
	 * restoreGuts method comment.
	 */
	public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
	{	
		super.restoreGuts( obj, vstr, polystr );
		NotifCustomerEmailMsg nEmailMsg = (NotifCustomerEmailMsg) obj;


		nEmailMsg.setTo( (String)vstr.restoreObject(SimpleMappings.CString) );
		nEmailMsg.setCustomerID( vstr.extractInt() );
		nEmailMsg.setSubject( (String)vstr.restoreObject(SimpleMappings.CString) );
		nEmailMsg.setBody( (String)vstr.restoreObject(SimpleMappings.CString) );
		nEmailMsg.setTo_CC( (String)vstr.restoreObject(SimpleMappings.CString) );
		nEmailMsg.setTo_BCC( (String)vstr.restoreObject(SimpleMappings.CString) );
		nEmailMsg.setAttachments( (Vector)vstr.restoreObject( polystr ) );
	}
	
	/**
	 * saveGuts method comment.
	 */
	public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
	{
		super.saveGuts( obj, vstr, polystr );
		NotifCustomerEmailMsg nEmailMsg = (NotifCustomerEmailMsg) obj;
		
		
		vstr.saveObject( nEmailMsg.getTo(), SimpleMappings.CString );
		vstr.insertInt( nEmailMsg.getCustomerID() );
		vstr.saveObject( nEmailMsg.getSubject(), SimpleMappings.CString );
		vstr.saveObject( nEmailMsg.getBody(), SimpleMappings.CString );
		vstr.saveObject( nEmailMsg.getTo_CC(), SimpleMappings.CString );
		vstr.saveObject( nEmailMsg.getTo_BCC(), SimpleMappings.CString );
		vstr.saveObject( nEmailMsg.getAttachments(), polystr);
	}
}
