package com.cannontech.web.loadcontrol.tasks;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.events.loggers.EstimatedLoadEventLogService;
import com.cannontech.common.exception.PointDataException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.estimatedload.ApplianceCategoryInfoNotFoundException;
import com.cannontech.dr.estimatedload.ApplianceCategoryNotFoundException;
import com.cannontech.dr.estimatedload.EstimatedLoadAmount;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.EstimatedLoadResult;
import com.cannontech.dr.estimatedload.EstimatedLoadSummary;
import com.cannontech.dr.estimatedload.GearNotFoundException;
import com.cannontech.dr.estimatedload.InputOutOfRangeException;
import com.cannontech.dr.estimatedload.LmDataNotFoundException;
import com.cannontech.dr.estimatedload.LmServerNotConnectedException;
import com.cannontech.dr.estimatedload.NoAppCatFormulaException;
import com.cannontech.dr.estimatedload.NoGearFormulaException;
import com.cannontech.dr.estimatedload.InputOutOfRangeException.Type;
import com.cannontech.dr.estimatedload.InputValueNotFoundException;
import com.cannontech.dr.estimatedload.service.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.user.YukonUserContext;

public class RepeatingEstimatedLoadTask extends YukonTaskBase {

    private Logger log = YukonLogManager.getLogger(RepeatingEstimatedLoadTask.class);

    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private AttributeService attributeService;
    @Autowired private ControlAreaDao controlAreaDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private EstimatedLoadEventLogService estimatedLoadEventLogService;
    @Autowired private EstimatedLoadService estimatedLoadService;
    @Autowired private JobManager jobManager;
    @Autowired private LMGearDao lmGearDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PointCreationService PointCreationService;
    @Autowired private PointCreationService pointCreationService;
    @Autowired private PointService pointService;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private SimplePointAccessDao pointAccessDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private final boolean estimatedLoadEnabled;
    private static MessageSourceAccessor systemMessasgeSourceAccessor = null;
    
    @Autowired 
    public RepeatingEstimatedLoadTask(ConfigurationSource configurationSource) {
        this.estimatedLoadEnabled
            = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.ENABLE_ESTIMATED_LOAD, false);
    }

    @Override
    public void start() {
        if (!estimatedLoadEnabled) {
            log.info("Ignoring job " + this.getJob().getBeanName() + ". This job is not authorized to run on this machine.");
            return;
        }

        log.info("Updating Estimated Load Historical data");
        List<PaoIdentifier> lmPaos
            = paoDao.getAllPaoIdentifiersForTags(PaoTag.LM_PROGRAM, PaoTag.LM_CONTROL_AREA, PaoTag.LM_SCENARIO);

        createPoints(lmPaos);
        populatePointData(lmPaos);
    }

    private void populatePointData(List<PaoIdentifier> lmPaos) {
        for (PaoIdentifier paoIdent : lmPaos) {
            EstimatedLoadResult estimatedLoadResult = null;
            EstimatedLoadAmount amount = null;
            String errorReason = null;
            
            if (paoIdent.getPaoType().isLmProgram()) {
                estimatedLoadResult = backingServiceHelper.findProgramValue(paoIdent.getPaoId(), true);
            } else if (paoIdent.getPaoType() == PaoType.LM_CONTROL_AREA) {
                estimatedLoadResult = backingServiceHelper.getControlAreaValue(paoIdent, true);
            } else if (paoIdent.getPaoType() == PaoType.LM_SCENARIO) {
                estimatedLoadResult = backingServiceHelper.getScenarioValue(paoIdent, true);
            }
            if (estimatedLoadResult != null && estimatedLoadResult instanceof EstimatedLoadAmount) {
                amount = (EstimatedLoadAmount) estimatedLoadResult;
            } else if (estimatedLoadResult != null && estimatedLoadResult instanceof EstimatedLoadSummary) {
                EstimatedLoadSummary summary = (EstimatedLoadSummary) estimatedLoadResult;
                if (summary.getContributing() > 0 || summary.getCalculating() > 0) {
                    amount = summary.getSummaryAmount();
                }
            } else if (estimatedLoadResult != null && estimatedLoadResult instanceof EstimatedLoadException) {
                MessageSourceAccessor messageSourceAccessor = messageSourceResolver
                        .getMessageSourceAccessor(YukonUserContext.system);
                errorReason = messageSourceAccessor.getMessage(backingServiceHelper.resolveException(
                        ((EstimatedLoadException) estimatedLoadResult), YukonUserContext.system));
                
                //Add event log entry for failed calculation
                eventLogError(estimatedLoadResult, paoIdent);
            }
            
            LitePoint connectedPoint = attributeService.getPointForAttribute(paoIdent, BuiltInAttribute.CONNECTED_LOAD);
            LitePoint diversifiedPoint = attributeService.getPointForAttribute(paoIdent, BuiltInAttribute.DIVERSIFIED_LOAD);
            LitePoint maxKwPoint = attributeService.getPointForAttribute(paoIdent, BuiltInAttribute.MAX_LOAD_REDUCTION);
            LitePoint nowKwPoint = attributeService.getPointForAttribute(paoIdent, BuiltInAttribute.AVAILABLE_LOAD_REDUCTION);
            
            if (amount != null) {
                
                pointAccessDao.setPointValue(connectedPoint, amount.getConnectedLoad());
                pointAccessDao.setPointValue(diversifiedPoint, amount.getDiversifiedLoad());
                pointAccessDao.setPointValue(maxKwPoint, amount.getMaxKwSavings());
                pointAccessDao.setPointValue(nowKwPoint, amount.getNowKwSavings());
            } else {
                if (errorReason != null) {
                    log.warn("Unable to archive estimated load data for pao: " + paoIdent + " because: " + errorReason);
                } else {
                    // Only programs provide specific error messages. Control areas and scenarios do not
                    log.warn("Unable to archive estimated load data for pao: " + paoIdent); 
                }
                markPointAsNonUpdated(connectedPoint);
                markPointAsNonUpdated(diversifiedPoint);
                markPointAsNonUpdated(maxKwPoint);
                markPointAsNonUpdated(nowKwPoint);
            }
        }
    }
    
    private void markPointAsNonUpdated(LitePoint point) {
    	
        try {
            log.info("Point:" + point.getLiteID() + " marked as NonUpdated");
            double value = pointAccessDao.getPointValue(point);
            pointAccessDao.setPointValue(point, value, PointQuality.NonUpdated);
        } catch (PointDataException e) {
            log.warn("Unable to mark point:" + point.getLiteID() + " as NonUpdated. Point value is not found", e);
        }
    }
    
    /**
     * Adds a record to the event log describing the error that occurred in the estimated load calculation.
     */
    private void eventLogError(EstimatedLoadResult result, PaoIdentifier paoIdentifier) {
        
        String programName = paoDao.getYukonPAOName(paoIdentifier.getPaoId());
        
        if (result instanceof ApplianceCategoryNotFoundException) {
            estimatedLoadEventLogService.applianceCategoryNotFound(programName);
        } else if (result instanceof ApplianceCategoryInfoNotFoundException) {
            int appCatId = ((ApplianceCategoryInfoNotFoundException) result).getApplianceCategoryId();
            String applianceCategoryName = applianceCategoryDao.getById(appCatId).getName();
            estimatedLoadEventLogService.applianceCategoryInfoNotFound(programName, applianceCategoryName);
        } else if (result instanceof GearNotFoundException) {
            int gearNumber = ((GearNotFoundException) result).getGearNumber();
            estimatedLoadEventLogService.gearNotFound(programName, gearNumber);
        } else if (result instanceof InputOutOfRangeException) {
            InputOutOfRangeException inputException = ((InputOutOfRangeException) result);
            
            //Formula name
            String formulaName = inputException.getFormula().getName();
            
            //Component type and name
            String componentType;
            String componentName;
            if (inputException.getType() == Type.FUNCTION) {
                componentType = getSystemMessage("yukon.web.modules.dr.estimatedLoad.function");
                componentName = inputException.getFormula().getFunctionById(inputException.getId()).getName();
            } else if (inputException.getType() == Type.LOOKUP) {
                componentType = getSystemMessage("yukon.web.modules.dr.estimatedLoad.lookup");
                componentName = inputException.getFormula().getTableById(inputException.getId()).getName();
            } else if (inputException.getType() == Type.TIME_LOOKUP) {
                componentType = getSystemMessage("yukon.web.modules.dr.estimatedLoad.timeLookup");
                componentName = inputException.getFormula().getTimeTableById(inputException.getId()).getName();
            } else {
                componentType = getSystemMessage("yukon.web.modules.dr.estimatedLoad.unknown");
                componentName = getSystemMessage("yukon.web.modules.dr.estimatedLoad.unknown");
            }
            
            //Input value, valid min and max
            String inputValue = inputException.getInputValue();
            String validMin = inputException.getMinValue();
            String validMax = inputException.getMaxValue();
            
            estimatedLoadEventLogService.inputOutOfRange(formulaName, componentType, componentName, inputValue, validMin, validMax);
            
        } else if (result instanceof InputValueNotFoundException) {
            String formulaName = ((InputValueNotFoundException) result).getFormulaName();
            estimatedLoadEventLogService.inputValueNotFound(programName, formulaName);
        } else if (result instanceof LmDataNotFoundException) {
            estimatedLoadEventLogService.lmDataNotFound(programName);
        } else if (result instanceof LmServerNotConnectedException) {
            estimatedLoadEventLogService.notConnectedToLmServer();
        } else if (result instanceof NoAppCatFormulaException) {
            int appCatId = ((ApplianceCategoryInfoNotFoundException) result).getApplianceCategoryId();
            String applianceCategoryName = applianceCategoryDao.getById(appCatId).getName();
            estimatedLoadEventLogService.noApplianceCategoryFormula(applianceCategoryName, programName);
        } else if (result instanceof NoGearFormulaException) {
            int gearId = ((NoGearFormulaException) result).getGearId();
            String gearName = lmGearDao.getByGearId(gearId).getGearName();
            estimatedLoadEventLogService.noGearFormula(gearName, programName);
        } else {
            estimatedLoadEventLogService.unknownError(programName);
        }
    }
    
    /** Get the i18n string for the specified key, using the System user context */
    private String getSystemMessage(String key) {
        if (systemMessasgeSourceAccessor == null) {
            systemMessasgeSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        }
        YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable(key);
        return systemMessasgeSourceAccessor.getMessage(resolvable);
    }
    
    /**
     * Will create 'Connected Load', Diversifed Load', 'Max Load Reduction', and 'Availalbe Load Reduction' points
     * on all LM paos if the points don't already exist.
     */
    private void createPoints(List<PaoIdentifier> lmPaos) {
        for (PaoIdentifier paoIdent : lmPaos) {
            if (attributeService.createPointForAttribute(paoIdent, BuiltInAttribute.CONNECTED_LOAD)) {
                log.info("New point created for Connected Load on pao " + paoIdent);
            }
            if (attributeService.createPointForAttribute(paoIdent, BuiltInAttribute.DIVERSIFIED_LOAD)) {
                log.info("New point created for Diversifed Load on pao " + paoIdent);
            }
            if (attributeService.createPointForAttribute(paoIdent, BuiltInAttribute.MAX_LOAD_REDUCTION)) {
                log.info("New point created for Max Load Reduction on pao " + paoIdent);
            }
            if (attributeService.createPointForAttribute(paoIdent, BuiltInAttribute.AVAILABLE_LOAD_REDUCTION)) {
                log.info("New point created for Available Load Reduction on pao " + paoIdent);
            }
        }
    }
}