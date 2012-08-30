package com.cannontech.cbc.cyme.impl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cbc.cyme.CymeWebService;
import com.cannontech.cbc.cyme.model.CymeSimulationStatus;
import com.cannontech.cbc.cyme.model.SimulationResultSummaryData;
import com.cannontech.clientutils.YukonLogManager;

public class CymeTaskExecutor {

    @Autowired private CymeWebService cymeWebService;
    @Autowired private CymeSimulationHelper cymeSimulationHelper;
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    
    private static final Logger logger = YukonLogManager.getLogger(CymeTaskExecutor.class);
    
    public void monitorSimulation(final String simulationId, final Instant simulationTime) {
        Runnable task = new Runnable() {        
            @Override
            public void run() {
                try {
                    logger.debug("Cyme IVVC: Started processing simulation with Id: " + simulationId);
                    
                    if (simulationId == null) {
                        logger.warn("Cyme IVVC: Simulation Id of NULL !");
                        return;
                    }
                    
                    for (int i = 0; i < 15; i++) {
                        CymeSimulationStatus simStatus = cymeWebService.getSimulationReportStatus(simulationId);
                        if (simStatus == CymeSimulationStatus.QUEUED || simStatus == CymeSimulationStatus.ACTIVE) {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                logger.warn("Cyme IVVC: PointDataReceived interupted while waiting for Simlation Report with id: " + simulationId, e);
                            }
                        } else if (simStatus == CymeSimulationStatus.COMPELTED) {
                            
                            List<SimulationResultSummaryData> resultSummaries = cymeWebService.generateResultSummary(simulationId);
                            
                            for (SimulationResultSummaryData summary : resultSummaries) {
                                String report = cymeWebService.getSimulationReport(summary);
        
                                //Verify we got it!
                                
                                
                                // Process report into list of Point changes and send to dispatch
                                cymeSimulationHelper.processReport(report,simulationTime);
                            }
                            logger.debug("Cyme IVVC: Completed processing simulation with Id: " + simulationId);
                            return;
                        } else if (simStatus == CymeSimulationStatus.ERRORED) {
                            logger.error("Simulation failed with Id: " + simulationId);
                            return;
                        }
                    }
                    
                    logger.debug("Cyme IVVC: Timeout while processing simulation with Id: " + simulationId);
                } catch (Exception e) {
                    logger.warn("Exception Caught in monitorSimulation.", e);
                }
            }
        };
        
        //Execute now
        scheduledExecutor.execute(task);
    }
}
