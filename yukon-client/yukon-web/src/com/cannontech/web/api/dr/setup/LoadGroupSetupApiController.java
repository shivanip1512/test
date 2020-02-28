package com.cannontech.web.api.dr.setup;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.dr.setup.ControlRawState;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.yukon.IDatabaseCache;

@RestController
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/dr/setup/loadGroup")
public class LoadGroupSetupApiController {
    
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DemandResponseEventLogService logService;
    @Autowired LoadGroupSetupService loadGroupService;
    @Autowired LMDeleteValidator lmDeleteValidator;
    @Autowired LMCopyValidator lmCopyValidator;
    private List<LoadGroupSetupValidator<? extends LoadGroupBase>> validators;
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        LoadGroupBase loadGroup = loadGroupService.retrieve(id);
        return new ResponseEntity<>(loadGroup, HttpStatus.OK);
    }

    @PostMapping("/create")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody LoadGroupBase loadGroup) {
        int paoId = loadGroupService.create(loadGroup);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("groupId", paoId);
        logService.loadGroupCreated(loadGroup.getName(), loadGroup.getType().getDbString(),
                ApiRequestContext.getContext().getLiteYukonUser());
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }
    
    @PostMapping("/update/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<Object> update(@Valid @RequestBody LoadGroupBase loadGroup, @PathVariable int id) {
        int paoId = loadGroupService.update(id, loadGroup);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("groupId", paoId);
        logService.loadGroupUpdated(loadGroup.getName(), loadGroup.getType().getDbString(),
                ApiRequestContext.getContext().getLiteYukonUser());
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @PostMapping("/copy/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> copy(@Valid @RequestBody LMCopy lmCopy, @PathVariable int id) {
        int paoId = loadGroupService.copy(id, lmCopy);
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("groupId", paoId);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@Valid @RequestBody LMDelete lmDelete, @PathVariable int id) {

        Optional<LiteYukonPAObject> liteLoadGroup = dbCache.getAllLMGroups()
                .stream()
                .filter(group -> group.getLiteID() == id)
                .findFirst();
        int paoId = loadGroupService.delete(id, lmDelete.getName());
        HashMap<String, Integer> paoIdMap = new HashMap<>();
        paoIdMap.put("groupId", paoId);
        logService.loadGroupDeleted(lmDelete.getName(), liteLoadGroup.get().getPaoType().getDbString(),
                ApiRequestContext.getContext().getLiteYukonUser());
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @GetMapping("/availableLoadGroup")
    public ResponseEntity<Object> retrieveAvailableLoadGroup() {
        List<LMPaoDto> availableLoadGroups = loadGroupService.retrieveAvailableLoadGroup();
        HashMap<String, List<LMPaoDto>> paoIdMap = new HashMap<>();
        paoIdMap.put("availableLoadGroups", availableLoadGroups);
        return new ResponseEntity<>(paoIdMap, HttpStatus.OK);
    }

    @InitBinder("loadGroupBase")
    public void setupBinder(WebDataBinder binder) {
        validators.stream().forEach(e -> {
            if (e.supports(binder.getTarget().getClass())) {
                binder.addValidators(e);
            }
        });
    }

    @InitBinder("LMDelete")
    public void setupBinderDelete(WebDataBinder binder) {
        binder.addValidators(lmDeleteValidator);
    }

    @InitBinder("LMCopy")
    public void setupBinderCopy(WebDataBinder binder) {
        binder.addValidators(lmCopyValidator);
    }

    @GetMapping("/getPointGroupStartState/{pointId}")
    public ResponseEntity<List<ControlRawState>> getPointGroupStartState(@PathVariable int pointId) {
        return new ResponseEntity<>(loadGroupService.getPointGroupStartState(pointId), HttpStatus.OK);
    }

    @Autowired
    void setValidators(List<LoadGroupSetupValidator<? extends LoadGroupBase>> validators) {
        this.validators = validators;
    }
}
