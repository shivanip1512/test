package com.cannontech.stars.dr.settlement.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.stars.dr.settlement.dao.SettlementConfigDao;
import com.cannontech.stars.dr.settlement.model.AvailableRate;
import com.cannontech.stars.dr.settlement.model.SettlementDto;
import com.cannontech.stars.dr.settlement.service.SettlementService;
import com.cannontech.stars.util.SettlementConfigFuncs;
import com.google.common.collect.Lists;

public class SettlementServiceImpl implements SettlementService {

    private SettlementConfigDao settlementConfigDao;
    private StarsDatabaseCache starsDatabaseCache;
    
    
    private List<String> nonEditableFieldNames = Lists.newArrayList(SettlementConfig.HECO_RATE_DEMAND_CHARGE_STRING);
    
    
    @Override
    public List<LiteSettlementConfig> getEditableConfigs(int yukonDefId) {
        List<LiteSettlementConfig> allLiteSettlementConfigs = SettlementConfigFuncs.getAllLiteConfigsBySettlementType(yukonDefId);
        
        List<LiteSettlementConfig> editableLiteSettlementConfigs = Lists.newArrayListWithExpectedSize(allLiteSettlementConfigs.size());
        for (LiteSettlementConfig liteSettlementConfig : allLiteSettlementConfigs) {
            if (!nonEditableFieldNames.contains(liteSettlementConfig.getFieldName())) {
                editableLiteSettlementConfigs.add(liteSettlementConfig);
            }
        }
        
        return editableLiteSettlementConfigs;
    }
    
    @Override
    public List<AvailableRate> getAvailableRates(int energyCompanyId, int yukonDefId) {
        List<AvailableRate> availableRates = Lists.newArrayList();
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        
        List<YukonListEntry> allAvailRateSchedules = SettlementConfigFuncs.getAllAvailRateSchedules(energyCompany, yukonDefId);
        for (YukonListEntry yukonListEntry : allAvailRateSchedules) {
            List<LiteSettlementConfig> rateConfigs = SettlementConfigFuncs.getRateScheduleConfigs(yukonDefId, yukonListEntry.getEntryID());

            AvailableRate availableRate = new AvailableRate(yukonListEntry.getEntryID(), yukonListEntry.getEntryText(), rateConfigs);
            availableRates.add(availableRate);
        }

        return availableRates;
    }
    
    @Override
    @Transactional
    public void saveSettlementDto(SettlementDto settlementDto, int energyCompanyId, int settlementYukonDefId){
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        int settlementEntryId = energyCompany.getYukonListEntry(settlementYukonDefId).getEntryID();

        // Save editable settlement configurations
        for (LiteSettlementConfig liteSettlementConfig : settlementDto.getEditableLiteSettlementConfigs()) {
            settlementConfigDao.save(liteSettlementConfig, settlementYukonDefId, settlementEntryId, 0);
        }
        
        // Save rate configurations
        List<Integer> validRateEntryIds = new ArrayList<Integer>();
        for (AvailableRate availableRate : settlementDto.getAvailableRates()) {

            for (LiteSettlementConfig rateConfiguration : availableRate.getRateConfigurations()) {
                if (availableRate.isEnabled()) {
                    settlementConfigDao.save(rateConfiguration, settlementYukonDefId, settlementEntryId, availableRate.getEntryId());
                    validRateEntryIds.add(rateConfiguration.getEntryID());
                } else {
                    if (rateConfiguration.getConfigID() >= 0) {
                        settlementConfigDao.delete(rateConfiguration);
                    }
                }
            }
        }

        //loop through all updated/inserted Rates and remove all from the settlement config that were not submitted in this request
        List<LiteSettlementConfig> allConfigs = SettlementConfigFuncs.getAllLiteConfigsBySettlementType(settlementYukonDefId);
        for (LiteSettlementConfig liteSettlementConfig : allConfigs) {
            
            //0 is default for no mapping to a YukonListEntry
            if( liteSettlementConfig.getEntryID() > 0 && liteSettlementConfig.getEntryID() != settlementYukonDefId) {   
                // This entry is not one of the new entries.  Delete it. 
                if (!validRateEntryIds.contains(liteSettlementConfig.getEntryID())) {
                    settlementConfigDao.delete(liteSettlementConfig);
                }
            }
        }
    }
        
    // DI Setters 
    @Autowired
    public void setSettlementConfigDao(SettlementConfigDao settlementConfigDao) {
        this.settlementConfigDao = settlementConfigDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
}