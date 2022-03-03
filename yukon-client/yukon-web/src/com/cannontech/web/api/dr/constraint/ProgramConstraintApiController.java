package com.cannontech.web.api.dr.constraint;

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

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.constraint.service.ProgramConstraintService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.setup.LMDeleteValidator;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/constraints")
public class ProgramConstraintApiController {

    @Autowired LMDeleteValidator lmDeleteValidator;
    @Autowired ProgramConstraintService programConstraintService;
    @Autowired ProgramConstraintValidator programConstraintValidator;

    @GetMapping("/{id}")
    public ResponseEntity<ProgramConstraint> retrieve(@PathVariable int id, YukonUserContext userContext) {
        return new ResponseEntity<>(programConstraintService.retrieve(id, userContext.getYukonUser()), HttpStatus.OK);
    }

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody ProgramConstraint programConstraint, YukonUserContext userContext) {
        ProgramConstraint createProgramConstraint = programConstraintService.create(programConstraint,
                userContext.getYukonUser());
        return new ResponseEntity<>(createProgramConstraint, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<Object> update(@PathVariable int id, @Valid @RequestBody ProgramConstraint programConstraint,
            YukonUserContext userContext) {
        ProgramConstraint updateProgramConstraint = programConstraintService.update(id, programConstraint,
                userContext.getYukonUser());
        return new ResponseEntity<>(updateProgramConstraint, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<HashMap<String, Integer>> delete(@PathVariable int id, YukonUserContext userContext) {
        Integer constraintId = programConstraintService.delete(id, userContext.getYukonUser());
        HashMap<String, Integer> constraintIdMap = new HashMap<>();
        constraintIdMap.put("id", constraintId);
        return new ResponseEntity<>(constraintIdMap, HttpStatus.OK);
    }

    @GetMapping("/seasonSchedules")
    public ResponseEntity<List<LMDto>> getSeasonSchedules() {
        return new ResponseEntity<>(programConstraintService.getSeasonSchedules(), HttpStatus.OK);
    }

    @GetMapping("/holidaySchedules")
    public ResponseEntity<List<LMDto>> getHolidaySchedules() {
        return new ResponseEntity<>(programConstraintService.getHolidaySchedules(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<LMDto>> getAllProgramConstraint() {
        return new ResponseEntity<>(programConstraintService.getAllProgramConstraint(), HttpStatus.OK);
    }

    @InitBinder("programConstraint")
    public void setupBinder(WebDataBinder binder) {
        binder.setValidator(programConstraintValidator);
    }

}
