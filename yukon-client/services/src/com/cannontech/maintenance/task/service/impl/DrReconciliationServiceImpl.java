package com.cannontech.maintenance.task.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.impl.LMGroupDaoImpl;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.cannontech.dr.dao.impl.ExpressComReportedAddressDaoImpl;
import com.cannontech.maintenance.task.dao.DrReconciliationDao;
import com.cannontech.maintenance.task.service.DrReconciliationService;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.hardware.model.ExpressComAddressView;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.google.common.collect.Multimap;

public class DrReconciliationServiceImpl implements DrReconciliationService {
    @Autowired private DrReconciliationDao drReconciliationDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private ExpressComReportedAddressDaoImpl expressComDaoImpl;
    @Autowired private LMGroupDaoImpl lmGroupDaoImpl;

    @Override
    public List<Integer> getOutOfServiceExpectedLcrs() {
        if (!isSuppressMessage()) {
            List<Integer> oosExpectedLcrInventoryIds = drReconciliationDao.getOutOfServiceExpectedLcrs();
            return oosExpectedLcrInventoryIds;
        }
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getInServiceExpectedLcrs() {
        if (!isSuppressMessage()) {
            List<Integer> inServiceExpectedLcrInventoryIds = drReconciliationDao.getInServiceExpectedLcrs();
            return inServiceExpectedLcrInventoryIds;
        }
        return Collections.emptyList();
    }
    
    @Override
    public List<Integer> getLCRWithConflictingAddressing() {

        List<Integer> conflictingLCR = new ArrayList<>();

        // Get Group which have atleast one LCR enrolled
        List<Integer> groupsWithRfnLcrEnrollements = drReconciliationDao.getGroupsWithRfnDeviceEnrolled();

        for (Integer groupId : groupsWithRfnLcrEnrollements) {
            // Get Group addressing
            ExpressComAddressView lmGroupAddressing = lmGroupDaoImpl.getExpressComAddressing(groupId);

            // Get LCR in each group
            List<Integer> lcrsInGroup = drReconciliationDao.getEnrolledRfnLcrForGroup(groupId);

            // Get LCR addressing - This can be heavy
            List<ExpressComReportedAddress> lcrAddressing = expressComDaoImpl.getCurrentAddresses(lcrsInGroup);

            // Check conflicts
            conflictingLCR = findConflicts(lmGroupAddressing, lcrAddressing);
        }
        // Get LCR enrolled in multiple group
        Multimap<Integer, Integer> lcrInMultipleGroups =
                drReconciliationDao.getLcrEnrolledInMultipleGroup(conflictingLCR);
        
        final List<Integer> groupConflictingLCR = new ArrayList<>();
        
        lcrInMultipleGroups.keySet().forEach(lcr -> {
            ArrayList<Integer> groups =   new ArrayList<Integer>(lcrInMultipleGroups.get(lcr));
            List<Integer> tempAddress = Arrays.asList(0, 0, 0, 0, 0);

            groups.forEach(group -> {
                if (!groupConflictingLCR.contains(lcr)) {
                    ExpressComAddressView lmGroupAddressing = lmGroupDaoImpl.getExpressComAddressing(group);
                    List<Integer> groupAddress = parseAddress(lmGroupAddressing);

                    for (int position = 0; position < tempAddress.size(); position++) {
                        if (groupAddress.get(position) != 0) {
                            if (tempAddress.get(position) != 0) {
                                if (groupAddress.get(position) != tempAddress.get(position)) {
                                    // mismatch, we can not fix it dont send message
                                    groupConflictingLCR.add(lcr);
                                    break;
                                }
                            } else {
                                tempAddress.set(position, groupAddress.get(position));
                            }
                        }
                    }
                }
            });
        });
        conflictingLCR.removeAll(groupConflictingLCR);
        return conflictingLCR;
    }

    
    private List<Integer> parseAddress(ExpressComAddressView lmGroupAddressing) {
        List<Integer> address = new ArrayList<>();
        address.add(lmGroupAddressing.getSpid());
        address.add(lmGroupAddressing.getGeo());
        address.add(lmGroupAddressing.getSubstation());
        address.add(lmGroupAddressing.getZip());
        address.add(lmGroupAddressing.getUser());
        return address;
    }

    private ArrayList<Integer> findConflicts(ExpressComAddressView lmGroupAddressing,
            List<ExpressComReportedAddress> lcrAddressing) {

        String addressUsage = lmGroupAddressing.getUsage().toLowerCase();
        ArrayList<Integer> incorrectAddressingLCR = new ArrayList<>();

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
                if ((lmGroupAddressing.getFeeder() & lcrAddress.getFeeder()) != lmGroupAddressing.getFeeder()) {
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

}
