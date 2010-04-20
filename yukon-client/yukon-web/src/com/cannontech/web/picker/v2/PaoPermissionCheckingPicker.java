package com.cannontech.web.picker.v2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.user.YukonUserContext;

public class PaoPermissionCheckingPicker extends FilterPaoPicker {

	private PaoAuthorizationService paoAuthorizationService;
	private Permission permission;
	
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
				
				YukonPao pao = new PaoIdentifier(object.getPaoId(), PaoType.getForDbString(object.getType()));
				return paoAuthorizationService.isAuthorized(userContext.getYukonUser(), permission, pao);
			}
		};
		
		ppFilters.add(lmPaoPermissionCheckingPostProcessingFilter);
		
		return super.search(ss, start, count, extraSqlFilters, ppFilters, userContext);
	}
	
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	
	@Autowired
	public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
		this.paoAuthorizationService = paoAuthorizationService;
	}
}
