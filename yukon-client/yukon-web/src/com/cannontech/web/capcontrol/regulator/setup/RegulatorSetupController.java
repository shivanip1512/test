package com.cannontech.web.capcontrol.regulator.setup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingResult;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingTask;
import com.cannontech.web.capcontrol.regulator.setup.service.RegulatorMappingService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class RegulatorSetupController {
    
    @Autowired private IDatabaseCache dbCache;
    @Autowired private VoltageRegulatorService regulatorService;
    @Autowired private FileExporter fileExporter;
    @Autowired private RegulatorMappingService mappingService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DateFormattingService dateFormatting;
    @Autowired @Qualifier("regulatorMapping") private RecentResultsCache<RegulatorMappingTask> resultsCache;
    
    private static final String json = MediaType.APPLICATION_JSON_VALUE;
    
    @RequestMapping("regulator-setup")
    public String view(ModelMap model) {
        
        List<RegulatorMappingTask> tasks = mappingService.getAllTasks();
        Collections.sort(tasks);
        model.addAttribute("tasks", tasks);
        
        return "regulator/setup.jsp";
    }
    
    /** Recent regulator mapping tasks. */
    @RequestMapping(value="regulator-setup/map-attributes/recent", produces=json)
    public @ResponseBody List<Map<String, Object>> recent(YukonUserContext userContext) {
        
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        List<RegulatorMappingTask> recent = new ArrayList<>(resultsCache.getTasks().values());
        Collections.sort(recent);
        
        for (RegulatorMappingTask task : recent) {
            
            Map<String, Object> data = taskData(userContext, task);
            
            tasks.add(data);
        }
        
        return tasks;
    }
    
    /** Build task data as JSON ready map */
    private Map<String, Object> taskData(YukonUserContext userContext, RegulatorMappingTask task) {
        
        Map<String, Object> data = new HashMap<>();
        
        data.put("id", task.getTaskId());
        String start = dateFormatting.format(task.getStart(), DateFormatEnum.BOTH, userContext);
        data.put("start", start);
        data.put("user", task.getUserContext().getYukonUser().getUsername());
        data.put("completed", task.getCompleteCount());
        data.put("complete", task.isComplete());
        data.put("failed", task.getFailedCount());
        data.put("partial", task.getPartialSuccessCount());
        data.put("success", task.getSuccessCount());
        data.put("total", task.getRegulators().size());
        
        return data;
    }
    
    /** Build a mapping import file. */
    @RequestMapping(value = "regulator-setup/map-attributes/build", method = RequestMethod.POST, consumes = json, produces = json)
    public @ResponseBody Map<String, String> build(HttpServletResponse resp, YukonUserContext userContext,
            @RequestBody IdsContainer idsContainer) throws IOException {

        String key = fileExporter.build(resp, idsContainer.ids, userContext);
        return ImmutableMap.of("key", key);
    }
    
    /** Build a mapping import file. */
    @RequestMapping("regulator-setup/map-attributes/export/{key}")
    public void export(HttpServletResponse resp, YukonUserContext userContext, @PathVariable String key) {
        fileExporter.export(key, resp);
    }
    
    /** Start a mapping task. */
    @RequestMapping(value = "regulator-setup/map-attributes", method = RequestMethod.POST, produces = json, consumes = json)
    public @ResponseBody RegulatorMappingTask map(YukonUserContext userContext, @RequestBody IdsContainer idsContainer) {

        List<YukonPao> regulators = Lists.transform(idsContainer.ids, new Function<Integer, YukonPao>() {
            @Override
            public YukonPao apply(Integer id) {
                return dbCache.getAllPaosMap().get(id);
            }
            
        });
        String taskId = mappingService.start(regulators, userContext);
        
        RegulatorMappingTask task = mappingService.getTask(taskId);
        
        return task;
    }
    
    /** Delete a task. */
    @RequestMapping(value="regulator-setup/map-attributes/{taskId}", method=RequestMethod.DELETE)
    public void delete(HttpServletResponse resp, @PathVariable String taskId) {
        
        mappingService.delete(taskId);
        resp.setStatus(HttpStatus.NO_CONTENT.value());
        
    }
    
    @RequestMapping("regulator-setup/map-attributes/{taskId}/result/{regulatorId}")
    public @ResponseBody Map<String, Object> result(ModelMap model, YukonUserContext userContext, 
            @PathVariable String taskId, @PathVariable int regulatorId) {
        
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(regulatorId);
        RegulatorMappingTask task = mappingService.getTask(taskId);
        RegulatorMappingResult result = task.getResult(pao);
        Map<String, Object> data = mappingService.buildJsonResult(result, userContext);
        data.put("taskId", taskId);
        data.put("regulatorName", pao.getPaoName());
        
        return data;
    }
    
    @RequestMapping("regulator-setup/map-attributes/{taskId}/results")
    public String results(ModelMap model, YukonUserContext userContext, @PathVariable String taskId) {
        
        model.addAttribute("taskId", taskId);
        Map<Integer, LiteYukonPAObject> paos = dbCache.getAllPaosMap();
        
        RegulatorMappingTask task = mappingService.getTask(taskId);
        
        model.addAttribute("task", taskData(userContext, task));
        
        ArrayList<YukonPao> from = Lists.newArrayList(task.getRegulators());
        List<LiteYukonPAObject> regulators = Lists.transform(from, new Function<YukonPao, LiteYukonPAObject>() {
            @Override
            public LiteYukonPAObject apply(YukonPao input) {
                return paos.get(input.getPaoIdentifier().getPaoId());
            }
        });
        // The transformed list does not support sorting, copy it.
        regulators = new ArrayList<>(regulators);
        Collections.sort(regulators);
        model.addAttribute("regulators", regulators);
        
        LiteYukonPAObject pao = regulators.get(0);
        Regulator regulator = regulatorService.getRegulatorById(pao.getLiteID());
        Map<RegulatorPointMapping, Integer> sortedMappings =
                regulatorService.sortMappingsAllKeys(regulator.getMappings(), userContext);
        regulator.setMappings(sortedMappings);
        model.addAttribute("regulator",  regulator);
        
        return "regulator/mapping-results.jsp";
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IdsContainer {
        public List<Integer> ids;
    }
}