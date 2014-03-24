package com.cannontech.web.picker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.result.UltraLightPao;
import com.cannontech.common.search.result.UltraLightPaoHolder;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class PaoPermissionCheckingPicker extends DatabasePaoPicker {

	private PaoAuthorizationService paoAuthorizationService;
	private Permission permission;

	@Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightPao>> postProcessingFilters,
            String extraArgs, final YukonUserContext userContext) {
        PostProcessingFilter<UltraLightPao> lmPaoPermissionCheckingPostProcessingFilter = 
            new PostProcessingFilter<UltraLightPao>() {

            @Override
            public List<UltraLightPao> process(List<UltraLightPao> objectsFromDb) {
                List<UltraLightPaoHolder> toFilter = Lists.newArrayList();
                for (UltraLightPao pao : objectsFromDb) {
                    toFilter.add(new UltraLightPaoHolder(pao));
                }
                List<UltraLightPaoHolder> authorized =
                    paoAuthorizationService.filterAuthorized(userContext.getYukonUser(), toFilter, permission);
                List<UltraLightPao> retVal = Lists.newArrayList();
                for (UltraLightPaoHolder holder : authorized) {
                    retVal.add(holder.getUltraLightPao());
                }
                return retVal;
            }
        };

        postProcessingFilters.add(lmPaoPermissionCheckingPostProcessingFilter);
    }

	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	
	@Autowired
	public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
		this.paoAuthorizationService = paoAuthorizationService;
	}
}
