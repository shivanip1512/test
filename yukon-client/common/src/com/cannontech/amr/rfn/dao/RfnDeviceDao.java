package com.cannontech.amr.rfn.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.model.RfnModelChange;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.services.systemDataPublisher.service.model.RfnDeviceDescendantCountData;

public interface RfnDeviceDao {

    /**
     * WARNING!!! 
     * This method only handles exact matches and does not handle rfn identifiers that may have
     * a new version of the model name ie: (FocusAXD-SD vs FocusAXD).
     * Use RfnDeviceLookupService.getDevice instead.
     * @param rfnIdentifier
     * @return RfnDevice
     * @throws NotFoundException
     */
    RfnDevice getDeviceForExactIdentifier(RfnIdentifier rfnIdentifier) throws NotFoundException;
    
    /**
     * Returns true if a device exists with the specified RfnIdentifier.
     */
    boolean deviceExists(RfnIdentifier rfnIdentifier);
    
    /**
     * Returns RfnDevice for pao. 
     * Will return at minimum RfnDevice populated with pao.
     * NOTE: If no RfnAddress is found for pao, an RfnDevice object will still be returned with a "blank" RfnIdentifier.
     */
    RfnDevice getDevice(YukonPao pao);

    /**
     * Get a map of YukonPao -> RfnIdentifier for the specified PAOs.  PAOs in the list not matching any
     * RfnIdentifier will simply be ignored.
     */
    <T extends YukonPao> Map<T, RfnIdentifier> getRfnIdentifiersByPao(Iterable<T> paos);

    /**
     * Returns RfnDevice for deviceId.
     * @throws NotFoundException - when no rfn meter exists, joined against RfnAddress table
     */
    RfnDevice getDeviceForId(int deviceId) throws NotFoundException;
    
    /** 
     * Updates the fields (currently just the RfnIdentifier fields) for the device 
     * identified by the PaoIdentifier in the RfnDevice object.
     * @param device
     * @throws NotFoundException
     */
    void updateDevice(RfnDevice device) throws NotFoundException;
    
    /**
     * Returns List of RfnDevices of the given PaoType. An empty list is returned if no RfnDevices
     * of the given PaoType exist.
     */
    List<RfnDevice> getDevicesByPaoType(PaoType paoType);
    
    /**
     * Returns List of RfnDevices of the given PaoTypes. An empty list is returned if no RfnDevices
     * of the given PaoTypes exist.
     */
    List<RfnDevice> getDevicesByPaoTypes(Iterable<PaoType> paoTypes);
    
    /**
     * Returns List of RfnDevices of the given PaoTypes and search criteria. An empty list is returned if no RfnDevices
     * of the given PaoTypes and criteria exist.
     */
    List<RfnDevice> searchDevicesByPaoTypes(Iterable<PaoType> paoTypes, RfnDeviceSearchCriteria criteria);
    
    /**
     * Returns a map of paoId to RfnDevice for all devices of the specified PaoType.
     */
    Map<Integer, RfnDevice> getPaoIdMappedDevicesByPaoType(PaoType paoType);
    
    /**
     * Returns a List of RfnDevices with the given paoIds. Any invalid ids will not cause an error - they will be
     * ignored.
     */
    List<RfnDevice> getDevicesByPaoIds(Iterable<Integer> paoIds);
    
    /**
     * Updates the YukonPaObject.Type of the gateway to GWY-800.
     * @return The updated gateway.
     */
    RfnDevice updateGatewayType(RfnDevice device);
    
    /**
     * Creates mappings for gateway/device/descendant count.
     */
    void saveDynamicRfnDeviceData(Set<DynamicRfnDeviceData> datas);

    /**
     * Returns list of devices for gateway.
     */
    List<RfnDevice> getDevicesForGateway(int gatewayId);
    
    /**
     * Returns list of devices for gateways
     * A list of PaoTypes can be specified for filtering or set to null to return all devices
     * @param gatewayIdsList - an integer list of gatewayIds
     * @param paoTypes - a iterable object containing PaoTypes
     */
    List<RfnDevice> getDevicesForGateways(List<Integer> gatewayIdsList, Iterable<PaoType> paoTypes);

    /**
     * Returns limited list of RfnIdentifier for gateway. Used by simulator.
     */
    List<RfnIdentifier> getRfnIdentifiersForGateway(int gatewayId, int rowLimit);

    /**
     * Deletes all mappings of gateway to device. Should be used by simulator only.
     */
    void clearDynamicRfnDeviceData();

    /**
     * Returns list of device ids for rfn identifiers. Cache lookup.
     */
    Set<Integer> getDeviceIdsForRfnIdentifiers(Iterable<RfnIdentifier> rfnIdentifiers);

    RfnDevice findDeviceBySensorSerialNumber(String sensorSerialNumber);

    /**
     * Returns device id for rfn identifier. Cache lookup.
     */
    Integer getDeviceIdForRfnIdentifier(RfnIdentifier rfnIdentifier);

    /**
     * Returns device to gateway identifier/descendant count
     */


    /**
     * Returns gateway ids for the set of devices
     */
    Set<Integer> getGatewayIdsForDevices(Set<Integer> deviceIds);

    /**
     * Returns  DynamicRfnDeviceData for device or null if the device is not associated with gateway
     */
    DynamicRfnDeviceData findDynamicRfnDeviceData(Integer deviceId);   
    /**
     * Returns gateway to collection of DynamicRfnDeviceData
     */
    Map<Integer, List<DynamicRfnDeviceData>> getDynamicRfnDeviceDataByGateways(Iterable<Integer> gatewayIds);
    
    /**
     * Returns gateway to collection of DynamicRfnDeviceData
     */
    Map<Integer, List<DynamicRfnDeviceData>> getDynamicRfnDeviceDataByDevices(Iterable<Integer> deviceIds);

    /**
     * Returns list of DynamicRfnDeviceDatas
     */
    List<DynamicRfnDeviceData> getDynamicRfnDeviceData(Iterable<Integer> deviceIds);

    /**
     * Returns all data in the table, used by simulator
     */
    List<DynamicRfnDeviceData> getAllDynamicRfnDeviceData();
    /**
     * Returns device RfnIdentifiers by gateway ids
     */
    Set<RfnIdentifier> getDeviceRfnIdentifiersByGatewayIds(Iterable<Integer> gatewayIds);
    /**
     * Return RfnDeviceDescendantCountData for paoTypes.
     */
    RfnDeviceDescendantCountData findDeviceDescendantCountDataForPaoTypes(Iterable<PaoType> paoTypes);

    /**
     * Returns devices with the same serial number and manufacturer
     */
    List<RfnDevice> getPartiallyMatchedDevices(String serialNumber, String manufacturer);

    /**
     * Creates or updated entry in RfnModelChange table with new and old model names
     */
    void updateRfnModelChange(RfnModelChange rfnModelChange);

    Instant findModelChangeDataTimestamp(int deviceId);
}
