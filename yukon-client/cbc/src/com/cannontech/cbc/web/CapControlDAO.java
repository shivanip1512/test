package com.cannontech.cbc.web;

import java.util.HashMap;
import java.util.List;

import com.cannontech.database.data.lite.LiteState;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.yukon.cbc.CBCArea;
import com.cannontech.yukon.cbc.CBCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

public interface CapControlDAO {

	/**
	 * 
	 * @return SubBus
	 */
	public abstract SubBus getSubBus(Integer subID);

	/**
	 * Returs the base object type for a SubBus, Feeder or CapBankDevice
	 * 
	 */
	public abstract StreamableCapObject getCapControlPAO(Integer paoID);

	/**
	 * 
	 * @return Feeder
	 */
	public abstract Feeder getFeeder(Integer feederID);

	/**
	 * @return CapBankDevice
	 */
	public abstract CapBankDevice getCapBankDevice(Integer capBankDeviceID);

	/**
	 * @return List<Feeder>
	 */
	public abstract List<Feeder> getFeedersBySubBus(Integer subBusID);
    
    /**
     * @return List<Feeder>
     */
    public abstract List<Feeder> getFeedersBySubStation(SubStation sub);

    public abstract String getSubBusNameForFeeder(Feeder fdr);
	/**
	 * @return CapBankDevice[]
	 */
	public abstract CapBankDevice[] getCapBanksByFeeder(Integer feederID);

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