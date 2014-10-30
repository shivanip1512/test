package com.cannontech.web.loadcontrol.tasks;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.estimatedload.EstimatedLoadAmount;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.EstimatedLoadResult;
import com.cannontech.dr.estimatedload.EstimatedLoadSummary;
import com.cannontech.dr.estimatedload.service.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.user.YukonUserContext;

public class RepeatingEstimatedLoadTask extends YukonTaskBase {

    private Logger log = YukonLogManager.getLogger(RepeatingEstimatedLoadTask.class);

    @Autowired private AttributeService attributeService;
    @Autowired private ControlAreaDao controlAreaDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private EstimatedLoadService estimatedLoadService;
    @Autowired private JobManager jobManager;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PointCreationService PointCreationService;
    @Autowired private PointCreationService pointCreationService;
    @Autowired private PointService pointService;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private SimplePointAccessDao pointAccessDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private final boolean estimatedLoadEnabled;

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
            }
            
            if (amount != null) {
                LitePoint connectedPoint = attributeService.getPointForAttribute(paoIdent, BuiltInAttribute.CONNECTED_LOAD);
                LitePoint diversifiedPoint = attributeService.getPointForAttribute(paoIdent, BuiltInAttribute.DIVERSIFIED_LOAD);
                LitePoint maxKwPoint = attributeService.getPointForAttribute(paoIdent, BuiltInAttribute.MAX_LOAD_REDUCTION);
                LitePoint nowKwPoint = attributeService.getPointForAttribute(paoIdent, BuiltInAttribute.AVAILABLE_LOAD_REDUCTION);
                
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
            }
        }
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
