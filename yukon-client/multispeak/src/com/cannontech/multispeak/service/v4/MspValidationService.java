package com.cannontech.multispeak.service.v4;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface MspValidationService {

    /**
     * Returns Meter for the meterNumber if MeterNumber is a Yukon MeterNumber.
     * Throws a RemoteException if the meterNumber is not found in Yukon.
     */
    public YukonMeter isYukonMeterNumber(String meterNumber) throws MultispeakWebServiceException;

    /**
     * Returns an ErrorObject when the scadaAnalog does not have all required information
     * to create a Yukon point translation.
     * @param scadaAnalog
     * @return
     */
    public ErrorObject isValidScadaAnalog(ScadaAnalog scadaAnalog);

}
