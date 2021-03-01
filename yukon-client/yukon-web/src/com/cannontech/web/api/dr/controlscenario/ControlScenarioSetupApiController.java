package com.cannontech.web.api.dr.controlscenario;

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

import com.cannontech.common.dr.setup.ControlScenario;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.setup.service.LMSetupService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/controlScenarios")
public class ControlScenarioSetupApiController {

    @Autowired @Qualifier("controlScenario") private LMSetupService <ControlScenario, LMCopy> controlScenarioService;
    @Autowired private ControlScenarioSetupValidator controlScenarioSetupValidator;

    @GetMapping("/{id}")
    public ResponseEntity<ControlScenario> retrieve(@PathVariable int id, YukonUserContext userContext) {
        return new ResponseEntity<>(controlScenarioService.retrieve(id, userContext.getYukonUser()), HttpStatus.OK);
    }

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody ControlScenario controlScenario, YukonUserContext userContext) {
        ControlScenario createControlScenario = controlScenarioService.create(controlScenario, userContext.getYukonUser());
        return new ResponseEntity<>(createControlScenario, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<Object> update(@PathVariable int id, @Valid @RequestBody ControlScenario controlScenario,
            YukonUserContext userContext) {
        ControlScenario updateControlScenario = controlScenarioService.update(id, controlScenario, userContext.getYukonUser());
        return new ResponseEntity<>(updateControlScenario, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<HashMap<String, Integer>> delete(@PathVariable int id, YukonUserContext userContext) {
        int paoId = controlScenarioService.delete(id, userContext.getYukonUser());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("id", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @InitBinder("controlScenario")
    public void setupBinder(WebDataBinder binder) {
        binder.setValidator(controlScenarioSetupValidator);
    }
}
