package com.cannontech.web.api.trend;

import java.util.HashMap;
import java.util.Map;

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

import com.cannontech.common.trend.model.ResetPeakModel;
import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.trend.service.TrendService;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.security.annotation.CheckRole;

@RestController
@RequestMapping("/trends")
@CheckRole(YukonRole.TRENDING)

public class TrendApiController {
    
    @Autowired private TrendService trendService;
    @Autowired private TrendCreateValidator trendCreateValidator;
    @Autowired private TrendValidator trendValidator;
    @Autowired private ResetPeakApiValidator resetPeakApiValidator;

    @PostMapping
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_TRENDS, level = HierarchyPermissionLevel.CREATE)
    public ResponseEntity<Object> create(@Valid @RequestBody TrendModel trendModel, YukonUserContext userContext) {
        TrendModel createdTrend = trendService.create(trendModel, userContext.getYukonUser());
        return new ResponseEntity<>(createdTrend, HttpStatus.CREATED);
    }

    //Delete Trend
    @DeleteMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_TRENDS, level = HierarchyPermissionLevel.OWNER)
    public ResponseEntity<Object> delete(@PathVariable int id, YukonUserContext userContext) {
        int trendId = trendService.delete(id, userContext.getYukonUser());
        Map<String, Integer> trendIdMap = new HashMap<>();
        trendIdMap.put("id", trendId);
        return new ResponseEntity<>(trendIdMap, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_TRENDS, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<TrendModel> update(@PathVariable int id, @Valid @RequestBody TrendModel trendModel,
            YukonUserContext userContext) {
        TrendModel trend = trendService.update(id, trendModel, userContext.getYukonUser());
        return new ResponseEntity<>(trend, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrendModel> retrieve(@PathVariable int id) {
        TrendModel trend = trendService.retrieve(id);
        return new ResponseEntity<>(trend, HttpStatus.OK);
    }
    
    @PostMapping("/{id}/resetPeak")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_TRENDS, level = HierarchyPermissionLevel.UPDATE)
    public ResponseEntity<HashMap<String, Integer>> resetPeak(@PathVariable int id,
            @Valid @RequestBody ResetPeakModel resetPeakModel, YukonUserContext userContext) {
        Integer trendId = trendService.resetPeak(id, resetPeakModel, userContext.getYukonUser());
        HashMap<String, Integer> trendIdMap = new HashMap<>();
        trendIdMap.put("trendId", trendId);
        return new ResponseEntity<>(trendIdMap, HttpStatus.OK);
    }

    @InitBinder("trendModel")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(trendValidator);

        String trendId = ServletUtils.getPathVariable("id");
        if (trendId == null) {
            binder.addValidators(trendCreateValidator);
        }
    }

    @InitBinder("resetPeakModel")
    public void setupResetPeakBinder(WebDataBinder binder) {
        binder.addValidators(resetPeakApiValidator);
    }
}
