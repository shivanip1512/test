package com.cannontech.cbc.cache;

import java.util.HashMap;
import java.util.List;

import com.cannontech.cbc.web.CBCWebUpdatedObjectMap;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.yukon.cbc.CBCArea;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

public interface CapControlCache {

    public CapBankDevice[] getCapBanksBySpecialArea(Integer areaID);
    
    public List<SubStation> getSubstationsBySpecialArea(Integer areaId);
    
    public CBCSpecialArea getSpecialArea(Integer areaId);
    
    public int getParentAreaID( int childID );
    
    public CBCArea getArea( Integer areaId);
    
    public CBCArea getCBCArea(int id);
    
    public CBCSpecialArea getCBCSpecialArea(int id);
    
    public LiteWrapper[] getOrphanedSubBuses();
    
    public LiteWrapper[] getOrphanedSubstations();
    
    public List<SubBus> getSubBusesBySubStation(SubStation sub);
    
    public SubStation getSubstation( Integer subId);
    
    public CBCClientConnection getConnection();
    
    public Boolean getSystemStatusOn();
    
    public CBCWebUpdatedObjectMap getUpdatedObjMap();
    
	/**
	 * 
	 * @return SubBus
	 */
	public SubBus getSubBus(Integer subID);

	/**
	 * Returns the base object type for a SubBus, Feeder or CapBankDevice
	 * 
	 */
	public StreamableCapObject getCapControlPAO(Integer paoID);

	/**
	 * 
	 * @return Feeder
	 */
	public Feeder getFeeder(Integer feederID);

	/**
	 * @return CapBankDevice
	 */
	public CapBankDevice getCapBankDevice(Integer capBankDeviceID);

	/**
	 * @return List<Feeder>
	 */
	public List<Feeder> getFeedersBySubBus(Integer subBusID);
    
    /**
     * @return List<Feeder>
     */
    public List<Feeder> getFeedersBySubStation(SubStation sub);

    public String getSubBusNameForFeeder(Feeder fdr);
	/**
	 * @return CapBankDevice[]
	 */
	public CapBankDevice[] getCapBanksByFeeder(Integer feederID);

	/**
	 * Instant lookup to check if this paoID is used by a SubBus
	 * 
	 */
	public abstract boolean isSubBus(int id);
    
    /**
     * Instant lookup to check if this paoID is used by a SpecialCBCArea
     * 
     */
    public abstract boolean isSpecialCBCArea(int id);
    
    /**
     * Instant lookup to check if this paoID is used by a CBCArea
     * 
     */
    public abstract boolean isCBCArea(int id);

	/**
	 * Instant lookup to check if this paoID is used by a Feeder
	 * 
	 */
	public abstract boolean isFeeder(int id);

	/**
	 * Instant lookup to check if this paoID is used by a CapBankDevice
	 * 
	 */
	public abstract boolean isCapBank(int id);

	/**
	 * @return CapBankDevice[]
	 * @param subBusID long
	 */
	public abstract CapBankDevice[] getCapBanksBySub(Integer subBusID);
    
    /**
     * @return List<CapBankDevice>
     * @param sub SubStation
     */
    public abstract List<CapBankDevice> getCapBanksBySubStation(SubStation sub);

	/**
	 * Returns all Substations for a given Area
	 * 
	 */
	public abstract List<SubStation> getSubstationsByArea(Integer areaID);
    
    /**
     * Returns all SubBuses for a given Area
     * 
     */
    public abstract List<SubBus> getSubBusesByArea(Integer areaID);

	/**
	 * Returns all CapBanks for a given Area
	 * 
	 */
	public abstract CapBankDevice[] getCapBanksByArea(Integer areaID);

	/**
	 * Returns all Feeders for a given Area
	 * 
	 */
	public abstract List<Feeder> getFeedersByArea(Integer areaID);

	/**
	 * Find all the orphaned CBC's in the system. This requires a database hit since
	 * the CapControl server does not know about these.
	 * 
	 */
	public abstract LiteWrapper[] getOrphanedCBCs();

	/**
	 * Find the orphaned CapBanks in the system. This requires a database hit since
	 * the CapControl server does not know about these.
	 * 
	 */
	public abstract LiteWrapper[] getOrphanedCapBanks();

	/**
	 * Find the orphaned Feeders in the system. This requires a database hit since
	 * the CapControl server does not know about these.
	 * 
	 */
	public abstract LiteWrapper[] getOrphanedFeeders();

	/**
	 * Create an array of SubBuses. Best usage is to store the results of this call
	 * instead of repeatingly calling this method. Never returns null.
	 * 
	 * @return SubBus[]
	 * @param
	 */
	public abstract SubBus[] getAllSubBuses();

	/**
	 * Distinct area Strings that are used by substations
	 * 
	 * @return List
	 */
	public abstract List<CBCArea> getCbcAreas();
    
    /**
     * Distinct special area Strings that are used by substations
     * 
     * @return List
     */
    public abstract List<CBCSpecialArea> getSpecialCbcAreas();

	/**
	 * State group & states to use for CapBanks
	 * 
	 * @return LiteState
	 */
	public abstract LiteState getCapBankState(int rawState);

	/**
	 * Returns the Parent SubBus ID for the given child id
	 */
	public abstract int getParentSubBusID(int childID);

    public HashMap getSpecialAreaStateMap();

    public HashMap getAreaStateMap();
}