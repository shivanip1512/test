package com.cannontech.multispeak.dao.v5;

import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;

public interface ScadaAnalogProcessingService {

    /**
     * Simple helper to create a blank ScadaAnalog for a given LiteYukonPaobject.
     */
    public SCADAAnalog createScadaAnalog(LiteYukonPAObject litePAObject, LitePoint litePoint, PointValueQualityHolder pointValue);
}
