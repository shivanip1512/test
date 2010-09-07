package com.cannontech.amr.rfn.dao;

import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.model.RfnMeterIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;

public interface RfnMeterDao {

    public RfnMeter getMeter(RfnMeterIdentifier meterIdentifier) throws NotFoundException;
    
    public RfnMeter getMeter(YukonPao pao) throws NotFoundException;
    
    public RfnMeter getForId(int deviceId) throws NotFoundException;
    
    /** 
     * Updates the fields (currently just the RfnMeterIdentifier fields) for the meter 
     * identified by the PaoIdentifier in the RfnMeter object.
     * @param meter
     * @throws NotFoundException
     */
    public void updateMeter(RfnMeter meter) throws NotFoundException;

}
