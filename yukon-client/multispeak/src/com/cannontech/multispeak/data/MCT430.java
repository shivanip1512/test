package com.cannontech.multispeak.data;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.multispeak.client.MultispeakFuncs;

/**
 * Class which represents billing data for an MCT430
 */
public class MCT430 extends MeterReadBase {

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
                    case 2: // kWh Channel 2
                    case 3: // kWh Channel 3
                    case 4: // kWh Channel 4
                }
                break;
            case DemandAccumulator:
                switch (pointIdentifier.getOffset()) {
    
                    case 11: // Peak KW Channel 1
                        getMeterRead().setKW(new Float(value.floatValue()));
                        getMeterRead().setKWDateTime(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                        setPopulated(true);
                        break;
                    case 12: // Peak KW Channel 2
                    case 13: // Peak KW Channel 3
                    case 14: // Peak KW Channel 4
                }
                break;
            case Analog:

                switch (pointIdentifier.getOffset()) {
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
                case 21: // Peak kW (IED)
                case 22: // Peak kM (Coincidental) (IED)
                }
                break;
        }
    }
}
