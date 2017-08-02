package com.cannontech.simulators.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.capcontrol.model.RegulatorEvent;

public interface RegulatorEventsSimulatorDao {

    public class RegulatorOperations {
        public Integer regulatorId;
        public RegulatorEvent.EventType eventType;
        public Instant timeStamp;
        public Integer setPointValue;
    }
    
    List<RegulatorOperations> getRegulatorTapOperationsAfter(Instant lastRegulatorEvaluationTime);
    
    /**
     * Gets Set point event operations after the timestamp passed
     */
    List<RegulatorOperations> getRegulatorSetPointOperationsAfter(Instant lastRegulatorEvaluationTime);

}
