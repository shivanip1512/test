package com.cannontech.stars.dr.optout.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.dao.OptOutDao;
import com.cannontech.stars.dr.optout.model.OptOut;
import com.cannontech.stars.dr.optout.util.OptOutUtil;
import com.cannontech.stars.xml.serialize.StarsLMProgramEvent;
import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;

public class OptOutDaoImpl implements OptOutDao {
    private ECMappingDao ecMappingDao;
    
    @Override
    public List<OptOut> getAll(final CustomerAccount customerAccount) {
        
        final int customerAccountId = customerAccount.getAccountId();

        LiteStarsEnergyCompany energyCompany = 
            ecMappingDao.getCustomerAccountEC(customerAccount);

        try {
            StarsLMProgramHistory programHistory = OptOutUtil.getLMProgramHistory(customerAccountId,
                                                                                  energyCompany);
            final List<OptOut> optOutList = 
                new ArrayList<OptOut>(programHistory.getStarsLMProgramEventCount());

            for (int x = 0; x < programHistory.getStarsLMProgramEventCount(); x++) {
                StarsLMProgramEvent event = programHistory.getStarsLMProgramEvent(x);
                OptOut optout = OptOutUtil.toOptOut(customerAccountId, event);
                optOutList.add(optout);
            }
            return optOutList;
        } catch (NotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

}
