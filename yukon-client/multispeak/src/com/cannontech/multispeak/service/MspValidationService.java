package com.cannontech.multispeak.service;

import java.rmi.RemoteException;
import java.util.Map;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadManagementEvent;
import com.cannontech.multispeak.deploy.service.ScadaAnalog;

public interface MspValidationService {

    /**
     * Returns the formattedBlock object for the readingTypesMap for readingType.
     * Throws a RemoteException if the readingType is not valid.
     */
    public FormattedBlockService<Block> isValidBlockReadingType(
            Map<String, FormattedBlockService<Block>> readingTypesMap,
            String readingType) throws RemoteException;

    /**
     * Returns Meter for the meterNumber if MeterNumber is a Yukon MeterNumber.
     * Throws a RemoteException if the meterNumber is not found in Yukon. 
     */
    public Meter isYukonMeterNumber(String meterNumber) throws RemoteException;
    
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
