package com.cannontech.multispeak.client.core.v5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.cannontech.msp.beans.v5.not_server.CDStatesChangedNotification;
import com.cannontech.msp.beans.v5.not_server.EndDeviceEventsNotification;
import com.cannontech.msp.beans.v5.not_server.EndDeviceStatesNotification;
import com.cannontech.msp.beans.v5.not_server.FormattedBlockNotification;
import com.cannontech.msp.beans.v5.not_server.GetMethods;
import com.cannontech.msp.beans.v5.not_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.not_server.MeterReadingsNotification;
import com.cannontech.msp.beans.v5.not_server.PingURL;
import com.cannontech.msp.beans.v5.not_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v5.CustomWebServiceMsgCallback;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class NOTClient implements INOTClient {
    private WebServiceTemplate webServiceTemplate;
    private HttpComponentsMessageSender messageSender;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;

    /**
     * NOT Client Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public NOTClient(@Qualifier("webServiceTemplateV5") WebServiceTemplate webServiceTemplate) {

        this.webServiceTemplate = webServiceTemplate;
        messageSender = (HttpComponentsMessageSender) webServiceTemplate.getMessageSenders()[0];
    }

    @Override
    public GetMethodsResponse getMethods(final MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (PingURLResponse) webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public void endDeviceStatesNotification(MultispeakVendor mspVendor, String uri,
            EndDeviceStatesNotification endDeviceStatesNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            webServiceTemplate.marshalSendAndReceive(uri, endDeviceStatesNotification,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public void cdStatesChangedNotification(MultispeakVendor mspVendor, String uri,
            CDStatesChangedNotification cdStatesChangedNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            webServiceTemplate.marshalSendAndReceive(uri, cdStatesChangedNotification,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public void endDeviceEventsNotification(MultispeakVendor mspVendor, String uri,
            EndDeviceEventsNotification deviceEventsNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            webServiceTemplate.marshalSendAndReceive(uri, deviceEventsNotification,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public void meterReadingsNotification(MultispeakVendor mspVendor, String uri,
            MeterReadingsNotification meterReadingsNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            webServiceTemplate.marshalSendAndReceive(uri, meterReadingsNotification,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public void formattedBlockNotification(MultispeakVendor mspVendor, String uri,
            FormattedBlockNotification formattedBlockNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            webServiceTemplate.marshalSendAndReceive(uri, formattedBlockNotification,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

}
