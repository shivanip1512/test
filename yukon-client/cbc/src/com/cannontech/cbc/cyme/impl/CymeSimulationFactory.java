package com.cannontech.cbc.cyme.impl;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.db.point.stategroup.TrueFalse;

public class CymeSimulationFactory {
    private static final Logger logger = YukonLogManager.getLogger(CymeSimulationFactory.class);
    
    public Runnable generateSimulation() {
        return new Runnable() {
 
            
            private int stepNumber = 1;//Start at 1. The 0th was sent out upon simulation start
            private boolean complete = false;
            
            @Override
            public void run() {
//                if (! runSimulation) {
//                    logger.debug("Simulation disabled, halting.");
//                    simulationRunning = false;
//                    throw new RuntimeException();//this ends future tasks
//                }
//
//                if (complete) {
//                    logger.debug("Simulation Complete, halting.");
//                    //Toggle the start simulation point to false,
//                    if (simulationControlPoint != null) {
//                        simplePointAccessDao.setPointValue(simulationControlPoint.getLiteID(), TrueFalse.FALSE);
//                    }
//                    throw new RuntimeException();//this ends future tasks
//                }
//                
//                int value = loadProfile.getValues().get(stepNumber++);
//                logger.debug("Running next simulation step with Profile Percent:" + value);
//
//                //Send the New Load Value to the system.
//                if (simulationLoadFactorPoint != null) {
//                    simplePointAccessDao.setPointValue(simulationLoadFactorPoint.getLiteID(), value);
//                }
//                
//                //Are we done? Change the status point
//                if (stepNumber+1 >= 1440/loadProfile.getTimeInterval().getMinutes()) {
//                    complete = true;
//                    simulationRunning = false;
//                }
            }            
        };
    }
    
}
