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
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.program.model.Program;

@Repository("displayableInventoryDao")
public class DisplayableInventoryDaoImpl extends AbstractDisplayableDao implements DisplayableInventoryDao {
    private static final Comparator<DisplayableInventory> displayableInventoryComparator = createComparator();
    private LMHardwareBaseDao lmHardwareBaseDao;
    private OptOutService optOutService = null; 
    private OptOutEventDao optOutEventDao;

    @Override
    public List<DisplayableInventory> getDisplayableInventory(int customerAccountId) {

        List<Appliance> applianceList = applianceDao.getByAccountId(customerAccountId);
        List<Program> programList = programDao.getByAppliances(applianceList);
        List<InventoryBase> inventoryList = 
            inventoryBaseDao.getByAccountId(customerAccountId);

        Map<Integer, List<Appliance>> inventoryToApplianceMap = toInventoryIdMap(applianceList);
        Map<Integer, Program> programIdMap = toProgramIdMap(programList);

        final List<DisplayableInventory> displayableInventoryList = new ArrayList<DisplayableInventory>();

        for (final InventoryBase inventory : inventoryList) {
            int inventoryId = inventory.getInventoryId();
            String displayName = inventoryBaseDao.getDisplayName(inventory);
            
            LMHardwareBase hardware = lmHardwareBaseDao.getById(inventoryId);
            String serialNumber = hardware.getManufacturerSerialNumber();
            
            List<Program> programs = 
                createProgramList(inventoryId, inventoryToApplianceMap, programIdMap);

            DisplayableInventory displayableInventory = new DisplayableInventory();
            displayableInventory.setInventoryId(inventoryId);
            displayableInventory.setDisplayName(displayName);
            displayableInventory.setSerialNumber(serialNumber);
            displayableInventory.setPrograms(programs);

            OptOutCountHolder holder = 
                optOutService.getCurrentOptOutCount(inventoryId, customerAccountId);
            displayableInventory.setUsedOptOuts(holder.getUsedOptOuts());
            displayableInventory.setRemainingOptOuts(holder.getRemainingOptOuts());

            displayableInventory.setCurrentlyOptedOut(optOutEventDao.isOptedOut(inventoryId,
                                                                                customerAccountId));

            displayableInventoryList.add(displayableInventory);
        }

        Collections.sort(displayableInventoryList, displayableInventoryComparator);
        return displayableInventoryList;
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
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }

    @Autowired
    public void setOptOutService(OptOutService optOutService) {
        this.optOutService = optOutService;
    }

    @Autowired
    public void setOptOutEventDao(OptOutEventDao optOutEventDao) {
        this.optOutEventDao = optOutEventDao;
    }
}
