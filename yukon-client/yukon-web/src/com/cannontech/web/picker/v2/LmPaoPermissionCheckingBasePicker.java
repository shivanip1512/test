package com.cannontech.web.picker.v2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.user.YukonUserContext;

public class LmPaoPermissionCheckingBasePicker extends FilterPaoPicker {

	private PaoAuthorizationService paoAuthorizationService;
	private PaoDao paoDao;
	
	
	@Override
	public SearchResult<UltraLightPao> search(String ss, int start, int count,
			List<SqlFilter> extraSqlFilters, List<PostProcessingFilter<UltraLightPao>> extraPostProcessingFilters,
			final YukonUserContext userContext) {
		
		List<PostProcessingFilter<UltraLightPao>> ppFilters = new ArrayList<PostProcessingFilter<UltraLightPao>>();
		if (extraPostProcessingFilters != null) {
			ppFilters.addAll(extraPostProcessingFilters);
		}
		
		PostProcessingFilter<UltraLightPao> lmPaoPermissionCheckingPostProcessingFilter = new PostProcessingFilter<UltraLightPao>() {
			
			@Override
			public boolean matches(UltraLightPao object) {
				return paoAuthorizationService.isAuthorized(userContext.getYukonUser(), Permission.LM_VISIBLE, paoDao.getLiteYukonPAO(object.getPaoId()));
			}
		};
		
		ppFilters.add(lmPaoPermissionCheckingPostProcessingFilter);
		
		return super.search(ss, start, count, extraSqlFilters, ppFilters, userContext);
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
