package com.cannontech.web.api.dr.program;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

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

import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.loadprogram.service.LoadProgramSetupService;
import com.cannontech.web.api.dr.setup.LMDeleteValidator;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RestController
@CheckRole(YukonRole.DEMAND_RESPONSE)
@RequestMapping("/dr/setup/loadProgram")
public class LoadProgramSetupApiController {
    @Autowired private LoadProgramSetupService loadProgramService;
    @Autowired LMDeleteValidator lmDeleteValidator;
    @Autowired LMProgramCopyValidator lmProgramCopyValidator;

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

    @PostMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@RequestBody LMDelete lmDelete, @PathVariable int id) {
        int paoId = loadProgramService.delete(id, lmDelete.getName());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("programId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/copy/{id}")
    public ResponseEntity<Object> copy(@Valid @RequestBody LoadProgramCopy loadProgramCopy, @PathVariable int id) {
        int paoId = loadProgramService.copy(id, loadProgramCopy);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("programId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Object> update(@RequestBody LoadProgram loadProgram, @PathVariable int id) {
        int paoId = loadProgramService.update(id, loadProgram);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("programId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @GetMapping("/allAvailableNotificationGroups")
    public ResponseEntity<Object> getAllAvailableProgramNotificationGroups() {
        List<NotificationGroup> notificationGroups = loadProgramService.getAllAvailableProgramNotificationGroups();
        return new ResponseEntity<>(notificationGroups, HttpStatus.OK);
    }

    @CheckRoleProperty(YukonRoleProperty.ALLOW_MEMBER_PROGRAMS)
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

    @GetMapping("/availableLoadGroups/{id}")
    public ResponseEntity<Object> getAvailableProgramLoadGroups(@PathVariable int id) {
        List<ProgramGroup> programGroups = loadProgramService.getAvailableProgramLoadGroups(id);
        return new ResponseEntity<>(programGroups, HttpStatus.OK);
    }

    @GetMapping("/availableNotificationGroups/{id}")
    public ResponseEntity<Object> getAvailableProgramNotificationGroups(@PathVariable int id) {
        List<NotificationGroup> notificationGroups =
            loadProgramService.getAvailableProgramNotificationGroups(id);
        return new ResponseEntity<>(notificationGroups, HttpStatus.OK);
    }

    @CheckRoleProperty(YukonRoleProperty.ALLOW_MEMBER_PROGRAMS)
    @GetMapping("/availableDirectMemberControls/{id}")
    public ResponseEntity<Object> getAvailableDirectMemberControls(@PathVariable int id) {
        List<ProgramDirectMemberControl> directMemberControls =
            loadProgramService.getAvailableDirectMemberControls(id);
        return new ResponseEntity<>(directMemberControls, HttpStatus.OK);
    }

    @GetMapping("/availablePrograms")
    public ResponseEntity<List<ProgramDetails>> getAvailablePrograms() {
        return new ResponseEntity<>(loadProgramService.getAvailablePrograms(), HttpStatus.OK);
    }
    
    @InitBinder("LMDelete")
    public void setupBinderDelete(WebDataBinder binder) {
        binder.addValidators(lmDeleteValidator);
    }
    
    @InitBinder("loadProgramCopy")
    public void setupBinderCopy(WebDataBinder binder) {
        binder.addValidators(lmProgramCopyValidator);
    }
}
