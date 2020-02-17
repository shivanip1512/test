package com.cannontech.amr.rfn.service;

import org.joda.time.Instant;

public interface RfnDataValidator {

    public boolean isTimestampValid(Instant timestamp, Instant now);

    public boolean isTimestampRecent(Instant timestamp, Instant now);

}