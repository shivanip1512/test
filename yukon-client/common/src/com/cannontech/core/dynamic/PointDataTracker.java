package com.cannontech.core.dynamic;

import java.util.Collection;
import java.util.Optional;

import com.cannontech.message.dispatch.message.PointData;

public interface PointDataTracker {

    /**
     * Adds tracking IDs to the pointData and returns them as a combined string for logging.
     * @param messagesToSend The pointData to track.
     * @return The combined tracking information as a string, if any.
     */
    Optional<String> trackValues(Collection<PointData> messagesToSend);

}