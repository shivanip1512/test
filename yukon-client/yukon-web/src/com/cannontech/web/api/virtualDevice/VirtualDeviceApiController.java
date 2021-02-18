package com.cannontech.web.api.virtualDevice;

import java.util.HashMap;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualDeviceSortableField;
import com.cannontech.common.device.virtualDevice.service.VirtualDeviceService;
import com.cannontech.common.model.Direction;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.device.VirtualBase;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@RequestMapping("/devices/virtualDevices")
@CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.VIEW)
public class VirtualDeviceApiController {

    @Autowired private VirtualDeviceService virtualDeviceService;
    @Autowired private VirtualDeviceCreateApiValidator<? extends VirtualDeviceBaseModel<?>> virtualDeviceCreateApiValidator;
    @Autowired private VirtualDeviceApiValidator<? extends VirtualDeviceBaseModel<?>> virtualDeviceApiValidator;

    @PostMapping("")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody VirtualDeviceBaseModel<? extends VirtualBase> virtualDevice) {
        return new ResponseEntity<>(virtualDeviceService.create(virtualDevice), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(virtualDeviceService.retrieve(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<Object> update(@PathVariable int id, @Valid @RequestBody VirtualDeviceBaseModel<? extends VirtualBase> virtualDevice) {
        return new ResponseEntity<>(virtualDeviceService.update(id, virtualDevice), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@PathVariable int id) {
        int virtualDeviceId = virtualDeviceService.delete(id);
        HashMap<String, Integer> virtualDeviceIdMap = new HashMap<>();
        virtualDeviceIdMap.put("id", virtualDeviceId);
        return new ResponseEntity<>(virtualDeviceIdMap, HttpStatus.OK);
    }

    @GetMapping("")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.VIEW)
    public ResponseEntity<Object> getAll(
            @RequestParam(name = "sort", defaultValue = "PAO_NAME") VirtualDeviceSortableField sort,
            @RequestParam(defaultValue = "asc") Direction direction, @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(name = "itemsPerPage", defaultValue = "250") Integer itemsPerPage) {
        return new ResponseEntity<>(virtualDeviceService.getPage(sort, direction, page, itemsPerPage), HttpStatus.OK);
    }

    @InitBinder("virtualDeviceBaseModel")
    public void setBinder(WebDataBinder binder) {
        binder.addValidators(virtualDeviceApiValidator);

        String virtualDeviceId = ServletUtils.getPathVariable("id");
        if (virtualDeviceId == null) {
            binder.addValidators(virtualDeviceCreateApiValidator);
        }
    }
}
