package com.cannontech.yukon.cbc;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:21:47 PM)
 * @author: 
 */
import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.message.util.CollectionInserter;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableSubBus extends DefineCollectableStreamableCapObject {
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
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.restoreGuts( obj, vstr, polystr );
	
	SubBus subBus = (SubBus) obj;
	
	subBus.setMaxDailyOperation( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setMaxOperationDisableFlag( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	subBus.setCurrentVarLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setCurrentVarLoadPointValue( new Double( vstr.extractDouble() ) );
	subBus.setCurrentWattLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setCurrentWattLoadPointValue( new Double( vstr.extractDouble() ) );
	subBus.setMapLocationID( (String) vstr.restoreObject( SimpleMappings.CString ) );
	subBus.setControlUnits( ControlAlgorithm.valueOf((String) vstr.restoreObject( SimpleMappings.CString )) );
	subBus.setDecimalPlaces( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setNewPointDataReceivedFlag( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	subBus.setBusUpdateFlag( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	subBus.setLastCurrentVarPointUpdateTime( (java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	subBus.setEstimatedVarLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setEstimatedVarLoadPointValue( new Double( vstr.extractDouble() ) );
	subBus.setDailyOperationsAnalogPointId( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setPowerFactorPointId( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setEstimatedPowerFactorPointId( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setCurrentDailyOperations( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setPeakTimeFlag( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	subBus.setRecentlyControlledFlag( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	subBus.setLastOperationTime( (java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	subBus.setVarValueBeforeControl( new Double( vstr.extractDouble() ) );
	subBus.setPowerFactorValue( new Double( vstr.extractDouble() ) );
	subBus.setEstimatedPFValue( new Double( vstr.extractDouble() ) );
	subBus.setCurrentVarPtQuality( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setWaiveControlFlag( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	subBus.setPeakLag( new Double( vstr.extractDouble() ) );
	subBus.setOffPkLag( new Double( vstr.extractDouble() ) );
	subBus.setPeakLead( new Double( vstr.extractDouble() ) );
	subBus.setOffPkLead( new Double( vstr.extractDouble() ) );
	subBus.setCurrentVoltLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	subBus.setCurrentVoltLoadPointValue( new Double( vstr.extractDouble() ) );
	subBus.setVerificationFlag( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	subBus.setSwitchOverStatus( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	subBus.setCurrentwattpointquality(new Integer( vstr.extractInt() ));
	subBus.setCurrentvoltpointquality(new Integer( vstr.extractInt() ));
	subBus.setTargetvarvalue( new Double( vstr.extractDouble() ));
	subBus.setSolution((String) vstr.restoreObject( SimpleMappings.CString ));
	subBus.setOvUvDisabledFlag( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
    subBus.setPeakPFSetPoint(new Double( vstr.extractDouble() ));
    subBus.setOffpeakPFSetPoint(new Double( vstr.extractDouble() ));
    subBus.setControlMethod(ControlMethod.valueOf((String) vstr.restoreObject( SimpleMappings.CString )));
    subBus.setPhaseA( new Double( vstr.extractDouble() ));
    subBus.setPhaseB( new Double( vstr.extractDouble() ));
    subBus.setPhaseC( new Double( vstr.extractDouble() ));
    subBus.setLikeDayControlFlag( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
    subBus.setDisplayOrder( new Integer( vstr.extractInt() ));
    subBus.setVoltReductionFlag( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
    subBus.setUsePhaseData( ((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	subBus.setPrimaryBusFlag(((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false) );
	subBus.setAlternateBusId(new Integer((int)vstr.extractUnsignedInt()));
	subBus.setDualBusEnabled(((int)vstr.extractUnsignedInt() == 1) ? new Boolean(true) : new Boolean(false));
	subBus.setStrategyId(new Integer((int)vstr.extractUnsignedInt()));
	
    subBus.setCcFeeders( CollectionExtracter.extractVector(vstr, polystr));
}

/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
    //This never gets called and the servers do not know how to restore it.
}
}
