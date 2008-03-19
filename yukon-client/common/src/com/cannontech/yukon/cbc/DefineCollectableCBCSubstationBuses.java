package com.cannontech.yukon.cbc;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

import com.cannontech.message.util.VectorExtract;
import com.roguewave.tools.v2_0.Comparator;

public class DefineCollectableCBCSubstationBuses extends DefineCollectableCBCMessage 
{
	//RogueWave classId
	public static final int CTI_CCSUBSTATIONBUS_MSG_ID = 501;
/**
 * DefineCollectableMessage constructor comment.
 */
public DefineCollectableCBCSubstationBuses() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new CCSubstationBuses();
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
	return CTI_CCSUBSTATIONBUS_MSG_ID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass() {
	return CCSubstationBuses.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );

	CCSubstationBuses cbcSubBuses = (CCSubstationBuses) obj;

	cbcSubBuses.setMsgInfoBitMask( new Integer( (int)vstr.extractUnsignedInt() ) );
    
    Vector strategyStore = VectorExtract.extractVector(vstr,polystr);

	cbcSubBuses.setSubBuses( strategyStore );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	com.cannontech.clientutils.CTILogger.info("**********************************************************************************");
	com.cannontech.clientutils.CTILogger.info("com.cannontech.cbc.DefineCollectableCBCStratgegies.saveGuts() should not be called");
	com.cannontech.clientutils.CTILogger.info("**********************************************************************************");
	//super.saveGuts( obj, vstr, polystr );
	//vstr.insertUnsignedInt( ((CBCStrategies) obj).getMs().intValue() );
	//java.util.Vector strategyStore = ((CBCStrategies) obj).getStrategies();
	//vstr.saveObject( strategyStore, com.roguewave.vsj.streamer.CollectableMappings.allCollectables );	
}
}
