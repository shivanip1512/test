package com.cannontech.web.common.widgets.service;

import org.joda.time.Instant;

import com.cannontech.web.common.widgets.model.AssetAvailabilityWidgetSummary;

public interface AssetAvailabilityWidgetService {

    /**
     * Gets the next available refresh time.
     * @param Instant value indicating the most recent time the data was loaded for pie chart
     * @return Instant the next refresh time
     */
    Instant getNextRefreshTime(Instant lastUpdateTime);

    /**
     * Gets the milliseconds for refresh time
     * @return long the refresh time
     */
    long getRefreshMilliseconds();

    /**
     * Returns Asset Availability summary for areaOrLMProgramOrScenarioId.
     */
    AssetAvailabilityWidgetSummary getAssetAvailabilitySummary(Integer areaOrLMProgramOrScenarioId, Instant lastUpdateTime);

}
