package com.cannontech.common.bulk.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.service.BulkMeterDeleterService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;

public class BulkMeterDeleterServiceImpl implements BulkMeterDeleterService {

    private String errors = "";
    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    private DBPersistentDao dbPersistentDao = null;

    public List<LiteYukonPAObject> showPAObjectsAddress(int address) {
        List<LiteYukonPAObject> liteYukonPAObjects = new ArrayList<LiteYukonPAObject>();
        liteYukonPAObjects.addAll(paoDao.getLiteYukonPaobjectsByAddress(address));

        if (liteYukonPAObjects.isEmpty()) {
            errors += "  Address does not exist -- " + address + "\n";
        }

        return liteYukonPAObjects;
    }

    public List<LiteYukonPAObject> showPAObjectsAddress(int minRange,int maxRange) {
        List<LiteYukonPAObject> liteYukonPAObjects = new ArrayList<LiteYukonPAObject>();

        for (int address = minRange; address <= maxRange; address++) {

            if (paoDao.getLiteYukonPaobjectsByAddress(address).isEmpty()) {
                errors += "  Meter does not exist -- " + address + "\n";
            } else {
                liteYukonPAObjects.addAll(paoDao.getLiteYukonPaobjectsByAddress(address));
            }
        }

        return liteYukonPAObjects;
    }

    public List<LiteYukonPAObject> showPAObjectsMeterNumber(String meterNumber) {
        List<LiteYukonPAObject> liteYukonObjects = deviceDao.getLiteYukonPaobjectListByMeterNumber(meterNumber);

        if (liteYukonObjects.isEmpty()) {
            errors += "  Meter does not exist -- " + meterNumber + "\n";
        }

        return liteYukonObjects;
    }

    /*
     * public List<LiteYukonPAObject> showPAObjectsMeterNumber(int minRange,
     * int maxRange) { }
     */
    public List<LiteYukonPAObject> showPAObjectsPaoName(String paoName) {
        List<LiteYukonPAObject> liteYukonObjects = paoDao.getLiteYukonPaoByName(paoName,
                                                                                false);

        if (liteYukonObjects.isEmpty()) {
            errors += "  Name does not exist -- " + paoName + "\n";
        }

        return liteYukonObjects;
    }
    
    public void remove(List<LiteYukonPAObject> liteYukonObjects) {
        if (!(errors.length() > 0)) {

            List<DBPersistent> liteYukonPersistentItems = new ArrayList<DBPersistent>();
            for (LiteYukonPAObject liteYukonObject : liteYukonObjects) {
                DBPersistent liteYukonPersistentItem = dbPersistentDao.retrieveDBPersistent(liteYukonObject);
                liteYukonPersistentItems.add(liteYukonPersistentItem);
            }
            dbPersistentDao.performDBChangeBulk(liteYukonPersistentItems,
                                                Transaction.DELETE);
        }
    }

    /**
     * This method returns any errors that may have happened during removal
     * @return
     */
    public String getErrors() {
        return errors;
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