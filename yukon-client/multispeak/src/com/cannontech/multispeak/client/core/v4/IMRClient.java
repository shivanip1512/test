package com.cannontech.multispeak.client.core.v4;


import com.cannontech.msp.beans.v4.GetReadingsByDate;
import com.cannontech.msp.beans.v4.GetReadingsByDateResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IMRClient {

    /**
     * get Readings By Date
     * 
     * @param mspVendor
     * @param uri
     * @param getReadingsByDate
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetReadingsByDateResponse getReadingsByDate(MultispeakVendor mspVendor, String uri,
            GetReadingsByDate getReadingsByDate) throws MultispeakWebServiceClientException;


}
