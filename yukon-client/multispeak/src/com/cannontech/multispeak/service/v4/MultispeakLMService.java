package com.cannontech.multispeak.service.v4;

import java.util.List;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.msp.beans.v4.SubstationLoadControlStatus;

public interface MultispeakLMService {

    /**
     * Returns a PointData object for the MultiSpeak scadaAnalog object.
     */
    public PointData buildPointData(int pointId, ScadaAnalog scadaAnalog, String userName);

    /**
     * Writes a MultiSpeak scadaAnalog value to dispatch as a PointData object..
     */
    public ErrorObject writeAnalogPointData(ScadaAnalog scadaAnalog, LiteYukonUser liteYukonUser);

    /**
     * Returns an array of SubstationLoadControlStatus values.
     * For each defined MspLMInterfaceMappings, a SubstationLoadControlStatus object is created.
     * The values are grouped by SubstationName, and contain a list of
     * SubstationLoadControlStatusControlledItemsControlItems for each Strategy on that Substation.
     * The count for all devices in program/scenario that maps to the Substation/Strategy is included.
     * The controlled count for all devices in program/scenario that maps to the Substation/Strategy is
     * included.
     * * NOTE: The controlled count is only the number of devices that were active, not opted out, for the
     * date range supplied. We currently do not have a way to get an accurate control count without
     * 2way load control switches.
     */
    public List<SubstationLoadControlStatus> getActiveLoadControlStatus() throws ConnectionException, NotFoundException;
}
