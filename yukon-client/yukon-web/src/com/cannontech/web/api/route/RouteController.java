package com.cannontech.web.api.route;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.route.service.RouteService;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@RequestMapping("/routes")
public class RouteController {

    @Autowired private RouteService routeService;
    @Autowired private RouteAPICreateValidator routeAPICreateValidator;
    @Autowired private RouteValidator routeValidator;

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody RouteBaseModel<?> routeBaseModel, YukonUserContext userContext) {
        RouteBaseModel<?> createdRoute = routeService.create(routeBaseModel, userContext.getYukonUser());
        return new ResponseEntity<>(createdRoute, HttpStatus.CREATED);
    }

    @InitBinder("routeBaseModel")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(routeValidator);

        String trendId = ServletUtils.getPathVariable("id");
        if (trendId == null) {
            binder.addValidators(routeAPICreateValidator);
        }
    }
}
