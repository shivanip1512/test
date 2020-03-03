package com.cannontech.web.api.deviceReadings;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.deviceReadings.model.DeviceReadingRequest;
import com.cannontech.deviceReadings.model.DeviceReadingsResponse;
import com.cannontech.deviceReadings.service.DeviceReadingsService;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@RequestMapping("/device/")
public class DeviceReadingsApiController {
    @Autowired private DeviceReadingsService deviceReadingService;

    @GetMapping("/getLatestReading")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_POINTS, level = HierarchyPermissionLevel.VIEW)
    public ResponseEntity<Object> getLatestReading(@RequestBody DeviceReadingRequest deviceReadingRequest) {
        List<DeviceReadingsResponse> responses = deviceReadingService.getLatestReading(deviceReadingRequest);
        HashMap<String, List<DeviceReadingsResponse>> deviceReadingMap = new HashMap<>();
        deviceReadingMap.put("DeviceReadings", responses);
        return new ResponseEntity<>(deviceReadingMap, HttpStatus.OK);

    }

}