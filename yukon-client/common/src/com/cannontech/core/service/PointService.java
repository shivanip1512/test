package com.cannontech.core.service;

import com.cannontech.database.data.lite.LiteState;

/**
 * PointService can be used to obtain current point related data
 * @author asolberg
 */
public interface PointService {

    public LiteState getCurrentState(int pointId);
    
}
