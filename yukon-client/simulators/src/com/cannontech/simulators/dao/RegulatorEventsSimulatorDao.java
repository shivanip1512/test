package com.cannontech.simulators.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.capcontrol.model.RegulatorEvent;

public interface RegulatorEventsSimulatorDao {

    public class RegulatorOperations {
        public Integer regulatorId;
        public RegulatorEvent.EventType eventType;
    }
    
    List<RegulatorOperations> getRegulatorTapOperationsAfter(Instant start);
}
