package com.cannontech.stars.database.cache;

import java.util.List;
import java.util.Map;

import org.joda.time.ReadableInstant;

import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMControlHistory;
import com.cannontech.stars.database.data.lite.LiteWebConfiguration;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;

public class MockStarsDatabaseCache extends StarsDatabaseCache {

    @Override
    public void init() {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public void loadData() {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public synchronized List<LiteStarsEnergyCompany> getAllEnergyCompanies() {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public synchronized Map<Integer,StarsYukonUser> getAllStarsYukonUsers() {
        throw new MethodNotImplementedException();
    }
    
    @Override
    @Deprecated
    public LiteStarsEnergyCompany getEnergyCompanyByUser(final LiteYukonUser user) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public Map<Integer, LiteStarsEnergyCompany> getAllEnergyCompanyMap() {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public LiteStarsEnergyCompany getEnergyCompany(YukonEnergyCompany yukonEnergyCompany) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public LiteStarsEnergyCompany getEnergyCompany(int energyCompanyID) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public void addEnergyCompany(LiteStarsEnergyCompany company) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public LiteStarsEnergyCompany getDefaultEnergyCompany() {
        throw new MethodNotImplementedException();
    }
    
    @Deprecated
    @Override
    public LiteWebConfiguration getWebConfiguration(int configID) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public void addWebConfiguration(LiteWebConfiguration config) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public LiteWebConfiguration deleteWebConfiguration(int configID) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public boolean isStarsUser(final LiteYukonUser yukonUser) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    @Deprecated
    public StarsYukonUser getStarsYukonUser(LiteYukonUser yukonUser) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public void deleteStarsYukonUser(int userID) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public void dbChangeReceived(DBChangeMsg msg) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public LiteStarsLMControlHistory getLMControlHistory(int groupID, ReadableInstant startReadableInstant) {
        throw new MethodNotImplementedException();
    }
    
}