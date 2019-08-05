package com.cannontech.web.api.core.setup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@RequestMapping("/core/setup/route")
public class RouteApiController {
    @Autowired ServerDatabaseCache cache;

    @GetMapping("/allRoutes")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.LIMITED)
    public ResponseEntity<Object> allRoutes() {
        List<LiteYukonPAObject> routes = cache.getAllRoutes();
        return new ResponseEntity<>(routes, HttpStatus.OK);
    }

}
