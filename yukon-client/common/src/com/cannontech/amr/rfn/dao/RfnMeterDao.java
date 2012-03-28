package com.cannontech.amr.rfn.dao;

import java.util.Map;

import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.model.RfnMeterIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;

public interface RfnMeterDao {

    /**
     * WARNING!!! 
     * This method only handles exact matches and does not handle meter identifiers that may have
     * a new version of the model name ie: (FocusAXD-SD vs FocusAXD).
     * Use RfnMeterLookupService.getMeter instead.
     * @param meterIdentifier
     * @return RfnMeter
     * @throws NotFoundException
     */
    public RfnMeter getMeterForExactIdentifier(RfnMeterIdentifier meterIdentifier) throws NotFoundException;
    
    public RfnMeter getMeter(YukonPao pao) throws NotFoundException;

    /**
     * Get a map of YukonPao -> RfnMeter for the specified PAOs.  PAOs in the list not matching any
     * RfnMeter will simply be ignored.
     */
    public <T extends YukonPao> Map<T, RfnMeterIdentifier> getMeterIdentifiersByPao(Iterable<T> paos);

    public RfnMeter getForId(int deviceId) throws NotFoundException;
    
    /** 
     * Updates the fields (currently just the RfnMeterIdentifier fields) for the meter 
     * identified by the PaoIdentifier in the RfnMeter object.
     * @param meter
     * @throws NotFoundException
     */
    public void updateMeter(RfnMeter meter) throws NotFoundException;

}
