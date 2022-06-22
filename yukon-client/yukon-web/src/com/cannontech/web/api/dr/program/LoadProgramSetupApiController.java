package com.cannontech.web.api.dr.program;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.core.Logger;
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

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.dr.loadprogram.service.LoadProgramSetupService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/loadPrograms")
public class LoadProgramSetupApiController {
    private static final Logger log = YukonLogManager.getLogger(LoadProgramSetupApiController.class);
    
    @Autowired private LoadProgramSetupService loadProgramService;
    @Autowired LMProgramCopyValidator lmProgramCopyValidator;
    @Autowired LMProgramValidator lmProgramValidator;

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id, YukonUserContext userContext) {
        LoadProgram loadProgram = loadProgramService.retrieve(id, userContext.getYukonUser());
        return new ResponseEntity<>(loadProgram, HttpStatus.OK);
    }

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody LoadProgram loadProgram, YukonUserContext userContext) {
        LoadProgram createLoadProgram = loadProgramService.create(loadProgram, userContext.getYukonUser());
        return new ResponseEntity<>(createLoadProgram, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@PathVariable int id, YukonUserContext userContext) {
        int paoId = loadProgramService.delete(id, userContext.getYukonUser());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("id", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/{id}/copy")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> copy(@PathVariable int id, @Valid @RequestBody LoadProgramCopy loadProgramCopy,
            YukonUserContext userContext) {
        log.info("copy start path id: {}", id);
        LoadProgram copiedLoadProgram = loadProgramService.copy(id, loadProgramCopy, userContext.getYukonUser());
        log.info("copy end copied program id: {}", copiedLoadProgram.getProgramId());
        return new ResponseEntity<>(copiedLoadProgram, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<Object> update(@PathVariable int id, @Valid @RequestBody LoadProgram loadProgram,
            YukonUserContext userContext) {
        LoadProgram updateLoadProgram = loadProgramService.update(id, loadProgram, userContext.getYukonUser());
        return new ResponseEntity<>(updateLoadProgram, HttpStatus.OK);
    }
    
   @GetMapping("/gears/{id}")
    public ResponseEntity<Object> getProgramGear(@PathVariable Integer id) {
        ProgramGear programGear = loadProgramService.getProgramGear(id);
        return new ResponseEntity<>(programGear, HttpStatus.OK);
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
