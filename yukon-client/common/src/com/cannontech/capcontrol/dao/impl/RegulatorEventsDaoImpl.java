package com.cannontech.capcontrol.dao.impl;

import java.util.Collections;
import java.util.List;

import org.joda.time.Instant;

import com.cannontech.capcontrol.dao.RegulatorEventsDao;
import com.cannontech.capcontrol.model.RegulatorEvent;
import com.cannontech.capcontrol.model.RegulatorEvent.EventType;
import com.cannontech.common.model.Phase;
import com.google.common.collect.ImmutableList;

public class RegulatorEventsDaoImpl implements RegulatorEventsDao {

    @Override
    public List<RegulatorEvent> getForRegulatorIdSinceTimestamp(int regulatorId, Instant start) {
        //TODO JOE Actually get this from the DB

        double chance_to_get_events = 0.99;

        if (start.equals(new Instant(0))) {
            return ImmutableList.of(
                RegulatorEvent.of(regulatorId, Instant.now(), randomEnum(EventType.class), randomEnum(Phase.class), "cap control"),
                RegulatorEvent.of(regulatorId, Instant.now(), randomEnum(EventType.class), randomEnum(Phase.class), "cap control")
            );
        }

        if (Math.random() < chance_to_get_events) {
            return ImmutableList.of(
                RegulatorEvent.of(regulatorId, start, randomEnum(EventType.class), randomEnum(Phase.class), "cap control")
            );
        } else {
            return Collections.emptyList();
        }

    }

    private static <T extends Enum<T>> T randomEnum(Class<T> clazz) {
        T[] values = clazz.getEnumConstants();

        int index = (int) ((Math.random() * Integer.MAX_VALUE) % values.length);
        return values[index];
    }

}
