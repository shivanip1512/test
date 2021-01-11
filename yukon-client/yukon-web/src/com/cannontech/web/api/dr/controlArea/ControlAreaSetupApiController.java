package com.cannontech.web.api.dr.controlArea;

import java.util.HashMap;
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

import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.area.service.ControlAreaSetupService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/controlAreas")
public class ControlAreaSetupApiController {

    @Autowired private ControlAreaSetupService controlAreaService;
    @Autowired private ControlAreaSetupValidator controlAreaSetupValidator;

    @GetMapping("/{id}")
    public ResponseEntity<ControlArea> retrieve(@PathVariable int id, YukonUserContext userContext) {
        return new ResponseEntity<>(controlAreaService.retrieve(id, userContext.getYukonUser()), HttpStatus.OK);
    }

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody ControlArea controlArea, YukonUserContext userContext) {
        ControlArea createControlArea = controlAreaService.create(controlArea, userContext.getYukonUser());
        return new ResponseEntity<>(createControlArea, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<Object> update(@PathVariable int id, @Valid @RequestBody ControlArea controlArea,
            YukonUserContext userContext) {
        ControlArea updateControlArea = controlAreaService.update(id, controlArea, userContext.getYukonUser());
        return new ResponseEntity<>(updateControlArea, HttpStatus.OK);
    }

    @DeleteMapping("/{controlAreaId}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@PathVariable int controlAreaId, YukonUserContext userContext) {
        int areaId = controlAreaService.delete(controlAreaId, userContext.getYukonUser());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("id", areaId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @InitBinder("controlArea")
    public void setupBinder(WebDataBinder binder) {
        binder.setValidator(controlAreaSetupValidator);
    }
    
}
