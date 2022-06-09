package com.cannontech.multispeak.data.v4;

import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.msp.beans.v4.ArrayOfReadingValue;
import com.cannontech.msp.beans.v4.ReadingValue;
import com.cannontech.multispeak.client.MultispeakFuncs;

/**
 * Class which represents billing data for an MCT410
 */
public class MCT410 extends MCTXXX {

    @Override
    public void populate(PointIdentifier pointIdentifier, Date dateTime, Double value) {

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(dateTime.getTime());
        
        switch (pointIdentifier.getPointType()) {
            case PulseAccumulator:
                switch (pointIdentifier.getOffset()) {
                case 1: // KWh
                    ArrayOfReadingValue arrayOfReadingValue = new ArrayOfReadingValue();
                    ReadingValue readingValue = new ReadingValue();
                    
                    readingValue.setValue(String.valueOf(value.intValue()));
                    readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                    readingValue.setFieldName(FieldNamesMspV4.USAGE.getFieldName());
                    arrayOfReadingValue.getReadingValue().add(readingValue);
                   
                    getMeterReading().setReadingValues(arrayOfReadingValue);
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
                    ArrayOfReadingValue arrayOfReadingValue = new ArrayOfReadingValue();
                    ReadingValue readingValue = new ReadingValue();

                    readingValue.setValue(String.valueOf(value.intValue()));
                    readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                    readingValue.setFieldName(FieldNamesMspV4.PEAK_DEMAND.getFieldName());
                    arrayOfReadingValue.getReadingValue().add(readingValue);

                    getMeterReading().setReadingValues(arrayOfReadingValue);
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
