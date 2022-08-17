package com.cannontech.multispeak.client.core.v5;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.not_server.ObjectFactory;
import com.cannontech.msp.beans.v5.not_server.CDStatesChangedNotification;
import com.cannontech.msp.beans.v5.not_server.DRProgramEnrollmentsNotification;
import com.cannontech.msp.beans.v5.not_server.DRProgramUnenrollmentsNotification;
import com.cannontech.msp.beans.v5.not_server.EndDeviceEventsNotification;
import com.cannontech.msp.beans.v5.not_server.EndDeviceStatesNotification;
import com.cannontech.msp.beans.v5.not_server.FormattedBlockNotification;
import com.cannontech.msp.beans.v5.not_server.GetMethods;
import com.cannontech.msp.beans.v5.not_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.not_server.IntervalDataNotification;
import com.cannontech.msp.beans.v5.not_server.MeterReadingsNotification;
import com.cannontech.msp.beans.v5.not_server.PingURL;
import com.cannontech.msp.beans.v5.not_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.client.core.v5.CustomWebServiceMsgCallback;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class NOTClient implements INOTClient {
    private WebServiceTemplate webServiceTemplate;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    /**
     * NOT Client Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public NOTClient(@Qualifier("webServiceTemplateV5") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public List<String> getMethods(final MultispeakVendor mspVendor, String uri)
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
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
        return methodList;
    }

    @Override
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException {
        try {
            PingURL pingURL = objectFactory.createPingURL();
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

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
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

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
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

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
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

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
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

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
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            webServiceTemplate.marshalSendAndReceive(uri, formattedBlockNotification,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public void drProgramEnrollmentsNotification(MultispeakVendor mspVendor, String uri, DRProgramEnrollmentsNotification drProgramEnrollmentsNotification)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            webServiceTemplate.marshalSendAndReceive(uri, drProgramEnrollmentsNotification, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }

    }

    @Override
    public void drProgramUnenrollmentsNotification(MultispeakVendor mspVendor, String uri,
            DRProgramUnenrollmentsNotification drProgramUnenrollmentsNotification) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            webServiceTemplate.marshalSendAndReceive(uri, drProgramUnenrollmentsNotification, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public void intervalDataNotification(MultispeakVendor mspVendor, String uri, IntervalDataNotification intervalDataNotification)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            webServiceTemplate.marshalSendAndReceive(uri, intervalDataNotification, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public void alarmAndEventDataNotification(MultispeakVendor mspVendor, String uri, EndDeviceEventsNotification endDeviceEventsNotification)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            webServiceTemplate.marshalSendAndReceive(uri, endDeviceEventsNotification, customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }
}
