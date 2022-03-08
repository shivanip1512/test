package com.cannontech.web.api.dr.macro;

import java.util.HashMap;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.cannontech.common.dr.setup.MacroLoadGroup;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.setup.service.LMSetupService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.LMCopyValidator;
import com.cannontech.web.api.dr.setup.LMDeleteValidator;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/macroLoadGroups")
public class MacroLoadGroupSetupApiController {

    @Autowired @Qualifier("macroLoadGroup") LMSetupService <MacroLoadGroup, LMCopy> macroLoadGroupService;
    @Autowired MacroLoadGroupValidator macroLoadGroupValidator;
    @Autowired LMDeleteValidator lmDeleteValidator;
    @Autowired LMCopyValidator lmCopyValidator;

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id, YukonUserContext userContext) {
        MacroLoadGroup loadGroup = macroLoadGroupService.retrieve(id, userContext.getYukonUser());
        return new ResponseEntity<>(loadGroup, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<Object> update(@PathVariable int id, @Valid @RequestBody MacroLoadGroup loadGroup,
            YukonUserContext userContext) {
        MacroLoadGroup updateLoadGroup = macroLoadGroupService.update(id, loadGroup, userContext.getYukonUser());
        return new ResponseEntity<>(updateLoadGroup, HttpStatus.OK);
    }

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody MacroLoadGroup loadGroup, YukonUserContext userContext) {
        MacroLoadGroup createLoadGroup = macroLoadGroupService.create(loadGroup, userContext.getYukonUser());
        return new ResponseEntity<>(createLoadGroup, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/copy")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> copy(@PathVariable int id, @Valid @RequestBody LMCopy lmCopy, YukonUserContext userContext) {
        MacroLoadGroup copiedMacroLoadGroup = macroLoadGroupService.copy(id, lmCopy, userContext.getYukonUser());
        return new ResponseEntity<>(copiedMacroLoadGroup, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@PathVariable int id, YukonUserContext userContext) {
        int paoId = macroLoadGroupService.delete(id, userContext.getYukonUser());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("id", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @InitBinder("LMCopy")
    public void setupBinderCopy(WebDataBinder binder) {
        binder.addValidators(lmCopyValidator);
    }

    @InitBinder("macroLoadGroup")
    public void setupBinderMacroLoadGroup(WebDataBinder binder) {
        binder.addValidators(macroLoadGroupValidator);
    }
}
