package com.cannontech.stars.dr.displayable.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.displayable.dao.AbstractDisplayableDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventory;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.program.model.Program;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Repository("displayableInventoryDao")
public class DisplayableInventoryDaoImpl extends AbstractDisplayableDao implements DisplayableInventoryDao {
    private static final Comparator<DisplayableInventory> displayableInventoryComparator = createComparator();
    private OptOutEventDao optOutEventDao;

    @Override
    public List<DisplayableInventory> getDisplayableInventory(int customerAccountId) {

        List<Appliance> applianceList = applianceDao.getAssignedAppliancesByAccountId(customerAccountId);
        List<Program> programList = programDao.getByAppliances(applianceList);
        List<HardwareSummary> hardwareList = 
            inventoryDao.getAllHardwareSummaryForAccount(customerAccountId);
        
        Map<Integer, List<Appliance>> inventoryToApplianceMap = toInventoryIdMap(applianceList);
        Map<Integer, Program> programIdMap = toProgramIdMap(programList);

        final List<DisplayableInventory> displayableInventoryList = new ArrayList<DisplayableInventory>();

        for (final HardwareSummary inventory : hardwareList) {
            int inventoryId = inventory.getInventoryId();
            String displayName = inventory.getDisplayName();
            String serialNumber = inventory.getSerialNumber();
            
            List<Program> programs = 
                createProgramList(inventoryId, inventoryToApplianceMap, programIdMap);

            DisplayableInventory displayableInventory = new DisplayableInventory();
            displayableInventory.setInventoryId(inventoryId);
            displayableInventory.setDisplayName(displayName);
            displayableInventory.setSerialNumber(serialNumber);
            displayableInventory.setPrograms(programs);

            displayableInventory.setCurrentlyOptedOut(optOutEventDao.isOptedOut(inventoryId,
                                                                                customerAccountId));
            displayableInventory.setCurrentlyScheduledOptOut(optOutEventDao.getScheduledOptOutEvent(inventoryId, 
                                                                                                    customerAccountId));

            displayableInventoryList.add(displayableInventory);
        }

        Collections.sort(displayableInventoryList, displayableInventoryComparator);
        return displayableInventoryList;
    }
    
    @Override
    public List<DisplayableInventory> getOptOutSupportingInventory(int accountId) {
        List<DisplayableInventory> displayableInventories = getDisplayableInventory(accountId);
        Predicate<DisplayableInventory> optOutFilter = new Predicate<DisplayableInventory> () {
            @Override
            public boolean apply(DisplayableInventory input) {
                return inventoryDao.getYukonInventory(input.getInventoryId()).getHardwareType().isSupportsOptOut();
            }
        };
        displayableInventories = Lists.newArrayList(Iterables.filter(displayableInventories, optOutFilter));
        return displayableInventories;
    }

    private List<Program> createProgramList(int inventoryId,
             Map<Integer, List<Appliance>> inventoryToApplianceMap, Map<Integer, Program> programIdMap) {

        final List<Appliance> appliancesByIdList = inventoryToApplianceMap.get(inventoryId);
        if (appliancesByIdList == null) return Collections.emptyList(); // not enrolled inventory

        final List<Program> resultList =
            new ArrayList<Program>(appliancesByIdList.size());

        for (final Appliance appliance : appliancesByIdList) {
            Program program = programIdMap.get(appliance.getProgramId());
            resultList.add(program);
        }

        return resultList;
    }

    private Map<Integer, List<Appliance>> toInventoryIdMap(List<Appliance> appliances) {
        final Map<Integer, List<Appliance>> resultMap = 
            new HashMap<Integer, List<Appliance>>(appliances.size());

        for (final Appliance appliance : appliances) {
            Integer key = appliance.getInventoryId();
            List<Appliance> value = resultMap.get(key);

            if (value == null) {
                value = new ArrayList<Appliance>();
                resultMap.put(key, value);
            }

            value.add(appliance);
        }

        return resultMap;
    }

    private Map<Integer, Program> toProgramIdMap(List<Program> programs) {
        final Map<Integer, Program> resultMap = 
            new HashMap<Integer, Program>(programs.size());

        for (final Program value : programs) {
            Integer key = value.getProgramId();
            resultMap.put(key, value);
        }

        return resultMap;
    }

    private static Comparator<DisplayableInventory> createComparator() {
        return new Comparator<DisplayableInventory>() {
            @Override
            public int compare(DisplayableInventory o1, DisplayableInventory o2) {
                List<Program> programList1 = o1.getPrograms();
                List<Program> programList2 = o2.getPrograms();
                
                int size1 = programList1.size();
                int size2 = programList2.size();
                
                if (size1 > size2) {
                    return -1;
                } else if (size1 < size2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }
    
    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
        this.optOutEventDao = optOutEventDao;
    }
}
