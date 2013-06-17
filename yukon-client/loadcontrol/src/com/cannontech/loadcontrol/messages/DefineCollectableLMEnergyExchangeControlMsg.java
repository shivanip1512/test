package com.cannontech.loadcontrol.messages;

/**
 * Creation date: (5/29/2001 11:40:59 AM)
 * @author: Aaron Lauinger
 */

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableLMEnergyExchangeControlMsg extends DefineCollectableLMMessage 
{
	//RogueWave classId
	public static final int RW_CLASS_ID = 623;
/**
 * DefineCollectableLMEnergyExchangeControlMsg constructor comment.
 */
public DefineCollectableLMEnergyExchangeControlMsg() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new LMEnergyExchangeControlMsg();
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
	return LMEnergyExchangeControlMsg.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	//Should never be called
	throw new java.lang.Error("LMEnergyExchangeControlMsg restoreGuts should never be receieved by the client" );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	LMEnergyExchangeControlMsg msg = (LMEnergyExchangeControlMsg) obj;

	vstr.insertUnsignedInt( msg.getCommand().longValue() );
	vstr.insertUnsignedLong( msg.getYukonID().longValue() );
	vstr.insertUnsignedLong( msg.getOfferID().longValue() );
	vstr.saveObject( msg.getOfferDate(), SimpleMappings.Time );
	vstr.saveObject( msg.getNotificationDateTime(), SimpleMappings.Time );
	vstr.saveObject( msg.getExpirationDateTime(), SimpleMappings.Time );
	vstr.saveObject( msg.getAdditionalInfo(), SimpleMappings.CString );

	Double[] amounts = msg.getAmountRequested();
	for( int i = 0; i < amounts.length; i++ )
	{
		vstr.insertDouble(amounts[i].doubleValue());
	}

	Integer[] prices = msg.getPricesOffered();
	for( int i = 0; i < prices.length; i++ )
	{
		vstr.insertUnsignedLong(prices[i].longValue());
	}
}
}
