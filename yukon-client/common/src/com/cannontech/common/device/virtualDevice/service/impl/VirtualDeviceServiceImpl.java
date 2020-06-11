package com.cannontech.common.device.virtualDevice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.device.virtualDevice.VirtualDeviceBase;
import com.cannontech.common.device.virtualDevice.service.VirtualDeviceService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.VirtualDevice;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class VirtualDeviceServiceImpl implements VirtualDeviceService {

    @Autowired private DBPersistentDao dBPersistentDao;
    @Autowired private IDatabaseCache dbCache;

    @Override
    public VirtualDeviceBase create(VirtualDeviceBase virtualDeviceBase) {
        VirtualDevice virtualDevice = new VirtualDevice();
        virtualDeviceBase.buildDBPersistent(virtualDevice);
        dBPersistentDao.performDBChange(virtualDevice, TransactionType.INSERT);

        virtualDeviceBase.buildModel(virtualDevice);
        return virtualDeviceBase;
    }

    @Override
    public VirtualDeviceBase retrieve(int virtualDeviceId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(virtualDeviceId);
        if (pao == null) {
            throw new NotFoundException("Virtual device ID not found");
        }
        VirtualDevice virtualDevice = (VirtualDevice) dBPersistentDao.retrieveDBPersistent(pao);
        VirtualDeviceBase virtualDeviceBase = new VirtualDeviceBase();
        virtualDeviceBase.buildModel(virtualDevice);
        return virtualDeviceBase;
    }

    @Override
    public VirtualDeviceBase update(int virtualDeviceId, VirtualDeviceBase virtualDevice) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(virtualDeviceId);
        if (pao == null) {
            throw new NotFoundException("ID not found " + virtualDeviceId);
        }
        VirtualDevice virtualDeviceRecord = (VirtualDevice) dBPersistentDao.retrieveDBPersistent(pao);
        virtualDevice.buildDBPersistent(virtualDeviceRecord);
        dBPersistentDao.performDBChange(virtualDeviceRecord, TransactionType.UPDATE);
        virtualDevice.buildModel(virtualDeviceRecord);
        return virtualDevice;
    }

}
