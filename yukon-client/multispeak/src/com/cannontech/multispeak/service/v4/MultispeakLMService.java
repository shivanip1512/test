package com.cannontech.multispeak.service.v4;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.ScadaAnalog;

public interface MultispeakLMService {

    /**
     * Returns a PointData object for the MultiSpeak scadaAnalog object.
     */
    public PointData buildPointData(int pointId, ScadaAnalog scadaAnalog, String userName);

    /**
     * Writes a MultiSpeak scadaAnalog value to dispatch as a PointData object..
     */
    public ErrorObject writeAnalogPointData(ScadaAnalog scadaAnalog, LiteYukonUser liteYukonUser);

}
