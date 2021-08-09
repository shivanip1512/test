package com.cannontech.web.api.macroRoute;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;
import com.cannontech.web.api.macroRoute.service.MacroRouteService;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@RequestMapping("/macroRoutes")
public class MacroRouteApiController {

    @Autowired MacroRouteService macroRouteService;
    @Autowired MacroRouteApiValidator macroApiRouteValidator;
    @Autowired MacroRouteApiCreateValidator macroRouteApiCreateValidator;

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody MacroRouteModel macroRouteModel,
            YukonUserContext userContext) {
        MacroRouteModel createMacroRoute = macroRouteService.create(macroRouteModel, userContext.getYukonUser());
        return new ResponseEntity<>(createMacroRoute, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<Object> update(@PathVariable("id") int id, @Valid @RequestBody MacroRouteModel<?> macroRouteModel,
            YukonUserContext userContext) {
        MacroRouteModel<?> updateMacroRoute = macroRouteService.update(id, macroRouteModel, userContext.getYukonUser());
        return new ResponseEntity<>(updateMacroRoute, HttpStatus.OK);
    }

    @InitBinder("macroRouteModel")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(macroApiRouteValidator);

        String trendId = ServletUtils.getPathVariable("id");
        if (trendId == null) {
            binder.addValidators(macroRouteApiCreateValidator);
        }
    }

}
