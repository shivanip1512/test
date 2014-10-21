package com.cannontech.web.loadcontrol.tasks;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.dr.estimatedload.EstimatedLoadAmount;
import com.cannontech.dr.estimatedload.EstimatedLoadResult;
import com.cannontech.dr.estimatedload.EstimatedLoadSummary;
import com.cannontech.dr.estimatedload.service.EstimatedLoadBackingServiceHelper;
import com.cannontech.dr.estimatedload.service.EstimatedLoadService;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonTaskBase;

public class RepeatingEstimatedLoadTask extends YukonTaskBase {

    private Logger log = YukonLogManager.getLogger(RepeatingEstimatedLoadTask.class);

    @Autowired private JobManager jobManager;
    @Autowired private PaoDao paoDao;
    @Autowired private EstimatedLoadService estimatedLoadService;
    @Autowired private EstimatedLoadBackingServiceHelper backingServiceHelper;
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private ControlAreaDao controlAreaDao;
    @Autowired private PointService pointService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PointCreationService PointCreationService;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PointCreationService pointCreationService;
    @Autowired private SimplePointAccessDao pointAccessDao;

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
            }
            
            if (amount != null) {
                LitePoint connectedPoint = pointService.getPointForPao(new PaoPointIdentifier(paoIdent,
                        new PointIdentifier(PointType.Analog, 1)));
                
                LitePoint diversifiedPoint = pointService.getPointForPao(new PaoPointIdentifier(paoIdent,
                        new PointIdentifier(PointType.Analog, 2)));
                
                LitePoint maxKwPoint = pointService.getPointForPao(new PaoPointIdentifier(paoIdent, new PointIdentifier(
                        PointType.Analog, 3)));
                
                LitePoint nowKwPoint = pointService.getPointForPao(new PaoPointIdentifier(paoIdent, new PointIdentifier(
                        PointType.Analog, 4)));
                
                pointAccessDao.setPointValue(connectedPoint, amount.getConnectedLoad());
                pointAccessDao.setPointValue(diversifiedPoint, amount.getDiversifiedLoad());
                pointAccessDao.setPointValue(maxKwPoint, amount.getMaxKwSavings());
                pointAccessDao.setPointValue(nowKwPoint, amount.getNowKwSavings());
            } else {
                log.warn("Unable to archive estimated load data for pao: " + paoIdent);
            }
        }
    }

    /**
     * Will create 'Connected Load', Diversifed Load', 'Max Load Reduction', and 'Availalbe Load Reduction' points
     * on all LM paos if the points don't already exist.
     */
    private void createPoints(List<PaoIdentifier> lmPaos) {
        PointIdentifier connectedPointIdent = new PointIdentifier(PointType.Analog, 1);
        PointIdentifier diversifiedPointIdent = new PointIdentifier(PointType.Analog, 2);
        PointIdentifier maxKwPointIdent = new PointIdentifier(PointType.Analog, 3);
        PointIdentifier nowKwPointIdent = new PointIdentifier(PointType.Analog, 4);
        
        for (PaoIdentifier paoIdent : lmPaos) {
            YukonPao pao = paoDao.getYukonPao(paoIdent.getPaoId());
            
            boolean connectedPointExists = pointService.pointExistsForPao(pao, connectedPointIdent);
            if(!connectedPointExists) {
                PointTemplate pointTemplate
                = paoDefinitionDao.getPointTemplateByTypeAndOffset(paoIdent.getPaoType(), connectedPointIdent);
                PointBase newPoint = pointCreationService.createPoint(paoIdent, pointTemplate);
                dbPersistentDao.performDBChange(newPoint, TransactionType.INSERT);
                log.info("New point created for Connected Load on pao " + paoIdent);
            }
            
            boolean diversifiedPointExits = pointService.pointExistsForPao(pao, diversifiedPointIdent);
            if (!diversifiedPointExits) {
                PointTemplate pointTemplate
                    = paoDefinitionDao.getPointTemplateByTypeAndOffset(paoIdent.getPaoType(), diversifiedPointIdent);
                PointBase newPoint = pointCreationService.createPoint(paoIdent, pointTemplate);
                dbPersistentDao.performDBChange(newPoint, TransactionType.INSERT);
                log.info("New point created for Diversifed Load on pao " + paoIdent);
            }
            
            boolean maxKwPointExists = pointService.pointExistsForPao(pao, maxKwPointIdent);
            if(!maxKwPointExists) {
                PointTemplate pointTemplate
                    = paoDefinitionDao.getPointTemplateByTypeAndOffset(paoIdent.getPaoType(), maxKwPointIdent);
                PointBase newPoint = pointCreationService.createPoint(paoIdent, pointTemplate);
                dbPersistentDao.performDBChange(newPoint, TransactionType.INSERT);
                log.info("New point created for Max kW Savings on pao " + paoIdent);
            }
            
            boolean nowKwPointExists = pointService.pointExistsForPao(pao, nowKwPointIdent);
            if(!nowKwPointExists) {
                PointTemplate pointTemplate
                = paoDefinitionDao.getPointTemplateByTypeAndOffset(paoIdent.getPaoType(), nowKwPointIdent);
                PointBase newPoint = pointCreationService.createPoint(paoIdent, pointTemplate);
                dbPersistentDao.performDBChange(newPoint, TransactionType.INSERT);
                log.info("New point created for Now kW Savings on pao " + paoIdent);
            }
        }
    }
}
