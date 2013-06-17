package com.cannontech.message.notif;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_NotifEmailAttchMsg extends com.cannontech.message.util.DefineCollectableMessage 
{
	//RogueWave classId
	public static final int NOTIF_EMAIL_ATTCH_MSG_ID = 703;


	/**
	 * DefColl_NotifEmailAttchMsg constructor comment.
	 */
	public DefColl_NotifEmailAttchMsg() {
		super();
	}
	/**
	 * create method comment.
	 */
	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new NotifEmailAttchMsg();
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
		return NOTIF_EMAIL_ATTCH_MSG_ID;
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
		return NotifEmailAttchMsg.class;
	}
	/**
	 * restoreGuts method comment.
	 */
	public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
	{	
		super.restoreGuts( obj, vstr, polystr );
		NotifEmailAttchMsg nEmailAttchMsg = (NotifEmailAttchMsg) obj;

		nEmailAttchMsg.setFileName( (String)vstr.restoreObject(SimpleMappings.CString) );
		
		long contentLength = vstr.extractUnsignedInt();
		nEmailAttchMsg.setFileContents( vstr.getChars( (int)contentLength ) );
	}
	
	/**
	 * saveGuts method comment.
	 */
	public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
	{
		super.saveGuts( obj, vstr, polystr );
		NotifEmailAttchMsg nEmailAttchMsg = (NotifEmailAttchMsg) obj;
		
		vstr.saveObject( nEmailAttchMsg.getFileName(), SimpleMappings.CString );
		
		vstr.insertUnsignedInt( nEmailAttchMsg.getFileContents().length );
		vstr.putChars( nEmailAttchMsg.getFileContents(), nEmailAttchMsg.getFileContents().length );
	}
}
