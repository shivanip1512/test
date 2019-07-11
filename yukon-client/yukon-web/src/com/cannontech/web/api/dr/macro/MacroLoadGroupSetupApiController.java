package com.cannontech.web.api.dr.macro;

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

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.MacroLoadGroup;
import com.cannontech.dr.macro.service.impl.MacroLoadGroupSetupServiceImpl;
import com.cannontech.web.api.dr.setup.LMCopyValidator;
import com.cannontech.web.api.dr.setup.LMDeleteValidator;

@RestController
@RequestMapping("/dr/setup/macroLoadGroup")
public class MacroLoadGroupSetupApiController {

    @Autowired MacroLoadGroupSetupServiceImpl macroLoadGroupService;
    @Autowired MacroLoadGroupValidator macroLoadGroupValidator;
    @Autowired LMDeleteValidator lmDeleteValidator;
    @Autowired LMCopyValidator lmCopyValidator;

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        MacroLoadGroup loadGroup = macroLoadGroupService.retrieve(id);
        return new ResponseEntity<>(loadGroup, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody MacroLoadGroup loadGroup, @PathVariable int id) {
        int paoId = macroLoadGroupService.update(id, loadGroup);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("paoId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@Valid @RequestBody MacroLoadGroup loadGroup) {
        int paoId = macroLoadGroupService.create(loadGroup);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("paoId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/copy/{id}")
    public ResponseEntity<Object> copy(@Valid @RequestBody LMCopy lmCopy, @PathVariable int id) {
        int paoId = macroLoadGroupService.copy(id, lmCopy);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("paoId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@Valid @RequestBody LMDelete lmDelete, @PathVariable int id) {
        int paoId = macroLoadGroupService.delete(id, lmDelete.getName());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("paoId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    public void setupBinderDelete(WebDataBinder binder) {
        binder.addValidators(lmDeleteValidator);
    }

    @InitBinder("LMCopy")
    public void setupBinderCopy(WebDataBinder binder) {
        binder.addValidators(lmCopyValidator);
    }

    @InitBinder("macroLoadGroup")
    public void setupBinderMacroLoadGroup(WebDataBinder binder) {
        binder.addValidators(macroLoadGroupValidator);
    }
}
