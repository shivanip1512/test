package com.cannontech.web.multispeak.visualDisplays.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.multispeak.service.MultispeakLMService;
import com.cannontech.web.multispeak.visualDisplays.model.PowerSuppliersEnum;
import com.cannontech.web.multispeak.visualDisplays.service.VisualDisplaysService;

public class VisualDisplaysServiceImpl implements VisualDisplaysService {
    
    @Autowired private MultispeakLMService multispeakLMService;
    @Autowired private FdrTranslationDao fdrTranslationDao;
    
    private static final FdrInterfaceType lm = FdrInterfaceType.MULTISPEAK_LM;
    
    public List<PowerSuppliersEnum> getAvailablePowerSuppliers() {
        
        List<PowerSuppliersEnum> available = new ArrayList<>();
        
        for (PowerSuppliersEnum supplier : PowerSuppliersEnum.values()) {
            
            int currentLoadObjectId = supplier.getCurrentLoadId();
            
            String pointString = Integer.valueOf(currentLoadObjectId).toString();
            String translation = multispeakLMService.buildFdrMultispeakLMTranslation(pointString);
            List<FdrTranslation> translations = fdrTranslationDao.getByInterfaceTypeAndTranslation(lm, translation);
            
            int pointId = -1;
            if (translations.size() > 0) {
                FdrTranslation fdrTranslation = translations.get(0);
                pointId = fdrTranslation.getPointId();
            }
            
            if (pointId > 0) {
                available.add(supplier);
            }
        }
        
        return available;
    }
    
}