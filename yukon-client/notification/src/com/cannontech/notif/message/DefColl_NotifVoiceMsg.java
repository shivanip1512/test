package com.cannontech.notif.message;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;

public class DefColl_NotifVoiceMsg extends com.cannontech.message.util.DefineCollectableMessage 
{
	//RogueWave classId
	public static final int MSG_ID = 704;


	/**
	 * DefColl_NotifVoiceMsg constructor comment.
	 */
	public DefColl_NotifVoiceMsg() {
		super();
	}
	/**
	 * create method comment.
	 */
	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new NotifVoiceMsg();
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
		return MSG_ID;
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
		return DefColl_NotifVoiceMsg.class;
	}
	/**
	 * restoreGuts method comment.
	 */
	public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
	{	
		super.restoreGuts( obj, vstr, polystr );
		NotifVoiceMsg msg = (NotifVoiceMsg) obj;


		msg.setNotifProgramID( vstr.extractInt() );
	}
	
	/**
	 * saveGuts method comment.
	 */
	public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
	{
		super.saveGuts( obj, vstr, polystr );
		NotifVoiceMsg msg = (NotifVoiceMsg) obj;
		
		
		vstr.insertInt( msg.getNotifProgramID() );
	}
}
