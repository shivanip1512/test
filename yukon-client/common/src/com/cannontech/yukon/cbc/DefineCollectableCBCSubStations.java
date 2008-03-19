package com.cannontech.yukon.cbc;

import java.util.Vector;

import com.cannontech.message.util.VectorExtract;
import com.roguewave.tools.v2_0.Comparator;

public class DefineCollectableCBCSubStations extends DefineCollectableCBCMessage 
{
	public static final int CTI_CCSUBSTATION_MSG_ID = 525;

	public DefineCollectableCBCSubStations() {
		super();
	}

	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new CCSubStations();
	}

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

	public int getCxxClassId() {
		return CTI_CCSUBSTATION_MSG_ID;
	}

	public Class getJavaClass() {
		return CCSubStations.class;
	}

	public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
	{
		super.restoreGuts( obj, vstr, polystr );

		CCSubStations cbcSub = (CCSubStations) obj;
		cbcSub.setMsgInfoBitMask( new Integer( (int)vstr.extractUnsignedInt() ) );
	    Vector stations = VectorExtract.extractVector(vstr,polystr);

		cbcSub.setSubStations( stations );
	}

	public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
	{
		com.cannontech.clientutils.CTILogger.info("**********************************************************************************");
		com.cannontech.clientutils.CTILogger.info("com.cannontech.cbc.DefineCollectableCBCSubStations.saveGuts() should not be called");
		com.cannontech.clientutils.CTILogger.info("**********************************************************************************");
	}
}
