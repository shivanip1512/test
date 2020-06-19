package com.cannontech.common.trend.service;

import com.cannontech.common.trend.model.TrendModel;

public interface TrendService {

    /*
     * Create a trend.
     */
    TrendModel create(TrendModel trend);

    /**
     * Delete the Trend object.
     */
    int delete(int id);

    /**
     * Update a Trend
     */
    TrendModel update(int id, TrendModel trendModel);

}
