package com.cannontech.yukon.cbc;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:21:47 PM)
 * @author: 
 */
import com.cannontech.message.util.VectorExtract;
import com.cannontech.message.util.VectorInsert;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableSubBus extends DefineCollectableStreamableCapObject
{
	//The roguewave class id
	private static int CTI_CCSUBSTATIONBUS_ID = 502;
/**
 * DefineCollectableCapBankVector constructor comment.
 */
public DefineCollectableSubBus() {
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new SubBus();
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId() {
	return CTI_CCSUBSTATIONBUS_ID;
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
	return SubBus.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );
	
	SubBus subBus = (SubBus) obj;
	
//	subBus.setControlMethod( (String) vstr.restoreObject( SimpleMappings.CString ) );
	subBus.setMaxDailyOperation( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setMaxOperationDisableFlag( 
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );
	
//	subBus.setPeakStartTime( new Integer( (int)vstr.extractUnsignedInt() ) );
//	subBus.setPeakStopTime( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setCurrentVarLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setCurrentVarLoadPointValue( new Double( vstr.extractDouble() ) );
	subBus.setCurrentWattLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setCurrentWattLoadPointValue( new Double( vstr.extractDouble() ) );
	
//	subBus.setControlInterval( new Integer( (int)vstr.extractUnsignedInt() ) );
//	subBus.setMinRepsonseTime( new Integer( (int)vstr.extractUnsignedInt() ) );
//	subBus.setMinConfirmPercent( new Integer( (int)vstr.extractUnsignedInt() ) );	
//	subBus.setFailurePercent( new Integer( (int)vstr.extractUnsignedInt() ) );
//	subBus.setDaysOfWeek( (String) vstr.restoreObject( SimpleMappings.CString ) );
	subBus.setMapLocationID( (String) vstr.restoreObject( SimpleMappings.CString ) );
	subBus.setControlUnits( (String) vstr.restoreObject( SimpleMappings.CString ) );
//	subBus.setControlDelayTime( new Integer( (int)vstr.extractUnsignedInt() ) );
//	subBus.setControlSendRetries( new Integer( (int)vstr.extractUnsignedInt() ) );
   
	subBus.setDecimalPlaces( new Integer( (int)vstr.extractUnsignedInt() ) );
//	subBus.setNextCheckTime( (java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	
	subBus.setNewPointDataReceivedFlag( 
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );
	subBus.setBusUpdateFlag( 
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );

	subBus.setLastCurrentVarPointUpdateTime( (java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	subBus.setEstimatedVarLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setEstimatedVarLoadPointValue( new Double( vstr.extractDouble() ) );


	subBus.setDailyOperationsAnalogPointId( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setPowerFactorPointId( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setEstimatedPowerFactorPointId( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setCurrentDailyOperations( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setPeakTimeFlag( 
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );

	subBus.setRecentlyControlledFlag( 
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );

	subBus.setLastOperationTime( (java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	subBus.setVarValueBeforeControl( new Double( vstr.extractDouble() ) );
//	subBus.setLastFeederControlledPAOID( new Integer( (int)vstr.extractUnsignedInt() ) );
//	subBus.setLastFeederControlledPosition( new Integer( (int)vstr.extractUnsignedInt() ) );

   subBus.setPowerFactorValue( new Double( vstr.extractDouble() ) );

//   subBus.setKVarSolution( new Double( vstr.extractDouble() ) );
   subBus.setEstimatedPFValue( new Double( vstr.extractDouble() ) );
   subBus.setCurrentVarPtQuality( new Integer( (int)vstr.extractUnsignedInt() ) );

	subBus.setWaiveControlFlag( 
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );
//	subBus.setAdditionalFlags( (String) vstr.restoreObject( SimpleMappings.CString ) );

	subBus.setPeakLag( new Double( vstr.extractDouble() ) );
	subBus.setOffPkLag( new Double( vstr.extractDouble() ) );
	subBus.setPeakLead( new Double( vstr.extractDouble() ) );
	subBus.setOffPkLead( new Double( vstr.extractDouble() ) );
	subBus.setCurrentVoltLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setCurrentVoltLoadPointValue( new Double( vstr.extractDouble() ) );

	subBus.setVerificationFlag( 
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );
		
	subBus.setSwitchOverStatus( 
			((int)vstr.extractUnsignedInt() == 1)
			? new Boolean(true) : new Boolean(false) );
	subBus.setCurrentwattpointquality(new Integer( vstr.extractInt() ));
	subBus.setCurrentvoltpointquality(new Integer( vstr.extractInt() ));
	subBus.setTargetvarvalue( new Double( vstr.extractDouble() ));
	subBus.setSolution((String) vstr.restoreObject( SimpleMappings.CString ));
	subBus.setCcFeeders( VectorExtract.extractVector(vstr, polystr));
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	SubBus subBus = (SubBus)obj;

//	vstr.saveObject( subBus.getControlMethod(), SimpleMappings.CString );
	vstr.insertUnsignedInt( subBus.getMaxDailyOperation().intValue() );
	vstr.insertUnsignedInt( 
		(subBus.getMaxOperationDisableFlag().booleanValue() == true)
		? 1 : 0 );
	
//	vstr.insertUnsignedInt( subBus.getPeakStartTime().intValue() );
//	vstr.insertUnsignedInt( subBus.getPeakStopTime().intValue() );
	vstr.insertUnsignedInt( subBus.getCurrentVarLoadPointID().intValue() );
	vstr.insertDouble( subBus.getCurrentVarLoadPointValue().doubleValue() );
	vstr.insertUnsignedInt( subBus.getCurrentWattLoadPointID().intValue() );
	vstr.insertDouble( subBus.getCurrentWattLoadPointValue().doubleValue() );
//	vstr.insertUnsignedInt( subBus.getControlInterval().intValue() );
//	vstr.insertUnsignedInt( subBus.getMinRepsonseTime().intValue() );
//	vstr.insertUnsignedInt( subBus.getMinConfirmPercent().intValue() );
//	vstr.insertUnsignedInt( subBus.getFailurePercent().intValue() );
//	
//	vstr.saveObject( subBus.getDaysOfWeek(), SimpleMappings.CString );
	vstr.saveObject( subBus.getMapLocationID(), SimpleMappings.CString );
	vstr.saveObject( subBus.getControlUnits(), SimpleMappings.CString );
//	vstr.insertUnsignedInt( subBus.getControlDelayTime().intValue() );
//	vstr.insertUnsignedInt( subBus.getControlSendRetries().intValue() );
   
	vstr.insertUnsignedInt( subBus.getDecimalPlaces().intValue() );
//	vstr.saveObject( subBus.getNextCheckTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( 
		(subBus.getNewPointDataReceivedFlag().booleanValue() == true)
		? 1 : 0 );

	vstr.insertUnsignedInt( 
		(subBus.getBusUpdateFlag().booleanValue() == true)
		? 1 : 0 );
	
	vstr.saveObject( subBus.getLastCurrentVarPointUpdateTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( subBus.getEstimatedVarLoadPointID().intValue() );
	vstr.insertDouble( subBus.getEstimatedVarLoadPointValue().doubleValue() );

	vstr.insertUnsignedInt( subBus.getDailyOperationsAnalogPointId().intValue() );	
	vstr.insertUnsignedInt( subBus.getPowerFactorPointId().intValue() );	
	vstr.insertUnsignedInt( subBus.getEstimatedPowerFactorPointId().intValue() );	
	vstr.insertUnsignedInt( subBus.getCurrentDailyOperations().intValue() );
	vstr.insertUnsignedInt( 
		(subBus.getPeakTimeFlag().booleanValue() == true)
		? 1 : 0 );
	
	vstr.insertUnsignedInt( 
		(subBus.getRecentlyControlledFlag().booleanValue() == true)
		? 1 : 0 );

	vstr.saveObject( subBus.getLastOperationTime(), SimpleMappings.Time );
	vstr.insertDouble( subBus.getVarValueBeforeControl().doubleValue() );
//	vstr.insertUnsignedInt( subBus.getLastFeederControlledPAOID().intValue() );
//	vstr.insertUnsignedInt( subBus.getLastFeederControlledPosition().intValue() );
   
   vstr.insertDouble( subBus.getPowerFactorValue().doubleValue() );

//   vstr.insertDouble( subBus.getKVarSolution().doubleValue() );
   vstr.insertDouble( subBus.getEstimatedPFValue().doubleValue() );
   vstr.insertUnsignedInt( subBus.getCurrentVarPtQuality().intValue() );

	vstr.insertUnsignedInt( 
		(subBus.getWaiveControlFlag().booleanValue() == true)
		? 1 : 0 );
//	vstr.saveObject( subBus.getAdditionalFlags(), SimpleMappings.CString );

	vstr.insertDouble( subBus.getPeakLag().doubleValue() );
	vstr.insertDouble( subBus.getOffPkLag().doubleValue() );
	vstr.insertDouble( subBus.getPeakLead().doubleValue() );
	vstr.insertDouble( subBus.getOffPkLead().doubleValue() );
	vstr.insertUnsignedInt( subBus.getCurrentVoltLoadPointID().intValue() );
	vstr.insertDouble( subBus.getCurrentVoltLoadPointValue().doubleValue() );

	vstr.insertUnsignedInt( 
		(subBus.getVerificationFlag().booleanValue() == true)
		? 1 : 0 );
	vstr.insertUnsignedInt( 
			(subBus.getSwitchOverStatus().booleanValue() == true)
			? 1 : 0 );
	
	vstr.insertInt( subBus.getCurrentwattpointquality() );
	vstr.insertInt( subBus.getCurrentvoltpointquality() );
	vstr.insertDouble( subBus.getTargetvarvalue() );
	vstr.saveObject(subBus.getSolution(), SimpleMappings.CString);
	
	VectorInsert.insertVector(subBus.getCcFeeders(), vstr, polystr);
}
}
