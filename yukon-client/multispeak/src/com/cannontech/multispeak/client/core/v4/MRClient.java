package com.cannontech.multispeak.client.core.v4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.msp.beans.v4.DeleteMeterGroup;
import com.cannontech.msp.beans.v4.DeleteMeterGroupResponse;
import com.cannontech.msp.beans.v4.EstablishMeterGroup;
import com.cannontech.msp.beans.v4.EstablishMeterGroupResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.GetReadingsByDate;
import com.cannontech.msp.beans.v4.GetReadingsByDateResponse;
import com.cannontech.msp.beans.v4.InsertMeterInMeterGroup;
import com.cannontech.msp.beans.v4.InsertMeterInMeterGroupResponse;
import com.cannontech.msp.beans.v4.MeterRemoveNotification;
import com.cannontech.msp.beans.v4.MeterRemoveNotificationResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.msp.beans.v4.RemoveMetersFromMeterGroup;
import com.cannontech.msp.beans.v4.RemoveMetersFromMeterGroupResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class MRClient implements IMRClient {
    private WebServiceTemplate webServiceTemplate;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private MultispeakFuncs multispeakFuncs;

    /**
     * MR Client Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired

    public MRClient(@Qualifier("webServiceTemplateV4") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (PingURLResponse) webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MR_Server_STR));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }
    
    public GetReadingsByDateResponse getReadingsByDate(MultispeakVendor mspVendor, String uri,
            GetReadingsByDate getReadingsByDate) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetReadingsByDateResponse) webServiceTemplate.marshalSendAndReceive(uri, getReadingsByDate,
                customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MR_Server_STR));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetMethodsResponse getMethods(final MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MR_Server_STR));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public MeterRemoveNotificationResponse meterRemoveNotification(MultispeakVendor mspVendor, String uri,
            MeterRemoveNotification meterRemoveNotification) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (MeterRemoveNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri,
                meterRemoveNotification, customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MR_Server_STR));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    public EstablishMeterGroupResponse establishMeterGroup(MultispeakVendor mspVendor, String uri,
            EstablishMeterGroup establishMeterGroup) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (EstablishMeterGroupResponse) webServiceTemplate.marshalSendAndReceive(uri, establishMeterGroup,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MR_Server_STR));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public InsertMeterInMeterGroupResponse insertMeterInMeterGroup(MultispeakVendor mspVendor, String uri,
            InsertMeterInMeterGroup insertMeterInMeterGroup) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (InsertMeterInMeterGroupResponse) webServiceTemplate.marshalSendAndReceive(uri,
                    insertMeterInMeterGroup,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MR_Server_STR));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public DeleteMeterGroupResponse deleteMeterGroup(MultispeakVendor mspVendor, String uri,
            DeleteMeterGroup deleteMeterGroup) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (DeleteMeterGroupResponse) webServiceTemplate.marshalSendAndReceive(uri, deleteMeterGroup,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MR_Server_STR));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public RemoveMetersFromMeterGroupResponse removeMetersFromMeterGroup(MultispeakVendor mspVendor, String uri,
            RemoveMetersFromMeterGroup removeMetersFromMeterGroup) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (RemoveMetersFromMeterGroupResponse) webServiceTemplate.marshalSendAndReceive(uri,
                    removeMetersFromMeterGroup,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MR_Server_STR));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }
}
