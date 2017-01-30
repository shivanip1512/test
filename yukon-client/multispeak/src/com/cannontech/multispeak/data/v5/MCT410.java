package com.cannontech.multispeak.data.v5;

import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.msp.beans.v5.enumerations.FieldNameKind;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCode;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCodeItem;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCodeItems;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCodeOption;
import com.cannontech.msp.beans.v5.multispeak.ReadingValue;
import com.cannontech.msp.beans.v5.multispeak.ReadingValues;
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
                        // Reading Timestamp
                        ReadingValues readingValues = new ReadingValues();
                        ReadingValue readingValue = new ReadingValue();
                        readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                        
                        readingValue.setValue(String.valueOf(value.intValue()));
                        readingValues.getReadingValue().add(readingValue );
                        getMeterRead().setReadingValues(readingValues);
                        
                        // Reading Type Code
                        ReadingTypeCodeItems readingTypeCodeItems = new ReadingTypeCodeItems();
                        ReadingTypeCodeItem readingTypeCodeItem = new ReadingTypeCodeItem();
                        ReadingTypeCode readingTypeCode = new ReadingTypeCode();
                        readingTypeCode.setFieldName(FieldNameKind.POS_K_WH);
                        readingTypeCodeItem.setReadingTypeCode(readingTypeCode );
                        readingTypeCodeItems.getReadingTypeCodeItem().add(readingTypeCodeItem );
                        getMeterRead().setReadingTypeCodeItems(readingTypeCodeItems );
                        
                        ReadingTypeCodeOption option = new ReadingTypeCodeOption();
                        option.setReadingTypeCode(readingTypeCode);
                        readingValue.setReadingTypeCodeOption(option);
                        
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
                        // Reading Timestamp
                        ReadingValues readingValues = new ReadingValues();
                        ReadingValue readingValue = new ReadingValue();
                        readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                        
                        readingValue.setValue(String.valueOf(value.intValue()));
                        readingValues.getReadingValue().add(readingValue );
                        getMeterRead().setReadingValues(readingValues);
                        
                        // Reading Type Code
                        ReadingTypeCodeItems readingTypeCodeItems = new ReadingTypeCodeItems();
                        ReadingTypeCodeItem readingTypeCodeItem = new ReadingTypeCodeItem();
                        ReadingTypeCode readingTypeCode = new ReadingTypeCode();
                        readingTypeCode.setFieldName(FieldNameKind.MAX_DEMAND);
                        readingTypeCodeItem.setReadingTypeCode(readingTypeCode );
                        readingTypeCodeItems.getReadingTypeCodeItem().add(readingTypeCodeItem );
                        getMeterRead().setReadingTypeCodeItems(readingTypeCodeItems );
                        
                        ReadingTypeCodeOption option = new ReadingTypeCodeOption();
                        option.setReadingTypeCode(readingTypeCode);
                        readingValue.setReadingTypeCodeOption(option);
                        
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
