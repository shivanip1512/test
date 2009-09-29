package com.cannontech.dr.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;

public class AuthorizedFilter implements UiFilter<DisplayablePao> {
    private PaoAuthorizationService paoAuthorizationService;
    private LiteYukonUser user;
    private Permission[] permissions;

    /**
     * Constructor for Authorization filter
     * @param paoAuthorizationService - authorization service
     * @param user - User to check permissions for
     * @param permissions - Permissions to check - the user must be authorized for ALL permissions
     *                      for the pao in order to 'match'
     */
    public AuthorizedFilter(PaoAuthorizationService paoAuthorizationService,
                            LiteYukonUser user,
                            Permission... permissions) {
        this.paoAuthorizationService = paoAuthorizationService;
        this.user = user;
        this.permissions = permissions;
    }

    @Override
    public List<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<DisplayablePao>> retVal =
            new ArrayList<PostProcessingFilter<DisplayablePao>>(1);
        retVal.add(new PostProcessingFilter<DisplayablePao>() {

            @Override
            public boolean matches(DisplayablePao pao) {
                
                for(Permission permission : permissions) {
                    if(!paoAuthorizationService.isAuthorized(user,
                                                             permission,
                                                             pao)) {
                        return false;
                    }
                }
                
                return true;
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
