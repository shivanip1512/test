package com.cannontech.web.api.dr.setup;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;
import com.cannontech.web.security.annotation.IgnoreCsrfCheck;

@RestController
@RequestMapping("/api/setup/loadGroup")
public class LoadGroupSetupRestController {

    private static final Logger log = YukonLogManager.getLogger(LoadGroupSetupRestController.class);
    @Autowired LoadGroupSetupService loadGroupService;
    @Autowired LoadGroupSetupValidator loadGroupValidator;
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        try {
            LoadGroupBase loadGroup = loadGroupService.retrieve(id);
            return new ResponseEntity<>(loadGroup, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while retrieving load group details " + e);
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Exception while retrieving group", e);
        } 
    }
    
    @IgnoreCsrfCheck
    @PostMapping("/save")
    public ResponseEntity<Object> save(@Valid @RequestBody LoadGroupBase loadGroup) {
        try {
            PaoIdentifier pao = loadGroupService.save(loadGroup);
            return new ResponseEntity<>(pao.getPaoId(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while saving load group details " + e);
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Exception in saving group", e);
        }
    }
    
    @InitBinder("loadGroupBase")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(loadGroupValidator);
    }
}
