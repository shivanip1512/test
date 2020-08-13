
package com.cannontech.web.api.commChannel;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.service.PortService;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/devices/commChannels")
public class CommChannelApiController {

    @Autowired private PortService portService;
    @Autowired private PortCreateApiValidator<? extends PortBase<?>> portApiCreationValidator;
    @Autowired private PortApiValidator<? extends PortBase<?>> portApiValidator;

    //Create port
    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody PortBase<?> port) {
        return new ResponseEntity<>(portService.create(port), HttpStatus.OK);
    }

    //Get port
    @GetMapping("/{portId}")
    public ResponseEntity<Object> retrieve(@PathVariable int portId) {
        return new ResponseEntity<>(portService.retrieve(portId), HttpStatus.OK);
    }

    //Update port
    @PatchMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> update(@Valid @RequestBody PortBase<?> port, @PathVariable("id") int portId) {
        return new ResponseEntity<>(portService.update(portId, port), HttpStatus.OK);
    }

    //Delete port
    @DeleteMapping("/{portId}")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Map<String, Integer>> delete(@PathVariable int portId) {
        int id = portService.delete(portId);
        Map<String, Integer> portIdMap = new HashMap<>();
        portIdMap.put("id", id);
        return new ResponseEntity<>(portIdMap, HttpStatus.OK);
    }

    //Get all ports
    @GetMapping("/")
    public ResponseEntity<Object> retrieveAllPorts() {
        List<PortBase> listOfPorts = portService.getAllPorts();
        return new ResponseEntity<>(listOfPorts, HttpStatus.OK);
    }

    //Get devices assigned to port
    @GetMapping("/{portId}/devicesAssigned")
    public ResponseEntity<Object> retrieveAllDevicesForPort(@PathVariable int portId) {
        List<DeviceBaseModel> listOfDevices = portService.getDevicesAssignedPort(portId);
        return new ResponseEntity<>(listOfDevices, HttpStatus.OK);
    }

    @InitBinder("portBase")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(portApiValidator);

        String portId = ServletUtils.getPathVariable("id");
        if (portId == null) {
            binder.addValidators(portApiCreationValidator);
        }
    }
   
}
