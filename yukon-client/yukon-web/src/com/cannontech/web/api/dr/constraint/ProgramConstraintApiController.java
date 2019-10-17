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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.constraint.service.ProgramConstraintService;
import com.cannontech.web.api.dr.setup.LMDeleteValidator;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/setup/constraint")
public class ProgramConstraintApiController {

    @Autowired LMDeleteValidator lmDeleteValidator;
    @Autowired ProgramConstraintService programConstraintService;
    @Autowired ProgramConstraintValidator programConstraintValidator;

    @GetMapping("/{id}")
    public ResponseEntity<ProgramConstraint> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(programConstraintService.retrieve(id), HttpStatus.OK);
    }

    @PostMapping("/create")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<HashMap<String, Integer>> create(@Valid @RequestBody ProgramConstraint programConstraint) {
        Integer constraintId = programConstraintService.create(programConstraint);
        HashMap<String, Integer> constraintIdMap = new HashMap<>();
        constraintIdMap.put("id", constraintId);
        return new ResponseEntity<>(constraintIdMap, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<HashMap<String, Integer>> update(@Valid @RequestBody ProgramConstraint programConstraint,
            @PathVariable int id) {
        Integer constraintId = programConstraintService.update(id, programConstraint);
        HashMap<String, Integer> constraintIdMap = new HashMap<>();
        constraintIdMap.put("id", constraintId);
        return new ResponseEntity<>(constraintIdMap, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<HashMap<String, Integer>> delete(@Valid @RequestBody LMDelete lmDelete,
            @PathVariable int id) {
        Integer constraintId = programConstraintService.delete(id, lmDelete.getName());
        HashMap<String, Integer> constraintIdMap = new HashMap<>();
        constraintIdMap.put("id", constraintId);
        return new ResponseEntity<>(constraintIdMap, HttpStatus.OK);
    }

    @GetMapping("/getSeasonSchedules")
    public ResponseEntity<List<LMDto>> getSeasonSchedules() {
        return new ResponseEntity<>(programConstraintService.getSeasonSchedules(), HttpStatus.OK);
    }

    @GetMapping("/getHolidaySchedules")
    public ResponseEntity<List<LMDto>> getHolidaySchedules() {
        return new ResponseEntity<>(programConstraintService.getHolidaySchedules(), HttpStatus.OK);
    }

    @GetMapping("/getAllProgramConstraint")
    public ResponseEntity<List<LMDto>> getAllProgramConstraint() {
        return new ResponseEntity<>(programConstraintService.getAllProgramConstraint(), HttpStatus.OK);
    }

    @InitBinder("LMDelete")
    public void setupBinderDelete(WebDataBinder binder) {
        binder.setValidator(lmDeleteValidator);
    }

    @InitBinder("programConstraint")
    public void setupBinder(WebDataBinder binder) {
        binder.setValidator(programConstraintValidator);
    }

}
