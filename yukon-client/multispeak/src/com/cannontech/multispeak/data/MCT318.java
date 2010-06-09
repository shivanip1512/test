package com.cannontech.multispeak.data;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.database.data.point.PointTypes;

/**
 * Class which represents billing data for an MCT318
 */
public class MCT318 extends MeterReadBase {

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
                }
        }
    }
}
