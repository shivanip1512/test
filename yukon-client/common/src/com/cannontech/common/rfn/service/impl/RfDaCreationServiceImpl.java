package com.cannontech.common.rfn.service.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.events.loggers.RfnDeviceEventLogService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.Rfn1200Detail;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfDaCreationService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.Rfn1200;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.port.PortTiming;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;

public class RfDaCreationServiceImpl implements RfDaCreationService {
    
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private RfnDeviceEventLogService rfnDeviceEventLogService;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DBPersistentDao dbPersistentDao;

    private final AtomicInteger newDeviceCreated = new AtomicInteger();

    @Override
    @Transactional
    public RfnDevice create(final RfnIdentifier rfnIdentifier) {
        String deviceName = rfnIdentifier.getCombinedIdentifier();
        
        YukonDevice newDevice = deviceCreationService.createRfnDeviceByDeviceType(PaoType.RFN_1200, deviceName, 
            rfnIdentifier, true);
        RfnDevice device = new RfnDevice(deviceName, newDevice.getPaoIdentifier(), rfnIdentifier);
        
        //clean up event log
        rfnDeviceEventLogService.createdNewDeviceAutomatically(device.getRfnIdentifier(), "N/A", device.getName());

        dbChangeManager.processPaoDbChange(newDevice, DbChangeType.ADD);
        return device;
    }
    
    @Override
    @Transactional
    public Rfn1200Detail create(Rfn1200Detail detail) {
        RfnIdentifier rfnId = new RfnIdentifier(detail.getRfnAddress().getSerialNumber(), detail.getRfnAddress().getManufacturer(), detail.getRfnAddress().getModel());
        YukonDevice newDevice = deviceCreationService.createRfnDeviceByDeviceType(detail.getPaoType(), detail.getName(), rfnId,
            true);
        
        dbChangeManager.processPaoDbChange(newDevice, DbChangeType.ADD);
        
        PortTiming pt = (PortTiming) dbPersistentDao.retrieveDBPersistent(new PortTiming(newDevice.getPaoIdentifier().getPaoId()));
        pt.setPostCommWait(detail.getPostCommWait());
        dbPersistentDao.performDBChangeWithNoMsg(pt, TransactionType.UPDATE);
        
        // add event log
        
        return retrieve(newDevice.getPaoIdentifier().getPaoId());
    }
    
    @Override
    @Transactional
    public Rfn1200Detail update(Rfn1200Detail detail) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(detail.getId());
        Rfn1200 rfn1200 = (Rfn1200) dbPersistentDao.retrieveDBPersistent(pao);
        rfn1200.setPAOName(detail.getName());
        detail.getRfnAddress().setDeviceID(rfn1200.getPAObjectID());
        rfn1200.setRfnAddress(detail.getRfnAddress());
        rfn1200.getTiming().setPostCommWait(detail.getPostCommWait());
        rfn1200.setDisableFlag(BooleanUtils.isFalse(detail.getEnabled()) ? 'Y' : 'N');
        dbPersistentDao.performDBChange(rfn1200, TransactionType.UPDATE);
        
        // add event log
        
        return retrieve(rfn1200.getPAObjectID());
    }
    
    @Override
    @Transactional
    public Rfn1200Detail retrieve(int id) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao == null) {
            throw new NotFoundException("Device Id not found");
        }

        Rfn1200 rfn1200 = (Rfn1200) dbPersistentDao.retrieveDBPersistent(pao);
        return getDetail(rfn1200);
    }

    private Rfn1200Detail getDetail(Rfn1200 rfn1200) {
        Rfn1200Detail detail = new Rfn1200Detail();
        detail.setName(rfn1200.getPAOName());
        detail.setPaoType(rfn1200.getPaoType());
        detail.setPostCommWait(rfn1200.getTiming().getPostCommWait());
        detail.setRfnAddress(rfn1200.getRfnAddress());
        detail.setId(rfn1200.getDevice().getDeviceID());
        detail.setEnabled(rfn1200.getPAODisableFlag() == 'N' ? true : false);
        return detail;
    }
    
    @Override
    public void incrementNewDeviceCreated() {
        newDeviceCreated.incrementAndGet();
    }
    
    @Override
    @ManagedAttribute
    public int getNewDeviceCreated() {
        return newDeviceCreated.get();
    }
    
}