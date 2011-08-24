package com.cannontech.cbc.cache;

import java.util.List;

import com.cannontech.cbc.web.CBCWebUpdatedObjectMap;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.yukon.cbc.CCArea;
import com.cannontech.yukon.cbc.CapControlClientConnection;
import com.cannontech.yukon.cbc.CCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.VoltageRegulatorFlags;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

public interface CapControlCache {

    public List<CapBankDevice> getCapBanksBySpecialArea(int areaID);
    
    public List<SubStation> getSubstationsBySpecialArea(int areaId);
    
    public int getParentAreaID(int childID);
    
    public StreamableCapObject getArea(int paoId) throws NotFoundException;
    
    public CCArea getCBCArea(int id);
    
    public VoltageRegulatorFlags getVoltageRegulatorFlags(int id);
    
    public CCSpecialArea getCBCSpecialArea(int id);
    
    public StreamableCapObject getObject(int id);
    
    public List<SubBus> getSubBusesBySubStation(SubStation sub);
    
    public SubStation getSubstation( int subId);
    
    public CapControlClientConnection getConnection();
    
    public Boolean getSystemStatusOn();
    
    public CBCWebUpdatedObjectMap getUpdatedObjMap();
    
	/**
	 * 
	 * @return SubBus
	 */
	public SubBus getSubBus(int subID);

	/**
	 * Returns the base object type for a SubBus, Feeder or CapBankDevice
	 * 
	 */
	public StreamableCapObject getCapControlPAO(int paoID);

	/**
	 * 
	 * @return Feeder
	 */
	public Feeder getFeeder(int feederID);

	/**
	 * @return CapBankDevice
	 */
	public CapBankDevice getCapBankDevice(int capBankDeviceID);
	
	/**
	 * @return CapBankDevice
	 */
	public CapBankDevice getCapBankDeviceByStatusPointID(int capBankStatusID);

	/**
	 * @return List<Feeder>
	 */
	public List<Feeder> getFeedersBySubBus(int subBusID);
    
    /**
     * @return List<Feeder>
     */
    public List<Feeder> getFeedersBySubStation(SubStation sub);

    public String getSubBusNameForFeeder(Feeder fdr);
	/**
	 * @return List<CapBankDevice>
	 */
	public List<CapBankDevice> getCapBanksByFeeder(int feederID);

	public List<CapBankDevice> getCapBanksByTypeAndId(CapControlType type, int id);
	
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
	public abstract List<CapBankDevice> getCapBanksBySubBus(int subBusID);
    
    /**
     * @return List<CapBankDevice>
     * @param sub SubStation
     */
    public abstract List<CapBankDevice> getCapBanksBySubStation(SubStation sub);

	/**
	 * Returns all Substations for a given Area
	 * 
	 */
	public abstract List<SubStation> getSubstationsByArea(int areaID);
    
    /**
     * Returns all SubBuses for a given Area
     * 
     */
    public abstract List<SubBus> getSubBusesByArea(int areaID);

	/**
	 * Returns all CapBanks for a given Area
	 * 
	 */
	public abstract List<CapBankDevice> getCapBanksByArea(int areaID);

	/**
	 * Returns all Feeders for a given Area
	 * 
	 */
	public abstract List<Feeder> getFeedersByArea(int areaID);
	
	/**
	 * Distinct area Strings that are used by substations
	 * 
	 * @return List
	 */
	public abstract List<CCArea> getCbcAreas();
    
    /**
     * Distinct special area Strings that are used by substations
     * 
     * @return List
     */
    public abstract List<CCSpecialArea> getSpecialCbcAreas();

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

}