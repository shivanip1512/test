package com.cannontech.web.api.dr.controlArea;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.dr.area.service.ControlAreaSetupService;
import com.cannontech.web.security.annotation.CheckRole;

@RestController
@CheckRole(YukonRole.DEMAND_RESPONSE)
@RequestMapping("/dr/setup/controlArea")
public class ControlAreaSetupApiController {

    @Autowired private ControlAreaSetupService controlAreaService;

    @GetMapping("/{id}")
    public ResponseEntity<ControlArea> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(controlAreaService.retrieve(id), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<HashMap<String, Integer>> create(@Valid @RequestBody ControlArea controlArea) {
        int controlAreaId = controlAreaService.create(controlArea);
        return buildResponse(controlAreaId);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<HashMap<String, Integer>> update(@Valid @RequestBody ControlArea controlArea,
            @PathVariable int id) {
        int controlAreaId = controlAreaService.update(id, controlArea);
        return buildResponse(controlAreaId);
    }

    @PostMapping("/delete/{controlAreaId}")
    public ResponseEntity<HashMap<String, Integer>> delete(@Valid @RequestBody LMDelete lmDelete,
            @PathVariable int controlAreaId) {
        int areaId = controlAreaService.delete(controlAreaId, lmDelete.getName());
        return buildResponse(areaId);
    }

    @GetMapping("/unAssignedPrograms")
    public ResponseEntity<List<LMDto>> getAvailablePrograms() {
        List<LMDto> programs = controlAreaService.retrieveUnassignedPrograms();
        return new ResponseEntity<>(programs, HttpStatus.OK);
    }

    @GetMapping("/normalState/{pointId}")
    public ResponseEntity<Object> getNormalState(@PathVariable int pointId) {
        List<LMDto> normalStates = controlAreaService.retrieveNormalState(pointId);
        HashMap<Integer, List<LMDto>> normalStateMap = new HashMap<>();
        normalStateMap.put(pointId, normalStates);
        return new ResponseEntity<>(normalStateMap, HttpStatus.OK);
    }

    private ResponseEntity<HashMap<String, Integer>> buildResponse(int controlAreaId) {
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("controlAreaId", controlAreaId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }
}
