package com.cannontech.stars.dr.settlement.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.dr.settlement.dao.SettlementConfigDao;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.SettlementConfigFuncs;

public class SettlementConfigDaoImpl implements SettlementConfigDao {

    private DBPersistentDao dbPersistentDao;
    
    @Override
    public void save(LiteSettlementConfig liteSettlementConfig, int settlementYukonDefId, int settlementEntryId, int availRateListEntryId) {
        if (liteSettlementConfig.getConfigID() >= 0) {
            SettlementConfigFuncs.updateSettlementConfigTrx(liteSettlementConfig, liteSettlementConfig.getFieldValue());
        } else {
            SettlementConfig newSC = new SettlementConfig();
            newSC.setConfigID(SettlementConfig.getNextConfigID());
            newSC.setFieldName(liteSettlementConfig.getFieldName());
            newSC.setFieldValue(liteSettlementConfig.getFieldValue());
            newSC.setCtiSettlement(SettlementConfigFuncs.getCTISettlementStr(settlementYukonDefId));
            newSC.setYukonDefID(settlementYukonDefId);
            newSC.setDescription(liteSettlementConfig.getDescription());
            newSC.setEntryID(settlementEntryId);
            newSC.setRefEntryID(availRateListEntryId);

            dbPersistentDao.performDBChange(newSC, TransactionType.INSERT);
            DBChangeMsg msg = new DBChangeMsg(newSC.getConfigID().intValue(), DBChangeMsg.CHANGE_SETTLEMENT_DB,
                                              DBChangeMsg.CAT_SETTLEMENT, DBChangeMsg.CAT_SETTLEMENT, DbChangeType.ADD);
            ServerUtils.handleDBChangeMsg(msg);
        }
    }
    
    @Override
    public void delete(LiteSettlementConfig liteSettlementConfig) {
        CTILogger.info("DELETEING RATE: " + liteSettlementConfig.getConfigID());
        SettlementConfig delSC = (SettlementConfig)LiteFactory.createDBPersistent(liteSettlementConfig);
        dbPersistentDao.performDBChange(delSC, TransactionType.DELETE);
        
        DBChangeMsg dbChangeMsg = 
            new DBChangeMsg(liteSettlementConfig.getConfigID(), DBChangeMsg.CHANGE_SETTLEMENT_DB, DBChangeMsg.CAT_SETTLEMENT,
                            DBChangeMsg.CAT_SETTLEMENT, DbChangeType.DELETE);
        ServerUtils.handleDBChangeMsg(dbChangeMsg);
    }
    
    // DI Setters
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
}