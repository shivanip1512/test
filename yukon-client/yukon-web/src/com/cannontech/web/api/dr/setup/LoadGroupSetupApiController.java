package com.cannontech.web.api.dr.setup;

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

import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;


@RestController
@RequestMapping("/dr/setup/loadGroup")
public class LoadGroupSetupApiController {

    @Autowired LoadGroupSetupService loadGroupService;
    @Autowired LMDeleteValidator lmDeleteValidator;
    private List<LoadGroupSetupValidator<? extends LoadGroupBase>> validators;
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        LoadGroupBase loadGroup = loadGroupService.retrieve(id);
        return new ResponseEntity<>(loadGroup, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Object> save(@Valid @RequestBody LoadGroupBase loadGroup) {
        int paoId = loadGroupService.save(loadGroup);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("paoId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/copy")
    public ResponseEntity<Object> copy(@RequestBody LoadGroupBase loadGroup) {
        int paoId = loadGroupService.copy(loadGroup.getId(), loadGroup.getName());
        return new ResponseEntity<>(paoId, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Object> delete(@Valid @RequestBody LMDelete lmDelete, @PathVariable int id) {
        int paoId = loadGroupService.delete(id, lmDelete.getName());
        return new ResponseEntity<>(paoId, HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<Object> testConnection() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @InitBinder("loadGroupBase")
    public void setupBinder(WebDataBinder binder) {
        validators.stream().forEach(e -> {
            if (e.supports(binder.getTarget().getClass())) {
                binder.addValidators(e);
            }
        });
    }

    @InitBinder("LMDelete")
    public void setupBinderDelete(WebDataBinder binder) {
        binder.addValidators(lmDeleteValidator);
    }

    @Autowired
    void setValidators(List<LoadGroupSetupValidator<? extends LoadGroupBase>> validators) {
        this.validators = validators;
    }
}
