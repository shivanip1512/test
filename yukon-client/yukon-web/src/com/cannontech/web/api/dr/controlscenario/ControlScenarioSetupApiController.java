package com.cannontech.web.api.dr.controlscenario;

import java.util.HashMap;
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

import com.cannontech.common.dr.setup.ControlScenario;
import com.cannontech.web.api.dr.setup.LMDeleteValidator;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.dr.controlscenario.service.ControlScenarioService;

@RestController
@RequestMapping("/dr/setup/controlScenario")
public class ControlScenarioSetupApiController {

    @Autowired private ControlScenarioService controlScenarioService;
    @Autowired private ControlScenarioSetupValidator controlScenarioSetupValidator;
    @Autowired private LMDeleteValidator lmDeleteValidator;

    @GetMapping("/{id}")
    public ResponseEntity<ControlScenario> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(controlScenarioService.retrieve(id), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<HashMap<String, Integer>> create(
            @Valid @RequestBody ControlScenario controlScenario) {
        int paoId = controlScenarioService.create(controlScenario);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("paoId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<HashMap<String, Integer>> update(@Valid @RequestBody ControlScenario controlScenario,
            @PathVariable int id) {
        int paoId = controlScenarioService.update(id, controlScenario);
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

    @InitBinder("controlScenario")
    public void setupBinder(WebDataBinder binder) {
        binder.setValidator(controlScenarioSetupValidator);
    }

    @InitBinder("LMDelete")
    public void setupBinderDelete(WebDataBinder binder) {
        binder.addValidators(lmDeleteValidator);
    }
}
