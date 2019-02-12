package com.cannontech.dr.itron.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.cannontech.common.events.loggers.ItronEventLogService;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.dao.ItronDao;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupRequestType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupResponseType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ObjectFactory;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.AddProgramRequest;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.AddProgramResponse;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.SetServicePointEnrollmentRequest;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AddServicePointRequest;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AddServicePointResponse;
import com.cannontech.dr.itron.service.ItronAddDeviceException;
import com.cannontech.dr.itron.service.ItronAddEditGroupException;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.dr.itron.service.ItronEditDeviceException;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;

public class ItronCommunicationServiceImpl implements ItronCommunicationService {
    
    private String url = "http://localhost:8083/";
    private String password;
    private String userName;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private ItronDao itronDao;
    @Autowired private ItronEventLogService itronEventLogService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private DeviceDao deviceDao;

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
        String mockUrl = "http://localhost:8083/DeviceManagerPort";
        
        AddHANDeviceRequest request = null;
        if (account != null) {
            //TODO handle itron error if account already exist implementation pending simulator
            log.debug("AddHANDeviceRequest - Sending request to Itron {} to add device with Mac Id {} to account {}.",
                mockUrl, hardware.getMacAddress(), account.getAccountNumber());
            //check if we haven't created account before
            addServicePoint(account);
            request = DeviceManagerHelper.buildAddRequestWithServicePoint(hardware, account);
        } else {
            log.debug("AddHANDeviceRequest - Sending request to Itron {} to add device with Mac Id {} and inventory Id {}.",
                mockUrl, hardware.getMacAddress(), hardware.getInventoryId());
            request = DeviceManagerHelper.buildAddRequestWithoutServicePoint(hardware);
        }
        AddHANDeviceResponse response = null;
        try {
            log.debug(XmlUtils.getPrettyXml(request));
            response = (AddHANDeviceResponse) deviceManagerTemplate.marshalSendAndReceive(mockUrl, request);
            itronEventLogService.addHANDevice(hardware.getDisplayName(), hardware.getMacAddress(), userName);
            log.debug(XmlUtils.getPrettyXml(response));
            if (!response.getErrors().isEmpty()) {
                throw new ItronAddDeviceException(response);
            }
        } catch (ItronAddDeviceException e) {
            throw e;
        } catch (Exception e) {
            throw new ItronCommunicationException("Communication error:" + XmlUtils.getPrettyXml(response), e);
        }
        log.debug("AddHANDeviceResponse - Request to Itron {} to add device with Mac Id {} is successful.", mockUrl,
            response.getMacID());
    }

    @Override
    public void removeDeviceFromServicePoint(String macAddress) {

        String itronUrl = url + "/editHANDevice";
        log.debug("EditHANDeviceRequest - Sending request to Itron {} to add device with Mac Id {}.", itronUrl, macAddress);
        EditHANDeviceRequest request = DeviceManagerHelper.buildEditRequestRemoveServicePoint(macAddress);
        EditHANDeviceResponse response = editDevice(request); 
        itronEventLogService.removeHANDeviceFromServicePoint(macAddress, userName);
        log.debug("EditHANDeviceResponse - Request to Itron {} to add device with Mac Id {} is successful.", itronUrl,
            response.getMacID());
    }
    
    @Override
    public void addServicePoint(AccountDto account, String macAddress) {
        addServicePoint(account);
        addDeviceToServicePoint(macAddress, account);
    }
    
    /**
     * Checks if itron program id is in database, if doesn't exist sends request to itron to create
     * group, persist the group id returned by itron to the database.
     * 
     */
    @Override
    public void createGroup(LiteYukonPAObject pao) {
        try {
            itronDao.getGroup(pao.getLiteID());
        } catch (NotFoundException e) {
            itronDao.addGroup(getGroupIdFromItron(pao), pao.getLiteID());
        }
    }

    @Override
    public void enroll(int accountId, int deviceId, LiteYukonPAObject programPao, LiteYukonPAObject groupPao) {
        createGroup(groupPao);
        createProgram(programPao);
        sendEnrollmentRequest(accountId);
        String macAddress = deviceDao.getDeviceMacAddress(deviceId);
        addMacAddressToGroup(groupPao, macAddress);
    }
    
    @Override
    public void unenroll(int accountId) {
        sendEnrollmentRequest(accountId);
    }

    /**
     * Sends enrollment requests to Itron
     */
    private void sendEnrollmentRequest(int accountId) {
        Map<Integer, LiteYukonPAObject> itronGroups = cache.getAllLMGroups().stream()
                .filter(group -> group.getPaoType() == PaoType.LM_GROUP_ITRON)
                .collect(Collectors.toMap(LiteYukonPAObject::getLiteID, group -> group));   
        
        CustomerAccount account = customerAccountDao.getById(accountId);
        List<ProgramEnrollment> enrollments = enrollmentDao.getActiveEnrollmentsByAccountId(accountId);
        enrollments.removeIf(enrollment -> !itronGroups.containsKey(enrollment.getLmGroupId()));
        List<Long> itronGroupIds = new ArrayList<>();
        
        if (!enrollments.isEmpty()) {
            List<Integer> assignedProgramIds =
                enrollments.stream().map(enrollment -> enrollment.getAssignedProgramId()).collect(Collectors.toList());
            Collection<Integer> programPaoIds =
                assignedProgramDao.getProgramIdsByAssignedProgramIds(assignedProgramIds).values();

            itronGroupIds.addAll(itronDao.getItronProgramIds(programPaoIds));
        }
   
        SetServicePointEnrollmentRequest request =
            ProgramManagerHelper.buildEnrollmentRequest(account.getAccountNumber(), itronGroupIds);
        // TODO send to itron
        // TODO add event log
        log.debug(XmlUtils.getPrettyXml(request));
    }
    
    /**
     * Checks if itron group id is in database, if doesn't exist sends request to itron to create
     * group, persist the program id returned by itron to the database.
     */
    private void createProgram(LiteYukonPAObject pao) {
        try {
            itronDao.getProgram(pao.getLiteID());
        } catch (NotFoundException e) {
            itronDao.addProgram(getProgramIdFromItron(pao), pao.getLiteID());
        }
    }

    /**
     * Sends Itron program id and receives itron program id
     */
    private long getProgramIdFromItron(LiteYukonPAObject pao) {
        // TODO use different URL
        String itronUrl = "http://localhost:8083/DeviceManagerPort";
        log.debug("AddProgramResponse - Sending request to Itron {} to add {} program.", itronUrl, pao.getPaoName());

        AddProgramResponse response = null;
        try {
            AddProgramRequest request = new AddProgramRequest();
            request.setProgramName(String.valueOf(pao.getLiteID()));
            log.debug(XmlUtils.getPrettyXml(request));
            response = new AddProgramResponse();
            // response = (AddProgramResponse) programManagerTemplate.marshalSendAndReceive(itronUrl,
            // request);
            itronEventLogService.addProgram(response.getProgramName(), response.getProgramID(), userName);
            log.debug(XmlUtils.getPrettyXml(response));

            log.debug(
                "AddProgramResponse - Sending request to Itron {} to add {} program is successful. Itron program id {} created",
                itronUrl, pao.getPaoName(), response.getProgramID());
            return response.getProgramID();
        } catch (Exception e) {
            throw new ItronCommunicationException("Communication error:" + XmlUtils.getPrettyXml(response), e);
        }
    }
    
    /**
     * Sends request to itron to add mac address to a group.
     */
    private void addMacAddressToGroup(LiteYukonPAObject groupPao, String macAddress) {
        String itronUrl = "http://localhost:8083/DeviceManagerPort";
        log.debug("ESIGroupRequestType - Sending request to Itron {} to add mac address {} to {} group.", itronUrl,
            macAddress, groupPao.getPaoName());

        ESIGroupResponseType response = null;
        try {
            ESIGroupRequestType requestType = DeviceManagerHelper.buildGroupEditRequest(groupPao, macAddress);
            JAXBElement<ESIGroupRequestType> request = new ObjectFactory().createEditESIGroupRequest(requestType);
            // TODO add event log
            log.debug(XmlUtils.getPrettyXml(request.getDeclaredType()));
            response = new ESIGroupResponseType();
            // response = (ESIGroupResponseType) deviceManagerTemplate.marshalSendAndReceive(itronUrl,
            // request);
            itronEventLogService.addGroup(String.valueOf(groupPao.getLiteID()), response.getGroupID(), userName);
            log.debug(XmlUtils.getPrettyXml(response));

            log.debug("ESIGroupRequestType - Sending request to Itron {} to add mac address {} to {} group is successful.", itronUrl,
                macAddress, groupPao.getPaoName());
            if (!response.getErrors().isEmpty()) {
                throw new ItronAddEditGroupException(response);
            }
        } catch (ItronAddEditGroupException e) {
            throw e;
        } catch (Exception e) {
            throw new ItronCommunicationException("Communication error:" + XmlUtils.getPrettyXml(response), e);
        }
    }
    
    /**
     * Sends Itron group id and receives for itron group id
     */
    private long getGroupIdFromItron(LiteYukonPAObject pao) {
        String itronUrl = "http://localhost:8083/DeviceManagerPort";
        log.debug("ESIGroupRequestType - Sending request to Itron {} to add {} group.", itronUrl, pao.getPaoName());

        ESIGroupResponseType response = null;
        try {
            ESIGroupRequestType requestType = DeviceManagerHelper.buildGroupAddRequest(pao);
            JAXBElement<ESIGroupRequestType> request = new ObjectFactory().createAddESIGroupRequest(requestType);
            log.debug(XmlUtils.getPrettyXml(request.getDeclaredType()));
            response = new ESIGroupResponseType();
            //response = (ESIGroupResponseType) deviceManagerTemplate.marshalSendAndReceive(itronUrl, request);
            itronEventLogService.addGroup(String.valueOf(pao.getLiteID()), response.getGroupID(), userName);
            log.debug(XmlUtils.getPrettyXml(response));
            
            log.debug("ESIGroupResponseType - Sending request to Itron {} to add {} group is successful", itronUrl,
                pao.getPaoName(), response.getGroupID());
            if (!response.getErrors().isEmpty()) {
                throw new ItronAddEditGroupException(response);
            }
            return response.getGroupID();
        } catch (ItronAddEditGroupException e) {
            throw e;
        } catch (Exception e) {
            throw new ItronCommunicationException("Communication error:" + XmlUtils.getPrettyXml(response), e);
        }
    }
    
    /**
     * Sends request to itron to add device to a service point
     */
    private void addDeviceToServicePoint(String macAddress, AccountDto account) {
        String itronUrl = url + "/editHANDevice";
        log.debug("EditHANDeviceRequest - Sending request to Itron {} to add device to a service point with Mac Id {} account number {}.",
            itronUrl, macAddress, account.getAccountNumber());
        EditHANDeviceRequest request = DeviceManagerHelper.buildEditRequestWithServicePoint(macAddress, account);
        EditHANDeviceResponse response = editDevice(request); 
        itronEventLogService.addHANDeviceToServicePoint(account.getAccountNumber(), macAddress, userName);
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
            log.debug(XmlUtils.getPrettyXml(response));
            if (!response.getErrors().isEmpty()) {
                throw new ItronEditDeviceException(response);
            }
        } catch (ItronEditDeviceException e) {
            throw e;
        } catch (Exception e) {
            throw new ItronCommunicationException("Communication error:" + XmlUtils.getPrettyXml(response), e);
        }
        return response;
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
        itronEventLogService.addServicePoint(account.getAccountNumber(), userName);
        log.debug(XmlUtils.getPrettyXml(response));
        log.debug("AddServicePointResponse - Request to Itron {} to add service point for account {} is successful.",
            itronUrl, account.getAccountNumber());
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
