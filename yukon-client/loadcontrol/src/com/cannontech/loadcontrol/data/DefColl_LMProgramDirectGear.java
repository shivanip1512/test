package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMProgramDirectGear implements com.roguewave.vsj.DefineCollectable
{
	//The roguewave class id
	private static int CTILMPROGRAMDIRECTGEAR_ID = 607;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMProgramDirectGear()
{
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMProgramDirectGear();
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
			return (int) (((LMProgramDirectGear)x).getYukonID().intValue() - ((LMProgramDirectGear)y).getYukonID().intValue() );
		}
	};
	
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	return CTILMPROGRAMDIRECTGEAR_ID;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId()
{
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass()
{
	return LMProgramDirectGear.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	LMProgramDirectGear lmProgramDirectGear = (LMProgramDirectGear) obj;

	Integer yukonID = new Integer( (int)vstr.extractUnsignedInt() );
	String gearName = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer gearNumber = new Integer( (int)vstr.extractUnsignedInt() );
	String controlMethod = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer methodRate = new Integer( (int)vstr.extractUnsignedInt() );
	Integer methodPeriod = new Integer( (int)vstr.extractUnsignedInt() );
	Integer methodRateCount = new Integer( (int)vstr.extractUnsignedInt() );
	Integer cycleRefreshRate = new Integer( (int)vstr.extractUnsignedInt() );
	String methodStopType = (String) vstr.restoreObject( SimpleMappings.CString );
	String changeCondition = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer changeDuration = new Integer( (int)vstr.extractUnsignedInt() );
	Integer changePriority = new Integer( (int)vstr.extractUnsignedInt() );
	Integer changeTriggerNumber = new Integer( (int)vstr.extractUnsignedInt() );
	Double changeTriggerOffset = new Double(vstr.extractDouble());
	Integer percentReduction = new Integer( (int)vstr.extractUnsignedInt() );
	String groupSelectionMethod = (String) vstr.restoreObject( SimpleMappings.CString );
	String methodOptionType = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer methodOptionMax = new Integer( (int)vstr.extractUnsignedInt() );
	Integer rampInInterval = new Integer( (int) vstr.extractUnsignedInt() );
	Integer rampInPercent = new Integer ( (int) vstr.extractUnsignedInt() );
	Integer rampOutInterval = new Integer( (int) vstr.extractUnsignedInt() );
	Integer rampOutPercent = new Integer( (int) vstr.extractUnsignedInt() );

	lmProgramDirectGear.setYukonID(yukonID);
	lmProgramDirectGear.setGearName(gearName);
	lmProgramDirectGear.setGearNumber(gearNumber);
	lmProgramDirectGear.setControlMethod(controlMethod);
	lmProgramDirectGear.setMethodRate(methodRate);
	lmProgramDirectGear.setMethodPeriod(methodPeriod);
	lmProgramDirectGear.setMethodRateCount(methodRateCount);
	lmProgramDirectGear.setCycleRefreshRate(cycleRefreshRate);
	lmProgramDirectGear.setMethodStopType(methodStopType);
	lmProgramDirectGear.setChangeCondition(changeCondition);
	lmProgramDirectGear.setChangeDuration(changeDuration);
	lmProgramDirectGear.setChangePriority(changePriority);
	lmProgramDirectGear.setChangeTriggerNumber(changeTriggerNumber);
	lmProgramDirectGear.setChangeTriggerOffset(changeTriggerOffset);
	lmProgramDirectGear.setPercentReduction(percentReduction);
	lmProgramDirectGear.setGroupSelectionMethod(groupSelectionMethod);
	lmProgramDirectGear.setMethodOptionType( methodOptionType );
	lmProgramDirectGear.setMethodOptionMax( methodOptionMax );
	lmProgramDirectGear.setRampInInterval( rampInInterval );
	lmProgramDirectGear.setRampInPercent( rampInPercent );
	lmProgramDirectGear.setRampOutInterval( rampOutInterval );
	lmProgramDirectGear.setRampOutPercent( rampOutPercent );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
/* This saveGuts isn't implemented because we won't be sending full LMControlAreas
	 to the Server */
}
}
