package com.cannontech.amr.rfn.service;

import org.joda.time.Instant;

public interface RfnDataValidator {

    /**
     * Checks for timestamp "reasonability". Expectation is that when not valid, the timestamp and quality might be adjusted.
     * @param timestamp The timestamp to validate
     * @param now The current time
     */
    public boolean isTimestampValid(Instant timestamp, Instant now);

    /**
     * Checks that the timestamp is recent. Expectation is that when not recent the data is thrown away.
     * @param timestamp The timestamp to check if it is recent.
     * @param now The current time
     */
    public boolean isTimestampRecent(Instant timestamp, Instant now);

}