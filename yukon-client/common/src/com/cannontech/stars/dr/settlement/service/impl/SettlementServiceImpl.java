package com.cannontech.stars.dr.settlement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.settlement.dao.SettlementConfigDao;
import com.cannontech.stars.dr.settlement.model.AvailableRate;
import com.cannontech.stars.dr.settlement.model.SettlementDto;
import com.cannontech.stars.dr.settlement.service.SettlementService;
import com.cannontech.stars.util.SettlementConfigFuncs;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SettlementServiceImpl implements SettlementService {

    private SettlementConfigDao settlementConfigDao;
    private StarsDatabaseCache starsDatabaseCache;
    
    private final List<String> nonEditableFieldNames = ImmutableList.of(SettlementConfig.HECO_RATE_DEMAND_CHARGE_STRING);
    
    @Override
    public List<LiteSettlementConfig> getEditableConfigs() {
        List<LiteSettlementConfig> allLiteSettlementConfigs = SettlementConfigFuncs.getAllLiteConfigsBySettlementType(true);
        
        List<LiteSettlementConfig> editableLiteSettlementConfigs = Lists.newArrayListWithExpectedSize(allLiteSettlementConfigs.size());
        for (LiteSettlementConfig liteSettlementConfig : allLiteSettlementConfigs) {
            if (!nonEditableFieldNames.contains(liteSettlementConfig.getFieldName())) {
                editableLiteSettlementConfigs.add(liteSettlementConfig);
            }
        }
        
        return editableLiteSettlementConfigs;
    }
    
    @Override
    public List<AvailableRate> getAvailableRates(int energyCompanyId) {
        List<AvailableRate> availableRates = Lists.newArrayList();
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        
        List<YukonListEntry> allAvailRateSchedules = SettlementConfigFuncs.getAllAvailRateSchedules(energyCompany);
        for (YukonListEntry yukonListEntry : allAvailRateSchedules) {
            List<LiteSettlementConfig> rateConfigs = SettlementConfigFuncs.getRateScheduleConfigs(yukonListEntry.getEntryID());

            boolean enabled = false;
            if (rateConfigs.size() > 0) {
                enabled = rateConfigs.get(0).getRefEntryID() > 0;
            }

            AvailableRate availableRate = new AvailableRate(yukonListEntry.getEntryID(), yukonListEntry.getEntryText(), rateConfigs, enabled);
            availableRates.add(availableRate);
        }

        return availableRates;
    }
    
    @Override
    @Transactional
    public void saveSettlementDto(SettlementDto settlementDto, int energyCompanyId){
        
        // Save editable settlement configurations
        for (LiteSettlementConfig liteSettlementConfig : settlementDto.getEditableLiteSettlementConfigs()) {
            settlementConfigDao.save(liteSettlementConfig, 0);
        }
        
        // Save new rate configurations, delete any that are no longer enabled.
        for (AvailableRate availableRate : settlementDto.getAvailableRates()) {
            for (LiteSettlementConfig rateConfiguration : availableRate.getRateConfigurations()) {
                if (availableRate.isEnabled()) {
                    settlementConfigDao.save(rateConfiguration, availableRate.getEntryId());
                } else {
                    if (rateConfiguration.getConfigID() >= 0) {
                        settlementConfigDao.delete(rateConfiguration);
                    }
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