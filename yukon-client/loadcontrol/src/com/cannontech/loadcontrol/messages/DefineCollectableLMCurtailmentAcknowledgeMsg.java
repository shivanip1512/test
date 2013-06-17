package com.cannontech.loadcontrol.messages;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableLMCurtailmentAcknowledgeMsg extends DefineCollectableLMMessage
{
	//RogueWave classId
	public static final int CTILMCURTAILMENTACK_MSG_ID = 615;
/**
 * DefineCollectableScheduleCommand constructor comment.
 */
public DefineCollectableLMCurtailmentAcknowledgeMsg()
{
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMCurtailmentAcknowledgeMsg();
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
	return CTILMCURTAILMENTACK_MSG_ID;
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
public Class getJavaClass()
{
	return LMCurtailmentAcknowledgeMsg.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	//Should never be called
	super.restoreGuts( obj, vstr, polystr );
	throw new java.lang.Error("LMCurtailmentAcknowledgeMsg restoreGuts should never be receieved by the client" );}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	LMCurtailmentAcknowledgeMsg curtailAckMsg = (LMCurtailmentAcknowledgeMsg) obj;

	vstr.insertUnsignedInt( (long)curtailAckMsg.getYukonID() );
	vstr.insertUnsignedInt( (long)curtailAckMsg.getCurtailReferenceID() );
	vstr.saveObject( curtailAckMsg.getAcknowledgeStatus(), SimpleMappings.CString );
	vstr.saveObject( curtailAckMsg.getIpAddressOfAckUser(), SimpleMappings.CString );
	vstr.saveObject( curtailAckMsg.getUserIdName(), SimpleMappings.CString );
	vstr.saveObject( curtailAckMsg.getNameOfAckPerson(), SimpleMappings.CString );
	vstr.saveObject( curtailAckMsg.getCurtailmentNotes(), SimpleMappings.CString );
}
}
