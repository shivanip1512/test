package com.cannontech.amr.rfn.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.core.dao.NotFoundException;

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
     * Creates mappings for gateway to device
     */
    void createGatewayToDeviceMapping(int gatewayId, List<Integer> deviceIds);

    /**
     * Returns list of devices for gateway
     */
    List<Integer> getDevicesForGateway(int gatewayId);

    /**
     * Deletes all mappings of gateway to device. Should be used by simulator only.
     */
    void clearNmToRfnDeviceData();

    /**
     * Returns list of device ids for rfn identifiers
     */
    List<Integer> getDeviceIdsForRfnIdentifiers(Iterable<RfnIdentifier> rfnIdentifiers);

}
