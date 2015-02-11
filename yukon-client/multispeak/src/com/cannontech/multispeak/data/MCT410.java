package com.cannontech.multispeak.data;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.multispeak.client.MultispeakFuncs;

/**
 * Class which represents billing data for an MCT410
 */
public class MCT410 extends MeterReadBase {

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
       
                    case 101: // Rate A KWh
                    case 121: // Rate B KWh
                    case 141: // Rate C KWh
                    case 161: // Rate D KWh
                }
                break;
            case DemandAccumulator:
                switch (pointIdentifier.getOffset()) {
    
                    case 11: // Peak KW
                        getMeterRead().setKW(new Float(value.floatValue()));
                        getMeterRead().setKWDateTime(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                        setPopulated(true);
                        break;
                    case 111: // Rate A KW
                    case 131: // Rate B KW
                    case 151: // Rate C KW
                    case 171: // Rate D KW
                }
                break;
        }
    }
}
