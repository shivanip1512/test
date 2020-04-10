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

import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.service.PortService;
import com.cannontech.stars.util.ServletUtils;

@RestController
@RequestMapping("/device/commChannel")
public class CommChannelApiController {

    @Autowired private PortService portService;
    @Autowired private PortCreationValidator<? extends PortBase<?>> portCreationValidator;
    private List<PortValidator<? extends PortBase<?>>> validators;

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
        // TODO : This will he completed in delete Jira.
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @InitBinder("portBase")
    public void setupBinder(WebDataBinder binder) {
        validators.stream().forEach(e -> {
            if (e.supports(binder.getTarget().getClass())) {
                binder.addValidators(e);
            }
        });
        String portId = ServletUtils.getPathVariable("portId");
        if (portId == null) {
            binder.addValidators(portCreationValidator);
        }
    }

    @Autowired
    void setValidators(List<PortValidator<? extends PortBase<?>>> validators) {
        this.validators = validators;
    }
}
