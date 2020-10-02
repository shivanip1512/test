package com.cannontech.common.device.virtualDevice.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.model.PaoModelFactory;
import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.device.virtualDevice.service.VirtualDeviceService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PaginatedResponse;
import com.cannontech.common.pao.LiteYukonPaoSortableField;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.VirtualBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class VirtualDeviceServiceImpl implements VirtualDeviceService {

    @Autowired private DBPersistentDao dBPersistentDao;
    @Autowired private IDatabaseCache dbCache;

    @Override
    public VirtualDeviceBaseModel<? extends VirtualBase> create(VirtualDeviceBaseModel virtualDeviceBase) {
        VirtualBase virtualDevice = (VirtualBase) DeviceFactory.createDevice(virtualDeviceBase.getType());
        virtualDeviceBase.buildDBPersistent(virtualDevice);
        dBPersistentDao.performDBChange(virtualDevice, TransactionType.INSERT);
        virtualDeviceBase.buildModel(virtualDevice);
        return virtualDeviceBase;
    }

    @Override
    public VirtualDeviceBaseModel<? extends VirtualBase> retrieve(int virtualDeviceId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(virtualDeviceId);
        if (pao == null) {
            throw new NotFoundException("Virtual device ID not found");
        }
        VirtualBase virtualDevice = (VirtualBase) dBPersistentDao.retrieveDBPersistent(pao);
        VirtualDeviceBaseModel virtualDeviceBase = (VirtualDeviceBaseModel) PaoModelFactory.getModel(virtualDevice.getPaoType());
        virtualDeviceBase.buildModel(virtualDevice);
        return virtualDeviceBase;
    }

    @Override
    public VirtualDeviceBaseModel<? extends VirtualBase> update(int virtualDeviceId, VirtualDeviceBaseModel virtualDevice) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(virtualDeviceId);
        if (pao == null) {
            throw new NotFoundException("ID not found " + virtualDeviceId);
        }
        VirtualBase virtualDeviceRecord = (VirtualBase) dBPersistentDao.retrieveDBPersistent(pao);
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
        VirtualBase virtualDeviceRecord = (VirtualBase) dBPersistentDao.retrieveDBPersistent(pao);
        dBPersistentDao.performDBChange(virtualDeviceRecord, TransactionType.DELETE);
        return virtualDeviceRecord.getPAObjectID();
    }

    @Override
    public PaginatedResponse<DeviceBaseModel> getPage(LiteYukonPaoSortableField sortBy, Direction direction, Integer page, Integer itemsPerPage) {
        Comparator<LiteYukonPAObject> comparator = (direction == Direction.desc ? sortBy.getComparator().reversed() : sortBy.getComparator());
        if (sortBy != LiteYukonPaoSortableField.PAO_NAME) {
            comparator = comparator.thenComparing(LiteYukonPaoSortableField.PAO_NAME.getComparator());
        }
        
        List<DeviceBaseModel> virtualModels = dbCache.getAllYukonPAObjects()
                .stream()
                .filter(pao -> pao.getPaoType() == PaoType.VIRTUAL_SYSTEM || pao.getPaoType() == PaoType.VIRTUAL_METER)
                .sorted(comparator)
                .map(pao -> ((VirtualDeviceBaseModel) PaoModelFactory.getModel(pao.getPaoType())).of(pao))
                .collect(Collectors.toList());

        return new PaginatedResponse<DeviceBaseModel>(virtualModels, page, itemsPerPage);
    }

}
