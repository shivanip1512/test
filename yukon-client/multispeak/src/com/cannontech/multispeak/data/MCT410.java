package com.cannontech.multispeak.data;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.database.data.point.PointTypes;

/**
 * Class which represents billing data for an MCT410
 */
public class MCT410 extends MeterReadBase {

    public void populate(int pointType, int pointOffSet, int uomID, Date dateTime, Double value) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(dateTime.getTime());
        
        switch (pointType) {
            case PointTypes.PULSE_ACCUMULATOR_POINT:
                switch (pointOffSet) {
                    case 1: // KWh
                        getMeterRead().setReadingDate(calendar);
                        getMeterRead().setPosKWh(new BigInteger(String.valueOf(value.intValue())));
                        setPopulated(true);
                        break;
       
                    case 101: // Rate A KWh
                    case 121: // Rate B KWh
                    case 141: // Rate C KWh
                    case 161: // Rate D KWh
                }
                break;
            case PointTypes.DEMAND_ACCUMULATOR_POINT:
                switch (pointOffSet) {
    
                    case 11: // Peak KW
                        getMeterRead().setKW(new Float(value.floatValue()));
                        getMeterRead().setKWDateTime(calendar);
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
