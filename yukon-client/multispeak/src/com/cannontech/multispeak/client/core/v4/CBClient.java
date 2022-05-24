package com.cannontech.multispeak.client.core.v4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.msp.beans.v4.GetAllServiceLocations;
import com.cannontech.msp.beans.v4.GetAllServiceLocationsResponse;
import com.cannontech.msp.beans.v4.GetCustomerByMeterID;
import com.cannontech.msp.beans.v4.GetCustomerByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetMeterByMeterID;
import com.cannontech.msp.beans.v4.GetMeterByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetMeterByServiceLocationID;
import com.cannontech.msp.beans.v4.GetMeterByServiceLocationIDResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.GetServiceLocationByMeterID;
import com.cannontech.msp.beans.v4.GetServiceLocationByMeterIDResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class CBClient implements ICBClient {

    private WebServiceTemplate webServiceTemplate;
    @Autowired CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private MultispeakFuncs multispeakFuncs;

    /**
     * CBClient Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public CBClient(@Qualifier("webServiceTemplateV4") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException {

        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (PingURLResponse) webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }

    }

    @Override
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }

    }
    
    @Override
    public GetMeterByMeterIDResponse getMeterByMeterID(final MultispeakVendor mspVendor, String uri,
            GetMeterByMeterID getMeterByMeterId) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetMeterByMeterIDResponse) webServiceTemplate.marshalSendAndReceive(uri, getMeterByMeterId,
                customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }
    
    @Override
    public GetCustomerByMeterIDResponse getCustomerByMeterId(final MultispeakVendor mspVendor, String uri,
            GetCustomerByMeterID getCustomerByMeterId) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetCustomerByMeterIDResponse) webServiceTemplate.marshalSendAndReceive(uri, getCustomerByMeterId,
                customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }
    
    @Override
    public GetServiceLocationByMeterIDResponse getServiceLocationByMeterId(final MultispeakVendor mspVendor,
            String uri, GetServiceLocationByMeterID getServiceLocationByMeterId)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetServiceLocationByMeterIDResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getServiceLocationByMeterId, customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetAllServiceLocationsResponse getAllServiceLocations(MultispeakVendor mspVendor, String uri,
            GetAllServiceLocations getAllServiceLocations) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetAllServiceLocationsResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getAllServiceLocations, customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMeterByServiceLocationIDResponse getMeterByServiceLocationID(MultispeakVendor mspVendor, String endpointUrl,
            GetMeterByServiceLocationID getMeterByServLocID) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetMeterByServiceLocationIDResponse) webServiceTemplate.marshalSendAndReceive(endpointUrl,
                    getMeterByServLocID,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }
}
