package com.cannontech.multispeak.data;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.multispeak.client.MultispeakFuncs;

/**
 * Class which represents billing data for an MCT310
 */
public class MCT2XX extends MeterReadBase {
    @Override
    public void populate(PointIdentifier pointIdentifier, Date dateTime, Double value) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(dateTime.getTime());
        
        switch (pointIdentifier.getPointType()) {
            case PulseAccumulator:
                switch (pointIdentifier.getOffset()) {
                    case 1: // KWh
                        getMeterRead().setReadingDate(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                        getMeterRead().setPosKWh(new BigInteger(String.valueOf(value.intValue())));
                        setPopulated(true);
                        break;
                }
        }
    }
}