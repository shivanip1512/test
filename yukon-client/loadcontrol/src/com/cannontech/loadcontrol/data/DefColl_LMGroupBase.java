package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMGroupBase implements com.roguewave.vsj.DefineCollectable 
{
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMGroupBase() {
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
	Exception e = new Exception("com.cannontech.loadcontrol.data.DefineCollectableLMProgramBase.getCxxClassId() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return -1;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId()
{
	Exception e = new Exception("com.cannontech.loadcontrol.data.DefineCollectableLMProgramBase.getCxxStringId() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass()
{
	Exception e = new Exception("com.cannontech.loadcontrol.data.DefineCollectableLMProgramBase.getJavaClass() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return LMGroupBase.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	LMGroupBase lmGroupBase = (LMGroupBase) obj;

	lmGroupBase.setYukonID( new Integer((int)vstr.extractUnsignedInt()) );
	lmGroupBase.setYukonCategory( (String) vstr.restoreObject(SimpleMappings.CString) );
	lmGroupBase.setYukonClass( (String) vstr.restoreObject(SimpleMappings.CString) );
	lmGroupBase.setYukonName( (String) vstr.restoreObject(SimpleMappings.CString) );
	lmGroupBase.setYukonType( new Integer((int)vstr.extractUnsignedInt()) );
	lmGroupBase.setYukonDescription( (String)vstr.restoreObject(SimpleMappings.CString) );
	lmGroupBase.setDisableFlag( new Boolean((int)vstr.extractUnsignedInt() > 0) );
	lmGroupBase.setGroupOrder( new Integer((int)vstr.extractUnsignedInt()) );	
	lmGroupBase.setKwCapacity( new Double(vstr.extractDouble()) );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {

/* This saveGuts isn't implemented because we won't be sending full LMControlAreas
	 to the Server */

	/*LMControlArea lmControlArea = (LMControlArea) obj;
	
	vstr.insertUnsignedInt( strategy.getCapStrategyID().intValue() );
	vstr.saveObject( strategy.getStrategyName(), SimpleMappings.CString );
	vstr.saveObject( strategy.getAreaName(), SimpleMappings.CString );
	vstr.insertUnsignedInt( strategy.getActualVarPointID().intValue() );
	vstr.insertDouble( strategy.getActualVarPointValue().doubleValue() );
	vstr.insertUnsignedInt( strategy.getMaxDailyOperation().intValue() );
	vstr.insertDouble( strategy.getPeakSetPoint().doubleValue() );
	vstr.insertDouble( strategy.getOffPeakSetPoint().doubleValue() );
	vstr.saveObject( strategy.getPeakStartTime().getTime(), SimpleMappings.Time );
	vstr.saveObject( strategy.getPeakStopTime().getTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( strategy.getCalculatedVarPointID().intValue() );
	vstr.insertDouble( strategy.getCalculatedVarPointValue().doubleValue() );
	vstr.insertDouble( strategy.getBandwidth().doubleValue() );
	vstr.insertUnsignedInt( strategy.getControlInterval().intValue() );
	vstr.insertUnsignedInt( strategy.getMinResponseTime().intValue() );
	vstr.insertUnsignedInt( strategy.getMinConfirmPercent().intValue() );
	vstr.insertUnsignedInt( strategy.getFailurePercent().intValue() );
	vstr.saveObject( strategy.getStatus(), SimpleMappings.CString );
	vstr.insertUnsignedInt( strategy.getOperations().intValue() );
	vstr.saveObject( strategy.getLastOperationTime().getTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( strategy.getLastCapBankControlled().intValue() );
	vstr.saveObject( strategy.getDaysOfWeek(), SimpleMappings.CString );
	vstr.insertUnsignedInt( strategy.getPeakOrOffPeak().intValue() );
	vstr.insertUnsignedInt( strategy.getRecentlyControlled().intValue() );
	vstr.insertDouble( strategy.getCalculatedValueBeforeControl().doubleValue() );
	vstr.saveObject( strategy.getLastControlTime().getTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( strategy.getDecimalPlaces().intValue() );
	vstr.saveObject( ((java.util.Vector)strategy.getCapBankDeviceVector()), polystr );*/
}
}
