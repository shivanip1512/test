package com.cannontech.loadcontrol.messages;

/**
 * This type was created in VisualAge.
 */
import java.util.List;

import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.messaging.message.loadcontrol.ManualControlResponseMessage;
import com.cannontech.messaging.message.loadcontrol.data.ConstraintViolation;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableLMManualControlResponse extends DefineCollectableLMMessage
{
	//RogueWave classId
	public static final int CTILMMANUALCONTROLRESPONSE_ID = 613;
/**
 * DefineCollectableScheduleCommand constructor comment.
 */
public DefineCollectableLMManualControlResponse()
{
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new ManualControlResponseMessage();
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
	return CTILMMANUALCONTROLRESPONSE_ID;
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
public Class<?> getJavaClass() {
	return ManualControlResponseMessage.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
	super.restoreGuts( obj, vstr, polystr );
	ManualControlResponseMessage lmManualControlResponse = (ManualControlResponseMessage) obj;
	int programID = (int) vstr.extractUnsignedInt();
	//Vector v = (Vector) vstr.restoreObject(polystr);
    List<ConstraintViolation> v = CollectionExtracter.extractList(vstr, polystr, ConstraintViolation.class);
	String bestFitAction = (String) vstr.restoreObject( SimpleMappings.CString );
	
	lmManualControlResponse.setConstraintViolations(v);
	lmManualControlResponse.setProgramId(programID);
	lmManualControlResponse.setBestFitAction(bestFitAction);								
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );
	//	Should never be called
	  throw new java.lang.Error("LMManualControlResponse restoreGuts should never be receieved by the client" );
}
}
