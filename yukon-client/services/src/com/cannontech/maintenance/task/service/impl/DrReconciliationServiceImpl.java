package com.cannontech.maintenance.task.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.LMGroupDaoImpl;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.cannontech.dr.dao.impl.ExpressComReportedAddressDaoImpl;
import com.cannontech.maintenance.task.dao.DrReconciliationDao;
import com.cannontech.maintenance.task.service.DrReconciliationService;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.model.ExpressComAddressView;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.UserUtils;
import com.google.common.collect.Multimap;

public class DrReconciliationServiceImpl implements DrReconciliationService {
    @Autowired private DrReconciliationDao drReconciliationDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private ExpressComReportedAddressDaoImpl expressComDaoImpl;
    @Autowired private LMGroupDaoImpl lmGroupDaoImpl;
    @Autowired private PaoDao paoDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private LmHardwareCommandService commandService;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    
    private static final Logger log = YukonLogManager.getLogger(DrReconciliationServiceImpl.class);

    private long minimumExecutionTime = 300000;
    private static final int calculationCycleMinutes = 12;
    private static final int perMinuteScheduling = 1;
    
    /**
     * Method gives a list of two way RFN LCRs whose expected service status is 'out of service' in Yukon.
     */
    private List<Integer> getOutOfServiceExpectedLcrs() {
        if (!isSuppressMessage()) {
            List<Integer> oosExpectedLcrInventoryIds = drReconciliationDao.getOutOfServiceExpectedLcrs();
            return oosExpectedLcrInventoryIds;
        }
        return Collections.emptyList();
    }

    /**
     * Method gives a list of two way RFN LCRs whose expected service status is 'InService' in Yukon
     */
    private List<Integer> getInServiceExpectedLcrs() {
        if (!isSuppressMessage()) {
            List<Integer> inServiceExpectedLcrInventoryIds = drReconciliationDao.getInServiceExpectedLcrs();
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
                        break;
                    }
            }
        });
        conflictingLCR.removeAll(groupConflictingLCR);
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
                    break;
                }
            }
            if (addressUsage.contains("g")) {
                if (lmGroupAddressing.getGeo() != lcrAddress.getGeo()) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    break;
                }
            }
            if (addressUsage.contains("b")) {
                if (lmGroupAddressing.getSubstation() != lcrAddress.getSubstation()) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    break;
                }
            }
            if (addressUsage.contains("f")) {
                if ((lmGroupAddressing.getFeeder() & lcrAddress.getFeeder()) == 0) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    break;
                }
            }
            if (addressUsage.contains("z")) {
                if (lmGroupAddressing.getZip() != lcrAddress.getZip()) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    break;
                }
            }
            if (addressUsage.contains("u")) {
                // Is user same as uda?
                if (lmGroupAddressing.getUser() != lcrAddress.getUda()) {
                    incorrectAddressingLCR.add(lcrAddress.getDeviceId());
                    break;
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

    private boolean isSuppressMessage() {
        List<EnergyCompany> allEnergyCompanies = (List<EnergyCompany>) energyCompanyDao.getAllEnergyCompanies();
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
    public boolean doDrReconcillation(Instant processEndTime) {

        ScheduledFuture<?> futureSchdTwelveMin, futureSchdOneMin;
        BlockingQueue<LCRCommandHolder> queue = new ArrayBlockingQueue<>(10000);

        // Get LCR's which are expected to be out of service, we need to send OOS messages to them.
        List<Integer> sendOOS = getOutOfServiceExpectedLcrs();

        // Get LCR's which are expected to be in service, we need to send IN service message to them.
        List<Integer> sendInService = getInServiceExpectedLcrs();

        // Get LCR's with incorrect addressing, we need to send config message to them
        Set<Integer> sendAddressing = getLCRWithConflictingAddressing();

        Set<Integer> allLcrs = new HashSet<>();
        allLcrs.addAll(sendOOS);
        allLcrs.addAll(sendInService);
        allLcrs.addAll(sendAddressing);

        // Do not have any LCR's to send message return back.
        if (allLcrs.isEmpty()) {
            return false;
        }

        // This will allow us to find how many messages will be send for LCR in multiple groups
        Multimap<Integer, Integer> lcrInMultipleGroups = drReconciliationDao.getLcrEnrolledInMultipleGroup(allLcrs);

        // Find out number of RFN gateways in the system
        int noOfGateways = paoDao.getPaoCount(PaoType.getRfGatewayTypes());

        // I am not sure if we want to send any message if we do not have gateways
        if (noOfGateways == 0) {
            return false;
        }
        // Have to send 1 message per gateway every 12 c
        int noOfMessagePerMin = (noOfGateways / calculationCycleMinutes == 0) ? 1 : noOfGateways / calculationCycleMinutes;

        // This scheduler will run initially (0 minute) and then after every 12 minute.
        // It will find and add in queue the list of LCR for which message have to be send in the next 12
        // minute.
        futureSchdTwelveMin = executor.scheduleAtFixedRate(() -> {
            int noOfLCRToSendMessage = noOfGateways;

            // This query should return top LCR for which message have to be send. noOfLCRToSendMessage is the number
            // of message to send in next 12 min
            Map<Integer, Integer> sendMessageToLcr =
                drReconciliationDao.getLcrWithLatestEvent(allLcrs, noOfLCRToSendMessage);

            // Queue the messages to send
            sendMessageToLcr.entrySet().stream().forEach(lcr -> {
                try {
                    int noOfMessages = 1;
                    if (sendOOS.contains(lcr.getKey())) {
                        queue.put(new LCRCommandHolder(lcr.getValue(), LmHardwareCommandType.OUT_OF_SERVICE, noOfMessages));
                    } else if (sendInService.contains(lcr.getKey())) {
                        queue.put(new LCRCommandHolder(lcr.getValue(), LmHardwareCommandType.IN_SERVICE, noOfMessages));
                    } else if (sendAddressing.contains(lcr.getKey())) {
                        if (lcrInMultipleGroups.containsKey(lcr.getKey())) {
                            noOfMessages = lcrInMultipleGroups.get(lcr.getKey()).size();
                        }
                        queue.put(new LCRCommandHolder(lcr.getValue(), LmHardwareCommandType.CONFIG, noOfMessages));
                    }
                } catch (InterruptedException e) {
                    log.error("Scheduler for finding message to send Interrupted " + e);
                }
            });
            log.debug("Will send message to " + sendMessageToLcr.size() + " LCR in next 12 min");

        }, 0, calculationCycleMinutes, TimeUnit.MINUTES);

        // It will send only that number of messages which have to be send per minute.
        futureSchdOneMin = executor.scheduleAtFixedRate(() -> {
            int messagesSend = 0;

            while (!queue.isEmpty() && messagesSend < noOfMessagePerMin) {
                LCRCommandHolder lcrCommandHolder;
                try {
                    lcrCommandHolder = queue.take();
                    if (sendCommand(lcrCommandHolder)) {
                        Integer inventoryId = lcrCommandHolder.getInventoryId();
                        LmHardwareCommandType lmHardwareCommandType = lcrCommandHolder.getOperation();
                        if (lmHardwareCommandType == LmHardwareCommandType.OUT_OF_SERVICE) {
                            sendOOS.remove(inventoryId);
                        } else if (lmHardwareCommandType == LmHardwareCommandType.IN_SERVICE) {
                            sendInService.remove(inventoryId);
                        } else if (lmHardwareCommandType == LmHardwareCommandType.CONFIG) {
                            sendAddressing.remove(inventoryId);
                        }
                        messagesSend = messagesSend + lcrCommandHolder.getNoOfMessages();
                    }
                } catch (InterruptedException e) {
                    log.error("Scheduler for sending message Interrupted " + e);
                }
            }
            log.debug("Have send message " + messagesSend + " in this minute");
        }, perMinuteScheduling, perMinuteScheduling, TimeUnit.MINUTES);

        // This will execute until there is time to process and will exist after canceling the schedulers.
        while (Instant.now().isBefore(processEndTime)) {
            if (Instant.now().isAfter(processEndTime.minus(minimumExecutionTime))) {
                log.debug("Timeup cancelling both schedulers");
                futureSchdTwelveMin.cancel(false);
                futureSchdOneMin.cancel(false);
                break;
            }
        }
        return true;
    }

    /**
     * This method send appropriate command to the LCR
     * @return true if command completion happens successfully.
     */
    private boolean sendCommand(LCRCommandHolder lcrCommandHolder) {
        int inventoryId = lcrCommandHolder.getInventoryId();
        LiteLmHardwareBase lmhb = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);
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
                log.debug("Success - inventory id =" + inventoryId + " 'Out of Service' Command was sent");
            } else if (lmHardwareCommandType == LmHardwareCommandType.IN_SERVICE) {
                log.debug("Sending In Service message for LCR " + inventoryId);
                commandService.sendInServiceCommand(command);
                log.debug("Success - inventory id =" + inventoryId + " 'In Service' Command was sent");
            } else if (lmHardwareCommandType == LmHardwareCommandType.CONFIG) {
                log.debug("Sending Config message for LCR " + inventoryId);
                commandService.sendConfigCommand(command);
                log.debug("Success - inventory id =" + inventoryId + " 'Config' Command was sent");
            }
        } catch (CommandCompletionException e) {
            success = false;
            log.error("Failed - Unable to send command " + lmHardwareCommandType + "to inventory id=" + inventoryId, e);
        }
        return success;
    }

    /**
     * Class to hold the LCR command type to send to LCR.
     */
    private class LCRCommandHolder {
        int inventoryId;
        LmHardwareCommandType commandType;
        int noOfMessages;

        public LCRCommandHolder(int inventoryId, LmHardwareCommandType commandType, int noOfMessages) {
            this.inventoryId = inventoryId;
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
    }
}
