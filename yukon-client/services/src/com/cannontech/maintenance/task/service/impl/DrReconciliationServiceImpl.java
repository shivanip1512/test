package com.cannontech.maintenance.task.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.LMGroupDaoImpl;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.LcrDeviceStatus;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressDao;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.cannontech.maintenance.task.dao.DrReconciliationDao;
import com.cannontech.maintenance.task.service.DrReconciliationService;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.ExpressComAddressView;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.UserUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

public class DrReconciliationServiceImpl implements DrReconciliationService {
    @Autowired private DrReconciliationDao drReconciliationDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private ExpressComReportedAddressDao expressComDaoImpl;
    @Autowired private LMGroupDaoImpl lmGroupDaoImpl;
    @Autowired private PaoDao paoDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private LmHardwareCommandService commandService;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private AttributeService attributeService;
    @Autowired public AsyncDynamicDataSource asyncDynamicDataSource;

    private static final Logger log = YukonLogManager.getLogger(DrReconciliationServiceImpl.class);
    private final List<ScheduledFuture<?>> schedulersFuture = new ArrayList<>();

    private long minimumExecutionTime = 300000;
    private static final int calculationCycleMinutes = 12;
    private static final int perMinuteScheduling = 1;
    
    /**
     * Method gives a list of two way RFN LCRs whose expected service status is 'out of service' in Yukon.
     */
    private List<Integer> getOutOfServiceExpectedLcrs() {
        if (!isSuppressMessage()) {
            log.trace("Finding Out of Service Expected LCRs");
            List<Integer> oosExpectedLcrInventoryIds = drReconciliationDao.getOutOfServiceExpectedLcrs();
            log.debug("Found " + oosExpectedLcrInventoryIds.size()  + " Out of Service Expected LCRs");
            return oosExpectedLcrInventoryIds;
        }
        
        return Collections.emptyList();
    }

    /**
     * Method gives a list of two way RFN LCRs whose expected service status is 'InService' in Yukon
     */
    private List<Integer> getInServiceExpectedLcrs() {
        if (!isSuppressMessage()) {
            log.trace("Finding In Service Expected LCRs");
            List<Integer> inServiceExpectedLcrInventoryIds = drReconciliationDao.getInServiceExpectedLcrs();
            log.debug("Found " + inServiceExpectedLcrInventoryIds.size() + " In Service Expected LCRs");
            return inServiceExpectedLcrInventoryIds;
        }
        
        return Collections.emptyList();
    }

    /**
     * Give a list of LCR which have conflicting addresses and messages have to be send for them.
     */
    private Set<Integer> getLCRWithConflictingAddressing() {

        Set<Integer> conflictingLCR = new HashSet<>();

        // Get Group which have atleast one LCR enrolled
        List<Integer> groupsWithRfnLcrEnrollements = drReconciliationDao.getGroupsWithRfnDeviceEnrolled();

        for (Integer groupId : groupsWithRfnLcrEnrollements) {
            // Get Group addressing
            ExpressComAddressView lmGroupAddressing = lmGroupDaoImpl.getExpressComAddressing(groupId);

            // Get LCR in each group
            List<Integer> lcrsInGroup = drReconciliationDao.getEnrolledRfnLcrForGroup(groupId);

            // Get LCR addressing - This can be heavy
            List<ExpressComReportedAddress> lcrAddressing = expressComDaoImpl.getCurrentAddresses(lcrsInGroup);

            Set<Integer> conflicts = findConflicts(lmGroupAddressing, lcrAddressing);
            
            // Check conflicts
            conflictingLCR.addAll(conflicts);
        }
        // Get LCR enrolled in multiple group
        Multimap<Integer, Integer> lcrInMultipleGroups =
            drReconciliationDao.getLcrEnrolledInMultipleGroup(conflictingLCR);
        
        log.debug("Found " + lcrInMultipleGroups.size() + " LCR to Group Combinations for LCRs with multiple groups");

        final List<Integer> groupConflictingLCR = new ArrayList<>();

        lcrInMultipleGroups.keySet().forEach(lcr -> {
            ArrayList<Integer> groups = new ArrayList<Integer>(lcrInMultipleGroups.get(lcr));
            ExpressComAddressView tempGroup = new ExpressComAddressView();
            ExpressComAddressView combinedGroup = new ExpressComAddressView();
            for (int group : groups) {
                ExpressComAddressView lmGroupAddressing = lmGroupDaoImpl.getExpressComAddressing(group);
                boolean couldCombineAddress = combineGroupAddressing(tempGroup, lmGroupAddressing, combinedGroup);

                // If false there is a mismatch and no further checking is required.
                if (couldCombineAddress) {
                    tempGroup = combinedGroup;
                } else {
                    // Mismatch, we can not fix it do not send message
                    groupConflictingLCR.add(lcr);
                    LiteLmHardwareBase lmhb = (LiteLmHardwareBase) inventoryBaseDao.getHardwareByDeviceId(lcr);
                    systemEventLogService.groupConflictLCRDetected(lmhb.getManufacturerSerialNumber());
                    break;
                }
            }
        });
        conflictingLCR.removeAll(groupConflictingLCR);
        
        log.debug("Found " + groupConflictingLCR.size() + " incompatible enrollments");
        
        return conflictingLCR;
    }

    private Set<Integer> findConflicts(ExpressComAddressView lmGroupAddressing,
            List<ExpressComReportedAddress> lcrAddressing) {

        String addressUsage = lmGroupAddressing.getUsage().toLowerCase();
        Set<Integer> incorrectAddressingLCR = new HashSet<>();

        for (ExpressComReportedAddress lcrAddress : lcrAddressing) {
            if (addressUsage.contains("s")) {
                if (lmGroupAddressing.getSpid() != lcrAddress.getSpid()) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    continue;
                }
            }
            if (addressUsage.contains("g")) {
                if (lmGroupAddressing.getGeo() != lcrAddress.getGeo()) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    continue;
                }
            }
            if (addressUsage.contains("b")) {
                if (lmGroupAddressing.getSubstation() != lcrAddress.getSubstation()) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    continue;
                }
            }
            if (addressUsage.contains("f")) {
                if ((lmGroupAddressing.getFeeder() & lcrAddress.getFeeder()) == 0) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    continue;
                }
            }
            if (addressUsage.contains("z")) {
                if (lmGroupAddressing.getZip() != lcrAddress.getZip()) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    continue;
                }
            }
            if (addressUsage.contains("u")) {
                // Is user same as uda?
                if (lmGroupAddressing.getUser() != lcrAddress.getUda()) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    continue;
                }
            }
            int program = 0;
            if (addressUsage.contains("p")) {
                program = lmGroupAddressing.getProgram();
            }
            int splinterId = 0;
            if (addressUsage.contains("r")) {
                splinterId = lmGroupAddressing.getSplinter();
            }
            boolean programFound = false;
            boolean splinterFound = false;

            // If program is set, check if any relay of LCR have this program if no match found then that LCR
            // has incorrect addressing
            for (ExpressComReportedAddressRelay relay : lcrAddress.getRelays()) {
                if (program != 0) {
                    if (!programFound && program == relay.getProgram()) {
                        programFound = true;
                    }
                }
                // If splinter is set, check if any relay of LCR have this splinter if no match found then
                // that LCR has incorrect addressing
                if (splinterId != 0) {
                    if (!splinterFound && splinterId == relay.getSplinter()) {
                        splinterFound = true;
                    }
                }
                // If splinter and program is set then both needs to be matched for the same relay.
                if (program != 0 && splinterId != 0) {
                    if (program == relay.getProgram() && splinterId == relay.getSplinter()) {
                        programFound = true;
                        splinterFound = true;
                    }
                }
            }
            if (program != 0 && !programFound) {
                incorrectAddressingLCR.add(lcrAddress.getDeviceId());
            }
            if (splinterId != 0 && !splinterFound) {
                incorrectAddressingLCR.add(lcrAddress.getDeviceId());
            }
        }
        return incorrectAddressingLCR;
    }

    private boolean combineGroupAddressing(ExpressComAddressView tempGroup, ExpressComAddressView group,
            ExpressComAddressView combinedGroup) {
        
        if (tempGroup.getSpid() == 0 && group.getSpid() != 0) {
            combinedGroup.setSpid(group.getSpid());
        } else if (tempGroup.getSpid() != 0 && group.getSpid() != 0 && tempGroup.getSpid() != group.getSpid()) {
            return false;
        }

        if (tempGroup.getGeo() == 0 && group.getGeo() != 0) {
            combinedGroup.setGeo(group.getGeo());
        } else if (tempGroup.getGeo() != 0 && group.getGeo() != 0 && tempGroup.getGeo() != group.getGeo()) {
            return false;
        }

        if (tempGroup.getSubstation() == 0 && group.getSubstation() != 0) {
            combinedGroup.setSubstation(group.getSubstation());
        } else if (tempGroup.getSubstation() != 0 && group.getSubstation() != 0 && tempGroup.getSubstation() != group.getSubstation()) {
            return false;
        }

        if (tempGroup.getFeeder() == 0 && group.getFeeder() != 0) {
            combinedGroup.setFeeder(group.getFeeder());
         // If both groups specify feeder, they must have at least 1 overlapping feeder value
        } else if ((tempGroup.getFeeder() != 0 && group.getFeeder() != 0 && (tempGroup.getFeeder() & group.getFeeder()) == 0)) {
            return false;
        }

        if (tempGroup.getZip() == 0 && group.getZip() != 0) {
            combinedGroup.setZip(group.getZip());
        } else if (tempGroup.getZip() != 0 && group.getZip() != 0 && tempGroup.getZip() != group.getZip()) {
            return false;
        }

        if (tempGroup.getUser() == 0 && group.getUser() != 0) {
            combinedGroup.setUser(group.getUser());
        } else if (tempGroup.getUser() != 0 && group.getUser() != 0 && tempGroup.getUser() != group.getUser()) {
            return false;
        }
        return true;
    }

    /**
     * Method looks through all energy company to see if any energy company have setting for
     * SUPPRESS_IN_OUT_SERVICE_MESSAGES as true.
     */
    private boolean isSuppressMessage() {
        List<EnergyCompany> allEnergyCompanies = Lists.newArrayList(energyCompanyDao.getAllEnergyCompanies());
        for (EnergyCompany energyCompany : allEnergyCompanies) {
            boolean isSuppressMessage = ecSettingDao.getBoolean(
                EnergyCompanySettingType.SUPPRESS_IN_OUT_SERVICE_MESSAGES, energyCompany.getId());
            if (isSuppressMessage) {
                return isSuppressMessage;
            }
        }
        return false;
    }
    
    @Override
    public boolean startDRReconciliation(Instant processEndTime) {
        try {
            CountDownLatch messageSendingDone = new CountDownLatch(1);
            stopSchedulers();
            if (!doDRReconciliation(messageSendingDone, processEndTime)) {
                return true;
            }
            long drReconEndTime = processEndTime.minus(minimumExecutionTime).getMillis() - Instant.now().getMillis();
            messageSendingDone.await(drReconEndTime, TimeUnit.MILLISECONDS);
            stopSchedulers();
        } catch (InterruptedException e) {
            log.debug("DR reconciliation thread was interrupted");
            stopSchedulers();
            return true;
        }
        return true;
    }
    
    /**
     * Stops the schedulers which are created to do DR reconciliation.
     */
    private void stopSchedulers() {
        schedulersFuture.stream().forEach(future -> {
            future.cancel(true);
        });
        schedulersFuture.clear();
    }

    /**
     * This method send the appropriate messages to LCR.
     */
    private boolean doDRReconciliation(CountDownLatch messageSendingDone, Instant processEndTime) {

        ScheduledFuture<?> futureSchdTwelveMin, futureSchdOneMin;
        BlockingQueue<LCRCommandHolder> queue = new ArrayBlockingQueue<>(10000);
        Map<Integer, Set<Integer>> lcrsToSendCommand = new HashMap<>(2);

        // Get LCR's which are expected to be out of service, we need to send OOS messages to them.
        List<Integer> sendOOS = getOutOfServiceExpectedLcrs();
        Set<Integer> sendOOSDevice = Sets.newHashSet(inventoryDao.getDeviceIds(sendOOS).values());
        lcrsToSendCommand.put(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL, sendOOSDevice);

        // Get LCR's which are expected to be in service, we need to send IN service message to them.
        List<Integer> sendInService = getInServiceExpectedLcrs();
        Set<Integer> sendInServiceDevice = Sets.newHashSet(inventoryDao.getDeviceIds(sendInService).values());
        lcrsToSendCommand.put(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL, sendInServiceDevice);

        compareExpectedServiceStatusWithReportedLcrs(lcrsToSendCommand);

        // Get LCR's with incorrect addressing, we need to send config message to them
        Set<Integer> sendAddressing = getLCRWithConflictingAddressing();
        log.debug("Devices suspected for sending In service command " + sendInServiceDevice);
        log.debug("Devices suspected for sending Out of service command " + sendOOSDevice);
        log.debug("Devices suspected for sending Config command " + sendAddressing);

        Set<Integer> allLcrs = new HashSet<>();
        allLcrs.addAll(sendOOSDevice);
        allLcrs.addAll(sendInServiceDevice);
        allLcrs.addAll(sendAddressing);
        
        // Check if message can be send to LCR in this run cycle of DR reconciliation. 
        // If not then remove those LCR from list and do not consider them in further processing for this run cycle
        long drReconEndTime = processEndTime.minus(minimumExecutionTime).getMillis() - Instant.now().getMillis();

        log.trace("Finding devices that have not had enough time since the last config was sent");
        Set<Integer> allLcrsForThisCycle = drReconciliationDao.getLcrsToSendMessageInCurrentCycle(allLcrs, drReconEndTime);
        log.trace("Done finding devices that have not had enough time since the last config was sent");
        sendOOSDevice.retainAll(allLcrsForThisCycle);
        sendInServiceDevice.retainAll(allLcrsForThisCycle);
        sendAddressing.retainAll(allLcrsForThisCycle);
        
        log.info("Devices picked for Send In service command " + sendInServiceDevice);
        log.info("Devices picked for Send Out of service command " + sendOOSDevice);
        log.info("Devices picked for Send Config command " + sendAddressing);

        // Do not have any LCR's to send message return back.
        if (allLcrs.isEmpty()) { // Should this be allLcrsForThisCycle?
            return false;
        }

        log.trace("Finding devices enrolled in multiple groups");
        // This will allow us to find how many messages will be send for LCR in multiple groups
        Multimap<Integer, Integer> lcrInMultipleGroups = drReconciliationDao.getLcrEnrolledInMultipleGroup(allLcrs); // Should this be allLcrsForThisCycle?
        log.trace("Done finding devices enrolled in multiple groups" + lcrInMultipleGroups);

        // Find out number of RFN gateways in the system
        int noOfGateways = paoDao.getPaoCount(PaoType.getRfGatewayTypes());
        log.debug("Found " + noOfGateways + " gateways");

        // I am not sure if we want to send any message if we do not have gateways
        if (noOfGateways == 0) {
            return false;
        }
        // Have to send 1 message per gateway every 12 min
        int noOfMessagePerMin = (noOfGateways / calculationCycleMinutes == 0) ? 1 : noOfGateways / calculationCycleMinutes;

        // This scheduler will run initially (0 minute) and then after every 12 minute.
        // It will find and add in queue the list of LCR for which message have to be send in the next 12
        // minute.
        futureSchdTwelveMin = executor.scheduleAtFixedRate(() -> {
            int noOfLCRToSendMessage = noOfGateways;
            log.debug("Finding new LCRs to send messages to");

            // This query should return top LCR for which message have to be send. noOfLCRToSendMessage is the number
            // of message to send in next 12 min
            Map<Integer, Integer> sendMessageToLcr =
                drReconciliationDao.getLcrWithLatestEvent(allLcrsForThisCycle, noOfLCRToSendMessage);
 
            log.debug("Will send message to " + sendMessageToLcr.size() + " LCR in next 12 min");

            // Queue the messages to send
            sendMessageToLcr.entrySet().stream().forEach(lcr -> {
                try {
                    int noOfMessages = 1;
                    if (sendOOSDevice.contains(lcr.getKey())) {
                        queue.put(new LCRCommandHolder(lcr.getValue(), lcr.getKey(), LmHardwareCommandType.OUT_OF_SERVICE, noOfMessages));
                    } else if (sendInServiceDevice.contains(lcr.getKey())) {
                        queue.put(new LCRCommandHolder(lcr.getValue(), lcr.getKey(), LmHardwareCommandType.IN_SERVICE, noOfMessages));
                    } else if (sendAddressing.contains(lcr.getKey())) {
                        if (lcrInMultipleGroups.containsKey(lcr.getKey())) {
                            noOfMessages = lcrInMultipleGroups.get(lcr.getKey()).size();
                        }
                        queue.put(new LCRCommandHolder(lcr.getValue(), lcr.getKey(), LmHardwareCommandType.CONFIG, noOfMessages));
                    }
                } catch (InterruptedException e) {
                    log.error("Scheduler for finding message to send Interrupted " + e);
                }
            });
           

        }, 0, calculationCycleMinutes, TimeUnit.MINUTES);

        // It will send only that number of messages which have to be send per minute.
        futureSchdOneMin = executor.scheduleAtFixedRate(() -> {
            int messagesSent = 0;
            
            while (!queue.isEmpty() && messagesSent < noOfMessagePerMin) {
                LCRCommandHolder lcrCommandHolder;
                try {
                    lcrCommandHolder = queue.take();
                    if (sendCommand(lcrCommandHolder)) {
                        Integer deviceId = lcrCommandHolder.getDeviceId();
                        LmHardwareCommandType lmHardwareCommandType = lcrCommandHolder.getOperation();
                        if (lmHardwareCommandType == LmHardwareCommandType.OUT_OF_SERVICE) {
                            sendOOSDevice.remove(deviceId);
                        } else if (lmHardwareCommandType == LmHardwareCommandType.IN_SERVICE) {
                            sendInServiceDevice.remove(deviceId);
                        } else if (lmHardwareCommandType == LmHardwareCommandType.CONFIG) {
                            sendAddressing.remove(deviceId);
                        }
                        messagesSent = messagesSent + lcrCommandHolder.getNoOfMessages();
                    }
                } catch (InterruptedException e) {
                    log.error("Scheduler for sending message Interrupted " + e);
                }
            }
            if (messagesSent > 0) {
                log.debug("Sent " + messagesSent + " messages");
            }
            if (sendOOSDevice.isEmpty() && sendInServiceDevice.isEmpty() && sendAddressing.isEmpty()) {
                messageSendingDone.countDown();
            }
        }, perMinuteScheduling, perMinuteScheduling, TimeUnit.MINUTES);
        
        schedulersFuture.add(futureSchdTwelveMin);
        schedulersFuture.add(futureSchdOneMin);
        return true;
    }

    /**
     * This method send appropriate command to the LCR
     * @return true if command completion happens successfully.
     */
    private boolean sendCommand(LCRCommandHolder lcrCommandHolder) {
        int inventoryId = lcrCommandHolder.getInventoryId();
        LiteLmHardwareBase lmhb = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);
        String serialNumber = lmhb.getManufacturerSerialNumber();
        LiteYukonUser user = UserUtils.getYukonUser();
        LmHardwareCommand command = new LmHardwareCommand();
        command.setDevice(lmhb);
        command.setUser(user);
        LmHardwareCommandType lmHardwareCommandType = lcrCommandHolder.getOperation();
        command.setType(lmHardwareCommandType);
        boolean success = true;
        try {
            if (lmHardwareCommandType == LmHardwareCommandType.OUT_OF_SERVICE) {
                log.debug("Sending OOS message for LCR " + inventoryId);
                commandService.sendOutOfServiceCommand(command);
                systemEventLogService.outOfServiceMessageSent(serialNumber);
                log.debug("Success - inventory id =" + inventoryId + " 'Out of Service' Command was sent");
            } else if (lmHardwareCommandType == LmHardwareCommandType.IN_SERVICE) {
                log.debug("Sending In Service message for LCR " + inventoryId);
                commandService.sendInServiceCommand(command);
                systemEventLogService.inServiceMessageSent(serialNumber);
                log.debug("Success - inventory id =" + inventoryId + " 'In Service' Command was sent");
            } else if (lmHardwareCommandType == LmHardwareCommandType.CONFIG) {
                log.debug("Sending Config message for LCR " + inventoryId);
                commandService.sendConfigCommand(command);
                systemEventLogService.configMessageSent(serialNumber);
                log.debug("Success - inventory id =" + inventoryId + " 'Config' Command was sent");
            }
        } catch (CommandCompletionException e) {
            success = false;
            log.error("Failed - Unable to send command " + lmHardwareCommandType + " to inventory id=" + inventoryId, e);
            systemEventLogService.messageSendingFailed(serialNumber, e.getMessage());
        }
        return success;
    }

    private void compareExpectedServiceStatusWithReportedLcrs(Map<Integer, Set<Integer>> lcrsToSendCommand) {

        log.trace("Comparing expected service status with what the LCR reported");
        List<Integer> deviceList = new ArrayList<>();
        deviceList = lcrsToSendCommand.values().stream()
                                               .flatMap(p -> p.stream())
                                               .collect(Collectors.toList());
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaos(deviceList);
        BiMap<PaoIdentifier, LitePoint> deviceToPoint =
            attributeService.getPoints(paos, BuiltInAttribute.SERVICE_STATUS);
        Set<Integer> statusPointIds =
            deviceToPoint.values().stream()
                                  .map(LitePoint :: getPointID)
                                  .collect(Collectors.toSet());
        log.trace("Requesting values from Dispatch");
        Set<? extends PointValueQualityHolder> pointValues = asyncDynamicDataSource.getPointDataOnce(statusPointIds);
        log.trace("Done requesting values from Dispatch");
        
        Map<Integer, Integer> pointsToPaos = 
                deviceToPoint.inverse().entrySet().stream()
                        .collect(Collectors.toMap(
                            p -> p.getKey().getPointID(),
                            p -> p.getValue().getPaoId()));

        ImmutableListMultimap<LcrDeviceStatus, ? extends PointValueQualityHolder> pointValueStates = Multimaps.index(
            pointValues, pointValue -> PointStateHelper.decodeRawState(LcrDeviceStatus.class, pointValue.getValue()));

        ListMultimap<LcrDeviceStatus, Integer> devicesByStatus =
            Multimaps.transformValues(pointValueStates, pointValue -> pointsToPaos.get(pointValue.getId()));

        List<Integer> inServiceLcrs = devicesByStatus.get(LcrDeviceStatus.IN_SERVICE);
        List<Integer> outOfServiceLcrs = devicesByStatus.get(LcrDeviceStatus.OUT_OF_SERVICE);

        if (inServiceLcrs != null) {
            log.trace("Removing all correct In Service LCRs from list");
            // If actual reporting status of LCR is already In Service, then we don't need to send
            // InService message to that LCR, hence removing it from sendInServiceDevice list
            lcrsToSendCommand.get(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL).removeAll(inServiceLcrs);
        }
        if (outOfServiceLcrs != null) {
            log.trace("Removing all correct OOS LCRs from list");
            // If actual reporting status of LCR is already OOS, then we don't need to send Out Of Service
            // message to that LCR, hence removing it from OOSDevice list
            lcrsToSendCommand.get(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL).removeAll(outOfServiceLcrs);
        }
        
        log.trace("Done comparing expected service status with what the LCR reported");
    }

    /**
     * Class to hold the LCR command type to send to LCR.
     */
    private class LCRCommandHolder {
        int inventoryId;
        int deviceId;
        
        LmHardwareCommandType commandType;
        int noOfMessages;

        public LCRCommandHolder(int inventoryId, int deviceId, LmHardwareCommandType commandType, int noOfMessages) {
            this.inventoryId = inventoryId;
            this.deviceId = deviceId;
            this.commandType = commandType;
            this.noOfMessages = noOfMessages;
        }

        public int getInventoryId() {
            return inventoryId;
        }

        public LmHardwareCommandType getOperation() {
            return commandType;
        }

        public int getNoOfMessages() {
            return noOfMessages;
        }
        
        public int getDeviceId() {
            return deviceId;
        }
    }
}
