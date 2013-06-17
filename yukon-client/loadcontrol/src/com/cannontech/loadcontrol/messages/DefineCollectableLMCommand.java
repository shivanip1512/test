package com.cannontech.loadcontrol.messages;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;

public class DefineCollectableLMCommand extends DefineCollectableLMMessage
{
	//RogueWave classId
	public static final int CTILMCOMMAND_ID = 601;
/**
 * DefineCollectableScheduleCommand constructor comment.
 */
public DefineCollectableLMCommand()
{
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new LMCommand();
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
	return CTILMCOMMAND_ID;
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
	return LMCommand.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	//Should never be called
	super.restoreGuts( obj, vstr, polystr );
	throw new java.lang.Error("LMCommand restoreGuts should never be receieved by the client" );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	LMCommand cmd = (LMCommand) obj;

	vstr.insertUnsignedInt( (long) cmd.getCommand() );
	vstr.insertUnsignedInt( (long) cmd.getYukonID() );
	vstr.insertUnsignedInt( (long) cmd.getNumber() );
	vstr.insertDouble( cmd.getValue() );
	
	vstr.insertUnsignedInt( (long) cmd.getCount() );
	vstr.insertUnsignedInt( (long) cmd.getAuxid() );	
}
}
