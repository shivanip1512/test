package com.cannontech.common.device.virtualDevice.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;
import com.cannontech.common.device.virtualDevice.service.VirtualDeviceService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PaginatedResponse;
import com.cannontech.common.pao.LiteYukonPaoSortableField;
import com.cannontech.common.pao.PaoType;
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
    public VirtualDeviceModel create(VirtualDeviceModel virtualDeviceBase) {
        VirtualDevice virtualDevice = new VirtualDevice();
        virtualDeviceBase.buildDBPersistent(virtualDevice);
        dBPersistentDao.performDBChange(virtualDevice, TransactionType.INSERT);

        virtualDeviceBase.buildModel(virtualDevice);
        return virtualDeviceBase;
    }

    @Override
    public VirtualDeviceModel retrieve(int virtualDeviceId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(virtualDeviceId);
        if (pao == null) {
            throw new NotFoundException("Virtual device ID not found");
        }
        VirtualDevice virtualDevice = (VirtualDevice) dBPersistentDao.retrieveDBPersistent(pao);
        VirtualDeviceModel virtualDeviceBase = new VirtualDeviceModel();
        virtualDeviceBase.buildModel(virtualDevice);
        return virtualDeviceBase;
    }

    @Override
    public VirtualDeviceModel update(int virtualDeviceId, VirtualDeviceModel virtualDevice) {
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

    @Override
    public int delete(int id) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao == null) {
            throw new NotFoundException("ID not found " + id);
        }
        VirtualDevice virtualDeviceRecord = (VirtualDevice) dBPersistentDao.retrieveDBPersistent(pao);
        dBPersistentDao.performDBChange(virtualDeviceRecord, TransactionType.DELETE);
        return virtualDeviceRecord.getPAObjectID();
    }

    @Override
    public PaginatedResponse<DeviceBaseModel> getPage(LiteYukonPaoSortableField sortBy, Direction direction, Integer page, Integer itemsPerPage) {
        Comparator<LiteYukonPAObject> comparator = (direction == Direction.desc ? sortBy.getComparator().reversed() : sortBy.getComparator());
        if (sortBy != LiteYukonPaoSortableField.PAO_NAME) {
            comparator = comparator.thenComparing(LiteYukonPaoSortableField.PAO_NAME.getComparator());
        }

        List<DeviceBaseModel> deviceModels = dbCache.getAllYukonPAObjects()
                .stream()
                .filter(pao -> pao.getPaoType() == PaoType.VIRTUAL_SYSTEM)
                .sorted(comparator).map(pao -> DeviceBaseModel.of(pao))
                .collect(Collectors.toList());

        return new PaginatedResponse<DeviceBaseModel>(deviceModels, page, itemsPerPage);
    }

}
