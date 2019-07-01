package com.cannontech.web.api.core.setup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.mbean.ServerDatabaseCache;

@RestController
@RequestMapping("/core/setup/route")
public class RouteApiController {
    @Autowired ServerDatabaseCache cache;

    @GetMapping("/allRoutes")
    public ResponseEntity<Object> allRoutes() {
        List<LiteYukonPAObject> routes = cache.getAllRoutes();
        return new ResponseEntity<>(routes, HttpStatus.OK);
    }

}
