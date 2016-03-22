package com.cannontech.web.capcontrol.service.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.CapBankCommunicationMedium;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.PoolManager;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.web.capcontrol.service.CapBankService;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class CapBankServiceImpl implements CapBankService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;


    @Override
    public CapBank getCapBank(int id) {

        CapBank capbank = new CapBank();

        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        DBPersistent dbPersistent = dbPersistentDao.retrieveDBPersistent(pao);
        DeviceBase deviceBase = (DeviceBase) dbPersistent;
        if(deviceBase  instanceof CapBank) {
            capbank = (CapBank) deviceBase;
        }

        CapBankAdditional additionalInfo = new CapBankAdditional();
        Connection connection = PoolManager.getInstance()
                                           .getConnection(CtiUtilities.getDatabaseAlias());
        additionalInfo.setDbConnection(connection);
        additionalInfo.setDeviceID(id);
        try {
            additionalInfo.retrieve();
        } catch (SQLException e) {
            CTILogger.error(e);
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                CTILogger.error(e);
            }
        }
        
        //check for custom communication medium
        additionalInfo.setCommMediumCustom(additionalInfo.getCommMedium());
        String commMedium = additionalInfo.getCommMedium();
        if(commMedium != null) {
            if(commMedium.equals(CapBankAdditional.STR_NONE)) {
                additionalInfo.setCustomCommMedium(false);
            } else {
                CapBankCommunicationMedium[] commMediums = CapBankCommunicationMedium.values();
                for (CapBankCommunicationMedium med : commMediums) {
                    if(med.getDisplayName().equals(commMedium)) {
                        additionalInfo.setCustomCommMedium(false);
                        break;
                    }
                }
            }
        }
        
        capbank.setCapbankAdditionalInfo(additionalInfo);
        
        return capbank;

    }

    @Override
    public int save(CapBank capbank) {
        if (capbank.getId() == null) {
            dbPersistentDao.performDBChange(capbank, TransactionType.INSERT);
            CapBankAdditional addInfo = capbank.getCapbankAdditionalInfo();
            addInfo.setDeviceID(capbank.getId());
            if(!addInfo.getCommMediumCustom().isEmpty()){
                addInfo.setCommMedium(addInfo.getCommMediumCustom());
            }
            dbPersistentDao.performDBChange(addInfo, TransactionType.INSERT);
        } else {
            assertCapBankExists(capbank.getId());
            dbPersistentDao.performDBChange(capbank,  TransactionType.UPDATE);
            CapBankAdditional addInfo = capbank.getCapbankAdditionalInfo();
            addInfo.setDeviceID(capbank.getId());
            if(!addInfo.getCommMediumCustom().isEmpty()){
                addInfo.setCommMedium(addInfo.getCommMediumCustom());
            }
            dbPersistentDao.performDBChange(addInfo, TransactionType.UPDATE);
        }

        return capbank.getId();
    }

    @Override
    public boolean delete(int id) {
        CapBank capbank = getCapBank(id);
        dbPersistentDao.performDBChange(capbank, TransactionType.DELETE);
        return true;
    }


    /**
     * @throws NotFoundException if the given id is not a capbank
     */
    private void assertCapBankExists(int id) throws NotFoundException {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao == null || pao.getPaoType() != PaoType.CAPBANK) {
            throw new NotFoundException("No capbank with id " + id + " found.");
        }
    }
}
