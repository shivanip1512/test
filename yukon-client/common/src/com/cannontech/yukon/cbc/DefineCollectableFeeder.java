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

public class DefineCollectableFeeder extends DefineCollectableStreamableCapObject
{
	//The roguewave class id
	private static int CTI_CCFEEDER_ID = 503;
/**
 * DefineCollectableCapBankVector constructor comment.
 */
public DefineCollectableFeeder() {
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new Feeder();
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId() {
	return CTI_CCFEEDER_ID;
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
	return Feeder.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts( obj, vstr, polystr );
	
	Feeder feeder = (Feeder)obj;
	
	feeder.setMaxDailyOperation( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setMaxOperationDisableFlag( 
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );
	
	feeder.setCurrentVarLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setCurrentVarLoadPointValue( new Double( vstr.extractDouble() ) );
	feeder.setCurrentWattLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setCurrentWattLoadPointValue( new Double( vstr.extractDouble() ) );
	feeder.setMapLocationID( (String) vstr.restoreObject( SimpleMappings.CString ) );
   
	//feeder.setDisplayOrder( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setDisplayOrder( new Float(vstr.extractFloat() ) );

	feeder.setNewPointDataReceivedFlag( 
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );

	feeder.setLastCurrentVarPointUpdateTime( (java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	feeder.setEstimatedVarLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setEstimatedVarLoadPointValue( new Double( vstr.extractDouble() ) );
	
	feeder.setDailyOperationsAnalogPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setPowerFactorPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setEstimatedPowerFactorPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setCurrentDailyOperations( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setRecentlyControlledFlag(
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );

	feeder.setLastOperationTime( (java.util.Date)vstr.restoreObject( SimpleMappings.Time ) );
	feeder.setVarValueBeforeControl( new Double( vstr.extractDouble() ) );
//	feeder.setLastCapBankControlledDeviceID( new Integer( (int)vstr.extractUnsignedInt() ) );

   feeder.setPowerFactorValue( new Double( vstr.extractDouble() ) );

//   feeder.setKVarSolution( new Double( vstr.extractDouble() ) );
   feeder.setEstimatedPFValue( new Double( vstr.extractDouble() ) );
   feeder.setCurrentVarPtQuality( new Integer( (int)vstr.extractUnsignedInt() ) );

	feeder.setWaiveControlFlag(
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );

	feeder.setControlUnits( (String)vstr.restoreObject(SimpleMappings.CString) );
	feeder.setDecimalPlaces( (int)vstr.extractUnsignedInt() );
	feeder.setPeakTimeFlag(
		 ((int)vstr.extractUnsignedInt() == 1)
		 ? new Boolean(true) : new Boolean(false) );

	feeder.setPeakLag( new Double( vstr.extractDouble() ) );
	feeder.setOffPkLag( new Double( vstr.extractDouble() ) );
	feeder.setPeakLead( new Double( vstr.extractDouble() ) );
	feeder.setOffPkLead( new Double( vstr.extractDouble() ) );
	feeder.setCurrentVoltLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setCurrentVoltLoadPointValue( new Double( vstr.extractDouble() ) );
	feeder.setCurrentwattpointquality(new Integer( vstr.extractInt() ));
	feeder.setCurrentvoltpointquality(new Integer( vstr.extractInt() ));
	feeder.setTargetvarvalue( new Double( vstr.extractDouble() ));
	feeder.setSolution((String) vstr.restoreObject( SimpleMappings.CString ));
	feeder.setOvUvDisabledFlag(
			((int)vstr.extractUnsignedInt() == 1)
			? new Boolean(true) : new Boolean(false));
    feeder.setPeakPFSetPoint(new Double( vstr.extractDouble() ));
    feeder.setOffpeakPFSetPoint(new Double( vstr.extractDouble() ));
	feeder.setControlmethod((String) vstr.restoreObject( SimpleMappings.CString ));
    feeder.setPhaseA( new Double( vstr.extractDouble() ));
    feeder.setPhaseB( new Double( vstr.extractDouble() ));
    feeder.setPhaseC( new Double( vstr.extractDouble() ));
    feeder.setLikeDayControlFlag(
			((int)vstr.extractUnsignedInt() == 1)
			? new Boolean(true) : new Boolean(false));
    feeder.setUsePhaseData( ((int)vstr.extractUnsignedInt() == 1) 
    						? new Boolean(true) : new Boolean(false));
    feeder.setOriginalParentId(new Integer(vstr.extractInt()));
	feeder.setCcCapBanks(VectorExtract.extractVector(vstr,polystr));
}

/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	Feeder feeder = (Feeder)obj;

	vstr.insertUnsignedInt( feeder.getMaxDailyOperation().intValue() );
	vstr.insertUnsignedInt( 
		(feeder.getMaxOperationDisableFlag().booleanValue() == true)
		? 1 : 0 );

	vstr.insertUnsignedInt( feeder.getCurrentVarLoadPointID().intValue() );
	vstr.insertDouble( feeder.getCurrentVarLoadPointValue().doubleValue() );
	vstr.insertUnsignedInt( feeder.getCurrentWattLoadPointID().intValue() );
	vstr.insertDouble( feeder.getCurrentWattLoadPointValue().doubleValue() );
	vstr.saveObject( feeder.getMapLocationID(), SimpleMappings.CString );
   
	//vstr.insertUnsignedInt( feeder.getDisplayOrder().intValue() );
    vstr.insertFloat( feeder.getDisplayOrder().floatValue() ); 
	vstr.insertUnsignedInt( 
		(feeder.getNewPointDataReceivedFlag().booleanValue() == true)
		? 1 : 0 );

	vstr.saveObject( feeder.getLastCurrentVarPointUpdateTime(), SimpleMappings.Time );
	vstr.insertUnsignedInt( feeder.getEstimatedVarLoadPointID().intValue() );
	vstr.insertDouble( feeder.getEstimatedVarLoadPointValue().doubleValue() );

	vstr.insertUnsignedInt( feeder.getDailyOperationsAnalogPointID().intValue() );	
	vstr.insertUnsignedInt( feeder.getPowerFactorPointID().intValue() );	
	vstr.insertUnsignedInt( feeder.getEstimatedPowerFactorPointID().intValue() );	
	vstr.insertUnsignedInt( feeder.getCurrentDailyOperations().intValue() );
	vstr.insertUnsignedInt( 
		(feeder.getRecentlyControlledFlag().booleanValue() == true)
		? 1 : 0 );

	vstr.saveObject( feeder.getLastOperationTime(), SimpleMappings.Time );
	vstr.insertDouble( feeder.getVarValueBeforeControl().doubleValue() );
//	vstr.insertUnsignedInt( feeder.getLastCapBankControlledDeviceID().intValue() );

   vstr.insertDouble( feeder.getPowerFactorValue().doubleValue() );

//   vstr.insertDouble( feeder.getKVarSolution().doubleValue() );
   vstr.insertDouble( feeder.getEstimatedPFValue().doubleValue() );
   vstr.insertUnsignedInt( feeder.getCurrentVarPtQuality().intValue() );

	vstr.insertUnsignedInt( 
		(feeder.getWaiveControlFlag().booleanValue() == true)
		? 1 : 0 );

	vstr.saveObject( feeder.getControlUnits(), SimpleMappings.CString );
	vstr.insertUnsignedInt( feeder.getDecimalPlaces() );
	vstr.insertUnsignedInt( 
		(feeder.getPeakTimeFlag().booleanValue() == true)
		? 1 : 0 );

	vstr.insertDouble( feeder.getPeakLag().doubleValue() );
	vstr.insertDouble( feeder.getOffPkLag().doubleValue() );
	vstr.insertDouble( feeder.getPeakLead().doubleValue() );
	vstr.insertDouble( feeder.getOffPkLead().doubleValue() );
	vstr.insertUnsignedInt( feeder.getCurrentVoltLoadPointID().intValue() );
	vstr.insertDouble( feeder.getCurrentVoltLoadPointValue().doubleValue() );

	vstr.insertInt( feeder.getCurrentwattpointquality() );
	vstr.insertInt( feeder.getCurrentvoltpointquality() );
	vstr.insertDouble( feeder.getTargetvarvalue() );
	vstr.saveObject(feeder.getSolution(), SimpleMappings.CString);
	vstr.insertUnsignedInt( 
			(feeder.getOvUvDisabledFlag().booleanValue() == true)
			? 1 : 0 );
    vstr.insertDouble(feeder.getPeakPFSetPoint());
    vstr.insertDouble(feeder.getOffpeakPFSetPoint());
	vstr.saveObject(feeder.getControlmethod(), SimpleMappings.CString);
	
	/*	we have to do this manually because the new Rogue Wave object in the server
			doesn't stream correctly */
//	vstr.insertUnsignedInt( feeder.getCcCapBanks().size() );
//	for(int i=0;i<feeder.getCcCapBanks().size();i++)
//	{
//		vstr.saveObject( ((java.util.Vector)feeder.getCcCapBanks()).get(i), polystr );
//	}
    vstr.insertDouble(feeder.getPhaseA());
    vstr.insertDouble(feeder.getPhaseB());
    vstr.insertDouble(feeder.getPhaseC());
    vstr.insertUnsignedInt( 
			(feeder.getLikeDayControlFlag().booleanValue() == true)
			? 1 : 0 );
	vstr.insertUnsignedInt((feeder.getUsePhaseData().booleanValue() == true)? 1 : 0 );
	vstr.insertInt(feeder.getOriginalParentId());
    VectorInsert.insertVector(feeder.getCcCapBanks(), vstr, polystr);
    
}
}
