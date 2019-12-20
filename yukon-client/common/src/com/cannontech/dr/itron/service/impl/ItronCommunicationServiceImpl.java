package com.cannontech.dr.itron.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBElement;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.ItronEventLogService;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SftpConnection;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.dao.ItronDao;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.AddHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupRequestType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ESIGroupResponseType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.EditHANDeviceResponse;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.FindDeviceResponseType;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.FindHANDeviceRequest;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.ObjectFactory;
import com.cannontech.dr.itron.model.jaxb.deviceManagerTypes_v1_8.PaginationType;
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
import com.cannontech.dr.itron.service.ItronEventNotFoundException;
import com.cannontech.encryption.ItronSecurityKeyPair;
import com.cannontech.encryption.ItronSecurityService;
import com.cannontech.encryption.impl.ItronSecurityException;
import com.cannontech.stars.dr.account.dao.ApplianceAndProgramDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ItronCommunicationServiceImpl implements ItronCommunicationService {
    
    @Autowired private ApplianceAndProgramDao applianceAndProgramDao;
    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private ItronDao itronDao;
    @Autowired private ItronEventLogService itronEventLogService;
    @Autowired private ItronSecurityService itronSecurityService;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private List<SoapFaultParser> soapFaultParsers;
    
    private static final Set<String> faultCodesToIgnore = Sets.newHashSet("UtilServicePointID.Exists", "macID.exists");
    
    private static final Logger log = YukonLogManager.getLogger(ItronCommunicationServiceImpl.class);
    private static final String READ_GROUP = "ITRON_READ_GROUP";
    public static final String FILE_PATH = CtiUtilities.getItronDirPath();
    private static DateTime lastItronFileDeletionDate = DateTime.now().minus(Duration.standardDays(1));
    
    private Cache<Integer, Enrollment> enrollmentCache =
            CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    private Cache<Integer, Enrollment> unenrollmentCache =
            CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
        
    enum ExportType {
        READ,
        ALL
    }

    @Transactional
    @Override
    public void addDevice(Hardware hardware, AccountDto account) {
        String url = ItronEndpointManager.DEVICE.getUrl(settingDao);
        AddHANDeviceRequest request = null;
        if (account != null) {
            log.debug("ITRON-addDevice url:{} account:{} mac address:{}.", url, account.getAccountNumber(),
                hardware.getMacAddress());
            addServicePoint(account);
            request = DeviceManagerHelper.buildAddRequestWithServicePoint(hardware.getMacAddress(), account);
        } else {
            log.debug("ITRON-addDevice url:{} mac address:{}.", url, hardware.getMacAddress());
            request = DeviceManagerHelper.buildAddRequestWithoutServicePoint(hardware.getMacAddress());
        }
        try {
            log.debug(XmlUtils.getPrettyXml(request));
            AddHANDeviceResponse response = (AddHANDeviceResponse) ItronEndpointManager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url, request);
            log.debug("ITRON-addDevice url:{} mac address:{} result:{}.", url, response.getMacID(), "success");
            itronEventLogService.addHANDevice(hardware.getSerialNumber(), hardware.getMacAddress());
            log.debug(XmlUtils.getPrettyXml(response));
            if (!response.getErrors().isEmpty()) {
                throw new ItronAddDeviceException(response);
            }
        } catch (Exception e) {
            // Add Device failed. This may mean that the device already exists on the Itron side.
            if (account == null) {
                handleException(e, ItronEndpointManager.DEVICE);
            } else {
                // If the device is attached to an account, attempt to add to Itron service point with an edit instead.
                log.debug("ITRON-addDevice encountered an error.", e);
                log.info("Failed to add device to Itron. The device may already exist. Attempting to add device to service point.");
                addDeviceToServicePoint(hardware.getMacAddress(), account);
            }
        }
    }
    
    @Override
    public void saveSecondaryMacAddress(PaoType type, int deviceId, String primaryMacAddress) {
        FindHANDeviceRequest request = new FindHANDeviceRequest();
        request.setESIMacID(primaryMacAddress);
        PaginationType pagination = new PaginationType();
        pagination.setPageSize(10);
        pagination.setPageNumber(1);
        request.setPagination(pagination);
        String url = ItronEndpointManager.DEVICE.getUrl(settingDao);
        log.debug("ITRON-findDevice url:{} primary mac:{}", url, primaryMacAddress);
        
        try {
            JAXBElement<FindDeviceResponseType> responseElement = 
                    (JAXBElement<FindDeviceResponseType>) ItronEndpointManager.DEVICE.getTemplate(settingDao)
                                                                                     .marshalSendAndReceive(url, request);
            
            FindDeviceResponseType response = responseElement.getValue();
            if (response.getDeviceCount() == 0) {
                log.info("No secondary mac found for device with primary mac {}", primaryMacAddress);
                return;
            }
            
            // 1 or more results found
            log.info("Found {} secondary macs for device with primary mac {}.", response.getDeviceCount(), primaryMacAddress);
            if (log.isDebugEnabled()) {
                // Log secondary macs in case we run into a weird situation where we get multiple results
                String secondaryMacs = response.getMacIDs().stream().collect(Collectors.joining(", "));
                log.debug("Secondary macs: {}", secondaryMacs);
            }
            deviceDao.updateSecondaryMacAddress(type, deviceId, response.getMacIDs().get(0));
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
    public void enroll(int accountId, int groupId) {
        List<ProgramEnrollment> enrollments = getItronProgramEnrollments(accountId);
        CustomerAccount account = customerAccountDao.getById(accountId);
        if (!isEnrollmentSentToItron(account, groupId, enrollments, enrollmentCache)) {
            log.debug("ITRON-enroll account number {}", account.getAccountNumber());
            sendEnrollmentRequest(account, enrollments, true);           
            Set<Integer> inventoryIds = getInventoryIdsForGroup(groupId);
            long itronGroupId = addMacAddressesToGroup(account, getGroup(groupId), inventoryIds);
            itronDao.updateGroupMapping(groupId, itronGroupId);
            unenrollmentCache.invalidate(accountId);
        }
    }
    
    @Transactional
    @Override
    public void unenroll(int accountId, int groupId) {
        List<ProgramEnrollment> enrollments = getItronProgramEnrollments(accountId);
        CustomerAccount account = customerAccountDao.getById(accountId);
        if (!isEnrollmentSentToItron(account, groupId, enrollments, unenrollmentCache)) {
            log.debug("ITRON-unenroll account number {}", account.getAccountNumber());
            sendEnrollmentRequest(account, enrollments, false);
            Set<Integer> inventoryIds = getInventoryIdsForGroup(groupId);
            addMacAddressesToGroup(account, getGroup(groupId), inventoryIds);
            enrollmentCache.invalidate(accountId);
        }
    }
     
    @Override
    public void optIn(int accountId, int inventoryId) {
        CustomerAccount account = customerAccountDao.getById(accountId);
        log.debug("ITRON-optIn account number {}", account.getAccountNumber());
        List<ProgramEnrollment> enrollments = getItronProgramEnrollments(accountId);
        Set<Integer> yukonGroupIds = getGroupIdsByInventoryId(inventoryId, enrollments);
        for(int yukonGroupId : yukonGroupIds) {
            Set<Integer> inventoryIds = getInventoryIdsForGroup(yukonGroupId);
            inventoryIds.add(inventoryId);
            addMacAddressesToGroup(account, getGroup(yukonGroupId), inventoryIds);
        }
    }
    
    @Override
    public void optOut(int accountId, int deviceId, int inventoryId) {
        CustomerAccount account = customerAccountDao.getById(accountId);
        List<ProgramEnrollment> enrollments = getItronProgramEnrollments(accountId);
        log.debug("ITRON-optOut account number {}", account.getAccountNumber());
        Set<Integer> yukonGroupIds = getGroupIdsByInventoryId(inventoryId, enrollments);
        String macAddress= deviceDao.getDeviceMacAddress(deviceId);
        for(int yukonGroupId : yukonGroupIds) {
            try {
                // Restore the single device immediately. Disable randomization (ramp out)
                sendRestore(yukonGroupId, macAddress, null, false);
            } catch (ItronEventNotFoundException e) {
                log.debug("No events to restore for Yukon group ID " + yukonGroupId + "while opting out.");
                log.trace("Exception: ", e);
            }
            itronEventLogService.optOut(account.getAccountNumber(), yukonGroupId, macAddress);
            Set<Integer> inventoryIds = getInventoryIdsForGroup(yukonGroupId);
            inventoryIds.remove(inventoryId);
            addMacAddressesToGroup(account, getGroup(yukonGroupId), inventoryIds);
        }
    }
    
    @Transactional
    @Override
    public void sendRestore(int yukonGroupId) {
        long itronGroupId = itronDao.getItronGroupId(yukonGroupId);
        itronEventLogService.sendRestore(yukonGroupId);
        // Restore the group. Enable randomization (ramp out) if the event was sent with randomization.
        sendRestore(yukonGroupId, null, itronGroupId, true);
        // Remove the event ID mapping from the DB
        itronDao.removeActiveEvent(yukonGroupId);
    }
    
    @Override
    public RecentEventParticipationItronData sendDREventForGroup(int yukonGroupId, int dutyCycleType, int dutyCyclePercent, int dutyCyclePeriod, int criticality,
            boolean rampIn, boolean rampOut, Duration duration) {
        String url = ItronEndpointManager.PROGRAM_EVENT.getUrl(settingDao);
        int relay = itronDao.getVirtualRelayId(yukonGroupId);
        int adjustedRelay = relay - 1;
        log.debug("Adjusted group relay " + relay + " to Itron Virtual Relay " + adjustedRelay);
        List<ProgramLoadGroup> programsByLMGroupId = applianceAndProgramDao.getProgramsByLMGroupId(yukonGroupId);
        int programId = programsByLMGroupId.get(0).getPaobjectId();
        int itronProgramId = itronDao.getItronProgramId(programId);
        int itronGroupId = itronDao.getItronGroupId(yukonGroupId);
        
        LiteYukonPAObject group = getGroup(yukonGroupId);
        LiteYukonPAObject program = getProgram(programId);
       
        long eventId = 0;
        
        try {
            itronEventLogService.sendDREventForGroup(yukonGroupId, dutyCyclePercent, dutyCyclePeriod, criticality);
            AddHANLoadControlProgramEventRequest request = ProgramEventManagerHelper.buildDrEvent(dutyCyclePercent, dutyCycleType,
               dutyCyclePeriod, criticality, adjustedRelay, itronGroupId, itronProgramId, String.valueOf(programId), rampIn, rampOut, duration);
            log.debug(XmlUtils.getPrettyXml(request));
            log.debug("ITRON-sendDREventForGroup url:{} yukon group:{} yukon program:{}", url, group.getPaoName(), program.getPaoName());
            JAXBElement<AddProgramEventResponseType> response =
                    (JAXBElement<AddProgramEventResponseType>) ItronEndpointManager.PROGRAM_EVENT.getTemplate(
                    settingDao).marshalSendAndReceive(url, request);
            AddProgramEventResponseType type = response.getValue();
            eventId = type.getProgramEventID();
            itronDao.updateActiveEvent(yukonGroupId, eventId);
            log.debug("ITRON-sendDREventForGroup url:{} mac address:{} itron group id:{} itron event id:{} result:{}.", url,
                group.getPaoName(), program.getPaoName(), eventId , "success");
            log.debug(XmlUtils.getPrettyXml(response));
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.PROGRAM_EVENT);
        }
        
        return new RecentEventParticipationItronData(programId, eventId);
    }
    
    /** 
     * Sends message to Itron to update device logs for all itron groups
     */
    private void updateDeviceLogs() {
        List<Long> itronIds = itronDao.getAllItronGroupIds();
        UpdateDeviceEventLogsRequest updateLogsRequest = new UpdateDeviceEventLogsRequest();
        updateLogsRequest.getGroupIDs().addAll(itronIds);
        updateDeviceLogs(updateLogsRequest);
    }
    
    /**
     * 1. Finds Itron group id used for reads
     * 2. Sends message to Itron to add the devices we are attempting to read to the group
     * 3. Sends message to Itron to update device logs
     */
    private long updateDeviceLogsBeforeRead(List<Integer> deviceIds) {      
        //add new mac addresses to group
        List<String> macAddresses = Lists.newArrayList(deviceDao.getDeviceMacAddresses(deviceIds).values());
        long itronReadGroupId = createOrUpdateGroup(READ_GROUP, macAddresses);
        //update event logs
        UpdateDeviceEventLogsRequest updateLogsRequest = new UpdateDeviceEventLogsRequest();
        updateLogsRequest.getGroupIDs().add(itronReadGroupId);
        updateDeviceLogs(updateLogsRequest);
        return itronReadGroupId;
    }

    /**
     * Attempt to edit the specified group, setting the mac addresses. If that operation fails, assume that the group
     * isn't created yet, and create the group, setting the mac addresses. If that also fails, an exception is thrown.
     */
    private long createOrUpdateGroup(String groupName, List<String> macAddresses) {
        try {
            return addMacAddressesToGroup(groupName, macAddresses, false);
        } catch (Exception e) {
            // (Ignoring exception here - it should already have been logged)
            log.info("Editing Itron group failed, attempting to create group.");
            return addMacAddressesToGroup(groupName, macAddresses, true);
        }
    }
    
    /**
     * Asks Itron to go get the latest data from the device and update itself.
     */
    void updateDeviceLogs(UpdateDeviceEventLogsRequest request) {
        String url = ItronEndpointManager.DEVICE.getUrl(settingDao);
        try {
            log.debug(XmlUtils.getPrettyXml(request));
            log.debug("ITRON-updateDeviceLogs url:{} groups:{}", url, request.getGroupIDs());
            UpdateDeviceEventLogsResponse response =
                (UpdateDeviceEventLogsResponse) ItronEndpointManager.DEVICE.getTemplate(
                    settingDao).marshalSendAndReceive(url, request);
            log.debug("ITRON-updateDeviceLogs url:{} groups:{} result:{}.", url, response.isUpdateRequested(),
                request.getGroupIDs(), "success");
            log.debug(XmlUtils.getPrettyXml(response));
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.DEVICE);
        }
    }
    
    /**
     * Sends restore message to Itron.
     * @param yukonGroupId - Used to get the active Itron events.
     * @param macAddress - If specified, the restore will target this device.
     * @param itronGroupId - If specified, the restore will target this itron group.
     * @param enableRandomization - If true, this sends the restore, but allows the "ramp out" randomization to occur,
     * if it was enabled in the original event request. 
     */
    void sendRestore(int yukonGroupId, String macAddress, Long itronGroupId, boolean enableRandomization) {
        LiteYukonPAObject group = getGroup(yukonGroupId);

        long eventId = itronDao.getActiveEvent(yukonGroupId)
                               .orElseThrow(() -> new ItronEventNotFoundException("Unable to restore, Itron event id doesn't exist."));
        
        String url = ItronEndpointManager.PROGRAM_EVENT.getUrl(settingDao);
        try {
            CancelHANLoadControlProgramEventOnDevicesRequest request =
                ProgramEventManagerHelper.buildRestoreRequest(itronGroupId, eventId, macAddress, enableRandomization);
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
    
    /**
     * 1. Finds all devices in the group
     * 2. Excludes all opted out inventory
     * 3. Finds mac address for each device
     * 4. For each group sends all mac addresses to itron
     * @return itron group id
     */
    private long addMacAddressesToGroup(CustomerAccount account, LiteYukonPAObject group, Set<Integer> inventoryIds) {
        log.debug("ITRON-group {} is associated with account number {}", group.getPaoName(),
            account.getAccountNumber());        
        if (!inventoryIds.isEmpty()) {
            log.debug("ITRON-{} device(s) in group {}", inventoryIds.size(), group.getPaoName());
            Map<Integer, Integer> inventoryIdsToDeviceIds = inventoryDao.getDeviceIds(inventoryIds);
            List<String> macAddresses =
                Lists.newArrayList(deviceDao.getDeviceMacAddresses(inventoryIdsToDeviceIds.values()).values());
            return createOrUpdateGroup(String.valueOf(group.getLiteID()), macAddresses);
        }
        
        //remove all devices from group
        //TODO: YUK-19991 - this is broken - we can't update an Itron group to be empty...
        return createOrUpdateGroup(String.valueOf(group.getLiteID()), new ArrayList<>());
    }
    
    /**
     * Returns active inventory without opt outs.
     */
    private Set<Integer> getInventoryIdsForGroup(int groupId) {
        Multimap<Integer, Integer> groupIdsToInventoryIds =
            enrollmentDao.getActiveEnrolledInventoryIdsMapForGroupIds(Lists.newArrayList(groupId));

        List<Integer> optOuts = enrollmentDao.getCurrentlyOptedOutInventory();

        Set<Integer> inventoryIds = new HashSet<>();
        inventoryIds.addAll(groupIdsToInventoryIds.get(groupId));
        inventoryIds.removeAll(optOuts);
        return inventoryIds;
    }
        
    @Override
    public ZipFile exportDeviceLogs(Long startRecordId, Long endRecordId) {
        updateDeviceLogs();
        
        ExportDeviceLogRequest request = new ExportDeviceLogRequest();
        request.setRecordIDRangeStart(startRecordId);
        if(endRecordId != null) {
            request.setRecordIDRangeEnd(endRecordId);
        }
        itronEventLogService.exportDeviceLogs(startRecordId, endRecordId);
        return exportDeviceLogs(request, ExportType.ALL);
    }
    
    @Override
    public synchronized ZipFile exportDeviceLogsForItronGroup(Long startRecordId, Long endRecordId, List<Integer> deviceId) {
        long itronReadGroup = updateDeviceLogsBeforeRead(deviceId);
        
        ExportDeviceLogRequest request = new ExportDeviceLogRequest();
        request.setRecordIDRangeStart(startRecordId);
        if (endRecordId != null) {
            request.setRecordIDRangeEnd(endRecordId);
        }
        request.getDeviceGroupIDs().add(itronReadGroup);
        return exportDeviceLogs(request, ExportType.READ);
    }

    private ZipFile exportDeviceLogs(ExportDeviceLogRequest request, ExportType type) {
        String url = ItronEndpointManager.REPORT.getUrl(settingDao);
        try {
            log.debug("ITRON-exportDeviceLog url:{} startRecordId:{} endRecordId:{} group:{}.", url,
                request.getRecordIDRangeStart(), request.getRecordIDRangeEnd(), request.getDeviceGroupIDs());
            log.debug(XmlUtils.getPrettyXml(request));
            CommandIDResponse response =
                (CommandIDResponse) ItronEndpointManager.REPORT.getTemplate(settingDao).marshalSendAndReceive(url,
                    request);
            log.debug(XmlUtils.getPrettyXml(response));
            log.debug("ITRON-exportDeviceLog url:{} startRecordId:{} endRecordId:{} group:{} commandId:{} result:{}.",
                url, request.getRecordIDRangeStart(), request.getRecordIDRangeEnd(), request.getDeviceGroupIDs(),
                response.getCommandID(), "success");
            return getExportedFiles(response.getCommandID(), type);
        } catch (Exception e) {
            handleException(e, ItronEndpointManager.REPORT);
        }
        return null;
    }
   
    /**
     * Asks Itron for file names and copies files to ExportArchive/Itron
     */
    private ZipFile getExportedFiles(long commandId, ExportType type) {
        String url = ItronEndpointManager.REPORT.getUrl(settingDao);
        try {
            itronEventLogService.getExportedFiles(commandId);
            GetReportGenerationStatusRequest request = new GetReportGenerationStatusRequest();
            request.setCommandID(commandId);
            log.debug("ITRON-getReport url:{} commandId:{}.", url, commandId);
            log.debug(XmlUtils.getPrettyXml(request));
            GetReportGenerationStatusResponse response = null;
            while (response == null || !response.isCompleted()) {
                response = (GetReportGenerationStatusResponse) ItronEndpointManager.REPORT.getTemplate(
                    settingDao).marshalSendAndReceive(url, request);
                TimeUnit.SECONDS.sleep(10);
            }
            log.debug(XmlUtils.getPrettyXml(response));
            if (response.isFailed()) {
                ItronCommunicationException exception = new ItronCommunicationException(
                    "Failed to get file for command id=" + commandId + " error=" + response.getFailureMessage());
                log.error(exception);
                throw exception;
            }
            
            List<String> fileNames = response.getReportFileNames();
            log.debug("ITRON-getReport url:{} commandId:{} files:{} result:success.", url, commandId, fileNames);
            
            if(fileNames.isEmpty()) {
                log.debug("No report file to parse.");
                return null;    
            }
            
            return downloadAndZipReportFiles(fileNames, commandId, type);

        } catch (Exception e) {
            handleException(e, ItronEndpointManager.REPORT);
        }
        return null;
    }
    
    /**
     * Downloads files from itron.
     * The files name is going to be filenumber_timestamp_commandId. Command Id can be correlated to log debug statements.
     * Zips the copied files and stores them in \Yukon\ExportArchive\Itron, the name of the zipped file is [READ/ALL]_timestamp_commandId.zip. 
     * Returns a zip file
     */
    private ZipFile downloadAndZipReportFiles(List<String> fileUrls, long commandId, ExportType type) throws ItronSecurityException, IOException {
        // Create some file strings
        String timestamp = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, YukonUserContext.system);
        String zipName = type + "_" + timestamp + "_" + commandId + ".zip";
        log.debug("Zip File Name: " + zipName);
        
        // Assemble the info needed for SFTP connection
        Optional<YukonHttpProxy> proxy = YukonHttpProxy.fromGlobalSetting(settingDao);
        String domain = settingDao.getString(GlobalSettingType.ITRON_SFTP_URL);
        String port = domain.contains(":") ? "" : "22"; //Use default if port isn't specified in domain string
        String user = settingDao.getString(GlobalSettingType.ITRON_SFTP_USERNAME);
        String password = settingDao.getString(GlobalSettingType.ITRON_SFTP_PASSWORD);
        log.debug("SFTP Connection Params - Domain: {}, Port: {}, User: {}", domain, port, user);
        
        ItronSecurityKeyPair keys;
        try {
            log.debug("Loading Itron SFTP key");
            keys = itronSecurityService.getItronSshRsaKeyPair();
            log.debug("Loaded Itron SFTP key");
        } catch (Exception e) {
            log.error("Error loading SFTP keys.", e);
            throw e;
        }
        
        // Copy the files over SFTP
        List<File> files = new ArrayList<>();
        
        SftpConnection sftp = null;
        try {
            log.debug("Initializing SFTP connection.");
            sftp = new SftpConnection(domain, port, proxy, user, password, keys.getPrivateKey());
            log.debug("Copying data files over SFTP");
            files = copySftpFiles(sftp, fileUrls, commandId, timestamp);
        } catch (Exception e) {
            log.debug("Error copying data files over SFTP", e);
        } finally {
            if (sftp != null) {
                try {
                    sftp.close();
                } catch (Exception e) {
                    log.debug("Error closing SFTP connection.", e);
                }
            }
        }
        
        // Check and possibly clean up old files
        if (lastItronFileDeletionDate.isBefore(DateTime.now().withTimeAtStartOfDay())) {
            deleteOldItronFiles();
        }
        
        return zipFiles(zipName, files);
    }
    
    /**
     * Use an SFTP connection to copy a list of files from the SFTP server to the local file system.
     */
    private List<File> copySftpFiles(SftpConnection sftp, List<String> fileUrls, long commandId, String timestamp) {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < fileUrls.size(); i++) {
            String fileUrl = fileUrls.get(i);
            fileUrl = trimItronSftpFilePathToReports(fileUrl);
            log.debug("Trimmed file path: " + fileUrl);
            
            try {
                // Determine the local file name/path
                String fileName = (i + 1) + "_" + timestamp + "_" + commandId + ".csv";
                File file = new File(FILE_PATH, fileName);
                String localFilePath = file.getAbsolutePath();
                
                // Perform the copy from SFTP server
                sftp.copyRemoteFile(fileUrl, localFilePath);
                files.add(file);
                log.debug("ITRON-downloaded Itron file: {} created file: {} commandId: {}.", fileUrl, fileName, commandId);
            } catch (Exception e) {
                log.error("Unable to download file: " + fileUrl, e);
            }
        }
        return files;
    }
    
    /**
     * If the file path root is above "reports", trim it up to that directory. This is the default "home"
     * directory for the Itron SFTP connection, at least in the Dakota Electric dev system.
     * For example:
     * Path reported by API: "/usr/ssn/home/ssn/.drm/reports/filename.csv"
     * Actual path needed by SFTP connection: "reports/filename.csv"
     */
    private String trimItronSftpFilePathToReports(String filePath) {
        int reportsPathSegmentIndex = filePath.indexOf("reports/");
        if (reportsPathSegmentIndex >= 0) {
            return filePath.substring(reportsPathSegmentIndex);
        }
        return filePath;
    }
    
    /**
     * Zip a list of files into a single zip with the specified name.
     */
    private ZipFile zipFiles(String zipName, List<File> files) {
        try (FileOutputStream fos = new FileOutputStream(new File(FILE_PATH, zipName));
             ZipOutputStream zos = new ZipOutputStream(fos);) {
            
            for (File file : files) {
                log.debug("Zipping file: {}", file.getName());
                zos.putNextEntry(new ZipEntry(file.getName()));
                byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
                zos.write(bytes, 0, bytes.length);
                zos.closeEntry();
            }
            
            zos.close(); // Manually, to ensure we close the output stream before using the file.
            String zip = FILE_PATH + System.getProperty("file.separator") + zipName;
            log.debug("Created zip file: " + zip);
            files.forEach(File::delete);
            return new ZipFile(zip);
        } catch (Exception e) {
            log.error("Unable to zip files", e);
            throw new ItronCommunicationException("Unable to zip files", e);
        }
    }
    
    /**
     * Delete Itron files older than the configured days to keep.
     */
    private void deleteOldItronFiles() {
        // Determine the retention date
        int daysToKeep = settingDao.getInteger(GlobalSettingType.HISTORY_CLEANUP_DAYS_TO_KEEP);
        if (daysToKeep <= 0) {
            log.info("Itron archive file cleanup is disabled. No files were deleted.");
            return;
        }
        DateTime retentionDate = DateTime.now().minusDays(daysToKeep);
        log.info("Itron archive file cleanup started. Deleting files last used before " + retentionDate.toDate().toString() + ".");
        
        // Get the files to check
        File dir = new File(CtiUtilities.getItronDirPath());
        File[] directoryListing = dir.listFiles();
        
        // Check for old files and delete them
        try {
            int filesDeleted = 0;
            for (File itronZip : directoryListing) {
                filesDeleted += deleteIfOldFile(itronZip, retentionDate);
            }
            log.info("Itron archive file cleanup is complete. " + filesDeleted + " log files were deleted.");
        } catch (Exception e) {
            log.error("Unable to delete old file archives", e);
        }
    }
    
    /**
     * Delete the specified file if it's older than the retention date.
     * @return The number of files deleted (either 1 or 0)
     */
    private int deleteIfOldFile(File file, DateTime retentionDate) {
        DateTime lastUsedDate = new DateTime(file.lastModified());
        if (file.exists() && lastUsedDate.isBefore(retentionDate)) {
            if (file.delete()) {
                log.info("Deleted itron archive file: " + file.getPath());
                return 1;
            }
        }
        return 0;
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
    private void sendEnrollmentRequest(CustomerAccount account, List<ProgramEnrollment> enrollments, boolean enroll) {
        String url = ItronEndpointManager.PROGRAM.getUrl(settingDao);
        List<Long> itronProgramIds = new ArrayList<>();
        
        if (!enrollments.isEmpty()) {
            List<Integer> assignedProgramIds =
                enrollments.stream().map(ProgramEnrollment::getAssignedProgramId).collect(Collectors.toList());
            Collection<Integer> programPaoIds = assignedProgramDao.getProgramIdsByAssignedProgramIds(assignedProgramIds).values();
            
            if(enroll) {
                createPrograms(programPaoIds);
            }
            itronProgramIds.addAll(itronDao.getItronProgramIds(programPaoIds).values());            
        }
        
        log.debug("Sending enrollment request to itron for account {} enrolling itron program ids {}, isEnroll: {} ", account.getAccountNumber(),
            itronProgramIds, enroll);
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
     * @return itron group id
     */
    private long addMacAddressesToGroup(String lmGroupId, List<String> macAddresses, boolean createGroup) {
        String url = ItronEndpointManager.DEVICE.getUrl(settingDao);

        try {
            ESIGroupRequestType requestType = DeviceManagerHelper.buildGroupEditRequest(lmGroupId, macAddresses);
            JAXBElement<ESIGroupRequestType> request;
            if (createGroup) {
                request = new ObjectFactory().createAddESIGroupRequest(requestType);
            } else {
                request = new ObjectFactory().createEditESIGroupRequest(requestType);
            }
            macAddresses.forEach(macAddress ->
                itronEventLogService.addMacAddressToGroup(macAddress, lmGroupId)
            );
            log.debug("ITRON-addMacAddressToGroup url:{} lm group id:{} mac addresses:{} create group: {}.", url, lmGroupId,
                macAddresses, createGroup);
            log.debug(XmlUtils.getPrettyXml(new ESIGroupRequestTypeHolder(request.getValue())));
            JAXBElement<ESIGroupResponseType> response =
                (JAXBElement<ESIGroupResponseType>) ItronEndpointManager.DEVICE.getTemplate(settingDao).marshalSendAndReceive(url,
                    request);
            log.debug("ITRON-addMacAddressToGroup url:{} lm group id:{} mac addresses:{} create group: {} result:{}.", 
                      url, lmGroupId, macAddresses, createGroup, "success");
            log.debug(XmlUtils.getPrettyXml(new ESIGroupResponseTypeHolder(response.getValue())));
            itronEventLogService.addGroup(lmGroupId, response.getValue().getGroupID());
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
        return cache.getAllLMGroups()
                    .stream()
                    .filter(group -> group.getLiteID() == yukonGroupId)
                    .findFirst()
                    .orElseThrow();
    }

    private LiteYukonPAObject getProgram(int yukonProgramId) {
        return cache.getAllLMPrograms()
                    .stream()
                    .filter(program -> program.getLiteID() == yukonProgramId)
                    .findFirst()
                    .orElseThrow();
    }
    
    /**
     * Get all groups that the specified inventory is enrolled in, based on the specified enrollments.
     */
    private Set<Integer> getGroupIdsByInventoryId(int inventoryId, List<ProgramEnrollment> enrollments) {
        return enrollments.stream()
                          .filter(enrollment -> enrollment.getInventoryId() == inventoryId)
                          .map(ProgramEnrollment::getLmGroupId)
                          .collect(Collectors.toSet());
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
                            .findFirst()
                            .orElseThrow()
                            .handleSoapFault((SoapFaultClientException) e, faultCodesToIgnore, log);
        } else {
            log.error("Communication error:", e);
            throw new ItronCommunicationException("Communication error:", e);
        }
    }
    
    /**
     * Returns true if enrollment was already sent to Itron. Caches enrollment information.
     */
    private boolean isEnrollmentSentToItron(CustomerAccount account, int groupId, List<ProgramEnrollment> enrollments,
            Cache<Integer, Enrollment> cacheToCheck) {
        Enrollment newValueToCache = new Enrollment(groupId, enrollments);
        Enrollment valueInCache = cacheToCheck.getIfPresent(account.getAccountId());
        if (valueInCache != null 
                && CollectionUtils.isEqualCollection(newValueToCache.inventoryIds, valueInCache.inventoryIds)
                    && newValueToCache.groupId == valueInCache.groupId) {
            
            log.debug("ITRON-skipping sending enroll/unroll messages for account number {}, as the messages were already sent. ",
                account.getAccountNumber());
            return true;
        }
        cacheToCheck.put(account.getAccountId(), newValueToCache);
        return false;
    }
        
    private static class Enrollment {
        private int groupId;
        private List<Integer> inventoryIds;
        
        public Enrollment(int groupId, List<ProgramEnrollment> enrollments){
            inventoryIds = enrollments.stream()
                    .map(ProgramEnrollment::getInventoryId)
                    .collect(Collectors.toList());
            this.groupId = groupId;
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
