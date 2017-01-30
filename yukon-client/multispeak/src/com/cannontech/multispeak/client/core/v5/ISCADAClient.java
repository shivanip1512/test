package com.cannontech.multispeak.client.core.v5;

import com.cannontech.msp.beans.v5.scada_server.GetMethods;
import com.cannontech.msp.beans.v5.scada_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.scada_server.PingURL;
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
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri, PingURL pingURL) throws MultispeakWebServiceException, MultispeakWebServiceClientException;

    /** Get Methods
     * @param mspVendor
     * @param uri
     * @param getMethods
     * @return
     * @throws MultispeakWebServiceClientException
     */
    GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

}
