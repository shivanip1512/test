package com.cannontech.multispeak.service.v4;

import java.util.Map;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.LoadManagementEvent;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.multispeak.block.v4.Block;
import com.cannontech.multispeak.dao.v4.FormattedBlockProcessingService;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface MspValidationService {

    /**
     * Returns Meter for the meterNumber if MeterNumber is a Yukon MeterNumber.
     * Throws a RemoteException if the meterNumber is not found in Yukon.
     */
    public YukonMeter isYukonMeterNumber(String meterNumber) throws MultispeakWebServiceException;

    /**
     * Returns an ErrorObject when the scadaAnalog does not have all required information
     * to create a Yukon point translation.
     * 
     * @param scadaAnalog
     * @return
     */
    public ErrorObject isValidScadaAnalog(ScadaAnalog scadaAnalog);

    /**
     * Returns an ErrorObject when the LoadMangementEvent does not have all required information
     * to process in Yukon.
     * 
     * @param loadManagementEvent
     * @return
     */
    public ErrorObject isValidLoadManagementEvent(LoadManagementEvent loadManagementEvent);

    FormattedBlockProcessingService<Block> getProcessingServiceByFormattedBlockTemplate(
            Map<String, FormattedBlockProcessingService<Block>> formattedBlockMap, String formattedBlockTemplateName)
            throws MultispeakWebServiceException;
    
    
    /**
     * Returns an ErrorObject when the given responseURL is empty.  Does not guarantee the URL
     * is valid or that it is live.
     */
    public ErrorObject validateResponseURL(String responseURL, String nounType, String method);

}
