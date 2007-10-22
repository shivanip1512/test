package com.cannontech.common.bulk.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.BulkDataContainer;
import com.cannontech.common.bulk.service.BulkMeterDeleterService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;

public class BulkMeterDeleterServiceImpl implements BulkMeterDeleterService {

    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    private DBPersistentDao dbPersistentDao = null;

    public BulkDataContainer getPAObjectsByAddress(int address, BulkDataContainer bulkDataContainer) {
        List<LiteYukonPAObject> liteYukonPAObjects = bulkDataContainer.getYukonPAObjects();
        List<LiteYukonPAObject> temp = paoDao.getLiteYukonPaobjectsByAddress(address);
        boolean noYukonPAObjects = temp.isEmpty();
        liteYukonPAObjects.addAll(temp);
        bulkDataContainer.setYukonPAObjects(liteYukonPAObjects);

        if (noYukonPAObjects) {
            addError("address", address, bulkDataContainer);
        }
        

        return bulkDataContainer;
    }

    public BulkDataContainer getPAObjectsByAddress(int minRange,int maxRange, BulkDataContainer bulkDataContainer) {
        List<LiteYukonPAObject> liteYukonPAObjects = bulkDataContainer.getYukonPAObjects();
        for (int address = minRange; address <= maxRange; address++) {

            List<LiteYukonPAObject> temp = paoDao.getLiteYukonPaobjectsByAddress(address);
            boolean noYukonPAObjects = temp.isEmpty();
            
            if (noYukonPAObjects) {
                addError("address", address, bulkDataContainer);
            } else {
                liteYukonPAObjects.addAll(temp);
            }
        }
        bulkDataContainer.setYukonPAObjects(liteYukonPAObjects);

        return bulkDataContainer;
    }

    public BulkDataContainer getPAObjectsByMeterNumber(String meterNumber, BulkDataContainer bulkDataContainer) {
        List<LiteYukonPAObject> liteYukonPAObjects = bulkDataContainer.getYukonPAObjects();
        List<LiteYukonPAObject> temp = deviceDao.getLiteYukonPaobjectListByMeterNumber(meterNumber);
        boolean noYukonPAObjects = temp.isEmpty();
        liteYukonPAObjects.addAll(temp);
        bulkDataContainer.setYukonPAObjects(liteYukonPAObjects);

        if (noYukonPAObjects) {
            addError("meterNumber", meterNumber, bulkDataContainer);
        }

        return bulkDataContainer;
    }

    /*
     * public List<LiteYukonPAObject> showPAObjectsMeterNumber(int minRange,
     * int maxRange) { }
     */
    public BulkDataContainer getPAObjectsByPaoName(String paoName, BulkDataContainer bulkDataContainer) {
        List<LiteYukonPAObject> liteYukonPAObjects = bulkDataContainer.getYukonPAObjects();
        List<LiteYukonPAObject> temp = paoDao.getLiteYukonPaoByName(paoName, false);
        boolean noYukonPAObjects = temp.isEmpty();
        liteYukonPAObjects.addAll(temp);
        bulkDataContainer.setYukonPAObjects(liteYukonPAObjects);

        if (noYukonPAObjects) {
            addError("paoName", paoName, bulkDataContainer);
        }

        return bulkDataContainer;
    }
    
    public void remove(BulkDataContainer bulkDataContainer) {
        if (bulkDataContainer.getFails().isEmpty()) {

            List<LiteYukonPAObject> liteYukonObjects = bulkDataContainer.getYukonPAObjects();
            List<DBPersistent> liteYukonPersistentItems = new ArrayList<DBPersistent>();
            for (LiteYukonPAObject liteYukonObject : liteYukonObjects) {
                DBPersistent liteYukonPersistentItem = dbPersistentDao.retrieveDBPersistent(liteYukonObject);
                liteYukonPersistentItems.add(liteYukonPersistentItem);
            }
            dbPersistentDao.performDBChangeWithNoMsg(liteYukonPersistentItems,
                                                     Transaction.DELETE);
        }
    }

    private void addError(String key, Object obj, BulkDataContainer bulkDataContainer){
        Map<String, List<String>> fails = bulkDataContainer.getFails();
        List<String> failsList = null;
        if(fails.containsKey(key)){
            failsList = fails.get(key);
            
        }else{
            failsList = new ArrayList<String>(); 
        }
                
        failsList.add(obj.toString());
        fails.put(key, failsList);
        bulkDataContainer.setFails(fails);
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
}