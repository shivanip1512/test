package com.cannontech.loadcontrol.messages;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableLMManualControlRequest extends DefineCollectableLMMessage
{
	//RogueWave classId
	public static final int CTILMMANUALCONTROLREQUEST_ID = 612;
/**
 * DefineCollectableScheduleCommand constructor comment.
 */
public DefineCollectableLMManualControlRequest()
{
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMManualControlRequest();
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
	return CTILMMANUALCONTROLREQUEST_ID;
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
	return LMManualControlRequest.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	//Should never be called
	super.restoreGuts( obj, vstr, polystr );
	throw new java.lang.Error("LMManualControlRequest restoreGuts should never be receieved by the client" );
}
/** 
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	LMManualControlRequest lmManualControlMsg = (LMManualControlRequest) obj;

	vstr.insertUnsignedInt( (long) lmManualControlMsg.getCommand() );
	vstr.insertUnsignedInt( (long) lmManualControlMsg.getYukonID() );
	vstr.saveObject( lmManualControlMsg.getNotifyTime().getTime(), SimpleMappings.Time );
	vstr.saveObject( lmManualControlMsg.getStartTime().getTime(), SimpleMappings.Time );
	vstr.saveObject( lmManualControlMsg.getStopTime().getTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( (long) lmManualControlMsg.getStartGear() );
	vstr.insertUnsignedInt( (long) lmManualControlMsg.getStartPriority() );
	vstr.saveObject( lmManualControlMsg.getAddditionalInfo(), SimpleMappings.CString );	
	vstr.insertUnsignedInt( (lmManualControlMsg.isOverrideConstraints() ? 1 : 0) );
	vstr.insertUnsignedInt( (lmManualControlMsg.isCoerceStartStopTimes() ? 1 : 0) );	
}
}
