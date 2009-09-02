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

    public AuthorizedFilter(PaoAuthorizationService paoAuthorizationService,
            LiteYukonUser user) {
        this.paoAuthorizationService = paoAuthorizationService;
        this.user = user;
    }

    @Override
    public List<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        List<PostProcessingFilter<DisplayablePao>> retVal =
            new ArrayList<PostProcessingFilter<DisplayablePao>>(1);
        retVal.add(new PostProcessingFilter<DisplayablePao>() {

            @Override
            public boolean matches(DisplayablePao pao) {
                // TODO:
                return true;
//                return paoAuthorizationService.isAuthorized(user,
//                                                            Permission.LM_VISIBLE,
//                                                            pao);
            }});
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
