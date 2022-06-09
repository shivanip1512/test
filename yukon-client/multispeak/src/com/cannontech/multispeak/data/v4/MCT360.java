package com.cannontech.multispeak.data.v4;

import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.msp.beans.v4.ArrayOfReadingValue;
import com.cannontech.msp.beans.v4.ReadingValue;
import com.cannontech.multispeak.client.MultispeakFuncs;

/**
 * Class which represents billing data for an MCT360
 */
public class MCT360 extends MCTXXX {

    @Override
    public void populate(PointIdentifier pointIdentifier, Date dateTime, Double value) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(dateTime.getTime());
        
        switch (pointIdentifier.getPointType()) {
        case PulseAccumulator:
            switch (pointIdentifier.getOffset()) {
            case 3: // KWh
                ArrayOfReadingValue arrayOfReadingValue = new ArrayOfReadingValue();
                ReadingValue readingValue = new ReadingValue();

                readingValue.setValue(String.valueOf(value.intValue()));
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                readingValue.setFieldName(FieldNamesMspV4.USAGE.getFieldName());
                arrayOfReadingValue.getReadingValue().add(readingValue);

                getMeterReading().setReadingValues(arrayOfReadingValue);
                setPopulated(true);
                break;
            }
            
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
