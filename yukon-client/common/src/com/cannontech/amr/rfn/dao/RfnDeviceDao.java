package com.cannontech.amr.rfn.dao;

import java.util.Map;

import com.cannontech.amr.rfn.model.RfnMeter;
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
    
    public RfnMeter getMeterForExactIdentifier(RfnIdentifier rfnIdentifier) throws NotFoundException;
    
    public RfnMeter getMeter(YukonPao pao) throws NotFoundException;
    
    public RfnDevice getDevice(YukonPao pao) throws NotFoundException;

    /**
     * Get a map of YukonPao -> RfnIdentifier for the specified PAOs.  PAOs in the list not matching any
     * RfnIdentifier will simply be ignored.
     */
    public <T extends YukonPao> Map<T, RfnIdentifier> getRfnIdentifiersByPao(Iterable<T> paos);

    public RfnDevice getDeviceForId(int deviceId) throws NotFoundException;
    
    public RfnMeter getMeterForId(int deviceId) throws NotFoundException;
    
    /** 
     * Updates the fields (currently just the RfnIdentifier fields) for the device 
     * identified by the PaoIdentifier in the RfnDevice object.
     * @param device
     * @throws NotFoundException
     */
    public void updateDevice(RfnDevice device) throws NotFoundException;

}
