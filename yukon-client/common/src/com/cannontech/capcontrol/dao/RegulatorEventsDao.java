package com.cannontech.capcontrol.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.capcontrol.model.RegulatorEvent;

public interface RegulatorEventsDao {

    List<RegulatorEvent> getForRegulatorIdSinceTimestamp(int regulatorId, Instant since);
}