package com.cannontech.web.picker.v2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.UltraLightPaoAdapter;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class PaoPermissionCheckingPicker extends FilterPaoPicker {

	private PaoAuthorizationService paoAuthorizationService;
	private Permission permission;
	
	@Override
    public SearchResult<UltraLightPao> search(String ss, int start, int count,
            String extraArgs, final YukonUserContext userContext) {

		List<PostProcessingFilter<UltraLightPaoAdapter>> ppFilters = Lists.newArrayList();
		PostProcessingFilter<UltraLightPaoAdapter> lmPaoPermissionCheckingPostProcessingFilter = 
		    new PostProcessingFilter<UltraLightPaoAdapter>() {

            @Override
            public List<UltraLightPaoAdapter> process(List<UltraLightPaoAdapter> objectsFromDb) {
                List<UltraLightPaoAdapter> authorized =
                    paoAuthorizationService.filterAuthorized(userContext.getYukonUser(), objectsFromDb, permission);
                return authorized;
            }
		};

		ppFilters.add(lmPaoPermissionCheckingPostProcessingFilter);
		return search(ss, start, count, null, ppFilters, userContext);
	}
	
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	
	@Autowired
	public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
		this.paoAuthorizationService = paoAuthorizationService;
	}
}
