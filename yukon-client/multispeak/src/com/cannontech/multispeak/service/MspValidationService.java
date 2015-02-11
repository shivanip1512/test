package com.cannontech.multispeak.service;

import java.util.Map;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.dao.FormattedBlockProcessingService;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;


public interface MspValidationService {

    /**
     * Returns the processingService for the readingTypesMap for readingType.
     * Throws a MultispeakWebServiceException if the readingType is not valid.
     */
    public FormattedBlockProcessingService<Block> getProcessingServiceByReadingType(Map<String, FormattedBlockProcessingService<Block>> readingTypesMap,
                                                                String readingType) throws MultispeakWebServiceException;
    /**
     * Returns Meter for the meterNumber if MeterNumber is a Yukon MeterNumber.
     * Throws a RemoteException if the meterNumber is not found in Yukon. 
     */
    public YukonMeter isYukonMeterNumber(String meterNumber) throws MultispeakWebServiceException;
    
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

    /**
     * Returns an ErrorObject when the given responseURL is empty.  Does not guarantee the URL
     * is valid or that it is live.
     */
    public ErrorObject validateResponseURL(String responseURL, String nounType, String method);
}
