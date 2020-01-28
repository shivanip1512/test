package com.cannontech.web.api.dr.setup;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.dr.setup.LMSetupFilter;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.service.LMSetupFilterService;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.util.YukonUserContextResolver;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/setup")
public class LMSetupFilterApiController {
    @Autowired private List<LMSetupFilterService<?>> filterServices;
    @Autowired private YukonUserContextResolver contextResolver;

    @PostMapping("/filter")
    public ResponseEntity<Object> filter(@RequestBody FilterCriteria<LMSetupFilter> filterCriteria, HttpServletRequest request) {
        LmSetupFilterType filterByType = filterCriteria.getFilteringParameters().getFilterByType();
        LMSetupFilterService<?> filteredService = filterServices.stream()
                                                                .filter(filterService -> filterService.getFilterType() == filterByType)
                                                                .findAny()
                                                                .get();
        LiteYukonUser user = ApiRequestContext.getContext().getLiteYukonUser();
        YukonUserContext userContext = contextResolver.resolveContext(user, request);
        SearchResults<?> filteredResults = filteredService.filter(filterCriteria, userContext);
        return new ResponseEntity<>(filteredResults, HttpStatus.OK);
    }
}
