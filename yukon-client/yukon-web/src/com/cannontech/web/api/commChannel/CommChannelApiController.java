package com.cannontech.web.api.commChannel;

import java.util.HashMap;
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
import com.cannontech.common.device.port.PortDelete;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.service.PortService;

@RestController
@RequestMapping("/device/commChannel")
public class CommChannelApiController {

    @Autowired private PortService portService;
    private List<PortValidator<? extends PortBase<?>>> validators;

    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody PortBase<?> port) {
        HashMap<String, Integer> portIdMap = new HashMap<>();
        portIdMap.put("portId", portService.create(port));
        return new ResponseEntity<>(portIdMap, HttpStatus.OK);
    }

    @GetMapping("/{portId}")
    public ResponseEntity<Object> retrieve(@PathVariable int portId) {
        return new ResponseEntity<>(portService.retrieve(portId), HttpStatus.OK);
    }

    @PostMapping("/update/{portId}")
    public ResponseEntity<Object> update(@Valid  @PathVariable int portId, @RequestBody PortBase<?> port) {
        HashMap<String, Integer> portIdMap = new HashMap<>();
        portIdMap.put("portId", portService.update(portId, port));
        return new ResponseEntity<>(portIdMap, HttpStatus.OK);
        
    }

    @DeleteMapping("/delete/{portId}")
   public ResponseEntity<Object> delete(@Valid @RequestBody PortDelete portDelete, @PathVariable int portId) {
        //Integer paoId = portService.delete(portId, portDelete.getName());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("portId", portService.delete(portId, portDelete.getName()));
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }
    @InitBinder("portBase")
    public void setupBinder(WebDataBinder binder) {
        validators.stream().forEach(e -> {
            if (e.supports(binder.getTarget().getClass())) {
                binder.addValidators(e);
            }
        });
    }

    @Autowired
    void setValidators(List<PortValidator<? extends PortBase<?>>> validators) {
        this.validators = validators;
    }
}
