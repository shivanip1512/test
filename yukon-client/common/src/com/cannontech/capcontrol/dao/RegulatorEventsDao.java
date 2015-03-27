package com.cannontech.capcontrol.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.capcontrol.model.RegulatorEvent;

public interface RegulatorEventsDao {

    /**
     * @return list of {@link RegulatorEvent} sorted starting with the most recent event.
     */
    List<RegulatorEvent> getForIdSinceTimestamp(int regulatorId, Instant since);
}