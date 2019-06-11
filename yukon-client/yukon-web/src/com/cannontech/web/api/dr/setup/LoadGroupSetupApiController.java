package com.cannontech.web.api.dr.setup;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;

@RestController
@RequestMapping("/dr/setup/loadGroup")
public class LoadGroupSetupApiController {

    @Autowired LoadGroupSetupService loadGroupService;
    @Autowired @Qualifier("loadGroupExpresscomSetupValidator") LoadGroupSetupValidator<? extends LoadGroupBase> loadGroupValidator;

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        LoadGroupBase loadGroup = loadGroupService.retrieve(id);
        return new ResponseEntity<>(loadGroup, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Object> save(@Valid @RequestBody LoadGroupBase loadGroup) {
        int paoId = loadGroupService.save(loadGroup);
        return new ResponseEntity<>(paoId, HttpStatus.OK);
    }

    @InitBinder("loadGroupBase")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(loadGroupValidator);
    }
}
