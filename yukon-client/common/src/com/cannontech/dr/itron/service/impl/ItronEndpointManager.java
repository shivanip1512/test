package com.cannontech.dr.itron.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;

import org.apache.logging.log4j.Logger;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public enum ItronEndpointManager {
    DEVICE("DeviceManagerPort", "com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8"),
    PROGRAM("ProgramManagerPort", "com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1"),
    PROGRAM_EVENT("ProgramEventManagerPort", "com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6"),
    SERVICE_POINT("ServicePointManagerPort", "com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3"),
    REPORT("ReportManagerPort", "com.cannontech.dr.itron.model.jaxb.reportManagerTypes_v1_2");

    private WebServiceTemplate template;
    private String port;
    private Jaxb2Marshaller marshaller;
    private Logger log = YukonLogManager.getLogger(ItronEndpointManager.class);

    ItronEndpointManager(String port, String path) {
        this.port = port;
        marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(path);
        try {
            marshaller.afterPropertiesSet();
            MessageFactory saajFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SoapMessageFactory factory = new SaajSoapMessageFactory(saajFactory);
            template = new WebServiceTemplate(factory);
            template.setMarshaller(marshaller);
            template.setUnmarshaller(marshaller);
        } catch (Exception e) {
            log.error("Unable to create template or marshaller", e);
        }
    }

    public WebServiceTemplate getTemplate(GlobalSettingDao settingDao) {
        String userName = settingDao.getString(GlobalSettingType.ITRON_HCM_USERNAME);
        String password = settingDao.getString(GlobalSettingType.ITRON_HCM_PASSWORD);
        ClientInterceptor[] interceptors = { new ItronCommunicationInterceptor(userName, password) };
        template.setInterceptors(interceptors);
        return template;
    }

    public Jaxb2Marshaller getMarshaller() {
        return marshaller;
    }

    public String getUrl(GlobalSettingDao settingDao) {
        String url = settingDao.getString(GlobalSettingType.ITRON_HCM_API_URL);
        return url + port;
    }

    private class ItronCommunicationInterceptor implements ClientInterceptor {
        private String username;
        private String password;

        public ItronCommunicationInterceptor(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void afterCompletion(MessageContext arg0, Exception arg1) throws WebServiceClientException {
        }

        @Override
        public boolean handleFault(MessageContext context) throws WebServiceClientException {
            // logs soap fault
            SoapMessage message = (SoapMessage) context.getResponse();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                message.writeTo(out);
                String msg = out.toString("UTF-8");
                log.debug(msg);
            } catch (Exception e) {
                log.debug("Failed to parse soap fault", e);
            }
            return true;
        }

        @Override
        public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
            TransportContext context = TransportContextHolder.getTransportContext();
            String userCredentials = username + ":" + password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
            HttpUrlConnection conn = (HttpUrlConnection) context.getConnection();
            conn.getConnection().setRequestProperty("authorization", basicAuth);
            //Soap 1.1 uses "text/xml". Soap 1.2 uses "application/soap+xml"
            //Itron uses 1.1 as indicated by namespace http://schemas.xmlsoap.org/wsdl/soap/ in WSDLs
            conn.getConnection().addRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            return true;
        }

        @Override
        public boolean handleResponse(MessageContext arg0) throws WebServiceClientException {
            return false;
        }
    }
}
