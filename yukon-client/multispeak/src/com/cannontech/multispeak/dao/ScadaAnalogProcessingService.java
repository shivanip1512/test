package com.cannontech.multispeak.dao;

import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.msp.beans.v3.ScadaAnalog;

public interface ScadaAnalogProcessingService {

    /**
     * Simple helper to create a blank ScadaAnalog for a given LiteYukonPaobject.
     */
    public ScadaAnalog createScadaAnalog(LiteYukonPAObject litePAObject, LitePoint litePoint, PointValueQualityHolder pointValue);
}
