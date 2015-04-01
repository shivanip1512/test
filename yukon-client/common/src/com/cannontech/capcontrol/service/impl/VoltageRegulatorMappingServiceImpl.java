package com.cannontech.capcontrol.service.impl;

import static com.cannontech.capcontrol.model.RegulatorPointMappingResult.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.model.RegulatorMappingTask;
import com.cannontech.capcontrol.service.VoltageRegulatorMappingService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ResultExpiredException;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Iterables;

public class VoltageRegulatorMappingServiceImpl implements VoltageRegulatorMappingService {
    
    private static final Logger log = YukonLogManager.getLogger(VoltageRegulatorMappingServiceImpl.class);
    private static final String mappingDelimiter = "-";
    
    private static final Comparator<RegulatorMappingTask> taskComparator = new Comparator<RegulatorMappingTask>() {
        @Override
        public int compare(RegulatorMappingTask task1, RegulatorMappingTask task2) {
            return task1.getStart().compareTo(task2.getStart());
        }
    };
    
    @Autowired @Qualifier("regulatorMapping") private RecentResultsCache<RegulatorMappingTask> resultsCache;
    @Autowired @Qualifier("longRunning") private Executor executor;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private PointDao pointDao;
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    @Autowired private CcMonitorBankListDao ccMonitorBankListDao;
    
    @Override
    public String initiateTask(DeviceCollection regulators, YukonUserContext userContext) {
        
        log.info(userContext.getYukonUser() + " initiated a regulator point mapping task for " 
                 + regulators.getDeviceCount() + " devices.");
        
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
        Collections.sort(allTasks, taskComparator);
        return allTasks;
    }
    
    /**
     * This processor handles the work of finding appropriately named points and performing the regulator point mapping.
     * The results of this work are stored in the task object.
     */
    final class RegulatorMappingProcessor implements Runnable {
        private final RegulatorMappingTask task;
        
        public RegulatorMappingProcessor(RegulatorMappingTask task) {
            this.task = task;
        }
        
        @Override
        public void run() {
            try {
                for (SimpleDevice regulator : task.getRegulators()) {
                    
                    //check to see if the task was cancelled
                    if (task.isCanceled()) {
                        break;
                    }
                    
                    log.debug("Regulator point mapping task working on device " + regulator);
                    
                    Map<RegulatorPointMapping, Integer> previousMappings = 
                            extraPaoPointAssignmentDao.getAssignments(regulator.getPaoIdentifier());
                    
                    for (RegulatorPointMapping mapping : RegulatorPointMapping.values()) {
                        
                        log.trace("Regulator point mapping task working on mapping " + mapping);
                        
                        //Build the expected point name for the mapping. E.g. "RegulatorName-PointMappingName"
                        LiteYukonPAObject regulatorPao = serverDatabaseCache.getAllPaosMap().get(regulator.getDeviceId());
                        String regulatorName = regulatorPao.getPaoName();
                        String mappedPointName = regulatorName + mappingDelimiter + mapping.getMappingString();
                        log.trace("Searching for point name: " + mappedPointName);
                        
                        //Load point with matching name
                        List<LitePoint> allPointsWithMappedName = pointDao.findAllPointsWithName(mappedPointName);
                        
                        //If there's no matching point or multiples, bail out and skip to the next mapping.
                        if (allPointsWithMappedName.size() > 1) {
                            log.trace("Multiple candidate points found. Skipping this point mapping.");
                            task.addResult(regulator, mapping, MULTIPLE_POINTS_FOUND);
                            continue;
                        } else if (allPointsWithMappedName.size() == 0) {
                            log.trace("No candidate points found. Skipping this point mapping.");
                            task.addResult(regulator, mapping, NO_POINTS_FOUND);
                            continue;
                        }
                        
                        LitePoint pointForMapping = Iterables.getOnlyElement(allPointsWithMappedName);
                        
                        //Don't bother doing the new mapping if the old mapping is the exact same point
                        if (previousMappings.get(mapping) != null
                                && previousMappings.get(mapping) == pointForMapping.getPointID()) {
                            
                            task.addResult(regulator, mapping, SUCCESS); //TODO: may want a new result type for this case
                            continue;
                        }
                        
                        //Assign the point to the regulator
                        extraPaoPointAssignmentDao.addAssignment(regulatorPao, pointForMapping.getPointID(), mapping, true);
                        if (mapping == RegulatorPointMapping.VOLTAGE_Y) {
                            ccMonitorBankListDao.deleteNonMatchingRegulatorPoint(regulator.getDeviceId(), pointForMapping.getPointID());
                            ccMonitorBankListDao.addRegulatorPoint(regulator.getDeviceId());
                        }
                        
                        //Save a successful result
                        if (previousMappings.containsKey(mapping)) {
                            log.trace("Successfully assigned point. Previous mapping was overwritten.");
                            task.addResult(regulator, mapping, SUCCESS_WITH_OVERWRITE);
                        } else {
                            log.trace("Successfully assigned point.");
                            task.addResult(regulator, mapping, SUCCESS);
                        }
                    }
                    
                    task.deviceComplete(regulator);
                }
            } catch (Exception e) {
                //In the face of disaster, die gracefully
                task.errorOccurred(e);
                log.error("An unexpected error occurred.", e);
            }
        }
    }
}
