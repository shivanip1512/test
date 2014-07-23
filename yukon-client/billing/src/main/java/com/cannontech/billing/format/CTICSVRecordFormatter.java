package com.cannontech.billing.format;

import java.text.SimpleDateFormat;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.dynamicBilling.model.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *                
 *                  pppppppppppppppppppp,ttttttttttttt,MM-dd-yy,HH:MM,s
 *                   
 *                  p - pao name
 *                  t - total consumption
 *                  MM-dd-yy - total consumption date
 *                  HH:MM - total consumption time
 *                  s - status
 *                  
 * </pre>
 */
public class CTICSVRecordFormatter extends BillingFormatterBase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yy");

    private static final String MEASURE_LABEL = "KWH";
    private static final String STATUS = "N";

    @Override
    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer();
        String paoName = device.getData(ReadingType.DEVICE_DATA, BillableField.paoName);

        writeToFile.append(this.getFormattedRow(device, paoName, BillableField.totalPeakDemand));
        writeToFile.append(this.getFormattedRow(device, paoName, BillableField.totalConsumption));

        return writeToFile.toString();

    }

    private String getFormattedRow(BillableDevice device, String paoName, BillableField field) {

        StringBuffer writeToFile = new StringBuffer();
        
        addToStringBufferWithTrailingFiller(writeToFile, paoName, 20, " ", true);

        if (device.getValue(ReadingType.ELECTRIC, field) == null) {
            return "";
        }

        addToStringBufferWithPrecedingFiller(writeToFile,
                                             format(device.getValue(ReadingType.ELECTRIC, field),
                                                    DECIMAL_FORMAT_10V2),
                                             13,
                                             " ",
                                             true);

        String unitOfMeasure = device.getUnitOfMeasure(ReadingType.ELECTRIC, field);
        // Default to 'KWH'
        if (unitOfMeasure == null) {
            unitOfMeasure = MEASURE_LABEL;
        }
        addToStringBufferWithTrailingFiller(writeToFile, unitOfMeasure, 6, " ", true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, field), DATE_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, field), TIME_FORMAT), true);

        writeToFile.append(STATUS + "\r\n");

        return writeToFile.toString();

    }

}
