package com.cannontech.web.api.commChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.service.PortService;

@RestController
@RequestMapping("/device/commChannel")
public class CommChannelApiController {

    @Autowired
    private PortService portService;

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody PortBase portInfo) {
        Integer portId = portService.create(portInfo);
        return new ResponseEntity<>(portId, HttpStatus.OK);
    }

    @GetMapping("/{portId}")
    public ResponseEntity<Object> retrieve(@PathVariable int portId) {
        PortBase portBase = portService.retrieve(portId);
        return new ResponseEntity<>(portBase, HttpStatus.OK);
    }

    @PostMapping("/update/{portId}")
    public ResponseEntity<Object> update(@RequestBody TcpPortDetail tcpPort, @PathVariable int portId) {
        // TODO : This will he completed in update Jira.
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{portId}")
    public ResponseEntity<Object> delete(@PathVariable int portId) {
        // TODO : This will he completed in delete Jira.
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
