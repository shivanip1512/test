package com.cannontech.multispeak.client.core.v5;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.msp.beans.v5.cb_server.GetAllServiceLocations;
import com.cannontech.msp.beans.v5.cb_server.GetAllServiceLocationsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetCustomersByMeterIDs;
import com.cannontech.msp.beans.v5.cb_server.GetCustomersByMeterIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetDomainsByDomainNames;
import com.cannontech.msp.beans.v5.cb_server.GetDomainsByDomainNamesResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByAccountIDs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByAccountIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByContactInfo;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByContactInfoResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByCustomerIDs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByCustomerIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByNetworkModelRefs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByNetworkModelRefsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersBySearchString;
import com.cannontech.msp.beans.v5.cb_server.GetMetersBySearchStringResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByServiceLocationIDs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByServiceLocationIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMethods;
import com.cannontech.msp.beans.v5.cb_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetServiceLocationsByMeterIDs;
import com.cannontech.msp.beans.v5.cb_server.GetServiceLocationsByMeterIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.ObjectFactory;
import com.cannontech.msp.beans.v5.cb_server.PingURL;
import com.cannontech.msp.beans.v5.cb_server.PingURLResponse;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class CBClient implements ICBClient {
    private WebServiceTemplate webServiceTemplate;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    /**
     * CBClient Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public CBClient(@Qualifier("webServiceTemplateV5") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException {
        try {
            PingURL pingURL = objectFactory.createPingURL();
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (PingURLResponse) webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public List<String> getMethods(MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException {
        List<String> methodList = new ArrayList<>();
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            GetMethods getMethods = objectFactory.createGetMethods();
            
            GetMethodsResponse response =
                (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor));
            if (response != null) {
                ArrayOfString arrayOfString = response.getArrayOfString();
                if (arrayOfString != null) {
                    methodList = arrayOfString.getTheString();
                }
            }
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
        return methodList;
    }

    @Override
    public GetMetersByContactInfoResponse getMetersByContactInfo(MultispeakVendor mspVendor, String uri,
            GetMetersByContactInfo getMetersByContactInfo) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetMetersByContactInfoResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getMetersByContactInfo, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMetersByAccountIDsResponse getMetersByAccountIDs(MultispeakVendor mspVendor, String uri,
            GetMetersByAccountIDs getMetersByAccountIDs) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetMetersByAccountIDsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMetersByAccountIDs,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMetersByCustomerIDsResponse getMetersByCustomerIDs(MultispeakVendor mspVendor, String uri,
            GetMetersByCustomerIDs getMetersByCustomerIDs) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetMetersByCustomerIDsResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getMetersByCustomerIDs, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMetersBySearchStringResponse getMetersBySearchString(MultispeakVendor mspVendor, String uri,
            GetMetersBySearchString getMetersBySearchString) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetMetersBySearchStringResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getMetersBySearchString, customWebServiceMsgCallback.addRequestHeader(mspVendor));
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
                getAllServiceLocations, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetDomainsByDomainNamesResponse getDomainsByDomainNames(MultispeakVendor mspVendor, String uri,
            GetDomainsByDomainNames getDomainsByDomainNames) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetDomainsByDomainNamesResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getDomainsByDomainNames, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMetersByNetworkModelRefsResponse getMetersByNetworkModelRef(MultispeakVendor mspVendor, String uri,
            GetMetersByNetworkModelRefs getMetersByNetworkModelRefs) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetMetersByNetworkModelRefsResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getMetersByNetworkModelRefs, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetCustomersByMeterIDsResponse getCustomersByMeterIDs(MultispeakVendor mspVendor, String uri,
            GetCustomersByMeterIDs getCustomersByMeterIDs) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetCustomersByMeterIDsResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getCustomersByMeterIDs, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetServiceLocationsByMeterIDsResponse getServiceLocationsByMeterIDs(MultispeakVendor mspVendor, String uri,
            GetServiceLocationsByMeterIDs getServiceLocationsByMeterIDs) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetServiceLocationsByMeterIDsResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getServiceLocationsByMeterIDs, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMetersByServiceLocationIDsResponse getMetersByServiceLocationIDs(MultispeakVendor mspVendor, String uri,
            GetMetersByServiceLocationIDs getMetersByServiceLocationIDs) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetMetersByServiceLocationIDsResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getMetersByServiceLocationIDs, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

}
