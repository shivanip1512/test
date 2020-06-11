package com.cannontech.web.api.virtualDevice;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;
import com.cannontech.common.device.virtualDevice.service.VirtualDeviceService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.virtualDevice.VirtualDeviceApiValidator;

@RestController
@RequestMapping("/device/virtualDevices")
public class VirtualDeviceApiController {

    @Autowired private VirtualDeviceService virtualDeviceService;
    @Autowired private VirtualDeviceCreateApiValidator virtualDeviceCreateApiValidator;
    @Autowired private VirtualDeviceApiValidator virtualDeviceApiValidator;

    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody VirtualDeviceModel virtualDevice) {
        return new ResponseEntity<>(virtualDeviceService.create(virtualDevice), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(virtualDeviceService.retrieve(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id, @Valid @RequestBody VirtualDeviceModel virtualDevice) {
        return new ResponseEntity<>(virtualDeviceService.update(id, virtualDevice), HttpStatus.OK);
    }

    @InitBinder("virtualDeviceBase")
    public void setBinder(WebDataBinder binder) {
        binder.addValidators(virtualDeviceApiValidator);

        String virtualDeviceId = ServletUtils.getPathVariable("id");
        if (virtualDeviceId == null) {
            binder.addValidators(virtualDeviceCreateApiValidator);
        }
    }
}
