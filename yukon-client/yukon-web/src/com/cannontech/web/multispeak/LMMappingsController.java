package com.cannontech.web.multispeak;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.multispeak.dao.MspLMInterfaceMappingDao;
import com.cannontech.multispeak.db.MspLMInterfaceMapping;
import com.cannontech.multispeak.db.MspLMInterfaceMappingStrategyNameComparator;
import com.cannontech.multispeak.db.MspLmInterfaceMappingColumnEnum;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.security.annotation.CheckGlobalSetting;

@CheckGlobalSetting(GlobalSettingType.MSP_LM_MAPPING_SETUP)
@Controller
@RequestMapping("setup/lmMappings/*")
public class LMMappingsController {

    @Autowired private PaoDao paoDao;
    @Autowired private MspLMInterfaceMappingDao mspLMInterfaceMappingDao;

    private static final MspLmInterfaceMappingColumnEnum defaultOrderedColumn = MspLmInterfaceMappingColumnEnum.STRATEGY;
    private static final boolean defaultAscending = true;

    @RequestMapping("home")
    public String home(ModelMap model) {

        addAllMapppingToModel(model, defaultOrderedColumn, defaultAscending);

        return "setup/lmMappings/home.jsp";
    }

    @RequestMapping("findMapping")
    public @ResponseBody Map<String, String> findMapping(ModelMap model, String strategyName, String substationName) {

        Map<String,String> result = new HashMap<>();

        String mappedName = findMappedName(strategyName, substationName);

        result.put("mappedName", mappedName);

        return result;
    }

    @RequestMapping("addOrUpdateMapping")
    public @ResponseBody Map<String,Object> addOrUpdateMapping(String strategyName, String substationName, Integer mappedNameId) {

        Map<String,Object> response = new HashMap<>();

        Integer existingMspLmInterfaceid = mspLMInterfaceMappingDao.findIdForStrategyAndSubstation(strategyName, substationName);

        if (mappedNameId == null) {
            response.put("error", "mappedNameId not found");
        } else if (mappedNameId <= 0) {
            response.put("error", "mappedNameId must be positive");
        } else {
            if (existingMspLmInterfaceid == null) {
                response.put("action", "add");
                boolean added = mspLMInterfaceMappingDao.add(strategyName, substationName, mappedNameId);
                response.put("success", added);
            } else {
                response.put("action", "update");
                boolean updated = mspLMInterfaceMappingDao.updatePaoIdForStrategyAndSubstation(strategyName, substationName, mappedNameId);
                response.put("success", updated);
            }
        }
        return response;
    }

    @RequestMapping("reloadAllMappingsTable")
    public String reloadAllMappingsTable(ModelMap model, MspLmInterfaceMappingColumnEnum col, Boolean ascending) {

        if (col == null) col = defaultOrderedColumn;
        if (ascending == null) ascending = defaultAscending;

        addAllMapppingToModel(model, col, ascending);

        return "setup/lmMappings/allMappingsTable.jsp";
    }

    @RequestMapping("removeMapping")
    public @ResponseBody Map<String,Object> removeMapping(int mspLMInterfaceMappingId) {

        Map<String,Object> result = new HashMap<>();

        boolean successful = mspLMInterfaceMappingDao.remove(mspLMInterfaceMappingId);

        result.put("success", successful);
        return result;
    }

    // returns null if mapping is not found or mapped paoId is not found
    private String findMappedName(String strategyName, String substationName) {

        String mappedName = null;
        try{
            MspLMInterfaceMapping mapping = mspLMInterfaceMappingDao.getForStrategyAndSubstation(strategyName, substationName);
            int paoId = mapping.getPaobjectId();
            mappedName = paoDao.getYukonPAOName(paoId);
        } catch (NotFoundException e) {
        }
        return mappedName;
    }

    private void addAllMapppingToModel(ModelMap model, MspLmInterfaceMappingColumnEnum col, boolean ascending) {

        List<MspLMInterfaceMapping> allMappings = mspLMInterfaceMappingDao.getAllMappings();
        Collections.sort(allMappings, new MspLMInterfaceMappingStrategyNameComparator(col, ascending));

        model.addAttribute("orderedColumnName", col.toString());
        model.addAttribute("ascending", ascending);
        model.addAttribute("allMappings", allMappings);
    }
}