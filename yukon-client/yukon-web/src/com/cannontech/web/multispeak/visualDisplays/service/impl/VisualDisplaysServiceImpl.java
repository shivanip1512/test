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
	
	private MultispeakLMService multispeakLMService;
	private FdrTranslationDao fdrTranslationDao;

	public List<PowerSuppliersEnum> getAvailablePowerSuppliers() {
		
		List<PowerSuppliersEnum> availablePowerSuppliers = new ArrayList<PowerSuppliersEnum>();
		
		for (PowerSuppliersEnum powerSupplier : PowerSuppliersEnum.values()) {
			
			int currentLoadObjectId = powerSupplier.getCurrentLoadId();
			
			String translation = multispeakLMService.buildFdrMultispeakLMTranslation((new Integer(currentLoadObjectId)).toString());
			List<FdrTranslation> fdrTranslationList = fdrTranslationDao.getByInterfaceTypeAndTranslation(FdrInterfaceType.MULTISPEAK_LM, translation);
			
			int pointId = -1;
			if (fdrTranslationList.size() > 0) {
				FdrTranslation fdrTranslation = fdrTranslationList.get(0);
				pointId = fdrTranslation.getPointId();
			}
			
			if (pointId > 0) {
				availablePowerSuppliers.add(powerSupplier);
			}
		}
		
		
		return availablePowerSuppliers;
	}
	
	@Autowired
	public void setMultispeakLMService(MultispeakLMService multispeakLMService) {
		this.multispeakLMService = multispeakLMService;
	}
	
	@Autowired
	public void setFdrTranslationDao(FdrTranslationDao fdrTranslationDao) {
		this.fdrTranslationDao = fdrTranslationDao;
	}
}
