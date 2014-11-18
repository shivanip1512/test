package com.cannontech.cbc.cache;

import java.util.List;

import com.cannontech.cbc.web.CBCWebUpdatedObjectMap;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.yukon.conns.CapControlClientConnection;

public interface CapControlCache {

    List<CapBankDevice> getCapBanksBySpecialArea(int areaId);
    
    List<SubStation> getSubstationsBySpecialArea(int areaId);
    
    int getParentAreaId(int childId);
    
    StreamableCapObject getStreamableArea(int areaId) throws NotFoundException;
    
    Area getArea(int areaId);
    
    VoltageRegulatorFlags getVoltageRegulatorFlags(int regulatorId);
    
    SpecialArea getSpecialArea(int specialAreaId);
    
    StreamableCapObject getObject(int id);
    
    List<SubBus> getSubBusesBySubStation(SubStation substation);
    
    SubStation getSubstation(int substationId);
    
    CapControlClientConnection getConnection();
    
    boolean getSystemStatusOn();
    
    CBCWebUpdatedObjectMap getUpdatedObjects();
    
    SubBus getSubBus(int subbusId);

    /**
     * Returns the base object type for a SubBus, Feeder or CapBankDevice.
     */
    StreamableCapObject getCapControlPao(int paoId);

    Feeder getFeeder(int feederId);

    CapBankDevice getCapBankDevice(int deviceId);

    List<Feeder> getFeedersBySubBus(int subbusId);
    
    List<Feeder> getFeedersBySubStation(SubStation substation);

    String getSubBusNameForFeeder(Feeder feeder);

    List<CapBankDevice> getCapBanksByFeeder(int feederId);

    List<CapBankDevice> getCapBanksByTypeAndId(CapControlType type, int id);
    
    /**
     * Instant lookup to check if this paoID is used by a SubBus.
     */
    boolean isSubBus(int subbusId);
    
    /**
     * Instant lookup to check if this paoID is used by a SpecialCBCArea.
     */
    boolean isSpecialArea(int id);
    
    /**
     * Instant lookup to check if this paoID is used by a CBCArea.
     */
    boolean isArea(int id);

    /**
     * Instant lookup to check if this paoID is used by a Feeder.
     */
    boolean isFeeder(int id);

    /**
     * Instant lookup to check if this paoID is used by a CapBankDevice.
     */
    boolean isCapBank(int id);

    /**
     * Can throw a not found exception if the subToBankMap is holding bad bank ID's.
     */
    List<CapBankDevice> getCapBanksBySubBus(int subbusId) throws NotFoundException;
    
    List<CapBankDevice> getCapBanksBySubStation(SubStation sub);

    /**
     * Returns all Substations for a given Area.
     */
    List<SubStation> getSubstationsByArea(int areaId);
    
    /**
     * Returns a list of assigned subbuses for Areas or SpecialAreas.
     */
    List<SubBus> getSubBusesByArea(int areaId);

    /**
     * Returns all CapBanks for a given Area.
     */
    List<CapBankDevice> getCapBanksByArea(int areaId);

    /**
     * Returns all Feeders for a given Area.
     */
    List<Feeder> getFeedersByArea(int areaId);
    
    /**
     * Returns distinct areas that are used by substations.
     */
    List<Area> getAreas();
    
    /**
     * Returns distinct special areas that are used by substations.
     */
    List<SpecialArea> getSpecialAreas();

    /**
     * Returns the matching {@link LiteState} for the given raw state id.
     */
    LiteState getCapBankState(int rawState);
    
    /**
     * Returns the parent SubBus ID for the given child id.
     */
    int getParentSubBusId(int childId);
    
    /**
     * Returns the parent SubBus for the given child id.
     */
    SubBus getParentSubBus(int childId);
    
    /**
     * Returns the parent Feeder for the given child id.
     */
    Feeder getParentFeeder(int childId) throws NotFoundException;
    
    /**
     * Returns the parent substation for the given child id.
     */
    SubStation getParentSubstation(int childId) throws NotFoundException;

    /**
     * Returns the parent substation id for the given child id.
     */
    int getParentSubStationId(int childId);
    
    /**
     * Returns the parent feeder id for the given child id.
     */
    int getParentFeederId(int childId);

    /**
     * Returns true if the id represents a substation.
     */
    boolean isSubstation(int substationId);

    /**
     * Sends a command request to the capcontrol server to rebuild this cache completely.
     */
    boolean refresh();

    void handleDeleteItem(int id);
}