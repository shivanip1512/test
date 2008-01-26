package com.cannontech.multispeak.service;

import java.rmi.RemoteException;
import java.util.Map;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.FormattedBlockService;

public interface MspValidationService {

    /**
     * Returns the formattedBlock object for the readingTypesMap for readingType.
     * Throws a RemoteException if the readingType is not valid.
     */
    public FormattedBlockService<Block> isValidBlockReadingType(
            Map<String, FormattedBlockService> readingTypesMap,
            String readingType) throws RemoteException;

    /**
     * Returns Meter for the meterNumber if MeterNumber is a Yukon MeterNumber.
     * Throws a RemoteException if the meterNumber is not found in Yukon. 
     */
    public Meter isYukonMeterNumber(String meterNumber) throws RemoteException;
}
