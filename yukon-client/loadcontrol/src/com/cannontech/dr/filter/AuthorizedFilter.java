package com.cannontech.dr.filter;

import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.PostProcessingFilterAdapter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class AuthorizedFilter<T extends YukonPao> implements UiFilter<T> {
    private PaoAuthorizationService paoAuthorizationService;
    private LiteYukonUser user;
    private Permission permission;

    /**
     * Constructor for Authorization filter
     * @param paoAuthorizationService - authorization service
     * @param user - User to check permissions for
     * @param permissions - Permissions to check - the user must be authorized for ALL permissions
     *                      for the pao in order to 'match'
     */
    public AuthorizedFilter(PaoAuthorizationService paoAuthorizationService,
                            LiteYukonUser user,
                            Permission permission) {
        this.paoAuthorizationService = paoAuthorizationService;
        this.user = user;
        this.permission = permission;
    }

    @Override
    public List<PostProcessingFilter<T>> getPostProcessingFilters() {
        List<PostProcessingFilter<T>> retVal = Lists.newArrayListWithCapacity(1);
        retVal.add(new PostProcessingFilterAdapter<T>() {

            @Override
            public List<T> process(List<T> objectsFromDb) {
                List<T> authorizedPaos = 
                    paoAuthorizationService.filterAuthorized(user, objectsFromDb, permission);
                return authorizedPaos;
            }

            @Override
            /**
             * Be very careful when using this method - checking authorization individually can
             * have extremely poor performance.  If checking authorization on several objects, use 
             * the process(List<T>) method.
             */
            protected boolean matches(T object) {
                return paoAuthorizationService.isAuthorized(user, permission, object);
            }

        });
        return retVal;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        return null;
    }
}
