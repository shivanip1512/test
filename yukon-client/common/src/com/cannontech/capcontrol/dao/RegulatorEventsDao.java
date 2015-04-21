package com.cannontech.capcontrol.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.capcontrol.model.RegulatorEvent;
import com.cannontech.common.util.TimeRange;

public interface RegulatorEventsDao {

    /**
     * @return list of {@link RegulatorEvent} sorted starting with the most recent event.
     */
    List<RegulatorEvent> getForIdSinceTimestamp(int regulatorId, Instant since);

    /**
     * Returns a list of events for the id that have occurred from time range until now.
     */
    List<RegulatorEvent> getForId(int id, TimeRange range);
}