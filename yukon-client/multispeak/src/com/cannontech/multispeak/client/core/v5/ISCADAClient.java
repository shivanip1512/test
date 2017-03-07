package com.cannontech.multispeak.client.core.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.scada_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface ISCADAClient {
    /**
     * Ping URL
     * 
     * @return
     * @throws MultispeakWebServiceException
     * @throws MultispeakWebServiceClientException 
     */
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri) throws MultispeakWebServiceException, MultispeakWebServiceClientException;

    /** Get Methods
     * @param mspVendor
     * @param uri
     * @return
     * @throws MultispeakWebServiceClientException
     */
    List<String> getMethods(MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException;

}
