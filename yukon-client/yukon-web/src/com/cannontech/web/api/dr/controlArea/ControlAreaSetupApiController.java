package com.cannontech.web.api.dr.controlArea;

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

import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.area.service.ControlAreaSetupService;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/setup/controlArea")
public class ControlAreaSetupApiController {

    @Autowired private ControlAreaSetupService controlAreaService;
    @Autowired private ControlAreaSetupValidator controlAreaSetupValidator;

    @GetMapping("/{id}")
    public ResponseEntity<ControlArea> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(controlAreaService.retrieve(id), HttpStatus.OK);
    }

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<HashMap<String, Integer>> create(@Valid @RequestBody ControlArea controlArea) {
        int controlAreaId = controlAreaService.create(controlArea);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("controlAreaId", controlAreaId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<HashMap<String, Integer>> update(@Valid @RequestBody ControlArea controlArea,
            @PathVariable int id) {
        int controlAreaId = controlAreaService.update(id, controlArea);
        return buildResponse(controlAreaId);
    }

    @DeleteMapping("/{controlAreaId}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@PathVariable int controlAreaId) {
        int areaId = controlAreaService.delete(controlAreaId);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("id", areaId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @GetMapping("/unAssignedPrograms")
    public ResponseEntity<List<LMDto>> getAvailablePrograms() {
        List<LMDto> programs = controlAreaService.retrieveUnassignedPrograms();
        return new ResponseEntity<>(programs, HttpStatus.OK);
    }

    @GetMapping("/normalState/{pointId}")
    public ResponseEntity<Object> getNormalState(@PathVariable int pointId) {
        List<LMDto> normalStates = controlAreaService.retrieveNormalState(pointId);
        return new ResponseEntity<>(normalStates, HttpStatus.OK);
    }

    private ResponseEntity<HashMap<String, Integer>> buildResponse(int controlAreaId) {
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("controlAreaId", controlAreaId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }
    
    @InitBinder("controlArea")
    public void setupBinder(WebDataBinder binder) {
        binder.setValidator(controlAreaSetupValidator);
    }
    
}
