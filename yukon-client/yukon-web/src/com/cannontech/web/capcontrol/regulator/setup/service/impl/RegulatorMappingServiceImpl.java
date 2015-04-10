package com.cannontech.web.capcontrol.regulator.setup.service.impl;

import static com.cannontech.capcontrol.model.RegulatorPointMappingResult.MULTIPLE_POINTS_FOUND;
import static com.cannontech.capcontrol.model.RegulatorPointMappingResult.NO_POINTS_FOUND;
import static com.cannontech.capcontrol.model.RegulatorPointMappingResult.SUCCESS;
import static com.cannontech.capcontrol.model.RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.capcontrol.model.RegulatorPointMappingResult;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ResultExpiredException;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingResult;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingResultType;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingTask;
import com.cannontech.web.capcontrol.regulator.setup.service.RegulatorMappingService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public class RegulatorMappingServiceImpl implements RegulatorMappingService {
    
    private static final Logger log = YukonLogManager.getLogger(RegulatorMappingServiceImpl.class);
    private static final String delimiter = "-";
    
    @Autowired @Qualifier("regulatorMapping") private RecentResultsCache<RegulatorMappingTask> resultsCache;
    @Autowired @Qualifier("longRunning") private Executor executor;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private PointDao pointDao;
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    @Autowired private CcMonitorBankListDao ccMonitorBankListDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @Override
    public String start(Collection<YukonPao> regulators, YukonUserContext userContext) {
        
        log.info(userContext.getYukonUser() + " initiated a regulator point mapping task for " 
                 + regulators.size() + " devices.");
        
        RegulatorMappingTask task = new RegulatorMappingTask(regulators, userContext);
        String taskId = resultsCache.addResult(task);
        task.setTaskId(taskId);
        
        RegulatorMappingProcessor processor = new RegulatorMappingProcessor(task);
        
        executor.execute(processor);
        
        return taskId;
    }
    
    @Override
    public RegulatorMappingTask getTask(String taskId) throws ResultExpiredException {
        return resultsCache.getResult(taskId);
    }
    
    @Override
    public List<RegulatorMappingTask> getAllTasks() {
        
        List<RegulatorMappingTask> allTasks = resultsCache.getAll();
        Collections.sort(allTasks);
        
        return allTasks;
    }
    
    /**
     * This processor handles the work of finding appropriately named points and performing the regulator point mapping.
     * The results of this work are stored in the task object.
     */
    public final class RegulatorMappingProcessor implements Runnable {
        
        private final RegulatorMappingTask task;
        
        public RegulatorMappingProcessor(RegulatorMappingTask task) {
            this.task = task;
        }
        
        @Override
        public void run() {
            
            try {
                for (YukonPao regulator : task.getRegulators()) {
                    
                    // Check to see if the task was cancelled
                    if (task.isCanceled()) {
                        break;
                    }
                    
                    RegulatorMappingResult result = task.getResult(regulator);
                    map(result);
                    
                    task.deviceComplete(regulator);
                }
            } catch (Exception e) {
                //In the face of disaster, die gracefully
                task.errorOccurred(e);
                log.error("An unexpected error occurred.", e);
            }
        }
    }
    
    @Override
    public RegulatorMappingResult start(Regulator regulator) {
        
        RegulatorMappingResult result = new RegulatorMappingResult(regulator);
        map(result);
        
        return result;
    }
    
    /**
     * Perform point mapping for all attributes on the device and store the results on 
     * the provided result object.
     */
    private void map(RegulatorMappingResult result) {
        
        YukonPao regulator = result.getRegulator();
        
        log.debug("Regulator point mapping for device " + regulator);
        
        PaoIdentifier id = regulator.getPaoIdentifier();
        Map<RegulatorPointMapping, Integer> previousMappings = 
                extraPaoPointAssignmentDao.getAssignments(id);
        
        for (RegulatorPointMapping mapping : RegulatorPointMapping.values()) {
            
            log.trace("Regulator point mapping task working on mapping " + mapping);
            
            // Build the expected point name for the mapping. E.g. "RegulatorName-PointMappingName"
            LiteYukonPAObject regulatorPao = serverDatabaseCache.getAllPaosMap().get(id.getPaoId());
            String regulatorName = regulatorPao.getPaoName();
            String mappedPointName = regulatorName + delimiter + mapping.getMappingString();
            log.trace("Searching for point name: " + mappedPointName);
            
            // Load point with matching name
            List<LitePoint> allPointsWithMappedName = pointDao.findAllPointsWithName(mappedPointName);
            
            // If there's no matching point or multiples, bail out and skip to the next mapping.
            if (allPointsWithMappedName.size() > 1) {
                log.trace("Multiple candidate points found. Skipping this point mapping.");
                result.addPointDetail(mapping, MULTIPLE_POINTS_FOUND);
                continue;
            } else if (allPointsWithMappedName.size() == 0) {
                log.trace("No candidate points found. Skipping this point mapping.");
                result.addPointDetail(mapping, NO_POINTS_FOUND);
                continue;
            }
            
            LitePoint pointForMapping = Iterables.getOnlyElement(allPointsWithMappedName);
            
            // Don't bother doing the new mapping if the old mapping is the exact same point
            if (previousMappings.get(mapping) != null
                    && previousMappings.get(mapping) == pointForMapping.getPointID()) {
                result.addPointDetail(mapping, SUCCESS);
                continue;
            }
            
            // Assign the point to the regulator
            extraPaoPointAssignmentDao.addAssignment(regulatorPao, pointForMapping.getPointID(), mapping, true);
            if (mapping == RegulatorPointMapping.VOLTAGE_Y) {
                ccMonitorBankListDao.deleteNonMatchingRegulatorPoint(id.getPaoId(), pointForMapping.getPointID());
                ccMonitorBankListDao.addRegulatorPoint(id.getPaoId());
            }
            
            // Save a successful result
            if (previousMappings.containsKey(mapping)) {
                log.trace("Successfully assigned point. Previous mapping was overwritten.");
                result.addPointDetail(mapping, SUCCESS_WITH_OVERWRITE);
            } else {
                log.trace("Successfully assigned point.");
                result.addPointDetail(mapping, SUCCESS);
            }
        }
        
        result.complete();
    }
    
    @Override
    public void delete(String taskId) {
        
        RegulatorMappingTask result = resultsCache.getResult(taskId);
        result.cancel();
        
        resultsCache.remove(taskId);
        
    }
    
    @Override
    public Map<String, Object> buildJsonResult(RegulatorMappingResult result, 
            YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        Map<String, Object> json = new HashMap<>();
        List<Map<String, Object>> mappings = new ArrayList<>();
        
        RegulatorMappingResultType status = result.getType();
        json.put("status", ImmutableMap.of("type", status, "text", accessor.getMessage(status)));
        json.put("complete", result.isComplete());
        json.put("regulatorId", result.getRegulator().getPaoIdentifier().getPaoId());
        
        for (RegulatorPointMapping mapping : result.getPointMappingResults().keySet()) {
            RegulatorPointMappingResult mappingResult = result.getPointMappingResults().get(mapping);
            mappings.add(ImmutableMap.of(
                    "type", mapping, 
                    "name", accessor.getMessage(mapping), 
                    "success", mappingResult.isSuccess(),
                    "text", accessor.getMessage(mappingResult)));
        }
        json.put("mappings", mappings);
        
        return json;
    }
}