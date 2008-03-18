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
 *     aaaaa,pppppppppppppppppppp,tttttttt.ttt,MM/dd/yy,HH:MM,q
 *      
 *     a - address (variable length)
 *     p - total consumption point name
 *     t - total consumption
 *     MM/dd/yy - total consumption date
 *     HH:MM - total consumption time
 *     q - quality
 *     
 * </pre>
 */
public class CTIStandard2RecordFormatter extends BillingFormatterBase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");

    private static final String QUALITY = "N";

    @Override
    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer();

        String data = device.getData(ReadingType.DEVICE_DATA, BillableField.address);
        addToStringBuffer(writeToFile, data, true);

        addToStringBufferWithTrailingFiller(writeToFile,
                                            device.getData(ReadingType.DEVICE_DATA, BillableField.totalConsumption),
                                            20,
                                            " ",
                                            true);
        Double totalConsumptionValue = device.getValue(ReadingType.ELECTRIC, BillableField.totalConsumption);
        if (totalConsumptionValue == null) {
            return "";
        }
        addToStringBuffer(writeToFile, format(totalConsumptionValue, DECIMAL_FORMAT_8V3), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                              DATE_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(ReadingType.ELECTRIC, BillableField.totalConsumption),
                                              TIME_FORMAT), true);

        writeToFile.append(QUALITY + "\r\n");

        return writeToFile.toString();
    }

}
