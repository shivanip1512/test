package com.cannontech.amr.meter.service.impl;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.service.MeterService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.DBPersistent;

public class MeterServiceImpl implements MeterService {
    
    private DBPersistentDao dbPersistentDao = null;
    private AttributeService attributeService = null;

    public void addDisconnectAddress(SimpleDevice device, int disconnectAddress) throws IllegalArgumentException, TransactionException, IllegalUseOfAttribute {
        
        // must be 410 type to set disconnect
        if (!DeviceTypesFuncs.isMCT410(device.getType())) {
            throw new IllegalArgumentException("Device type does not accept disconnect addresses.");
        }
        
        // get heavy meter
        MCT400SeriesBase mct400 = new MCT400SeriesBase();
        mct400.setDeviceID(device.getDeviceId());
        mct400 = (MCT400SeriesBase)Transaction.createTransaction(Transaction.RETRIEVE, mct400).execute();

        // set disconnect address and update
        mct400.getDeviceMCT400Series().setDisconnectAddress(disconnectAddress);
        dbPersistentDao.performDBChange(mct400, Transaction.UPDATE);
        
        // add disconnect point if needed
        BuiltInAttribute disconnectAttr = BuiltInAttribute.DISCONNECT_STATUS;
        if (!attributeService.pointExistsForAttribute(device, disconnectAttr)) {
            attributeService.createPointForAttribute(device, disconnectAttr);
        }
    }
    
    public void removeDisconnectAddress(SimpleDevice device) throws IllegalArgumentException, TransactionException, IllegalUseOfAttribute {
        
        // must be 410 type to remove disconnect
        if (!DeviceTypesFuncs.isMCT410(device.getType())) {
            throw new IllegalArgumentException("Device type does not accept disconnect addresses.");
        }
        
        // get heavy meter
        MCT400SeriesBase mct400 = new MCT400SeriesBase();
        mct400.setDeviceID(device.getDeviceId());
        mct400 = (MCT400SeriesBase)Transaction.createTransaction(Transaction.RETRIEVE, mct400).execute();
    
        // set to remove disconnect and update
        mct400.deleteAnAddress();
        dbPersistentDao.performDBChange(mct400, Transaction.UPDATE);
        
    
        // remove disconnect point if exists
        if (attributeService.pointExistsForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS)) {
            
            LitePoint liteDisconnectPoint = attributeService.getPointForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS);
            DBPersistent dbDisconnectPoint = dbPersistentDao.retrieveDBPersistent(liteDisconnectPoint);
            dbPersistentDao.performDBChange(dbDisconnectPoint, Transaction.DELETE);
        }
    }
    
    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
