package com.cannontech.yukon.cbc;

import com.cannontech.message.util.VectorExtract;
import com.cannontech.message.util.VectorInsert;
import com.roguewave.vsj.DefineCollectable;

public class DefineCollectableSubStation extends DefineCollectableStreamableCapObject {

	private static int CTI_CCSUBSTATION_ID = 524;
	
	public DefineCollectableSubStation() {
		super();
	}
    
	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new SubStation();
	}
    
	/**
	 * getCxxClassId method comment.
	 */
	public int getCxxClassId() {
		return CTI_CCSUBSTATION_ID;
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
		return SubStation.class;
	}
    
	/**
	 * restoreGuts method comment.
	 */
	public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
		super.restoreGuts( obj, vstr, polystr );
		SubStation sub = (SubStation) obj;

		sub.setOvuvDisableFlag( ((int)vstr.extractUnsignedInt() == 1)? new Boolean(true) : new Boolean(false) );
        int[] ids = VectorExtract.extractIntArray(vstr, polystr);
		sub.setSubBusIds( ids);
        sub.setPowerFactorValue( new Double( vstr.extractDouble() ) );
        sub.setEstimatedPFValue( new Double( vstr.extractDouble() ) );
        sub.setSpecialAreaEnabled(((int)vstr.extractUnsignedInt() == 1)? new Boolean(true) : new Boolean(false));
        sub.setSpecialAreaId(vstr.extractInt());
        sub.setVoltReductionFlag( ((int)vstr.extractUnsignedInt() == 1)? new Boolean(true) : new Boolean(false) );
        sub.setRecentlyControlledFlag( ((int)vstr.extractUnsignedInt() == 1)? new Boolean(true) : new Boolean(false) );
        
	}
    
	/**
	 * saveGuts method comment.
	 */
	public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
		super.saveGuts( obj, vstr, polystr );
		SubStation sub = (SubStation)obj;
		
		vstr.insertUnsignedInt( (sub.getOvuvDisableFlag().booleanValue() == true) ? 1 : 0 );
		VectorInsert.insertIntArray(sub.getSubBusIds(), vstr, polystr);
        vstr.insertDouble( sub.getPowerFactorValue().doubleValue() );
        vstr.insertDouble( sub.getEstimatedPFValue().doubleValue() );
        vstr.insertUnsignedInt( (sub.getSpecialAreaEnabled().booleanValue() == true) ? 1 : 0 );
        vstr.insertUnsignedInt( sub.getSpecialAreaId() );
        vstr.insertUnsignedInt( (sub.getVoltReductionFlag().booleanValue() == true) ? 1 : 0 );
        vstr.insertUnsignedInt( (sub.getRecentlyControlledFlag().booleanValue() == true) ? 1 : 0 );
		
	}
}
