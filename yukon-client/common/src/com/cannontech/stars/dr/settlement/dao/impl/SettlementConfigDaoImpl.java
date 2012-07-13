package com.cannontech.stars.dr.settlement.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.stars.dr.settlement.dao.SettlementConfigDao;
import com.cannontech.stars.util.SettlementConfigFuncs;

public class SettlementConfigDaoImpl implements SettlementConfigDao {

    private DBPersistentDao dbPersistentDao;
    private Logger log = YukonLogManager.getLogger(SettlementConfigDaoImpl.class);
    
    @Override
    public void save(LiteSettlementConfig liteSettlementConfig, int availRateListEntryId) {
        if (liteSettlementConfig.getConfigID() >= 0) {
            SettlementConfigFuncs.updateSettlementConfigTrx(liteSettlementConfig, liteSettlementConfig.getFieldValue());
        } else {
            SettlementConfig newSC = new SettlementConfig();
            newSC.setConfigID(SettlementConfig.getNextConfigID());
            newSC.setFieldName(liteSettlementConfig.getFieldName());
            newSC.setFieldValue(liteSettlementConfig.getFieldValue());
            newSC.setDescription(liteSettlementConfig.getDescription());
            newSC.setRefEntryID(availRateListEntryId);

            dbPersistentDao.performDBChange(newSC, TransactionType.INSERT);
        }
    }
    
    @Override
    public void delete(LiteSettlementConfig liteSettlementConfig) {
        log.info("DELETEING RATE: " + liteSettlementConfig.getConfigID());
        SettlementConfig delSC = (SettlementConfig)LiteFactory.createDBPersistent(liteSettlementConfig);
        dbPersistentDao.performDBChange(delSC, TransactionType.DELETE);
    }
    
    // DI Setters
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
}