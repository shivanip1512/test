package com.cannontech.multispeak.service;

import java.rmi.RemoteException;
import java.util.Map;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.dao.FormattedBlockProcessingService;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadManagementEvent;
import com.cannontech.multispeak.deploy.service.ScadaAnalog;

public interface MspValidationService {

    /**
     * Returns the processingService for the readingTypesMap for readingType.
     * Throws a RemoteException if the readingType is not valid.
     */
    public FormattedBlockProcessingService<Block> getProcessingServiceByReadingType(Map<String, FormattedBlockProcessingService<Block>> readingTypesMap,
                                                                String readingType) throws RemoteException;
    /**
     * Returns Meter for the meterNumber if MeterNumber is a Yukon MeterNumber.
     * Throws a RemoteException if the meterNumber is not found in Yukon. 
     */
    public YukonMeter isYukonMeterNumber(String meterNumber) throws RemoteException;
    
    /**
     * Returns an ErrorObject when the scadaAnalog does not have all required information
     * to create a Yukon point translation.
     * @param scadaAnalog
     * @return
     */
    public ErrorObject isValidScadaAnalog(ScadaAnalog scadaAnalog);
    
    /**
     * Returns an ErrorObject when the LoadMangementEvent does not have all required information
     * to process in Yukon.
     * @param loadManagementEvent
     * @return
     */
    public ErrorObject isValidLoadManagementEvent(LoadManagementEvent loadManagementEvent);
}
