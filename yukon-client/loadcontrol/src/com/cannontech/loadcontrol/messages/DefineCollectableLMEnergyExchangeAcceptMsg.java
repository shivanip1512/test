package com.cannontech.loadcontrol.messages;

/**
 * Creation date: (5/29/2001 12:06:26 PM)
 * @author: Aaron Lauinger
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableLMEnergyExchangeAcceptMsg extends DefineCollectableLMMessage 
{
	//RogueWave classId
	public static final int RW_CLASS_ID = 624;
/**
 * DefineCollectableLMEnergyExchangeAcceptMsg constructor comment.
 */
public DefineCollectableLMEnergyExchangeAcceptMsg() {
	super();
}
/**
 * create method comment. 
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new LMEnergyExchangeAcceptMsg();
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
	return RW_CLASS_ID;
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
	return LMEnergyExchangeAcceptMsg.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	//Should never be called
	throw new java.lang.Error("LMEnergyExchangeAcceptMsg restoreGuts should never be receieved by the client" );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	LMEnergyExchangeAcceptMsg msg = (LMEnergyExchangeAcceptMsg) obj;

	vstr.insertUnsignedLong( msg.getYukonID().longValue() );
	vstr.insertUnsignedLong( msg.getOfferID().longValue() );
	vstr.insertUnsignedLong( msg.getRevisionNumber().longValue() );
	vstr.saveObject( msg.getAcceptStatus(), SimpleMappings.CString );
	vstr.saveObject( msg.getIpAddressOfCustomer(), SimpleMappings.CString );
	vstr.saveObject( msg.getUserIDName(), SimpleMappings.CString );
	vstr.saveObject( msg.getNameOfAcceptPerson(), SimpleMappings.CString );
	vstr.saveObject( msg.getEnergyExchangeNotes(), SimpleMappings.CString );

	Double[] amounts = msg.getAmountCommitted();

	for( int i = 0; i < 24; i++ )
	{
		vstr.insertDouble(amounts[i].doubleValue());
	}
}
}
