package com.cannontech.common.rfn.service.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.creation.BadTemplateDeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.events.loggers.RfnDeviceEventLogService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.rfn.endpoint.IgnoredTemplateException;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.Rfn1200Detail;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfDaCreationService;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.Rfn1200;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
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
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    
    private static final Logger log = YukonLogManager.getLogger(RfDaCreationServiceImpl.class);

    @Override
    @Transactional
    public synchronized RfnDevice create(RfnIdentifier identifier) {
        try {
            String deviceName = identifier.getCombinedIdentifier();

            YukonDevice newDevice = deviceCreationService.createRfnDeviceByDeviceType(PaoType.RFN_1200, deviceName,
                    identifier, true);
            RfnDevice device = new RfnDevice(deviceName, newDevice.getPaoIdentifier(), identifier);

            rfnDeviceEventLogService.createdNewDeviceAutomatically(device.getRfnIdentifier(), "N/A", device.getName());
            
            dbChangeManager.processPaoDbChange(newDevice, DbChangeType.ADD);
            return device;
        } catch (IgnoredTemplateException e) {
            throw createRuntimeException("Unable to create device for " + identifier + " because template is ignored", e);
        } catch (BadTemplateDeviceCreationException e) {
            throw createRuntimeException(
                    "Creation failed for " + identifier + ". Manufacturer, Model and Serial Number combination do "
                            + "not match any templates.",
                    e);
        } catch (DeviceCreationException e) {
            log.warn("Creation failed for " + identifier + ", checking cache for any new entries.");
            // Try another lookup in case someone else beat us to it
            try {
                return rfnDeviceLookupService.getDevice(identifier);
            } catch (NotFoundException e1) {
                throw createRuntimeException("Creation failed for " + identifier, e);
            }
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                // Only log full exception when trace is on so lots of failed creations don't kill performance.
                throw createRuntimeException("Creation failed for " + identifier, e);
            } else {
                throw createRuntimeException("Creation failed for " + identifier + " : " + e);
            }
        }
    }
    
    @Override
    @Transactional
    public synchronized Rfn1200Detail create(Rfn1200Detail detail, LiteYukonUser user) {
        RfnIdentifier rfnId = new RfnIdentifier(detail.getRfnAddress().getSerialNumber(),
                detail.getRfnAddress().getManufacturer(), detail.getRfnAddress().getModel());
        try {
            YukonDevice newDevice = deviceCreationService.createRfnDeviceByDeviceType(detail.getPaoType(), detail.getName(),
                    rfnId, true);

            dbChangeManager.processPaoDbChange(newDevice, DbChangeType.ADD);

            PortTiming pt = (PortTiming) dbPersistentDao
                    .retrieveDBPersistent(new PortTiming(newDevice.getPaoIdentifier().getPaoId()));
            pt.setPostCommWait(detail.getPostCommWait());
            dbPersistentDao.performDBChangeWithNoMsg(pt, TransactionType.UPDATE);

            rfnDeviceEventLogService.rfn1200Created(rfnId, detail.getName(), user.getUsername());

            detail.setId(newDevice.getPaoIdentifier().getPaoId());
            return detail;
        } catch (Exception e) {
            throw createRuntimeException("Creation failed for " + rfnId, e);
        }
    }
    
    @Override
    @Transactional
    public Rfn1200Detail update(Rfn1200Detail detail, LiteYukonUser user) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(detail.getId());
        Rfn1200 updatedRfn1200 = (Rfn1200) dbPersistentDao.retrieveDBPersistent(pao);
        Rfn1200 originalRfn1200 = (Rfn1200) dbPersistentDao.retrieveDBPersistent(pao);
        updatedRfn1200.setPAOName(detail.getName());
        detail.getRfnAddress().setDeviceID(updatedRfn1200.getPAObjectID());
        updatedRfn1200.setRfnAddress(detail.getRfnAddress());
        updatedRfn1200.getTiming().setPostCommWait(detail.getPostCommWait());
        updatedRfn1200.setDisableFlag(BooleanUtils.isFalse(detail.getEnabled()) ? 'Y' : 'N');
        dbPersistentDao.performDBChange(updatedRfn1200, TransactionType.UPDATE);
        
        rfnDeviceEventLogService.rfn1200Updated(originalRfn1200, updatedRfn1200, user.getUsername());

        return retrieve(updatedRfn1200.getPAObjectID());
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
    
    private RuntimeException createRuntimeException(String error) {
        log.warn(error);
        return new RuntimeException(error);
    }
    
    private RuntimeException createRuntimeException(String error, Exception e) {
        log.warn(error, e);
        return new RuntimeException(error, e);
    }
}