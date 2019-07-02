package com.cannontech.web.api.dr.setup;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.loadprogram.service.LoadProgramSetupService;

@RestController
@RequestMapping("/dr/setup/loadProgram")
public class LoadProgramSetupApiController {
    @Autowired private LoadProgramSetupService loadProgramService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        LoadProgram loadProgram = loadProgramService.retrieve(id);
        return new ResponseEntity<>(loadProgram, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody LoadProgram loadProgram) {
        int paoId = loadProgramService.create(loadProgram);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("programId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/delete/{programId}")
    public ResponseEntity<Object> delete(@RequestBody LMDelete lmDelete, @PathVariable int programId) {
        int paoId = loadProgramService.delete(programId, lmDelete.getName());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("programId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/copy/{programId}")
    public ResponseEntity<Object> copy(@RequestBody LoadProgramCopy loadProgramCopy, @PathVariable int programId) {
        int paoId = loadProgramService.copy(programId, loadProgramCopy);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("programId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/update/{programId}")
    public ResponseEntity<Object> update(@RequestBody LoadProgram loadProgram, @PathVariable int programId) {
        int paoId = loadProgramService.update(programId, loadProgram);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("programId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @GetMapping("/allAvailableNotificationGroups")
    public ResponseEntity<Object> getAllAvailableProgramNotificationGroups() {
        List<NotificationGroup> notificationGroups = loadProgramService.getAllAvailableProgramNotificationGroups();
        return new ResponseEntity<>(notificationGroups, HttpStatus.OK);
    }

    @GetMapping("/allAvailableDirectMemberControls")
    public ResponseEntity<Object> getAllAvailableDirectMemberControls() {
        List<ProgramDirectMemberControl> directMemberControls =
            loadProgramService.getAllAvailableDirectMemberControls();
        return new ResponseEntity<>(directMemberControls, HttpStatus.OK);
    }

    @GetMapping("/allAvailableLoadGroups/{programType}")
    public ResponseEntity<Object> getAllAvailableProgramLoadGroups(@PathVariable PaoType programType) {
        List<ProgramGroup> programGroups = loadProgramService.getAllAvailableProgramLoadGroups(programType);
        return new ResponseEntity<>(programGroups, HttpStatus.OK);
    }

    @GetMapping("/availableLoadGroups/{programId}")
    public ResponseEntity<Object> getAvailableProgramLoadGroups(@PathVariable int programId) {
        List<ProgramGroup> programGroups = loadProgramService.getAvailableProgramLoadGroups(programId);
        return new ResponseEntity<>(programGroups, HttpStatus.OK);
    }

    @GetMapping("/availableNotificationGroups/{programId}")
    public ResponseEntity<Object> getAvailableProgramNotificationGroups(@PathVariable int programId) {
        List<NotificationGroup> notificationGroups =
            loadProgramService.getAvailableProgramNotificationGroups(programId);
        return new ResponseEntity<>(notificationGroups, HttpStatus.OK);
    }

    @GetMapping("/availableDirectMemberControls/{programId}")
    public ResponseEntity<Object> getAvailableDirectMemberControls(@PathVariable int programId) {
        List<ProgramDirectMemberControl> directMemberControls =
            loadProgramService.getAvailableDirectMemberControls(programId);
        return new ResponseEntity<>(directMemberControls, HttpStatus.OK);
    }

}
