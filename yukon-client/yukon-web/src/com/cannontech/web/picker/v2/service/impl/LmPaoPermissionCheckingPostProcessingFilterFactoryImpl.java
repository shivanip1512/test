package com.cannontech.web.picker.v2.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.picker.v2.service.LmPaoPermissionCheckingPostProcessingFilterFactory;

public class LmPaoPermissionCheckingPostProcessingFilterFactoryImpl implements LmPaoPermissionCheckingPostProcessingFilterFactory {

	private PaoAuthorizationService paoAuthorizationService;
	private PaoDao paoDao;
	
	@Override
	public PostProcessingFilter<UltraLightPao> getFilterForUser(final LiteYukonUser user) {

		PostProcessingFilter<UltraLightPao> filter = new PostProcessingFilter<UltraLightPao>() {
			
			@Override
			public boolean matches(UltraLightPao object) {
				return paoAuthorizationService.isAuthorized(user, Permission.LM_VISIBLE, paoDao.getLiteYukonPAO(object.getPaoId()));
			}
		};
        
        return filter;
	}
	
	@Autowired
	public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
		this.paoAuthorizationService = paoAuthorizationService;
	}
	
	@Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
}
