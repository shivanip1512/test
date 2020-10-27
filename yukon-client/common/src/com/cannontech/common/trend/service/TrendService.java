package com.cannontech.common.trend.service;

import com.cannontech.common.trend.model.ResetPeakModel;
import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface TrendService {

    /*
     * Create a trend.
     */
    TrendModel create(TrendModel trend, LiteYukonUser liteYukonUser);

    /**
     * Delete the Trend object.
     */
    int delete(int id, LiteYukonUser liteYukonUser);

    /**
     * Update a Trend
     */
    TrendModel update(int id, TrendModel trendModel, LiteYukonUser liteYukonUser);

    /**
     * Retrieve a Trend
     */
    TrendModel retrieve(int id);

    /**
     * Reset Peak for a Trend
     */
    int resetPeak(int id, ResetPeakModel resetPeakModel, LiteYukonUser liteYukonUser);

}
