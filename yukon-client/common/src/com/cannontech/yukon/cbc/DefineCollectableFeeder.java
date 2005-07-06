package com.cannontech.yukon.cbc;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:21:47 PM)
 * @author: 
 */
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
	return this.CTI_CCFEEDER_ID;
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
	
	feeder.setPeakSetPoint( new Double( vstr.extractDouble() ) );
	feeder.setOffPeakSetPoint( new Double( vstr.extractDouble() ) );
	feeder.setUpperBandWidth( new Double( vstr.extractDouble() ) );
	feeder.setCurrentVarLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setCurrentVarLoadPointValue( new Double( vstr.extractDouble() ) );
	feeder.setCurrentWattLoadPointID( new Integer( (int)vstr.extractUnsignedInt() ) );
	feeder.setCurrentWattLoadPointValue( new Double( vstr.extractDouble() ) );
	feeder.setMapLocationID( (String) vstr.restoreObject( SimpleMappings.CString ) );
   
   feeder.setLowerBandWidth( new Double( vstr.extractDouble() ) );
   
	/**/feeder.setDisplayOrder( new Integer( (int)vstr.extractUnsignedInt() ) );
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
	feeder.setLastCapBankControlledDeviceID( new Integer( (int)vstr.extractUnsignedInt() ) );

   feeder.setPowerFactorValue( new Double( vstr.extractDouble() ) );

   feeder.setKVarSolution( new Double( vstr.extractDouble() ) );
   feeder.setEstimatedPFValue( new Double( vstr.extractDouble() ) );
   feeder.setCurrentVarPtQuality( new Integer( (int)vstr.extractUnsignedInt() ) );

	feeder.setWaiveControlFlag(
		((int)vstr.extractUnsignedInt() == 1)
		? new Boolean(true) : new Boolean(false) );

//	feeder.setSubControlUnits( (String)vstr.restoreObject(SimpleMappings.CString) );
//	feeder.setSubDecimalPlaces( (int)vstr.extractUnsignedInt() );
//	feeder.setSubPeakTimeFlag(
//		 ((int)vstr.extractUnsignedInt() == 1)
//		 ? new Boolean(true) : new Boolean(false) );



	/*	we have to do this manually because the new Rogue Wave object in the server
			doesn't stream correctly */
	int numberOfCapBanks = (int)vstr.extractUnsignedInt();
	java.util.Vector capBanksVector = new java.util.Vector();
	for(int i=0;i<numberOfCapBanks;i++)
	{
		capBanksVector.addElement(vstr.restoreObject(polystr));
	}
	feeder.setCcCapBanks(capBanksVector);		
}

/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	Feeder feeder = (Feeder)obj;

	vstr.insertDouble( feeder.getPeakSetPoint().doubleValue() );
	vstr.insertDouble( feeder.getOffPeakSetPoint().doubleValue() );
	vstr.insertDouble( feeder.getUpperBandWidth().doubleValue() );
	vstr.insertUnsignedInt( feeder.getCurrentVarLoadPointID().intValue() );
	vstr.insertDouble( feeder.getCurrentVarLoadPointValue().doubleValue() );
	vstr.insertUnsignedInt( feeder.getCurrentWattLoadPointID().intValue() );
	vstr.insertDouble( feeder.getCurrentWattLoadPointValue().doubleValue() );
	vstr.saveObject( feeder.getMapLocationID(), SimpleMappings.CString );
   
   vstr.insertDouble( feeder.getLowerBandWidth().doubleValue() );
   
	vstr.insertUnsignedInt( feeder.getDisplayOrder().intValue() );
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
	vstr.insertUnsignedInt( feeder.getLastCapBankControlledDeviceID().intValue() );

   vstr.insertDouble( feeder.getPowerFactorValue().doubleValue() );

   vstr.insertDouble( feeder.getKVarSolution().doubleValue() );
   vstr.insertDouble( feeder.getEstimatedPFValue().doubleValue() );
   vstr.insertUnsignedInt( feeder.getCurrentVarPtQuality().intValue() );

	vstr.insertUnsignedInt( 
		(feeder.getWaiveControlFlag().booleanValue() == true)
		? 1 : 0 );

//	vstr.saveObject( feeder.getSubControlUnits(), SimpleMappings.CString );
//	vstr.insertUnsignedInt( feeder.getSubDecimalPlaces() );
//	vstr.insertUnsignedInt( 
//		(feeder.getSubPeakTimeFlag().booleanValue() == true)
//		? 1 : 0 );

	/*	we have to do this manually because the new Rogue Wave object in the server
			doesn't stream correctly */
	vstr.insertUnsignedInt( feeder.getCcCapBanks().size() );
	for(int i=0;i<feeder.getCcCapBanks().size();i++)
	{
		vstr.saveObject( ((java.util.Vector)feeder.getCcCapBanks()).get(i), polystr );
	}
}
}
