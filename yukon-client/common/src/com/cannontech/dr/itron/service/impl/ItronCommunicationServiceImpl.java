package com.cannontech.dr.itron.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.ItronEventLogService;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.dao.ItronDao;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupRequestType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupResponseType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ObjectFactory;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.CancelHANLoadControlProgramEventOnDevicesRequest;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.CancelHANLoadControlProgramEventOnDevicesResponse;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.AddProgramRequest;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.AddProgramResponse;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.SetServicePointEnrollmentRequest;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.SetServicePointEnrollmentResponse;
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
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ItronCommunicationServiceImpl implements ItronCommunicationService {
    
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private ItronDao itronDao;
    @Autowired private ItronEventLogService itronEventLogService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private DeviceDao deviceDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private GlobalSettingDao settingDao;

    private static final Logger log = YukonLogManager.getLogger(ItronCommunicationServiceImpl.class);

    private enum Manager {
        DEVICE("DeviceManagerPort", "com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8"),
        PROGRAM("ProgramManagerPort", "com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1"),
        PROGRAM_EVENT("ProgramEventManagerPort", "com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6"),
        SERVICE_POINT("ServicePointManagerPort", "com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3");
        
        private WebServiceTemplate template;
        private String port;
        
        Manager(String port, String path){
            this.port = port;
            this.template = createTemplate(path);
        }
        public WebServiceTemplate getTemplate(GlobalSettingDao settingDao) {
            String userName = settingDao.getString(GlobalSettingType.ITRON_HCM_USERNAME);
            String password = settingDao.getString(GlobalSettingType.ITRON_HCM_PASSWORD);
            ClientInterceptor[] interceptors = {new ItronCommunicationInterceptor(userName, password)};
            template.setInterceptors(interceptors);
            return template;
        }
        public String getUrl(GlobalSettingDao settingDao) {
            String url = settingDao.getString(GlobalSettingType.ITRON_HCM_API_URL);
            return url + port;
        }  
    }

    public static class ItronCommunicationInterceptor implements ClientInterceptor {
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
        public boolean handleFault(MessageContext arg0) throws WebServiceClientException {
            return false;
        }

        @Override
        public boolean handleRequest(MessageContext arg0) throws WebServiceClientException {
            TransportContext context = TransportContextHolder.getTransportContext();
            String userCredentials = username + ":" + password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
            HttpUrlConnection conn = (HttpUrlConnection) context.getConnection();
            conn.getConnection().setRequestProperty("authorization", basicAuth);
            conn.getConnection().addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            return true;
        }

        @Override
        public boolean handleResponse(MessageContext arg0) throws WebServiceClientException {
            return false;
        }
    }
 
    @Transactional
    @Override
    public void addDevice(Hardware hardware, AccountDto account) {
        String url = Manager.DEVICE.getUrl(settingDao);
        AddHANDeviceRequest request = null;
        if (account != null) {
            // TODO handle itron error if account already exist implementation pending simulator
            log.debug("ITRON-addDevice url:{} account:{} mac address:{}.", url, account.getAccountNumber(),
                hardware.getMacAddress());
            // check if we haven't created account before
            addServicePoint(account);
            request = DeviceManagerHelper.buildAddRequestWithServicePoint(hardware, account);
        } else {
            log.debug("ITRON-addDevice url:{} mac address:{}.", url, hardware.getMacAddress());
            request = DeviceManagerHelper.buildAddRequestWithoutServicePoint(hardware);
        }
        AddHANDeviceResponse response = null;
        try {
            log.debug(XmlUtils.getPrettyXml(request));
            response = (AddHANDeviceResponse) Manager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url, request);
            log.debug("ITRON-addDevice url:{} mac address:{} result:{}.", url, response.getMacID(), "success");
            //itronEventLogService.addHANDevice(hardware.getDisplayName(), hardware.getMacAddress(), account.getUserName());
            log.debug(XmlUtils.getPrettyXml(response));
            if (!response.getErrors().isEmpty()) {
                throw new ItronAddDeviceException(response);
            }
        } catch (ItronAddDeviceException e) {
            log.error(e);
            throw e;
        } catch (Exception e) {
            log.error("Communication error:" + XmlUtils.getPrettyXml(response), e);
            throw new ItronCommunicationException("Communication error:" + XmlUtils.getPrettyXml(response), e);
        }
    }

    @Transactional
    @Override
    public void removeDeviceFromServicePoint(String macAddress) {
        EditHANDeviceRequest request = DeviceManagerHelper.buildEditRequestRemoveServicePoint(macAddress);
        editDevice(request); 
        itronEventLogService.removeHANDeviceFromServicePoint(macAddress);
    }
    
    @Transactional
    @Override
    public void addServicePoint(AccountDto account, String macAddress) {
        addServicePoint(account);
        addDeviceToServicePoint(macAddress, account);
    }
    
    @Transactional
    @Override
    public void enroll(int accountId) {
        CustomerAccount account = customerAccountDao.getById(accountId);
        log.debug("ITRON-enroll account number {}", account.getAccountNumber());
        List<ProgramEnrollment> enrollments = getItronProgramEnrollments(accountId);
        sendEnrollmentRequest(enrollments, account, true);
        Map<Integer, LiteYukonPAObject> groups = getGroupsForEnrollments(enrollments);
        sendMacAddresses(account, groups);
    }
    
    @Transactional
    @Override
    public void optOut(int accountId, int deviceId, int inventoryId) {
        CustomerAccount account = customerAccountDao.getById(accountId);
        List<ProgramEnrollment> enrollments = getItronProgramEnrollments(accountId);
        log.debug("ITRON-optOut account number {}", account.getAccountNumber());
        int yukonGroupId = getGroupIdByInventoryId(inventoryId, enrollments);
        long itronGroupId = itronDao.getItronGroupId(yukonGroupId);
        String macAddress= deviceDao.getDeviceMacAddress(deviceId);
        String url = Manager.PROGRAM_EVENT.getUrl(settingDao);
        // TODO add event log
        CancelHANLoadControlProgramEventOnDevicesResponse response = null;
        try {
            CancelHANLoadControlProgramEventOnDevicesRequest request = ProgramEventManagerHelper.buildOptOutRequest(itronGroupId, macAddress);
            log.debug(XmlUtils.getPrettyXml(request));
            log.debug("ITRON-optOut url:{} mac address:{} itron group id:{}.", url, macAddress, itronGroupId);
            response =
                (CancelHANLoadControlProgramEventOnDevicesResponse) Manager.PROGRAM_EVENT.getTemplate(settingDao).marshalSendAndReceive(
                    url, request);
            log.debug("ITRON-optOut url:{} mac address:{} itron group id:{} result:{}.", url, macAddress, itronGroupId, "success");
            log.debug(XmlUtils.getPrettyXml(response));
        } catch (Exception e) {
            log.error("Communication error:" + XmlUtils.getPrettyXml(response), e);
            throw new ItronCommunicationException("Communication error:", e);
        }
        Map<Integer, LiteYukonPAObject> groups = new HashMap<>();
        groups.put(yukonGroupId, getGroup(yukonGroupId));
        sendMacAddresses(account, groups);
    }
    
    @Transactional
    @Override
    public void optIn(int accountId, int inventoryId) {
        CustomerAccount account = customerAccountDao.getById(accountId);
        log.debug("ITRON-optIn account number {}", account.getAccountNumber());
        List<ProgramEnrollment> enrollments = getItronProgramEnrollments(accountId);
        Map<Integer, LiteYukonPAObject> groups = new HashMap<>();
        int yukonGroupId = getGroupIdByInventoryId(inventoryId, enrollments);
        groups.put(yukonGroupId, getGroup(yukonGroupId));
        sendMacAddresses(account, groups);
    }

    /**
     * 1. Finds all devices in the groups
     * 2. Excludes all opted out inventory
     * 3. Finds mac address for each device
     * 4. For each group sends all mac addresses to itron in batches of 1000
     */
    private void sendMacAddresses(CustomerAccount account,  Map<Integer, LiteYukonPAObject> groups) {
        log.debug("ITRON-groups {} associated with account number {}",
            groups.values().stream().map(g -> g.getPaoName()).collect(Collectors.toList()), account.getAccountNumber());

        Multimap<Integer, Integer> groupIdsToInventoryIds =
            enrollmentDao.getActiveEnrolledInventoryIdsMapForGroupIds(groups.keySet());

        List<Integer> optOuts = enrollmentDao.getCurrentlyOptedOutInventory(); 
        groups.keySet().forEach(groupId -> {
            Collection<Integer> inventoryIds = groupIdsToInventoryIds.get(groupId);
            inventoryIds.removeAll(optOuts);
            log.debug("ITRON-{} device(s) in group {}", inventoryIds.size(), groups.get(groupId).getPaoName());
            Map<Integer, Integer> inventoryIdsToDeviceIds = inventoryDao.getDeviceIds(inventoryIds);
            List<String> macAddresses =
                Lists.newArrayList(deviceDao.getDeviceMacAddresses(inventoryIdsToDeviceIds.values()).values());
            macAddresses.forEach(macAddressesForGroup -> sendMacAddresses(groups.get(groupId), macAddresses));
        });
    }
    
    @Transactional
    @Override
    public void unenroll(int accountId) {
        CustomerAccount account = customerAccountDao.getById(accountId);
        log.debug("ITRON-unenroll account number {}", account.getAccountNumber());
        List<ProgramEnrollment> enrollments = getItronProgramEnrollments(accountId);
        sendEnrollmentRequest(enrollments, account, false);
        Map<Integer, LiteYukonPAObject> groups = getGroupsForEnrollments(enrollments);
        sendMacAddresses(account, groups);
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
    private void sendEnrollmentRequest(List<ProgramEnrollment> enrollments, CustomerAccount account, boolean enroll) {
        String url = Manager.PROGRAM.getUrl(settingDao);
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
        
        log.debug("Sending enrollment request to itron for account {} enrolling itron program ids {} ", account.getAccountNumber(),
            itronProgramIds);
        SetServicePointEnrollmentRequest request =
            ProgramManagerHelper.buildEnrollmentRequest(account.getAccountNumber(), itronProgramIds);
        log.debug("ITRON-sendEnrollmentRequest url:{} account number:{}.", url, account.getAccountNumber());
        log.debug(XmlUtils.getPrettyXml(request));
        
        try {
            SetServicePointEnrollmentResponse response =
                (SetServicePointEnrollmentResponse) Manager.PROGRAM.getTemplate(settingDao).marshalSendAndReceive(url, request);
            // TODO add event log
            log.debug("Response for this call is blank:" + XmlUtils.getPrettyXml(response));
            log.debug("ITRON-sendEnrollmentRequest url:{} account number:{} result:{}.", url, account.getAccountNumber(),
                "success");
        } catch (Exception e) {
            log.error("Communication error:", e);
            throw new ItronCommunicationException("Communication error:", e);
        }
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
            .forEach(paoId -> itronDao.addGroupMapping(getGroupIdFromItron(getGroup(paoId)), paoId));
    }
    
    /**
     * Checks if enrollment contains the program that was not previously sent to Itron.
     * Sends the program to itron to get the itron program Id back
     */
    private void createPrograms(Collection<Integer> programPaoIds) {       
        Map<Integer, Long> programPaoIdtoItronId = itronDao.getItronProgramIds(programPaoIds);
        programPaoIds.stream()
            .filter(paoId -> !programPaoIdtoItronId.containsKey(paoId))
            .forEach(paoId -> itronDao.addProgramMapping(getProgramIdFromItron(getProgram(paoId)), paoId));
    }
    
    /**
     * Sends Itron program id and receives itron program id
     */
    private long getProgramIdFromItron(LiteYukonPAObject programPao) {
        String url = Manager.PROGRAM.getUrl(settingDao);
        try {
            AddProgramRequest request = new AddProgramRequest();
            request.setProgramName(String.valueOf(programPao.getLiteID()));
            log.debug("ITRON-getProgramIdFromItron url:{} program name:{}.", url, programPao.getPaoName());
            log.debug(XmlUtils.getPrettyXml(request));
            AddProgramResponse response =
                (AddProgramResponse) Manager.PROGRAM.getTemplate(settingDao).marshalSendAndReceive(url, request);
            log.debug(XmlUtils.getPrettyXml(response));
            log.debug("ITRON-getProgramIdFromItron url:{} program name:{} result:{}.", url, programPao.getPaoName(),
                "success");
            itronEventLogService.addProgram(response.getProgramName(), response.getProgramID());
            return response.getProgramID();
        } catch (Exception e) {
            log.error("Communication error:", e);
            throw new ItronCommunicationException("Communication error:", e);
        }
    }
    
    /**
     * Sends request to itron to add mac addresses to a group.
     */
    private void sendMacAddresses(LiteYukonPAObject groupPao, List<String> macAddresses) {
        String url = Manager.DEVICE.getUrl(settingDao);

        JAXBElement<ESIGroupResponseType> response = null;
        try {
            ESIGroupRequestType requestType = DeviceManagerHelper.buildGroupEditRequest(groupPao, macAddresses);
            JAXBElement<ESIGroupRequestType> request = new ObjectFactory().createEditESIGroupRequest(requestType);
            // TODO add event log
            log.debug("ITRON-addMacAddressToGroup url:{} group name:{} mac addresses:{}.", url, groupPao.getPaoName(),
                macAddresses);
            log.debug(XmlUtils.getPrettyXml(new ESIGroupRequestTypeHolder(request.getValue())));
            response = (JAXBElement<ESIGroupResponseType>) Manager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url, request);
            log.debug("ITRON-addMacAddressToGroup url:{} group name:{} mac addresses:{} result:{}.", url,
                groupPao.getPaoName(), macAddresses, "success");
            log.debug(XmlUtils.getPrettyXml(new ESIGroupResponseTypeHolder(response.getValue())));
            itronEventLogService.addGroup(String.valueOf(groupPao.getLiteID()), response.getValue().getGroupID());
            if (!response.getValue().getErrors().isEmpty()) {
                throw new ItronAddEditGroupException(response.getValue());
            }
        } catch (ItronAddEditGroupException e) {
            log.error(e);
            throw e;
        } catch (Exception e) {
            log.error("Communication error:" + XmlUtils.getPrettyXml(response), e);
            throw new ItronCommunicationException(
                "Communication error:" + XmlUtils.getPrettyXml(new ESIGroupResponseTypeHolder(response.getValue())), e);
        }
    }
    
    /**
     * Returns itron group id
     */
    private long getGroupIdFromItron(LiteYukonPAObject groupPao) {
        String url = Manager.DEVICE.getUrl(settingDao);

        JAXBElement<ESIGroupResponseType> response = null;
        try {
            ESIGroupRequestType requestType = DeviceManagerHelper.buildGroupAddRequest(groupPao);
            JAXBElement<ESIGroupRequestType> request = new ObjectFactory().createAddESIGroupRequest(requestType);
            // TODO add event log
            log.debug("ITRON-getGroupIdFromItron url:{} group name:{}", url, groupPao.getPaoName());
            log.debug(XmlUtils.getPrettyXml(new ESIGroupRequestTypeHolder(request.getValue())));
            response = (JAXBElement<ESIGroupResponseType>) Manager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url, request);
            log.debug("ITRON-getGroupIdFromItron url:{} group name:{} result:{}.", url, groupPao.getPaoName(),
                "success");
            log.debug(XmlUtils.getPrettyXml(new ESIGroupResponseTypeHolder(response.getValue())));
            itronEventLogService.addGroup(String.valueOf(groupPao.getLiteID()), response.getValue().getGroupID());
            if (!response.getValue().getErrors().isEmpty()) {
                throw new ItronAddEditGroupException(response.getValue());
            }
            return response.getValue().getGroupID();
        } catch (ItronAddEditGroupException e) {
            log.error(e);
            throw e;
        } catch (Exception e) {
            log.error("Communication error:" + XmlUtils.getPrettyXml(response), e);
            throw new ItronCommunicationException("Communication error:" + XmlUtils.getPrettyXml(new ESIGroupResponseTypeHolder(response.getValue())), e);
        }
    }
    
    /**
     * Sends request to itron to add device to a service point
     */
    private void addDeviceToServicePoint(String macAddress, AccountDto account) {
        EditHANDeviceRequest request = DeviceManagerHelper.buildEditRequestWithServicePoint(macAddress, account);
        editDevice(request); 
        itronEventLogService.addHANDeviceToServicePoint(account.getAccountNumber(), macAddress, account.getUserName());
    }

    /**
     * Sends edit device request to itron
     */
    private void editDevice(EditHANDeviceRequest request) {
        String url = Manager.DEVICE.getUrl(settingDao);
        
        log.debug("ITRON-editDevice url {}", url);
        log.debug(XmlUtils.getPrettyXml(request));
        EditHANDeviceResponse response = null;
        try {
            response = (EditHANDeviceResponse) Manager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url, request);
            log.debug(XmlUtils.getPrettyXml(response));
            log.debug("ITRON-editDevice mac address:{} result:{}", response.getMacID(), "success");
            if (!response.getErrors().isEmpty()) {
                throw new ItronEditDeviceException(response);
            }
        } catch (ItronEditDeviceException e) {
            log.error(e);
            throw e;
        } catch (Exception e) {
            log.error("Communication error:" + XmlUtils.getPrettyXml(response), e);
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
        String url = Manager.SERVICE_POINT.getUrl(settingDao);

        AddServicePointRequest request = ServicePointHelper.buildAddRequest(account);
        log.debug("ITRON-addServicePoint url:{} account number:{}.", url, account.getAccountNumber());
        log.debug(XmlUtils.getPrettyXml(request));
        try {
            AddServicePointResponse response =
                (AddServicePointResponse) Manager.SERVICE_POINT.getTemplate(settingDao).marshalSendAndReceive(url, request);

            itronEventLogService.addServicePoint(account.getAccountNumber(), account.getUserName());
            log.debug(XmlUtils.getPrettyXml(response));
            log.debug("ITRON-addServicePoint url:{} account number:{} result:{}.", url, account.getAccountNumber());
        } catch (Exception e) {
            log.error("Communication error:", e);
            throw new ItronCommunicationException("Communication error:", e);
        }
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
    
    private int getGroupIdByInventoryId(int inventoryId, List<ProgramEnrollment> enrollments) {
        int yukonGroupId = enrollments.stream()
                .filter(enrollment -> enrollment.getInventoryId() == inventoryId)
                .findFirst().get()
                .getLmGroupId();
        return yukonGroupId;
    }
    

    private Map<Integer, LiteYukonPAObject> getGroupsForEnrollments(List<ProgramEnrollment> enrollments) {
        Map<Integer, LiteYukonPAObject> groups =
            enrollments.stream().map(enrollment -> getGroup(enrollment.getLmGroupId()))
                .distinct()
                .collect(Collectors.toMap(g -> g.getPaoIdentifier().getPaoId(), g -> g));
        return groups;
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
            return template;
        } catch(Exception e) {
            log.error("Unable to initialize template for path " + path, e);
            return null;
        }
    }
}
