package com.cannontech.dr.itron.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBElement;

import org.apache.commons.compress.utils.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.ItronEventLogService;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
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
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.UpdateDeviceEventLogsRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.UpdateDeviceEventLogsResponse;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.AddHANLoadControlProgramEventRequest;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.AddProgramEventResponseType;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.CancelHANLoadControlProgramEventOnDevicesRequest;
import com.cannontech.dr.itron.model.jaxb.programEventManagerTypes_v1_6.CancelHANLoadControlProgramEventOnDevicesResponse;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.AddProgramRequest;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.AddProgramResponse;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.SetServicePointEnrollmentRequest;
import com.cannontech.dr.itron.model.jaxb.programManagerTypes_v1_1.SetServicePointEnrollmentResponse;
import com.cannontech.dr.itron.model.jaxb.reportManagerTypes_v1_2.CommandIDResponse;
import com.cannontech.dr.itron.model.jaxb.reportManagerTypes_v1_2.ExportDeviceLogRequest;
import com.cannontech.dr.itron.model.jaxb.reportManagerTypes_v1_2.GetReportGenerationStatusRequest;
import com.cannontech.dr.itron.model.jaxb.reportManagerTypes_v1_2.GetReportGenerationStatusResponse;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AddServicePointRequest;
import com.cannontech.dr.itron.model.jaxb.servicePointManagerTypes_v1_3.AddServicePointResponse;
import com.cannontech.dr.itron.service.ItronAddDeviceException;
import com.cannontech.dr.itron.service.ItronAddEditGroupException;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.dr.itron.service.ItronEditDeviceException;
import com.cannontech.stars.dr.account.dao.ApplianceAndProgramDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Iterables;
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
    @Autowired private ApplianceAndProgramDao applianceAndProgramDao;
    @Autowired private List<SoapFaultParser> soapFaultParsers;
    
    private static final Set<String> faultCodesToIgnore = Sets.newHashSet("UtilServicePointID.Exists");
    private static final Map<Integer, Long> groupPaoIdToItronEventId = new HashMap<>();

    private static final Logger log = YukonLogManager.getLogger(ItronCommunicationServiceImpl.class);
    public static final String FILE_PATH = CtiUtilities.getItronDirPath();
    public static final SimpleDateFormat FILE_NAME_DATE_FORMATTER = new SimpleDateFormat("YYYYMMddHHmm");

    @Transactional
    @Override
    public void addDevice(Hardware hardware, AccountDto account) {
        String url = ItronEndpointManager.DEVICE.getUrl(settingDao);
        AddHANDeviceRequest request = null;
        if (account != null) {
            log.debug("ITRON-addDevice url:{} account:{} mac address:{}.", url, account.getAccountNumber(),
                hardware.getMacAddress());
            addServicePoint(account);
            request = DeviceManagerHelper.buildAddRequestWithServicePoint(hardware, account);
        } else {
            log.debug("ITRON-addDevice url:{} mac address:{}.", url, hardware.getMacAddress());
            request = DeviceManagerHelper.buildAddRequestWithoutServicePoint(hardware);
        }
        try {
            log.debug(XmlUtils.getPrettyXml(request));
            AddHANDeviceResponse response = (AddHANDeviceResponse) ItronEndpointManager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url, request);
            log.debug("ITRON-addDevice url:{} mac address:{} result:{}.", url, response.getMacID(), "success");
            itronEventLogService.addHANDevice(hardware.getDisplayName(), hardware.getMacAddress());
            log.debug(XmlUtils.getPrettyXml(response));
            if (!response.getErrors().isEmpty()) {
                throw new ItronAddDeviceException(response);
            }
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.DEVICE);
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
        addMacAddressesToGroup(account, groups);
    }
    
    @Transactional
    @Override
    public void optOut(int accountId, int deviceId, int inventoryId) {
        CustomerAccount account = customerAccountDao.getById(accountId);
        List<ProgramEnrollment> enrollments = getItronProgramEnrollments(accountId);
        log.debug("ITRON-optOut account number {}", account.getAccountNumber());
        int yukonGroupId = getGroupIdByInventoryId(inventoryId, enrollments);
        String macAddress= deviceDao.getDeviceMacAddress(deviceId);
        sendRestore(yukonGroupId, macAddress, null);
        itronEventLogService.optOut(account.getAccountNumber(), yukonGroupId, macAddress);
        Map<Integer, LiteYukonPAObject> groups = new HashMap<>();
        groups.put(yukonGroupId, getGroup(yukonGroupId));
        addMacAddressesToGroup(account, groups);
    }
    
    @Transactional
    @Override
    public void sendRestore(int yukonGroupId) {
        long itronGroupId = itronDao.getItronGroupId(yukonGroupId);
        sendRestore(yukonGroupId, null, itronGroupId);
    }
    
    @Override
    public void sendDREventForGroup(int yukonGroupId, int dutyCyclePercent, int dutyCyclePeriod, int criticality,
            int rampIn, int rampOut, Duration duration) {
        String url = ItronEndpointManager.PROGRAM_EVENT.getUrl(settingDao);
        int relay = itronDao.getVirtualRelayId(yukonGroupId);
        List<ProgramLoadGroup> programsByLMGroupId = applianceAndProgramDao.getProgramsByLMGroupId(yukonGroupId);
        int programId = programsByLMGroupId.get(0).getPaobjectId();
        int itronProgramId = itronDao.getItronProgramId(programId);
        
        LiteYukonPAObject group = getGroup(yukonGroupId);
        LiteYukonPAObject program = getProgram(programId);
       
        try {
            AddHANLoadControlProgramEventRequest request = ProgramEventManagerHelper.buildDrEvent(dutyCyclePercent,
               dutyCyclePeriod, criticality, relay, itronProgramId, String.valueOf(programId), rampIn, rampOut, duration);
            log.debug(XmlUtils.getPrettyXml(request));
            log.debug("ITRON-sendDREventForGroup url:{} yukon group:{} yukon program:{}", url, group.getPaoName(), program.getPaoName());
            JAXBElement<AddProgramEventResponseType> response =
                    (JAXBElement<AddProgramEventResponseType>) ItronEndpointManager.PROGRAM_EVENT.getTemplate(
                    settingDao).marshalSendAndReceive(url, request);
            AddProgramEventResponseType type = response.getValue();
            groupPaoIdToItronEventId.put(yukonGroupId, type.getProgramEventID());
            log.debug("ITRON-sendDREventForGroup url:{} mac address:{} itron group id:{} itron event id:{} result:{}.", url,
                group.getPaoName(), program.getPaoName(), type.getProgramEventID() , "success");
            log.debug(XmlUtils.getPrettyXml(response));
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.PROGRAM_EVENT);
        }
    }
    
    @Override
    public void updateDeviceLogs(List<Integer> deviceIds) {
        String url = ItronEndpointManager.DEVICE.getUrl(settingDao);
        Map<Integer, String> macAddresses = deviceDao.getDeviceMacAddresses(deviceIds);
        //One request should have no more then 1000 devices
        Iterables.partition(macAddresses.values(), 1000).forEach(list -> {
            try {
                UpdateDeviceEventLogsRequest request = DeviceManagerHelper.buildUpdateDeviceEventLogs(list);
                log.debug(XmlUtils.getPrettyXml(request));
                log.debug("ITRON-updateDeviceLogs url:{} device count:{}", url, list.size());
                UpdateDeviceEventLogsResponse response =
                    (UpdateDeviceEventLogsResponse) ItronEndpointManager.DEVICE.getTemplate(
                        settingDao).marshalSendAndReceive(url, request);
                log.debug("ITRON-updateDeviceLogs url:{} device count:{} update requested by itron:{} result:{}.", url,
                    response.isUpdateRequested(), list.size());
                log.debug(XmlUtils.getPrettyXml(response));
            } catch (Exception e) {
                handleException(e, ItronEndpointManager.DEVICE);
            }
        });
    }

    /**
     * Sends restore message to Itron
     */
    void sendRestore(int yukonGroupId, String macAddress, Long itronGroupId) {
        LiteYukonPAObject group = getGroup(yukonGroupId);

        Long eventId = groupPaoIdToItronEventId.get(yukonGroupId);
        if (eventId == null) {
            throw new ItronCommunicationException(
                "Unable to restore, Itron event id doesn't exists. Web Server might have been restarted.");
        }
        String url = ItronEndpointManager.PROGRAM_EVENT.getUrl(settingDao);
        try {
            CancelHANLoadControlProgramEventOnDevicesRequest request =
                ProgramEventManagerHelper.buildRestoreRequest(itronGroupId, eventId, macAddress);
            log.debug(XmlUtils.getPrettyXml(request));
            log.debug("ITRON-sendRestore url:{} mac address:{} yukon group:{} itron event id:{}.", url, macAddress,
                group.getPaoName(), eventId);
            CancelHANLoadControlProgramEventOnDevicesResponse response =
                (CancelHANLoadControlProgramEventOnDevicesResponse) ItronEndpointManager.PROGRAM_EVENT.getTemplate(
                    settingDao).marshalSendAndReceive(url, request);
            log.debug("ITRON-sendRestore url:{} mac address:{} yukon group:{} itron event id:{} result:{}.", url,
                macAddress, group.getPaoName(), eventId, "success");
            log.debug(XmlUtils.getPrettyXml(response));
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.PROGRAM_EVENT);
        }
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
        addMacAddressesToGroup(account, groups);
    }

    /**
     * 1. Finds all devices in the groups
     * 2. Excludes all opted out inventory
     * 3. Finds mac address for each device
     * 4. For each group sends all mac addresses to itron
     */
    private void addMacAddressesToGroup(CustomerAccount account,  Map<Integer, LiteYukonPAObject> groups) {
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
            macAddresses.forEach(macAddressesForGroup -> addMacAddressesToGroup(groups.get(groupId), macAddresses));
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
        addMacAddressesToGroup(account, groups);
    }
    
    @Override
    public ZipFile exportDeviceLogs(long startRecordId, long endRecordId) {
        String url = ItronEndpointManager.REPORT.getUrl(settingDao);
        try {
            ExportDeviceLogRequest request = new ExportDeviceLogRequest();
            request.setRecordIDRangeStart(startRecordId);
            request.setRecordIDRangeEnd(endRecordId);
            log.debug("ITRON-exportDeviceLog url:{} startRecordId:{} endRecordId:{}.", url, startRecordId, endRecordId);
            log.debug(XmlUtils.getPrettyXml(request));
            CommandIDResponse response =
                (CommandIDResponse) ItronEndpointManager.REPORT.getTemplate(settingDao).marshalSendAndReceive(url, request);
            log.debug(XmlUtils.getPrettyXml(response));
            log.debug("ITRON-exportDeviceLog url:{} startRecordId:{} endRecordId:{} commandId:{} result:{}.", url,
                startRecordId, endRecordId, response.getCommandID(), "success");
            return getExportedFiles(response.getCommandID());
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.REPORT);
        }
        return null;
    }
   
    /**
     * Asks Itron for file names and copies files to ExportArchive/Itron
     */
    private ZipFile getExportedFiles(long commandId) {
        String url = ItronEndpointManager.REPORT.getUrl(settingDao);
        try {
            GetReportGenerationStatusRequest request = new GetReportGenerationStatusRequest();
            request.setCommandID(commandId);
            log.debug("ITRON-getReport url:{} commandId:{}.", url, commandId);
            log.debug(XmlUtils.getPrettyXml(request));
            GetReportGenerationStatusResponse response = new GetReportGenerationStatusResponse();
            response.setCompleted(true);
            response.getReportFileNames().add("https://sample-videos.com/csv/Sample-Spreadsheet-10-rows.csv");
            response.getReportFileNames().add("https://sample-videos.com/csv/Sample-Spreadsheet-100-rows.csv");
            response.setFailed(false);
         /*   while(true) {
                response = ( GetReportGenerationStatusResponse) ItronEndpointManager.REPORT.getTemplate(settingDao).marshalSendAndReceive(url, request);
                if(response.isCompleted()) {
                    break;
                }
            }*/
            log.debug(XmlUtils.getPrettyXml(response));
            if (response.isFailed()) {
                ItronCommunicationException exception = new ItronCommunicationException(
                    "Failed to get file for command id=" + commandId + " error=" + response.getFailureMessage());
                log.error(exception);
                throw exception;
            }
            
            log.debug("ITRON-getReport url:{} commandId:{} result:{}.", url, commandId, "success");
            return downloadAndZipReportFiles(response.getReportFileNames(), commandId);

        } catch (Exception e) {
            handleException(e, ItronEndpointManager.REPORT);
        }
        return null;
    }
    
    /**
     * Downloads files from itron and copies to ExportArchive/Itron.
     * The files name is going to be timestamp_coomanId. Command Id can be correlated to log debug statements.
     * Returns a zip file
     */
    private ZipFile downloadAndZipReportFiles(List<String> fileNames, long commandId) {
        List<File> files = new ArrayList<>();
        String zipName = FILE_NAME_DATE_FORMATTER.format(new Date()) + "_" + commandId + ".zip";

        for (int i = 0; i < fileNames.size(); i++) {
            File file = null;
            try {
                //test on local
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.etn.com", 8080));
                URLConnection conn = new URL(fileNames.get(i)).openConnection(proxy);
              
                //URLConnection conn = new URL(path).openConnection();
                file = new File(FILE_PATH,
                    (i + 1) + "_" + FILE_NAME_DATE_FORMATTER.format(new Date()) + "_" + commandId + ".csv");
                OutputStream out = new FileOutputStream(file);
                IOUtils.copy(conn.getInputStream(), out);
                IOUtils.closeQuietly(out);
                files.add(file);
                log.debug("ITRON-downoladed Itron file:{} created file:{} commandId: {}.", fileNames.get(0),
                    file.getPath(), commandId);
            } catch (Exception e) {
                log.error("Unable to download file: " + fileNames.get(0));
            }
        }
        return zipFiles(zipName, files);
    }
    
    private ZipFile zipFiles(String zipName, List<File> files) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(FILE_PATH, zipName));
            ZipOutputStream zos = new ZipOutputStream(fos);
            for (File file : files) {
                zos.putNextEntry(new ZipEntry(file.getName()));
                byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
                zos.write(bytes, 0, bytes.length);
                zos.closeEntry();
            }
            zos.close();
            String zip = FILE_PATH + System.getProperty("file.separator") + zipName;
            log.debug("Created zip file:"+zip);
            files.forEach(file -> file.delete());
            return new ZipFile(zip);
        } catch (Exception e) {
            log.error("Unable to zip files", e);
            throw new ItronCommunicationException("Unable to zip files", e);
        }
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
        String url = ItronEndpointManager.PROGRAM.getUrl(settingDao);
        List<Long> itronProgramIds = new ArrayList<>();
        
        if (!enrollments.isEmpty()) {
            List<Integer> assignedProgramIds =
                enrollments.stream().map(enrollment -> enrollment.getAssignedProgramId()).collect(Collectors.toList());
            Collection<Integer> programPaoIds = assignedProgramDao.getProgramIdsByAssignedProgramIds(assignedProgramIds).values();
            
            if(enroll) {
                createPrograms(programPaoIds);
                updateGroupMappingWithItronId(enrollments);
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
                (SetServicePointEnrollmentResponse) ItronEndpointManager.PROGRAM.getTemplate(settingDao).marshalSendAndReceive(url, request);
            itronProgramIds.forEach(itronProgramId -> 
                itronEventLogService.sendEnrollmentRequest(account.getAccountNumber(), itronProgramId)
            );
            log.debug("Response for this call is blank:" + XmlUtils.getPrettyXml(response));
            log.debug("ITRON-sendEnrollmentRequest url:{} account number:{} result:{}.", url, account.getAccountNumber(),
                "success");
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.PROGRAM);
        }
    }

    /**
     * Checks if enrollment contains the group that was not previously sent to Itron.
     * Sends the group to itron to get the itron group Id back
     */
    private void updateGroupMappingWithItronId(List<ProgramEnrollment> enrollments) {    
       List<Integer> groupPaoIds = enrollments.stream()
               .map(enrollment -> enrollment.getLmGroupId())
               .collect(Collectors.toList());
       
      // ---
       // TODO remove the group creation as the group will be created from db editor
        Map<Integer, Long> groupPaoIdtoItronId = itronDao.getItronGroupIds(groupPaoIds);
        groupPaoIds.stream()
            .filter(paoId -> !groupPaoIdtoItronId.containsKey(paoId))
            .forEach(paoId -> itronDao.addGroupMapping(paoId, 1));
       // ---
        
        List<Integer> lmGroupsWithoutItronGroup = itronDao.getLmGroupsWithoutItronGroup(groupPaoIds);
        lmGroupsWithoutItronGroup.forEach(paoId -> itronDao.updateGroupMapping(paoId, getGroupIdFromItron(getGroup(paoId))));
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
        String url = ItronEndpointManager.PROGRAM.getUrl(settingDao);
        try {
            AddProgramRequest request = new AddProgramRequest();
            request.setProgramName(String.valueOf(programPao.getLiteID()));
            log.debug("ITRON-getProgramIdFromItron url:{} program name:{}.", url, programPao.getPaoName());
            log.debug(XmlUtils.getPrettyXml(request));
            AddProgramResponse response =
                (AddProgramResponse) ItronEndpointManager.PROGRAM.getTemplate(settingDao).marshalSendAndReceive(url, request);
            log.debug(XmlUtils.getPrettyXml(response));
            log.debug("ITRON-getProgramIdFromItron url:{} program name:{} result:{}.", url, programPao.getPaoName(),
                "success");
            itronEventLogService.addProgram(response.getProgramName(), response.getProgramID());
            return response.getProgramID();
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.PROGRAM);
            return 0;
        }
    }
    
    /**
     * Sends request to itron to add mac addresses to a group.
     */
    private void addMacAddressesToGroup(LiteYukonPAObject groupPao, List<String> macAddresses) {
        String url = ItronEndpointManager.DEVICE.getUrl(settingDao);

        try {
            ESIGroupRequestType requestType = DeviceManagerHelper.buildGroupEditRequest(groupPao, macAddresses);
            JAXBElement<ESIGroupRequestType> request = new ObjectFactory().createEditESIGroupRequest(requestType);
            macAddresses.forEach(macAddress ->
                itronEventLogService.addMacAddressToGroup(macAddress, groupPao.getPaoName())
            );
            log.debug("ITRON-addMacAddressToGroup url:{} group name:{} mac addresses:{}.", url, groupPao.getPaoName(),
                macAddresses);
            log.debug(XmlUtils.getPrettyXml(new ESIGroupRequestTypeHolder(request.getValue())));
            JAXBElement<ESIGroupResponseType> response =
                (JAXBElement<ESIGroupResponseType>) ItronEndpointManager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url,
                    request);
            log.debug("ITRON-addMacAddressToGroup url:{} group name:{} mac addresses:{} result:{}.", url,
                groupPao.getPaoName(), macAddresses, "success");
            log.debug(XmlUtils.getPrettyXml(new ESIGroupResponseTypeHolder(response.getValue())));
            itronEventLogService.addGroup(String.valueOf(groupPao.getLiteID()), response.getValue().getGroupID());
            if (!response.getValue().getErrors().isEmpty()) {
                throw new ItronAddEditGroupException(response.getValue());
            }
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.DEVICE);
        }
    }
    
    /**
     * Returns itron group id
     */
    private long getGroupIdFromItron(LiteYukonPAObject groupPao) {
        String url = ItronEndpointManager.DEVICE.getUrl(settingDao);

        try {
            ESIGroupRequestType requestType = DeviceManagerHelper.buildGroupAddRequest(groupPao);
            JAXBElement<ESIGroupRequestType> request = new ObjectFactory().createAddESIGroupRequest(requestType);
            itronEventLogService.getGroupIdFromItron(groupPao.getPaoName());
            log.debug("ITRON-getGroupIdFromItron url:{} group name:{}", url, groupPao.getPaoName());
            log.debug(XmlUtils.getPrettyXml(new ESIGroupRequestTypeHolder(request.getValue())));
            JAXBElement<ESIGroupResponseType> response =
                (JAXBElement<ESIGroupResponseType>) ItronEndpointManager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url,
                    request);
            log.debug("ITRON-getGroupIdFromItron url:{} group name:{} result:{}.", url, groupPao.getPaoName(),
                "success");
            log.debug(XmlUtils.getPrettyXml(new ESIGroupResponseTypeHolder(response.getValue())));
            itronEventLogService.addGroup(String.valueOf(groupPao.getLiteID()), response.getValue().getGroupID());
            if (!response.getValue().getErrors().isEmpty()) {
                throw new ItronAddEditGroupException(response.getValue());
            }
            return response.getValue().getGroupID();
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.DEVICE);
            return 0;
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
        String url = ItronEndpointManager.DEVICE.getUrl(settingDao);
        
        log.debug("ITRON-editDevice url {}", url);
        log.debug(XmlUtils.getPrettyXml(request));
        EditHANDeviceResponse response = null;
        try {
            response = (EditHANDeviceResponse) ItronEndpointManager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url, request);
            log.debug(XmlUtils.getPrettyXml(response));
            log.debug("ITRON-editDevice mac address:{} result:{}", response.getMacID(), "success");
            if (!response.getErrors().isEmpty()) {
                throw new ItronEditDeviceException(response);
            }
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.DEVICE);
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
        String url = ItronEndpointManager.SERVICE_POINT.getUrl(settingDao);

        AddServicePointRequest request = ServicePointManagerHelper.buildAddRequest(account);
        log.debug("ITRON-addServicePoint url:{} account number:{}.", url, account.getAccountNumber());
        log.debug(XmlUtils.getPrettyXml(request));
        try {
            AddServicePointResponse response =
                (AddServicePointResponse) ItronEndpointManager.SERVICE_POINT.getTemplate(settingDao).marshalSendAndReceive(url, request);

            itronEventLogService.addServicePoint(account.getAccountNumber(), account.getUserName());
            log.debug(XmlUtils.getPrettyXml(response));
            log.debug("ITRON-addServicePoint url:{} account number:{} result:{}.", url, account.getAccountNumber());
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.SERVICE_POINT);
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
     * Handles exceptions
     * 
     * If this is Communication exception - re-throws exception
     * If this is a Soap Fault
     *   Finds a parser
     *   Parses exception
     *   If it only contains the codes that we are ignoring, ignores exception
     *   Otherwise throws an Communication exception with the code and description of the first not ignored error it found
     *   The full Fault is logged in WS log if debug is turned on
     *  If this is an unknown exception, creates and throws Communication exception
     */
    private void handleException(Exception e, ItronEndpointManager manager) throws ItronCommunicationException{
        if (e instanceof ItronCommunicationException) {
            log.error(e);
            throw (ItronCommunicationException) e;
        } else if (e instanceof SoapFaultClientException) {
            soapFaultParsers.stream()
                .filter(parser -> parser.isSupported(manager))
                .findFirst().get()
                .handleSoapFault((SoapFaultClientException) e, faultCodesToIgnore, log);
        } else {
            log.error("Communication error:", e);
            throw new ItronCommunicationException("Communication error:", e);
        }
    }
}
