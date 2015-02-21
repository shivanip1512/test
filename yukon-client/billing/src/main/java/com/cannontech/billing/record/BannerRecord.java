package com.cannontech.billing.record;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

/**
 * Fixed length format. No delimiter
 * Field    Field Description   Format          Size    Start   End
 * 1        Premise #                           5       1       5
 * 2        Service #           Leading Zero    3       6       8
 * 3        Blanks                              21      9       29
 * 4        Customer Meter Number Lagging Space 21      30      50
 * 5        Reading             Leading Zero    9       51      59
 * 6        Filler "00"                         2       60      61
 * 7        Date                YYMMDD          6       62      67
 * 8        Time                HHMMSS          6       68      73
 * 9        Blanks                              4       74      77
 * 10       Route #             Leading Zero    6       78      83
 * 11       Scat #              Leading Zero    2       84      85
 * 12       Blanks                              10      86      95
 * 13       Premise # (repeated)                5       96      100
 * 14       Service # (repeated)                3       101     103
 * 15       Blanks                              29      104      132
 */
public class BannerRecord implements BillingRecordBase {
    
    private final BannerData bannerData;
    private final BuiltInAttribute attribute;
    private final Double reading; 
    private final Date readDate;
    
    private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyMMdd");
    private static java.text.SimpleDateFormat TIME_FORMAT = new java.text.SimpleDateFormat("HHmmss");
    private static java.text.DecimalFormat KW_FORMAT_6v3 = new java.text.DecimalFormat("000000.000");
    private static java.text.DecimalFormat KWH_FORMAT_9 = new java.text.DecimalFormat("000000000");
    
    public BannerRecord(BannerData bannerData, BuiltInAttribute attribute, Double reading, Date readDate) {
        this.bannerData = bannerData;
        this.attribute = attribute;
        this.reading = reading;
        this.readDate = readDate;
    }

    @Override
    public String dataToString() {
        StringBuilder writeToFile = new StringBuilder();
        writeToFile.append(StringUtils.leftPad(getBannerData().getPremiseNumber(),  5)); // length 5, pad spaces
        writeToFile.append(StringUtils.leftPad(getBannerData().getServiceNumber(),  3, '0'));    //length 3, pad 0
        writeToFile.append(StringUtils.leftPad("", 21));    // length 21, blanks
        writeToFile.append(StringUtils.rightPad(getBannerData().getMeterNumber(),  21));    //length 3, pad spaces
        writeToFile.append(getFormattedReading());
        writeToFile.append("00");
        writeToFile.append(getFormattedReadDate());
        writeToFile.append(getFormattedReadTime());
        writeToFile.append(StringUtils.leftPad("", 4));    // length 4, blanks
        writeToFile.append(StringUtils.leftPad(getBannerData().getRouteNumber(),  6, '0'));    //length 6, pad 0
        writeToFile.append(StringUtils.leftPad(String.valueOf(getBannerData().getScatNumber()),  2, '0'));    //length 2, pad 0
        writeToFile.append(StringUtils.leftPad("", 10));    // length 10, blanks
        writeToFile.append(StringUtils.leftPad(getBannerData().getPremiseNumber(),  5)); // length 5, pad spaces
        writeToFile.append(StringUtils.leftPad(getBannerData().getServiceNumber(),  3, '0'));    //length 3, pad 0
        writeToFile.append(StringUtils.leftPad("", 28));    // length 28, blanks // NOTE: Using 28 as length because the \r\n line separator is adding one to the total length
        writeToFile.append(System.getProperty("line.separator"));
        return writeToFile.toString();
    }

    public BannerData getBannerData() {
        return bannerData;
    }
    
    public String getFormattedReading() {
        String formattedReading;
        switch (attribute) {
        case USAGE:
        case USAGE_WATER:
            formattedReading = KWH_FORMAT_9.format(reading);
            break;
        case PEAK_DEMAND:
        default:
            formattedReading = StringUtils.remove(KW_FORMAT_6v3.format(reading), ".");
            break;
        }
        return formattedReading;
    }

    private String getFormattedReadDate() {
        return DATE_FORMAT.format(readDate);
    }

    private String getFormattedReadTime() {
        return TIME_FORMAT.format(readDate);
    }
}
