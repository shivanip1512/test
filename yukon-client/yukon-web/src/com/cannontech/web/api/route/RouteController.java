package com.cannontech.web.api.route;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Autowired private RouteApiCreateValidator routeAPICreateValidator;
    @Autowired private RouteValidator routeValidator;

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody RouteBaseModel<?> routeBaseModel, YukonUserContext userContext) {
        RouteBaseModel<?> createdRoute = routeService.create(routeBaseModel, userContext.getYukonUser());
        return new ResponseEntity<>(createdRoute, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        RouteBaseModel<?> routeBaseModel = routeService.retrieve(id);
        return new ResponseEntity<>(routeBaseModel, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<Object> retrieveAllRoutes() {
        List<RouteBaseModel> listOfPorts = routeService.retrieveAllRoutes();
        return new ResponseEntity<>(listOfPorts, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@PathVariable int id, YukonUserContext userContext) throws SQLException {
        int routeId = routeService.delete(id, userContext.getYukonUser());
        Map<String, Integer> routeIdMap = new HashMap<>();
        routeIdMap.put("id", routeId);
        return new ResponseEntity<>(routeIdMap, HttpStatus.OK);
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
