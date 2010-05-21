package com.cannontech.amr.crf.dao;

import com.cannontech.amr.crf.model.CrfMeter;
import com.cannontech.amr.crf.model.CrfMeterIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;

public interface CrfMeterDao {

    public CrfMeter getMeter(CrfMeterIdentifier meterIdentifier) throws NotFoundException;
    
    public CrfMeter getMeter(YukonPao pao) throws NotFoundException;
    
    public CrfMeter getForId(int deviceId) throws NotFoundException;
    
    /** 
     * Updates the fields (currently just the CrfMeterIdentifier fields) for the meter 
     * identified by the PaoIdentifier in the CrfMeter object.
     * @param meter
     * @throws NotFoundException
     */
    public void updateMeter(CrfMeter meter) throws NotFoundException;

}
