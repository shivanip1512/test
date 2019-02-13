package com.cannontech.dr.itron.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

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
import com.google.common.collect.Lists;

public class ItronCommunicationServiceImpl implements ItronCommunicationService {
    
    private String settingsUrl;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private ItronDao itronDao;
    @Autowired private ItronEventLogService itronEventLogService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private DeviceDao deviceDao;
    String userName = "test";

    private static final Logger log = YukonLogManager.getLogger(ItronCommunicationServiceImpl.class);

    enum Manager {
        DEVICE("DeviceManagerPort", createTemplate("com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8")),
        PROGRAM("ProgramManagerPort", createTemplate("com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1")),
        PROGRAM_EVENT("ProgramEventManagerPort", createTemplate("com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6")),
        SERVICE("ServicePointManagerPort", createTemplate("com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3"));
        
        private WebServiceTemplate template;
        private String port;
        
        Manager(String port, WebServiceTemplate template){
            this.port = port;
            this.template = template;
        }
        public WebServiceTemplate getTemplate() {
            return template;
        }
        public String getUrl(String url) {
            return url + port;
        }  
    }

    @Autowired
    public ItronCommunicationServiceImpl(GlobalSettingDao settingDao) {
        settingsUrl = settingDao.getString(GlobalSettingType.ITRON_HCM_API_URL);
        String userName = settingDao.getString(GlobalSettingType.ITRON_HCM_USERNAME);
        String password = settingDao.getString(GlobalSettingType.ITRON_HCM_PASSWORD);
        Lists.newArrayList(Manager.values()).forEach(manager -> manager.getTemplate().setMessageSender(getAuth(userName, password)));
    }
      
    @Override
    public void addDevice(Hardware hardware, AccountDto account) {
        String url = Manager.DEVICE.getUrl(settingsUrl);

        AddHANDeviceRequest request = null;
        if (account != null) {
            // TODO handle itron error if account already exist implementation pending simulator
            log.debug("ITRON-addDevice url:{} account:{} mac id:{}.", url, account.getAccountNumber(),
                hardware.getMacAddress());
            // check if we haven't created account before
            addServicePoint(account);
            request = DeviceManagerHelper.buildAddRequestWithServicePoint(hardware, account);
        } else {
            log.debug("ITRON-addDevice url:{} mac id:{}.", url, hardware.getMacAddress());
            request = DeviceManagerHelper.buildAddRequestWithoutServicePoint(hardware);
        }
        AddHANDeviceResponse response = null;
        try {
            log.debug(XmlUtils.getPrettyXml(request));
            response = (AddHANDeviceResponse) Manager.DEVICE.getTemplate().marshalSendAndReceive(url, request);
            log.debug("ITRON-addDevice url:{} mac id:{} result:{}.", url, response.getMacID(), "success");
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
    }

    @Override
    public void removeDeviceFromServicePoint(String macAddress) {
        EditHANDeviceRequest request = DeviceManagerHelper.buildEditRequestRemoveServicePoint(macAddress);
        editDevice(request); 
        itronEventLogService.removeHANDeviceFromServicePoint(macAddress, userName);
    }
    
    @Override
    public void addServicePoint(AccountDto account, String macAddress) {
        addServicePoint(account);
        addDeviceToServicePoint(macAddress, account);
    }
    
    /**
     * Checks if itron program id is in database, if doesn't exist sends request to itron to create
     * group, persist the group id returned by itron to the database.
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
    public void enroll(int accountId, int deviceId, int groupId) {
        sendEnrollmentRequest(accountId, true);
        String macAddress = deviceDao.getDeviceMacAddress(deviceId);
        addMacAddressToGroup(getGroup(groupId), macAddress);
    }
    
    @Override
    public void unenroll(int accountId) {
        sendEnrollmentRequest(accountId, false);
    }

    /**
     * Returns the list of enrollments in itron programs for account
     */
    private List<ProgramEnrollment> getItronProgramEnrollments(int accountId) {
        Map<Integer, LiteYukonPAObject> itronGroups = cache.getAllLMGroups().stream()
                .filter(group -> group.getPaoType() == PaoType.LM_GROUP_ITRON)
                .collect(Collectors.toMap(LiteYukonPAObject::getLiteID, group -> group));   
        
        List<ProgramEnrollment> enrollments = enrollmentDao.getActiveEnrollmentsByAccountId(accountId);
        enrollments.removeIf(enrollment -> !itronGroups.containsKey(enrollment.getLmGroupId()));
        return enrollments;
    }
    
    /**
     * Sends enrollment requests to Itron
     */
    private void sendEnrollmentRequest(int accountId, boolean enroll) {
        List<ProgramEnrollment> enrollments = getItronProgramEnrollments(accountId);
        
        List<Long> itronProgramIds = new ArrayList<>();
        
        if (!enrollments.isEmpty()) {
            List<Integer> assignedProgramIds =
                enrollments.stream().map(enrollment -> enrollment.getAssignedProgramId()).collect(Collectors.toList());
            Collection<Integer> programPaoIds = assignedProgramDao.getProgramIdsByAssignedProgramIds(assignedProgramIds).values();
            
            if(enroll) {
                createPrograms(programPaoIds);
                createGroups(enrollments);
            }
            itronProgramIds.addAll(itronDao.getItronProgramIds(programPaoIds).values());            
        }
        
        CustomerAccount account = customerAccountDao.getById(accountId);
   
        log.debug("Sending enrollment request to itron for account {} groups to enroll {} ", account.getAccountNumber(),
            itronProgramIds);
        SetServicePointEnrollmentRequest request =
            ProgramManagerHelper.buildEnrollmentRequest(account.getAccountNumber(), itronProgramIds);
        // TODO send to itron
        // TODO add event log
        log.debug(XmlUtils.getPrettyXml(request));
    }

    /**
     * Checks if enrollment contains the group that was not previously sent to Itron.
     * Sends the group to itron to get the itron group Id back
     */
    private void createGroups(List<ProgramEnrollment> enrollments) {    
       List<Integer> groupPaoIds = enrollments.stream()
               .map(enrollment -> enrollment.getLmGroupId())
               .collect(Collectors.toList());
        Map<Integer, Long> groupPaoIdtoItronId = itronDao.getItronGroupIds(groupPaoIds);
        groupPaoIds.stream()
            .filter(paoId -> !groupPaoIdtoItronId.containsKey(paoId))
            .forEach(paoId -> itronDao.addGroup(getGroupIdFromItron(getGroup(paoId)), paoId));
    }
    
    /**
     * Checks if enrollment contains the program that was not previously sent to Itron.
     * Sends the program to itron to get the itron program Id back
     */
    private void createPrograms(Collection<Integer> programPaoIds) {       
        Map<Integer, Long> programPaoIdtoItronId = itronDao.getItronProgramIds(programPaoIds);
        programPaoIds.stream()
            .filter(paoId -> !programPaoIdtoItronId.containsKey(paoId))
            .forEach(paoId -> itronDao.addProgram(getProgramIdFromItron(getProgram(paoId)), paoId));
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
            request.setProgramName(String.valueOf(pao.getLiteID()));;
            response = new AddProgramResponse();
            // response = (AddProgramResponse) programManagerTemplate.marshalSendAndReceive(itronUrl,
            // request);
            itronEventLogService.addProgram(response.getProgramName(), response.getProgramID(), userName);
            log.debug("AddProgramResponse - Sending request to Itron {} to add {} program is successful. Itron program id {} created",
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
     * Returns itron group id
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
        EditHANDeviceRequest request = DeviceManagerHelper.buildEditRequestWithServicePoint(macAddress, account);
        editDevice(request); 
        itronEventLogService.addHANDeviceToServicePoint(account.getAccountNumber(), macAddress, userName);
    }

    /**
     * Sends edit device request to itron
     */
    private void editDevice(EditHANDeviceRequest request) {
        String url = Manager.DEVICE.getUrl(settingsUrl);
        log.debug("ITRON-editDevice url {}", url);
        log.debug(XmlUtils.getPrettyXml(request));
        EditHANDeviceResponse response = null;
        try {
            response = (EditHANDeviceResponse) Manager.DEVICE.getTemplate().marshalSendAndReceive(url, request);
            log.debug(XmlUtils.getPrettyXml(response));
            log.debug("ITRON-editDevice mac id:{} result:{}", response.getMacID(), "success");
            if (!response.getErrors().isEmpty()) {
                throw new ItronEditDeviceException(response);
            }
        } catch (ItronEditDeviceException e) {
            throw e;
        } catch (Exception e) {
            throw new ItronCommunicationException("Communication error:" + XmlUtils.getPrettyXml(response), e);
        }
    }
    
    /**
     * Sends request to itron to add service point
     */
    private void addServicePoint(AccountDto account) {
        /*
         * Note: The newly created Service Point is not available in the user interface until an ESI is
         * associated to it. It is, however, in the database. (see DeviceManagerHelper buildRequest) 
         */
        String itronUrl = "";

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
    
    private LiteYukonPAObject getGroup(int yukonGroupId) {
        LiteYukonPAObject groupPao = cache.getAllLMGroups().stream()
                .filter(group -> group.getLiteID() == yukonGroupId)
                .findFirst().get();
        return groupPao;
    }

    private LiteYukonPAObject getProgram(int yukonProgramId) {
        LiteYukonPAObject programPao = cache.getAllLMPrograms().stream()
                .filter(program -> program.getLiteID() == yukonProgramId)
                .findFirst().get();
        return programPao;
    }
    
    /**
     * Creates a template
     */
    private static WebServiceTemplate createTemplate(String path) {
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
    
    private HttpComponentsMessageSender getAuth(String userName, String password) {
        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        httpComponentsMessageSender.setCredentials(new UsernamePasswordCredentials(userName, password));
        return httpComponentsMessageSender;
    }
}
