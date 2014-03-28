package com.cannontech.billing.record;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

/**
 * Fixed length format. No delimiter
 * Field    Field Description   Format          Size    Start   End
 * 1        Premise #                           5       1       5
 * 2        Service #           Leading Zero    3       6       8
 * 3        Blanks                              21      9       29
 * 4        Customer Meter Number               21      30      50
 * 5        Reading             Leading Zero    9       51      59
 * 6        Filler "00"                         2       60      61
 * 7        Date                YYMMDD          6       62      67
 * 8        Time                HHMMSS          6       68      73
 * 9        Blanks                              4       74      77
 * 10       Route #             Leading Zero    6       78      83
 * 11       Scat #              Leading Zero    2       84      85
 * 12       Blanks                              47      86      132
 */
public class BannerRecord implements BillingRecordBase {
    
    private BannerData bannerData;
    private BuiltInAttribute attribute;
    private String reading;
    private String readDate;     //yymmdd
    private String readTime;     //hhmmss
    
    private static java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyMMdd");
    private static java.text.SimpleDateFormat TIME_FORMAT = new java.text.SimpleDateFormat("HHmmss");
    private static java.text.DecimalFormat KW_FORMAT_5v3 = new java.text.DecimalFormat("00000.000");
    private static java.text.DecimalFormat KWH_FORMAT_9 = new java.text.DecimalFormat("000000000");
    
    public BannerRecord(BannerData bannerData, BuiltInAttribute attribute) {
        this.bannerData = bannerData;
        this.attribute = attribute;
    }

    @Override
    public String dataToString() {
        
        StringBuffer writeToFile = new StringBuffer();
        writeToFile.append(StringUtils.leftPad(getBannerData().getPremiseNumber(),  5)); // length 5, pad spaces
        writeToFile.append(StringUtils.leftPad(getBannerData().getServiceNumber(),  3, '0'));    //length 3, pad 0
        writeToFile.append(StringUtils.leftPad("", 21));    // length 21, blanks
        writeToFile.append(StringUtils.leftPad(getBannerData().getMeterNumber(),  21));    //length 3, pad spaces
        writeToFile.append(getReading());
        writeToFile.append("00");
        writeToFile.append(getReadDate());
        writeToFile.append(getReadTime());
        writeToFile.append(StringUtils.leftPad("", 4));    // length 4, blanks
        writeToFile.append(StringUtils.leftPad(getBannerData().getRouteNumber(),  6, '0'));    //length 6, pad 0
        writeToFile.append(StringUtils.leftPad(getScatNumber(),  2, '0'));    //length 2, pad 0
        writeToFile.append(StringUtils.leftPad("", 47));    // length 47, blanks
        writeToFile.append("\r\n");
        return writeToFile.toString();
    }

    public BannerData getBannerData() {
        return bannerData;
    }

    private String getReading() {
        return reading;
    }

    public void setReading(Double reading) {
        switch (attribute) {
        case USAGE: {
            this.reading = KWH_FORMAT_9.format(reading);
            break;
        }
        case PEAK_DEMAND: {
            this.reading = KW_FORMAT_5v3.format(reading);
            break;
        }
        default:
            this.reading = KW_FORMAT_5v3.format(reading);
        }
    }

    public void setTimestamp(Date readDate) {
        this.readDate = DATE_FORMAT.format(readDate);
        this.readTime = TIME_FORMAT.format(readDate);
    }

    private String getReadDate() {
        return readDate;
    }

    private String getReadTime() {
        return readTime;
    }

    private String getScatNumber() {
        switch (attribute) {
        case USAGE:
            return "1";
        case PEAK_DEMAND:
            return "2";
        default:
            return "0";
        }
    }

}
