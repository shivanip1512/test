package com.cannontech.dr.itron.service.impl;

import javax.xml.bind.JAXBElement;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupRequestType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupResponseType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ObjectFactory;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.AddProgramRequest;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.AddProgramResponse;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AddServicePointRequest;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AddServicePointResponse;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.dr.itron.service.ItronException;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class ItronCommunicationServiceImpl implements ItronCommunicationService {
    
    private String url = "http://localhost:8083/";
    private String password;
    private String userName;

    private static final Logger log = YukonLogManager.getLogger(ItronCommunicationServiceImpl.class);

    private static WebServiceTemplate deviceManagerTemplate =
        getTemplate("com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8");
    private static WebServiceTemplate servicePointTemplate =
            getTemplate("com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3");
    private static WebServiceTemplate programManagerTemplate =
            getTemplate("com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1");

    @Autowired
    public ItronCommunicationServiceImpl(GlobalSettingDao settingDao) {
        url = "http://localhost:8083/itronSimulatorServer";
        // settingDao.getString(GlobalSettingType.ITRON_HCM_API_URL);
        userName = settingDao.getString(GlobalSettingType.ITRON_HCM_USERNAME);
        password = settingDao.getString(GlobalSettingType.ITRON_HCM_PASSWORD);
    }
      
    @Override
    public void addDevice(Hardware hardware, AccountDto account) {
        //String itronUrl = "http://localhost:8083/DeviceManagerPort";
        String itronUrl = "http://localhost:8083/DeviceManagerPort";
        AddHANDeviceRequest request = null;
        if (account != null) {
            //TODO handle itron error if account already exist implementation pending simulator
            log.debug("AddHANDeviceRequest - Sending request to Itron {} to add device with Mac Id {} to account {}.",
                itronUrl, hardware.getMacAddress(), account.getAccountNumber());
            //check if we haven't created account before
            addServicePoint(account);
            request = DeviceManagerHelper.buildAddRequestWithServicePoint(hardware, account);
        } else {
            log.debug("AddHANDeviceRequest - Sending request to Itron {} to add device with Mac Id {} and inventory Id {}.",
                itronUrl, hardware.getMacAddress(), hardware.getInventoryId());
            request = DeviceManagerHelper.buildAddRequestWithoutServicePoint(hardware);
        }
              
        AddHANDeviceResponse response = null;
        try {
            log.debug(XmlUtils.getPrettyXml(request));

           // response = (AddHANDeviceResponse) deviceManagerTemplate.marshalSendAndReceive(url, request);
            response = new AddHANDeviceResponse();
            response = (AddHANDeviceResponse) deviceManagerTemplate.marshalSendAndReceive(url, request);
            //TODO add event log - yukon.common.events.dr.itron.addHANDevice
            log.debug(XmlUtils.getPrettyXml(response));
            if (!response.getErrors().isEmpty()) {
                throw new ItronException("Error recieved from Itron:" + XmlUtils.getPrettyXml(response));
            }
        } catch (ItronException e) {
            e.setErrorAddDevice(response);
            throw e;
        } catch (Exception e) {
            ItronException itronException = response == null? new ItronException("Communication error", e) :
                new ItronException("Communication error:" + XmlUtils.getPrettyXml(response), e);
            itronException.setErrorAddDevice(response);
            throw itronException;
        }
        log.debug("AddHANDeviceResponse - Request to Itron {} to add device with Mac Id {} is successful.", itronUrl,
            response.getMacID());
    }

    private void addDeviceToServicePoint(String macAddress, AccountDto account) {
        String itronUrl = url + "/editHANDevice";
        log.debug("EditHANDeviceRequest - Sending request to Itron {} to add device to a service point with Mac Id {} account number {}.",
            itronUrl, macAddress, account.getAccountNumber());
        EditHANDeviceRequest request = DeviceManagerHelper.buildEditRequestWithServicePoint(macAddress, account);
        EditHANDeviceResponse response = editDevice(request); 
        //TODO add event log - yukon.common.events.dr.itron.addHANDeviceToServicePoint
        log.debug("EditHANDeviceResponse - Request to Itron {} to add device with Mac Id {} is successful.", itronUrl,
            response.getMacID());
    }

    /**
     * Sends edit device request to itron
     */
    private EditHANDeviceResponse editDevice(EditHANDeviceRequest request) {
        // EditHANDeviceResponse response = (EditHANDeviceResponse) deviceManagerTemplate.marshalSendAndReceive(url, request);
        EditHANDeviceResponse response = new EditHANDeviceResponse();
        try {
            log.debug(XmlUtils.getPrettyXml(request));
            response = new EditHANDeviceResponse();
            log.debug(XmlUtils.getPrettyXml(response));
            if (!response.getErrors().isEmpty()) {
                throw new ItronException("Error recieved from Itron:" + XmlUtils.getPrettyXml(response));
            }
        } catch (ItronException e) {
            e.setErrorEditDevice(response);
            throw e;
        } catch (Exception e) {
            ItronException itronException = response == null? new ItronException("Communication error", e) :
                new ItronException("Communication error:" + XmlUtils.getPrettyXml(response), e);
            itronException.setErrorEditDevice(response);
            throw itronException;
        }
        return response;
    }
    
    @Override
    public void removeDeviceFromServicePoint(String macAddress) {

        String itronUrl = url + "/editHANDevice";
        log.debug("EditHANDeviceRequest - Sending request to Itron {} to add device with Mac Id {}.", itronUrl, macAddress);
        EditHANDeviceRequest request = DeviceManagerHelper.buildEditRequestRemoveServicePoint(macAddress);
        EditHANDeviceResponse response = editDevice(request); 
        //TODO add event log - yukon.common.events.dr.itron.removeHANDeviceFromServicePoint
        log.debug("EditHANDeviceResponse - Request to Itron {} to add device with Mac Id {} is successful.", itronUrl,
            response.getMacID());
    }
    
    @Override
    public void addServicePoint(AccountDto account, String macAddress) {
        addServicePoint(account);
        addDeviceToServicePoint(macAddress, account);
    }
    
    /**
     * Sends request to itron to add service point
     */
    private void addServicePoint(AccountDto account) {
        /*
         * Note: The newly created Service Point is not available in the user interface until an ESI is
         * associated to it. It is, however, in the database. (see DeviceManagerHelper buildRequest) 
         */
        String itronUrl = url + "/addServicePoint";

        AddServicePointRequest request = ServicePointHelper.buildAddRequest(account);
        log.debug(XmlUtils.getPrettyXml(request));
        log.debug("AddServicePointRequest - Sending request to Itron {} to add service point for account {}.", itronUrl,
            account.getAccountNumber());
        // AddServicePointResponse response = (AddServicePointResponse) servicePointTemplate.marshalSendAndReceive(url, request);
        
        AddServicePointResponse response= new AddServicePointResponse();
        //TODO add event log - yukon.common.events.dr.itron.addServicePoint
        log.debug(XmlUtils.getPrettyXml(response));
        log.debug("AddServicePointResponse - Request to Itron {} to add service point for account {} is successful.",
            itronUrl, account.getAccountNumber());
    }
    
    @Override
    public long getGroup(LiteYukonPAObject pao) {
        // TODO check is pao id is in the table and return itron id otherwise send request to itron

        String itronUrl = "http://localhost:8083/DeviceManagerPort";
        log.debug("ESIGroupRequestType - Sending request to Itron {} to add {} group.", itronUrl, pao.getPaoName());

        ESIGroupResponseType response = null;
        long itronGroupId = 0;
        try {
            ESIGroupRequestType requestType = new ESIGroupRequestType();
            requestType.setGroupName(String.valueOf(pao.getLiteID()));
            JAXBElement<ESIGroupRequestType> request = new ObjectFactory().createAddESIGroupRequest(requestType);
            log.debug(XmlUtils.getPrettyXml(request));
            response = new ESIGroupResponseType();
            //response = (ESIGroupResponseType) deviceManagerTemplate.marshalSendAndReceive(itronUrl, request);
            //TODO add event log - yukon.common.events.dr.itron.addGroup
            log.debug(XmlUtils.getPrettyXml(response));
            
            //TODO persist to a table
            response.getGroupID();
            log.debug("ESIGroupResponseType - Sending request to Itron {} to add {} group is successful", itronUrl,
                pao.getPaoName(), response.getGroupID());
            return itronGroupId;
            
        } catch (ItronException e) {
            e.setErrorAddGroup(response);
            throw e;
        } catch (Exception e) {
            ItronException itronException = response == null? new ItronException("Communication error", e) :
                new ItronException("Communication error:" + XmlUtils.getPrettyXml(response), e);
            itronException.setErrorAddGroup(response);
            throw itronException;
        }
    }
    
    @Override
    public long getProgram(LiteYukonPAObject pao) {        
                
        // TODO check is pao id is in the table and return itron id otherwise send request to itron
        long itronProgramId = 0;

        // TODO use different URL
        String itronUrl = "http://localhost:8083/DeviceManagerPort";
        log.debug("AddProgramResponse - Sending request to Itron {} to add {} program.", itronUrl, pao.getPaoName());

        AddProgramResponse response = null;
        try {            
            AddProgramRequest request = new AddProgramRequest();
            request.setProgramName(String.valueOf(pao.getLiteID()));
            log.debug(XmlUtils.getPrettyXml(request));
            response = new AddProgramResponse();
            //response = (AddProgramResponse) programManagerTemplate.marshalSendAndReceive(itronUrl, request);
            //TODO add event log - yukon.common.events.dr.itron.addProgram
            log.debug(XmlUtils.getPrettyXml(response));
            
            // TODO persist to a table
            response.getProgramID();
            log.debug("AddProgramResponse - Sending request to Itron {} to add {} program is successful. Itron program id {} created",
                itronUrl, pao.getPaoName(), response.getProgramID());
            return itronProgramId;
        } catch (ItronException e) {
            e.setErrorAddProgram(response);
            throw e;
        } catch (Exception e) {
            ItronException itronException = response == null? new ItronException("Communication error", e) :
                new ItronException("Communication error:" + XmlUtils.getPrettyXml(response), e);
            itronException.setErrorAddProgram(response);
            throw itronException;
        }  
    }
    
    /**
     * Creates a template
     */
    private static WebServiceTemplate getTemplate(String path) {
        try {
            Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
            marshaller.setContextPath(path);
            marshaller.afterPropertiesSet();
            MessageFactory saajFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SoapMessageFactory factory = new SaajSoapMessageFactory(saajFactory);
    
            WebServiceTemplate template = new WebServiceTemplate(factory);
            template.setMarshaller(marshaller);
            template.setUnmarshaller(marshaller);
            template.afterPropertiesSet();
            return template;
        } catch(Exception e) {
            log.error("Unable to initaialize template for path " + path, e);
            return null;
        }
    }
}
