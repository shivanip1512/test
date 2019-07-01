package com.cannontech.web.api.dr.setup;

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

import com.cannontech.common.dr.setup.ControlScenarioBase;
import com.cannontech.common.dr.setup.ControlScenarioProgram;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.dr.loadgroup.service.ControlScenarioService;

@RestController
@RequestMapping("/dr/setup/controlScenario")
public class ControlScenarioSetupApiController {

    @Autowired private ControlScenarioService controlScenarioService;
    @Autowired private ControlScenarioSetupValidator controlScenarioSetupValidator;
    @Autowired LMDeleteValidator lmDeleteValidator;

    @GetMapping("/{id}")
    public ResponseEntity<ControlScenarioBase> retrieve(@PathVariable int id) {
        ControlScenarioBase controlScenario = controlScenarioService.retrieve(id);
        return new ResponseEntity<>(controlScenario, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<HashMap<String, Integer>> create(
            @Valid @RequestBody ControlScenarioBase controlScenarioBase) {
        int paoId = controlScenarioService.create(controlScenarioBase);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("paoId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<HashMap<String, Integer>> update(@Valid @RequestBody ControlScenarioBase controlScenarioBase,
            @PathVariable int id) {
        int paoId = controlScenarioService.update(id, controlScenarioBase);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("paoId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HashMap<String, Integer>> delete(@Valid @RequestBody LMDelete lmDelete,
            @PathVariable int id) {
        int paoId = controlScenarioService.delete(id, lmDelete.getName());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("paoId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @GetMapping("/availablePrograms")
    public ResponseEntity<List<ControlScenarioProgram>> getAvailablePrograms() {
        List<ControlScenarioProgram> programs = controlScenarioService.getAvailablePrograms();
        return new ResponseEntity<>(programs, HttpStatus.OK);
    }

    @InitBinder("controlScenarioBase")
    public void setupBinder(WebDataBinder binder) {
        binder.setValidator(controlScenarioSetupValidator);
    }

    @InitBinder("LMDelete")
    public void setupBinderDelete(WebDataBinder binder) {
        binder.addValidators(lmDeleteValidator);
    }
}
