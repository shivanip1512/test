
package com.cannontech.web.api.commChannel;

import java.util.List;

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

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.service.PortService;
import com.cannontech.stars.util.ServletUtils;

@RestController
@RequestMapping("/device/commChannel")
public class CommChannelApiController {

    @Autowired private PortService portService;
    @Autowired private PortCreationValidator<? extends PortBase<?>> portCreationValidator;
    @Autowired private PortValidator<? extends PortBase<?>> portValidator;

    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody PortBase<?> port) {
        return new ResponseEntity<>(portService.create(port), HttpStatus.OK);
    }

    @GetMapping("/{portId}")
    public ResponseEntity<Object> retrieve(@PathVariable int portId) {
        return new ResponseEntity<>(portService.retrieve(portId), HttpStatus.OK);
    }

    @PostMapping("/update/{portId}")
    public ResponseEntity<Object> update(@Valid @RequestBody PortBase<?> port, @PathVariable int portId) {
        return new ResponseEntity<>(portService.update(portId, port), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{portId}")
    public ResponseEntity<Object> delete(@PathVariable int portId) {
        return new ResponseEntity<>(portService.delete(portId), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> retrieveAllPorts() {
        List<PortBase> listOfPorts = portService.getAllPorts();
        return new ResponseEntity<>(listOfPorts, HttpStatus.OK);
    }
    
    @GetMapping("/devicesAssigned/{portId}")
    public ResponseEntity<Object> retrieveAllDevicesForPort(@PathVariable int portId) {
        List<DeviceBaseModel> listOfDevices = portService.getDevicesAssignedPort(portId);
        return new ResponseEntity<>(listOfDevices, HttpStatus.OK);
    }

    @InitBinder("portBase")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(portValidator);

        String portId = ServletUtils.getPathVariable("portId");
        if (portId == null) {
            binder.addValidators(portCreationValidator);
        }
    }
   
}
