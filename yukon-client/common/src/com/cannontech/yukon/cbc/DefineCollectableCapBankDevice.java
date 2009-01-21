package com.cannontech.yukon.cbc;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:21:47 PM)
 * @author: 
 */
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableCapBankDevice extends DefineCollectableStreamableCapObject
{
	//The roguewave class id
	private static int CTI_CCCAPBANK_ID = 504;
/**
 * DefineCollectableCapBankVector constructor comment.
 */
public DefineCollectableCapBankDevice() {
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new CapBankDevice();
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId() {
	return DefineCollectableCapBankDevice.CTI_CCCAPBANK_ID;
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
public Class<CapBankDevice> getJavaClass() {
	return CapBankDevice.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );
	
	CapBankDevice capBank = (CapBankDevice) obj;

	capBank.setMaxDailyOperation( new Integer( (int)vstr.extractUnsignedInt() ) );
	capBank.setMaxOperationDisableFlag( 
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );

	capBank.setAlarmInhibit( new Integer( (int)vstr.extractUnsignedInt() ) );
	capBank.setControlInhibit( new Integer( (int)vstr.extractUnsignedInt() ) );
	capBank.setOperationalState( (String) vstr.restoreObject( SimpleMappings.CString ) );
	capBank.setControllerType( (String) vstr.restoreObject( SimpleMappings.CString ) );
	capBank.setControlDeviceID( new Integer( (int)vstr.extractUnsignedInt() ) );
	capBank.setBankSize( new Integer( (int)vstr.extractUnsignedInt() ) );
	capBank.setTypeOfSwitch( (String) vstr.restoreObject( SimpleMappings.CString ) );
	capBank.setSwitchManufacture( (String) vstr.restoreObject( SimpleMappings.CString ) );	
	capBank.setMapLocationID((String) vstr.restoreObject( SimpleMappings.CString ) );
	capBank.setRecloseDelay( new Integer( (int)vstr.extractUnsignedInt() ) );
	
	capBank.setControlOrder( new Float(vstr.extractFloat() ) );
	
	capBank.setStatusPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	capBank.setControlStatus( new Integer( (int)vstr.extractUnsignedInt() ) );
	capBank.setOperationAnalogPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	capBank.setTotalOperations( new Integer( (int)vstr.extractUnsignedInt() ) );	
	capBank.setLastStatusChangeTime( (java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );	
	capBank.setTagControlStatus( new Integer( (int)vstr.extractUnsignedInt() ) );

	capBank.setOrigFeederID( (int)vstr.extractUnsignedInt() );
	capBank.setCurrentDailyOperations( new Integer( (int)vstr.extractUnsignedInt() ) );	
	capBank.setIgnoreFlag(new Boolean( (int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	capBank.setIgnoreReason(new Integer( (int) vstr.extractUnsignedInt() ) );
	capBank.setOvUVDisabled(new Boolean ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
    capBank.setTripOrder( new Float(vstr.extractFloat() ) );
    capBank.setCloseOrder( new Float(vstr.extractFloat() ) );
    capBank.setControlDeviceType( (String) vstr.restoreObject( SimpleMappings.CString ) );
    capBank.setBeforeVars( (String) vstr.restoreObject( SimpleMappings.CString ) );
    capBank.setAfterVars( (String) vstr.restoreObject( SimpleMappings.CString ) );
    capBank.setPercentChange( (String) vstr.restoreObject( SimpleMappings.CString ) );
    capBank.setMaxDailyOperationHitFlag(new Boolean ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
    capBank.setOvuvSituationFlag(new Boolean ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
    capBank.setControlStatusQuality( new Integer( (int)vstr.extractUnsignedInt() ) );
    capBank.setLocalControlFlag(new Boolean ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
    capBank.setPartialPhaseInfo( (String) vstr.restoreObject( SimpleMappings.CString ) );
	
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	CapBankDevice capBank = (CapBankDevice) obj;

	vstr.insertUnsignedInt( capBank.getMaxDailyOperation().intValue() );
	vstr.insertUnsignedInt( 
		(capBank.getMaxOperationDisableFlag().booleanValue() == true)
		? 1 : 0 );

	vstr.insertUnsignedInt( capBank.getAlarmInhibit().intValue() );
	vstr.insertUnsignedInt( capBank.getControlInhibit().intValue() );
	vstr.saveObject( capBank.getOperationalState(), SimpleMappings.CString );
	vstr.saveObject( capBank.getControllerType(), SimpleMappings.CString );
	vstr.insertUnsignedInt( capBank.getControlDeviceID().intValue() );

	vstr.insertUnsignedInt( capBank.getBankSize().intValue() );
	vstr.saveObject( capBank.getTypeOfSwitch(), SimpleMappings.CString );
	vstr.saveObject( capBank.getSwitchManufacture(), SimpleMappings.CString );
	vstr.saveObject( capBank.getMapLocationID(), SimpleMappings.CString );
	vstr.insertUnsignedInt( capBank.getRecloseDelay().intValue() );
	
	vstr.insertFloat( capBank.getControlOrder().floatValue() );	
	vstr.insertUnsignedInt( capBank.getStatusPointID().intValue() );
	vstr.insertUnsignedInt( capBank.getControlStatus().intValue() );
	vstr.insertUnsignedInt( capBank.getOperationAnalogPointID().intValue() );
	vstr.insertUnsignedInt( capBank.getTotalOperations().intValue() );
	vstr.saveObject( capBank.getLastStatusChangeTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( capBank.getTagControlStatus().intValue() );

	vstr.insertUnsignedInt( capBank.getOrigFeederID() );	
	vstr.insertUnsignedInt( capBank.getCurrentDailyOperations().intValue() );
	vstr.insertUnsignedInt( 
			(capBank.isIgnoreFlag().booleanValue() == true)
			? 1 : 0 );
	vstr.insertUnsignedInt( capBank.getIgnoreReason().intValue() );
    vstr.insertUnsignedInt( 
                        (capBank.getOvUVDisabled().booleanValue() == true)
                        ? 1 : 0 );
    vstr.insertFloat( capBank.getTripOrder().floatValue() ); 
    vstr.insertFloat( capBank.getCloseOrder().floatValue() ); 
    vstr.saveObject( capBank.getControlDeviceType(), SimpleMappings.CString);
    vstr.saveObject( capBank.getBeforeVars(), SimpleMappings.CString);
    vstr.saveObject( capBank.getAfterVars(), SimpleMappings.CString);
    vstr.saveObject( capBank.getPercentChange(), SimpleMappings.CString);
    vstr.insertUnsignedInt( (capBank.getMaxDailyOperationHitFlag() == true)?1:0);
    vstr.insertUnsignedInt( (capBank.getOvuvSituationFlag() == true)?1:0);
    vstr.insertUnsignedInt( capBank.getControlStatusQuality().intValue() );
    vstr.insertUnsignedInt( (capBank.getLocalControlFlag() == true)?1:0);
    vstr.saveObject( capBank.getPartialPhaseInfo(), SimpleMappings.CString);
}
}
