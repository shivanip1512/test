package com.cannontech.yukon.cbc;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;

public class DefineCollectableCBCSubAreaName extends DefineCollectableCBCMessage
{
	//RogueWave classId
	public static final int CTIAREA_MESSAGE_ID = 509;
/**
 * DefineCollectableMessage constructor comment.
 */
public DefineCollectableCBCSubAreaName() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new CBCSubAreaNames();
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
	return this.CTIAREA_MESSAGE_ID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass() {
	return com.cannontech.yukon.cbc.CBCSubAreaNames.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {

	super.restoreGuts( obj, vstr, polystr );
	java.util.Vector areaNames = (java.util.Vector)vstr.restoreObject( polystr );

	((CBCSubAreaNames)obj).setAreaNames( areaNames );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	com.cannontech.clientutils.CTILogger.info("**********************************************************************************");
	com.cannontech.clientutils.CTILogger.info("com.cannontech.cbc.DefineCollectableCBCStrategyAreaMessage.saveGuts() should not be called");
	com.cannontech.clientutils.CTILogger.info("**********************************************************************************");
	
	//super.saveGuts( obj, vstr, polystr );
	//java.util.Vector areaStore = ((CBCAreaNames) obj).getAreaNames();
	//vstr.saveObject( areaStore, com.roguewave.vsj.streamer.CollectableMappings.allCollectables );
}
}
