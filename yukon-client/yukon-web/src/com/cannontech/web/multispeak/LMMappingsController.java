package com.cannontech.web.multispeak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.multispeak.dao.MspLmInterfaceMappingDao;
import com.cannontech.multispeak.db.MspLmMapping;
import com.cannontech.multispeak.db.MspLmMappingColumn;
import com.cannontech.multispeak.db.MspLmMappingComparator;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckGlobalSetting;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableMap;

@CheckGlobalSetting(GlobalSettingType.MSP_LM_MAPPING_SETUP)
@Controller
@RequestMapping("setup/lmMappings/*")
public class LMMappingsController {

    @Autowired private IDatabaseCache databaseCache;
    @Autowired private MspLmInterfaceMappingDao mspLMMappingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @RequestMapping("home")
    public String home(ModelMap model, YukonUserContext userContext) {

        addAllMapppingToModel(model, SortingParameters.of(MspLmMappingColumn.STRATEGY.name(), Direction.desc), userContext);

        return "setup/lmMappings/home.jsp";
    }

    @RequestMapping("find-mapping")
    public @ResponseBody Map<String, ? extends Object> findMapping(ModelMap model, String strategyName, String substationName) {

        String mappedName = findMappedName(strategyName, substationName);

        if (mappedName != null) {
            return ImmutableMap.of("found", true, "mappedName", mappedName);
        } else {
            return ImmutableMap.of("found", false);
        }
    }
    
    @RequestMapping("find-mappingId")
    public @ResponseBody Map<String, ? extends Object> findMappingId(ModelMap model, String strategyName, String substationName) {
        
        try {
            MspLmMapping mapping = mspLMMappingDao.getForStrategyAndSubstation(strategyName, substationName);
            return ImmutableMap.of("found", true, "mappedNameId", mapping.getPaobjectId());
        } catch (NotFoundException e) {
            return ImmutableMap.of("found", false);
        }
    }

    @RequestMapping("addOrUpdateMapping")
    public @ResponseBody Map<String,Object> addOrUpdateMapping(String strategyName, String substationName, Integer mappedNameId) {

        Map<String,Object> response = new HashMap<>();

        Integer existingMspLmInterfaceid = mspLMMappingDao.findIdForStrategyAndSubstation(strategyName, substationName);

        if (mappedNameId == null) {
            response.put("error", "mappedNameId not found");
        } else if (mappedNameId <= 0) {
            response.put("error", "mappedNameId must be positive");
        } else if (StringUtils.length(strategyName) > 100 || StringUtils.length(strategyName) < 1) {
            response.put("error", "Strategy name must be between 1 and 100 characters");
        } else if (StringUtils.length(substationName) > 100 || StringUtils.length(substationName) < 1) {
            response.put("error", "Substation name must be between 1 and 100 characters");
        } else {
            if (existingMspLmInterfaceid == null) {
                response.put("action", "add");
                boolean added = mspLMMappingDao.add(strategyName, substationName, mappedNameId);
                response.put("success", added);
            } else {
                response.put("action", "update");
                boolean updated = mspLMMappingDao.updatePaoIdForStrategyAndSubstation(strategyName, substationName, mappedNameId);
                response.put("success", updated);
            }
        }
        return response;
    }
    
    @RequestMapping("updateMappingById")
    public @ResponseBody Map<String, Object> updateMappingById(Integer mappingId, String strategyName, String substationName, Integer mappedNameId){
        Map<String,Object> response = new HashMap<>();
        Integer existingMspLmInterfaceid = mspLMMappingDao.findIdForStrategyAndSubstation(strategyName, substationName);

        if (mappedNameId == null) {
            response.put("error", "mappedNameId not found");
        } else if (mappedNameId <= 0) {
            response.put("error", "mappedNameId must be greater than 0");
        } else if (mappingId == null) {
            response.put("error", "mappingId not found");
        } else if (existingMspLmInterfaceid != null && existingMspLmInterfaceid.intValue() != mappingId.intValue()) {
            response.put("error", "Selected strategy name and substation name already exist.");
        } else {
            response.put("action", "update");
            boolean updated = mspLMMappingDao.updateMappingById(mappingId, strategyName, substationName, mappedNameId);
            response.put("success", updated);
        }
    	return response;
    }

    @RequestMapping("reloadAllMappingsTable")
    public String reloadAllMappingsTable(ModelMap model, 
            @DefaultSort(dir=Direction.desc, sort="STRATEGY") SortingParameters sorting,
            YukonUserContext userContext) {

        addAllMapppingToModel(model, sorting, userContext);

        return "setup/lmMappings/allMappingsTable.jsp";
    }

    @RequestMapping("removeMapping")
    public @ResponseBody Map<String,Object> removeMapping(int mspLMInterfaceMappingId) {

        Map<String,Object> result = new HashMap<>();

        boolean successful = mspLMMappingDao.remove(mspLMInterfaceMappingId);

        result.put("success", successful);
        return result;
    }

    // returns null if mapping is not found or mapped paoId is not found
    private String findMappedName(String strategyName, String substationName) {

        String mappedName = null;
        try{
            MspLmMapping mapping = mspLMMappingDao.getForStrategyAndSubstation(strategyName, substationName);
            int paoId = mapping.getPaobjectId();
            mappedName = databaseCache.getAllPaosMap().get(paoId).getPaoName();
        } catch (NotFoundException e) {
        }
        return mappedName;
    }

    private void addAllMapppingToModel(ModelMap model, SortingParameters sorting, YukonUserContext userContext) {

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        List<SortableColumn> columns = new ArrayList<>();

        for (MspLmMappingColumn column : MspLmMappingColumn.values()) {
            String resolvedName = accessor.getMessage(column);
            columns.add( SortableColumn.of(sorting, resolvedName, column.name()));
        }

        model.addAttribute("columns", columns);

        List<MspLmMapping> allMappings = mspLMMappingDao.getAllMappings();

        MspLmMappingColumn column = MspLmMappingColumn.valueOf(sorting.getSort());
        boolean asc = sorting.getDirection().equals(Direction.asc);
        Collections.sort(allMappings, new MspLmMappingComparator(column, asc));

        model.addAttribute("allMappings", allMappings);
    }
}