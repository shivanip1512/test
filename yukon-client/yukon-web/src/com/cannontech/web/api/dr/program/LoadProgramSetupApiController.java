package com.cannontech.web.api.dr.program;

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

import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.dr.loadprogram.service.LoadProgramSetupService;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/loadPrograms")
public class LoadProgramSetupApiController {
    @Autowired private LoadProgramSetupService loadProgramService;
    @Autowired LMProgramCopyValidator lmProgramCopyValidator;
    @Autowired LMProgramValidator lmProgramValidator;

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        LoadProgram loadProgram = loadProgramService.retrieve(id);
        return new ResponseEntity<>(loadProgram, HttpStatus.OK);
    }

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody LoadProgram loadProgram) {
        LoadProgram createLoadProgram = loadProgramService.create(loadProgram);
        return new ResponseEntity<>(createLoadProgram, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@PathVariable int id) {
        int paoId = loadProgramService.delete(id);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("id", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/{id}/copy")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> copy(@Valid @RequestBody LoadProgramCopy loadProgramCopy, @PathVariable int id) {
        int paoId = loadProgramService.copy(id, loadProgramCopy);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("programId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<Object> update(@Valid @RequestBody LoadProgram loadProgram, @PathVariable int id) {
        LoadProgram updateLoadProgram = loadProgramService.update(id, loadProgram);
        return new ResponseEntity<>(updateLoadProgram, HttpStatus.OK);
    }

    @GetMapping("/allAvailableNotificationGroups")
    public ResponseEntity<Object> getAllAvailableProgramNotificationGroups() {
        List<NotificationGroup> notificationGroups = loadProgramService.getAllAvailableProgramNotificationGroups();
        return new ResponseEntity<>(notificationGroups, HttpStatus.OK);
    }
    
    @GetMapping("/gears/{id}")
    public ResponseEntity<Object> getProgramGear(@PathVariable Integer id) {
        ProgramGear programGear = loadProgramService.getProgramGear(id);
        return new ResponseEntity<>(programGear, HttpStatus.OK);
    }

    @GetMapping("/allAvailableDirectMemberControls")
    @CheckRoleProperty(YukonRoleProperty.ALLOW_MEMBER_PROGRAMS)
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

    @GetMapping("/{id}/loadGroups/available")
    public ResponseEntity<Object> getAvailableProgramLoadGroups(@PathVariable int id) {
        List<ProgramGroup> programGroups = loadProgramService.getAvailableProgramLoadGroups(id);
        return new ResponseEntity<>(programGroups, HttpStatus.OK);
    }

    @GetMapping("/{id}/notificationGroups/available")
    public ResponseEntity<Object> getAvailableProgramNotificationGroups(@PathVariable int id) {
        List<NotificationGroup> notificationGroups =
            loadProgramService.getAvailableProgramNotificationGroups(id);
        return new ResponseEntity<>(notificationGroups, HttpStatus.OK);
    }

    
    @GetMapping("/{id}/directMemberControls/available")
    @CheckRoleProperty(YukonRoleProperty.ALLOW_MEMBER_PROGRAMS)
    public ResponseEntity<Object> getAvailableDirectMemberControls(@PathVariable int id) {
        List<ProgramDirectMemberControl> directMemberControls =
            loadProgramService.getAvailableDirectMemberControls(id);
        return new ResponseEntity<>(directMemberControls, HttpStatus.OK);
    }

    @GetMapping("/availablePrograms")
    public ResponseEntity<List<ProgramDetails>> getAvailablePrograms() {
        return new ResponseEntity<>(loadProgramService.getAvailablePrograms(), HttpStatus.OK);
    }

    @GetMapping("/{id}/gears")
    public ResponseEntity<List<LiteGear>> getGearsForProgram(@PathVariable int id) {
        return new ResponseEntity<>(loadProgramService.getGearsForProgram(id), HttpStatus.OK);
    }

    @InitBinder("loadProgramCopy")
    public void setupBinderProgramCopy(WebDataBinder binder) {
        binder.addValidators(lmProgramCopyValidator);
    }
    
    @InitBinder("loadProgram")
    public void setupBinderProgramCreation(WebDataBinder binder) {
        binder.addValidators(lmProgramValidator);
    }
}
