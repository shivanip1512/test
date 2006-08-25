package com.cannontech.multispeak.data;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.database.data.point.PointTypes;

/**
 * Class which represents billing data for an MCT470
 */
public class MCT470 extends MeterReadBase {

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

                    case 2: // kWh Channel 2
                    case 3: // kWh Channel 3
                    case 4: // kWh Channel 4
                }
                break;
            case PointTypes.DEMAND_ACCUMULATOR_POINT:
                switch (pointOffSet) {
    
                    case 11: // Peak KW Channel 1
                        getMeterRead().setKW(new Float(value.floatValue()));
                        getMeterRead().setKWDateTime(calendar);
                        setPopulated(true);
                        break;
                    case 12: // Peak KW Channel 2
                    case 13: // Peak KW Channel 3
                    case 14: // Peak KW Channel 4
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
                // Variable - could be any type of reading
                case 11: // Total Consumption
                case 12: // Rate A Demand
                case 13: // Rate A Consumption
                case 14: // Rate B Demand
                case 15: // Rate B Consumption
                case 16: // Rate C Demand
                case 17: // Rate C Consumption
                case 18: // Rate D Demand
                case 19: // Rate D Consumption
                }
                break;
        }
    }
}
