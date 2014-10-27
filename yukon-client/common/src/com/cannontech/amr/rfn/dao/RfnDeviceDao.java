package com.cannontech.amr.rfn.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
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
    public RfnDevice getDeviceForExactIdentifier(RfnIdentifier rfnIdentifier) throws NotFoundException;
    
    /**
     * Returns RfnDevice for pao. 
     * Will return at minimum RfnDevice populated with pao.
     * NOTE: If no RfnAddress is found for pao, an RfnDevice object will still be returned with a "blank" RfnIdentifier.
     */
    public RfnDevice getDevice(YukonPao pao);

    /**
     * Get a map of YukonPao -> RfnIdentifier for the specified PAOs.  PAOs in the list not matching any
     * RfnIdentifier will simply be ignored.
     */
    public <T extends YukonPao> Map<T, RfnIdentifier> getRfnIdentifiersByPao(Iterable<T> paos);

    /**
     * Returns RfnDevice for deviceId.
     * @throws NotFoundException - when no rfn meter exists, joined against RfnAddress table
     */
    public RfnDevice getDeviceForId(int deviceId) throws NotFoundException;
    
    /** 
     * Updates the fields (currently just the RfnIdentifier fields) for the device 
     * identified by the PaoIdentifier in the RfnDevice object.
     * @param device
     * @throws NotFoundException
     */
    public void updateDevice(RfnDevice device) throws NotFoundException;
    
    /**
     * Returns List of RfnDevices of the given PaoType. An empty list is returned if no RfnDevices
     * of the given PaoType exist.
     * @param paoType
     * @return List
     */
    public List<RfnDevice> getDevicesByPaoType(PaoType paoType);
    
    /**
     * Returns a map of paoId to RfnDevice for all devices of the specified PaoType.
     */
    Map<Integer, RfnDevice> getPaoIdMappedDevicesByPaoType(PaoType paoType); 
}
