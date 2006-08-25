package com.cannontech.multispeak.data;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.database.data.point.PointTypes;

/**
 * Class which represents billing data for an MCT360
 */
public class MCT360 extends MeterReadBase{

    public void populate(int pointType, int pointOffSet, int uomID, Date dateTime, Double value) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(dateTime.getTime());
        
        switch (pointType) {
            case PointTypes.PULSE_ACCUMULATOR_POINT:
                switch (pointOffSet) {
                    case 3: // KWh
                        getMeterRead().setReadingDate(calendar);
                        getMeterRead().setPosKWh(new BigInteger(String.valueOf(value.intValue())));
                        setPopulated(true);
                        break;
                }
                break;

            case PointTypes.ANALOG_POINT:

                switch (pointOffSet) {
                // Electric
                case 1: // KWh
                case 2: // Rate A KW
                case 3: // Rate A KWh
                case 4: // Rate B KW
                case 5: // Rate B KWh
                case 6: // Rate C KW
                case 7: // Rate C KWh
                case 8: // Rate D KW
                case 9: // Rate D KWh
                // kVar
                case 11: // Total Kvarh
                case 12: // Rate A Kvar
                case 13: // Rate A Kvarh
                case 14: // Rate B Kvar
                case 15: // Rate B Kvarh
                case 16: // Rate C Kvar
                case 17: // Rate C Kvarh
                case 18: // Rate D Kvar
                case 19: // Rate D Kvarh
                // kVa
                case 21: // Total Kvah
                case 22: // Rate A Kva
                case 23: // Rate A Kvah
                case 24: // Rate B Kva
                case 25: // Rate B Kvah
                case 26: // Rate C Kva
                case 27: // Rate C Kvah
                case 28: // Rate D Kva
                case 29: // Rate D Kvah
                }
                break;
        }
    }
}
