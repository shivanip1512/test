package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import java.util.Date;

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMDirectGroupBase extends DefColl_LMGroupBase
{
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMDirectGroupBase() {
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	throw new RuntimeException();
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
			return (int) (((LMGroupBase)x).getYukonID().intValue() - ((LMGroupBase)y).getYukonID().intValue() );
		}
	};
	
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	Exception e = new Exception(this.getClass().getName() + ".getCxxClassId() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return -1;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId()
{
	Exception e = new Exception(this.getClass().getName() + ".getCxxStringId() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass()
{
	Exception e = new Exception(this.getClass().getName() + ".getJavaClass() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return LMDirectGroupBase.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );

	LMDirectGroupBase lmDirectGroup = (LMDirectGroupBase) obj;
	
	lmDirectGroup.setChildOrder( new Integer( (int)vstr.extractUnsignedInt() ) );
	lmDirectGroup.setAlarmInhibit( new Boolean( ((int)vstr.extractUnsignedInt())>0 ) );
	lmDirectGroup.setControlInhibit( new Boolean( ((int)vstr.extractUnsignedInt())>0 ) );
	lmDirectGroup.setGroupControlState( new Integer( (int)vstr.extractUnsignedInt() ) );
	lmDirectGroup.setCurrentHoursDaily( new Integer( (int)vstr.extractUnsignedInt() ) );
	lmDirectGroup.setCurrentHoursMonthly( new Integer( (int)vstr.extractUnsignedInt() ) );
	lmDirectGroup.setCurrentHoursSeasonal( new Integer( (int)vstr.extractUnsignedInt() ) );
	lmDirectGroup.setCurrentHoursAnnually( new Integer( (int)vstr.extractUnsignedInt() ) );
	
	java.util.GregorianCalendar lastControlSent = new java.util.GregorianCalendar();
	lastControlSent.setTime((java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	lmDirectGroup.setLastControlSent( lastControlSent );
	
	lmDirectGroup.setControlStartTime( (Date)vstr.restoreObject( SimpleMappings.Time ) );
	lmDirectGroup.setControlCompleteTime( (Date)vstr.restoreObject( SimpleMappings.Time ) );
	lmDirectGroup.setNextControlTime( (Date)vstr.restoreObject( SimpleMappings.Time ));
	lmDirectGroup.setInternalState( (int) vstr.extractUnsignedInt());	

}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

/* This saveGuts isn't implemented because we won't be sending full LMControlAreas
	 to the Server */
}
}
