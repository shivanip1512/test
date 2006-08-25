package com.cannontech.billing.format;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.billing.device.base.BillableField;

/**
 * Class used to create a billing file with row format as follows:
 * 
 * <pre>
 *   
 *     &quot;pppppppppppppppppppp,nnnnnnnnnnnnnnnnnnnn,ccccccc.cc,MM/dd/yyyy,HH:MM,q&quot;
 *      
 *     p - pao name
 *     n - total consumption point name
 *     c - total consumption
 *     MM/dd/yyyy - total consumption date
 *     HH:MM - total consumption time
 *     q - quality
 *     
 * </pre>
 */
public class OPURecordFormatter extends BillingFormatterBase {

    private static final String QUALITY = "N";

    @Override
    public String dataToString(BillableDevice device) {

        StringBuffer writeToFile = new StringBuffer("\"");

        addToStringBufferWithTrailingFiller(writeToFile,
                                            device.getData(BillableField.paoName),
                                            20,
                                            " ",
                                            true);

        addToStringBufferWithTrailingFiller(writeToFile,
                                            device.getData(BillableField.totalConsumption),
                                            20,
                                            " ",
                                            true);

        Double totalConsumptionValue = device.getCalculatedValue(BillableField.totalConsumption);
        if (totalConsumptionValue == null) {
            return "";
        }
        addToStringBufferWithPrecedingFiller(writeToFile, format(totalConsumptionValue,
                                                                 DECIMAL_FORMAT_7V2), 10, " ", true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(BillableField.totalConsumption),
                                              DATE_FORMAT), true);

        addToStringBuffer(writeToFile, format(device.getTimestamp(BillableField.totalConsumption),
                                              TIME_FORMAT), true);

        writeToFile.append(QUALITY + "\"\r\n");

        return writeToFile.toString();
    }

}
