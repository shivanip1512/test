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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/loadGroups")
public class LoadGroupSetupApiController {
    
    @Autowired LoadGroupSetupService loadGroupService;
    @Autowired LMDeleteValidator lmDeleteValidator;
    @Autowired LMCopyValidator lmCopyValidator;
    private List<LoadGroupSetupValidator<? extends LoadGroupBase>> validators;
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id, YukonUserContext userContext) {
        LoadGroupBase loadGroup = loadGroupService.retrieve(id, userContext.getYukonUser());
        return new ResponseEntity<>(loadGroup, HttpStatus.OK);
    }

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody LoadGroupBase loadGroup, YukonUserContext userContext) {
        LoadGroupBase createLoadGroup = loadGroupService.create(loadGroup, userContext.getYukonUser());
        return new ResponseEntity<>(createLoadGroup, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<Object> update(@PathVariable int id, @Valid @RequestBody LoadGroupBase loadGroup,
            YukonUserContext userContext) {
        LoadGroupBase updateLoadGroup = loadGroupService.update(id, loadGroup, userContext.getYukonUser());
        return new ResponseEntity<>(updateLoadGroup, HttpStatus.OK);
    }

    @PostMapping("/{id}/copy")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> copy(@PathVariable int id, @Valid @RequestBody LMCopy lmCopy, YukonUserContext userContext) {
        LoadGroupBase copiedLoadGroup = loadGroupService.copy(id, lmCopy, userContext.getYukonUser());
        return new ResponseEntity<>(copiedLoadGroup, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@PathVariable int id, YukonUserContext userContext) {

        int paoId = loadGroupService.delete(id, userContext.getYukonUser());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("id", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @InitBinder("loadGroupBase")
    public void setupBinder(WebDataBinder binder) {
        validators.stream().forEach(e -> {
            if (e.supports(binder.getTarget().getClass())) {
                binder.addValidators(e);
            }
        });
    }

    @InitBinder("LMCopy")
    public void setupBinderCopy(WebDataBinder binder) {
        binder.addValidators(lmCopyValidator);
    }

    @Autowired
    void setValidators(List<LoadGroupSetupValidator<? extends LoadGroupBase>> validators) {
        this.validators = validators;
    }
}
