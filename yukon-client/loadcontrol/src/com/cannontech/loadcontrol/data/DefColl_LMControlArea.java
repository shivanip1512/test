package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

import com.cannontech.common.pao.PaoType;
import com.cannontech.message.util.CollectionExtracter;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMControlArea implements com.roguewave.vsj.DefineCollectable
{
	//The roguewave class id
	private static int CTILMCONTROLAREA_ID = 604;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMControlArea() {
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMControlArea();
}
/**
 * getComparator method comment.
 */
public com.roguewave.tools.v2_0.Comparator getComparator() 
{
	return new Comparator() 
	{
		public int compare(Object x, Object y) 
		{
			return (int) (((LMControlArea)x).getYukonID().intValue() - ((LMControlArea)y).getYukonID().intValue() );
		}
	};
	
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId() {
	return DefColl_LMControlArea.CTILMCONTROLAREA_ID;
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
	return LMControlArea.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	LMControlArea lmControlArea = (LMControlArea) obj;
	
	Integer yukonID = new Integer( (int)vstr.extractUnsignedInt() );
	String yukonCategory = (String) vstr.restoreObject( SimpleMappings.CString );	// No longer used, but still needs to be restored. Replaced by PaoType.paoCategory
	String yukonClass = (String) vstr.restoreObject( SimpleMappings.CString );	// No longer used, but still needs to be restored. Replaced by PaoType.paoClass
	String yukonName = (String) vstr.restoreObject( SimpleMappings.CString );
	String yukonType = (String) vstr.restoreObject( SimpleMappings.CString );
	String yukonDescription = (String) vstr.restoreObject( SimpleMappings.CString );
	int disableFlag = (int)vstr.extractUnsignedInt();
	String defOperationalState = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer controlInterval = new Integer( (int)vstr.extractUnsignedInt() );
	Integer minResponseTime = new Integer( (int)vstr.extractUnsignedInt() );
	Integer defDailyStartTime = new Integer( (int)vstr.extractUnsignedInt() );
	Integer defDailyStopTime = new Integer( (int)vstr.extractUnsignedInt() );
	int requireAllTriggersActiveFlag = (int)vstr.extractUnsignedInt();
	java.util.GregorianCalendar nextCheckTime = new java.util.GregorianCalendar();
	nextCheckTime.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	int newPointDataReceivedFlag = (int)vstr.extractUnsignedInt();
	int updatedFlag = (int)vstr.extractUnsignedInt();
	Integer controlAreaStatusPointId = new Integer( (int)vstr.extractUnsignedInt() );
	Integer controlAreaState = new Integer( (int)vstr.extractUnsignedInt() );
	Integer currentPriority = new Integer( (int)vstr.extractUnsignedInt() );
	Integer currentDailyStartTime = new Integer( (int)vstr.extractUnsignedInt() );
	Integer currentDailyStopTime = new Integer( (int)vstr.extractUnsignedInt() );
    Vector triggerVector = CollectionExtracter.extractVector(vstr, polystr);
    Vector lmProgramVector = CollectionExtracter.extractVector(vstr, polystr);

	lmControlArea.setYukonID(yukonID);
	lmControlArea.setYukonName(yukonName);
	lmControlArea.setYukonType(PaoType.getForDbString(yukonType));
	lmControlArea.setYukonDescription(yukonDescription);
	lmControlArea.setDisableFlag(new Boolean(disableFlag>0));
	lmControlArea.setDefOperationalState(defOperationalState);
	lmControlArea.setControlInterval(controlInterval);
	lmControlArea.setMinResponseTime(minResponseTime);
	lmControlArea.setDefDailyStartTime(defDailyStartTime);
	lmControlArea.setDefDailyStopTime(defDailyStopTime);
	lmControlArea.setRequireAllTriggersActiveFlag(new Boolean(requireAllTriggersActiveFlag>0));
	lmControlArea.setNextCheckTime(nextCheckTime);
	lmControlArea.setNewPointDataReceivedFlag(new Boolean(newPointDataReceivedFlag>0));
	lmControlArea.setUpdatedFlag(new Boolean(updatedFlag>0));
	lmControlArea.setControlAreaStatusPointId(controlAreaStatusPointId);
	lmControlArea.setControlAreaState(controlAreaState);
	lmControlArea.setCurrentPriority(currentPriority);
	lmControlArea.setCurrentDailyStartTime(currentDailyStartTime);
	lmControlArea.setCurrentDailyStopTime(currentDailyStopTime);
	lmControlArea.setTriggerVector(triggerVector);
	lmControlArea.setLmProgramVector(lmProgramVector);
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {

/* This saveGuts isn't implemented because we won't be sending full LMControlAreas
	 to the Server */

	/*LMControlArea lmControlArea = (LMControlArea) obj; */

}
}
