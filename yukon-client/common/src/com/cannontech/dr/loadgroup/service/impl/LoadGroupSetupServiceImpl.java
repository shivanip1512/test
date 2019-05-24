package com.cannontech.dr.loadgroup.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupMeterDisconnect;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.Device;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;
import com.cannontech.yukon.IDatabaseCache;

public class LoadGroupSetupServiceImpl implements LoadGroupSetupService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    
    @Override
    public int save(LoadGroupBase loadGroup) {
        LMGroup loadGroupPersistence = buildLMDbPersistent(loadGroup);

        if (loadGroup.getId() == null) {
            dbPersistentDao.performDBChange(loadGroupPersistence, TransactionType.INSERT);
        } else {
            dbPersistentDao.performDBChange(loadGroupPersistence, TransactionType.UPDATE);
        }
        return loadGroupPersistence.getPAObjectID();
    }
    
    @Override
    public LoadGroupBase retrieve(int loadGroupId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(loadGroupId);
        LMGroup loadGroup = (LMGroup) dbPersistentDao.retrieveDBPersistent(pao);
        LoadGroupBase loadGroupBase = LoadGroupBase.of(loadGroup);
        return loadGroupBase;
    }
    
    private LMGroup buildLMDbPersistent(LoadGroupBase loadGroup) {
        LMGroup lmGroupMeterDisconnect = (LMGroup) LMFactory.createLoadManagement(loadGroup.getType());
        if (loadGroup.getId() != null) {
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(loadGroup.getId());
            lmGroupMeterDisconnect = (LMGroupMeterDisconnect) dbPersistentDao.retrieveDBPersistent(pao);
        }
        
        // PAO settings
        lmGroupMeterDisconnect.setPAOName(loadGroup.getName());
        lmGroupMeterDisconnect.setDisabled(loadGroup.isDisableGroup());

        // Device settings
        Device lmDevice = DeviceFactory.createDevice(loadGroup.getType()).getDevice();
        char disableControl = loadGroup.isDisableControl() ? 'Y' : 'N';
        lmDevice.setControlInhibit(disableControl);
        lmDevice.setDeviceID(loadGroup.getId());
        lmGroupMeterDisconnect.setDevice(lmDevice);
        
        // Load Group settings
        com.cannontech.database.db.device.lm.LMGroup lmGroup = lmGroupMeterDisconnect.getLmGroup();
        lmGroup.setKwCapacity(loadGroup.getkWCapacity());
        lmGroupMeterDisconnect.setLmGroup(lmGroup);
       
        return lmGroupMeterDisconnect;
    }

}
