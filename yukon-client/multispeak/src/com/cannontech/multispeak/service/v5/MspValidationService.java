package com.cannontech.multispeak.service.v5;

import java.util.List;
import java.util.Map;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.multispeak.LoadManagementEvent;
import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;
import com.cannontech.multispeak.block.v5.Block;
import com.cannontech.multispeak.dao.v5.FormattedBlockProcessingService;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;


public interface MspValidationService {
    
    /**
     * Returns Meter for the meterNumber if MeterNumber is a Yukon MeterNumber.
     * Throws a MultispeakWebServiceException if the meterNumber is not found in Yukon. 
     */
    public YukonMeter isYukonMeterNumber(String meterNumber) throws MultispeakWebServiceException;

    /**
     * Returns true if meter count is less than max Records and false if meter count is more than max records
     * and also the update responseHeader.resultIdentifier.replyCodeCategory (index of “3” (Too many entities
     * in result set).)
     * Throws a MultispeakWebServiceException if meterID is null or empty list
     */
    public boolean isValidMeterCount(List<MeterID> meterIDs, int maxRecords) throws MultispeakWebServiceException;
    
    /**
     * Returns an ErrorObject when the scadaAnalog does not have all required information
     * to create a Yukon point translation.
     * @param scadaAnalog
     * @return
     */
    public ErrorObject isValidScadaAnalog(SCADAAnalog scadaAnalog);
    
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

    /**
     * Returns the processingService for the readingTypesMap for readingType.
     * Throws a MultispeakWebServiceException if the readingType is not valid.
     */
    public FormattedBlockProcessingService<Block> getProcessingServiceByReadingType(
            Map<String, FormattedBlockProcessingService<Block>> readingTypesMap, String readingType)
            throws MultispeakWebServiceException;

}
