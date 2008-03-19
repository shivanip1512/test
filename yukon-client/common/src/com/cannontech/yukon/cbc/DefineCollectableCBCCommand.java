package com.cannontech.yukon.cbc;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;

public class DefineCollectableCBCCommand extends DefineCollectableCBCMessage {

	//RogueWave classId
	public static final int CTICCCOMMAND_ID = 505;
/**
 * DefineCollectableScheduleCommand constructor comment.
 */
public DefineCollectableCBCCommand() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new CapControlCommand();
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
	return CTICCCOMMAND_ID;
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
	return CapControlCommand.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.restoreGuts( obj, vstr, polystr );
	
	CapControlCommand cmd = (CapControlCommand) obj;

	cmd.setCommand( (int)vstr.extractUnsignedInt() );
	cmd.setDeviceID( (int)vstr.extractUnsignedInt() );
}

/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	CapControlCommand cmd = (CapControlCommand) obj;

	vstr.insertUnsignedInt( (long) cmd.getCommand() );
	vstr.insertUnsignedInt( (long) cmd.getDeviceID() );
}
}
