package com.cannontech.dr.program.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteHardwarePAObject;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.assetavailability.dao.DRGroupDeviceMappingDao;
import com.cannontech.dr.program.service.DisconnectStatusService;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class DisconnectStatusServiceImpl implements DisconnectStatusService {
    
    @Autowired private AttributeDynamicDataSource attributeDynamicDataSource;
    @Autowired private DRGroupDeviceMappingDao drGroupDeviceMappingDao;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private OptOutEventDao optOutEventDao;
    
    public Map<LiteHardwarePAObject, PointValueHolder> getDisconnectStatuses(int programId, String[] disconnectStatus, YukonUserContext userContext) {
        //get all devices for program
        List<LoadGroup> loadGroups = loadGroupDao.getByProgramId(programId);
        List<Integer> loadGroupIds = new ArrayList<>();
        loadGroups.forEach(group -> loadGroupIds.add(group.getLoadGroupId()));
        Set<Integer> optOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(loadGroupIds);

        List<LiteHardwarePAObject> devices = new ArrayList<>();
        for (LoadGroup group : loadGroups) {
            Map<Integer, SimpleDevice> inventoryPaoMap = drGroupDeviceMappingDao.getInventoryPaoMapForGrouping(group);
            for (Entry<Integer, SimpleDevice> device : inventoryPaoMap.entrySet()) {
                Integer inventoryId = device.getKey();
                LiteYukonPAObject obj = cache.getAllPaosMap().get(device.getValue().getPaoIdentifier().getPaoId());
                LiteHardwarePAObject hwObj = new LiteHardwarePAObject(obj, inventoryId);
                if (optOutInventory.contains(inventoryId)) {
                    hwObj.setOptedOut(true);
                }
                devices.add(hwObj);
            }
        }

        //get all disconnect status for devices
        List<String> statusList = Arrays.asList(Optional.ofNullable(disconnectStatus).orElse(new String[0]));
        Map<LiteHardwarePAObject, PointValueHolder> disconnectStatusMap = attributeDynamicDataSource.getFilteredPointValues(devices, BuiltInAttribute.DISCONNECT_STATUS, 
                                                                                                                            statusList, userContext);
        
        return disconnectStatusMap;
    }

}
