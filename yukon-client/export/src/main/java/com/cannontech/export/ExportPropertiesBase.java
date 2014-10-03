package com.cannontech.export;

import java.util.Date;
import java.util.GregorianCalendar;

public class ExportPropertiesBase {
    private int formatID = -1;

    // CSVBILLING PROPERTIES
    private GregorianCalendar runDate = null;
    private GregorianCalendar maxTimestamp = null;
    private GregorianCalendar minTimestamp = null;

    private Character delimiter = new Character('|');
    private boolean showColumnHeadings = false;

    // DBPURGE PROPERTIES
    private int daysToRetain = 90;
    private Integer runTimeHour = null;
    private boolean purgeData = false;

    public int getFormatID() {
        return formatID;
    }

    public void setFormatID(int formatID) {
        this.formatID = formatID;
    }

    public int getDaysToRetain() {
        if (daysToRetain < 0) {
            daysToRetain = 90;
        }
        return daysToRetain;
    }

    public Character getDelimiter() {
        return delimiter;
    }

    public GregorianCalendar getMaxTimestamp() {
        if (this.maxTimestamp == null) {
            maxTimestamp = new GregorianCalendar();
            GregorianCalendar today = new GregorianCalendar();
            today.set(GregorianCalendar.HOUR_OF_DAY, 0);
            today.set(GregorianCalendar.MINUTE, 0);
            today.set(GregorianCalendar.SECOND, 0);
            maxTimestamp.setTime(today.getTime());
        }
        return this.maxTimestamp;
    }

    public GregorianCalendar getMinTimestamp() {
        if (this.minTimestamp == null) {
            minTimestamp = new GregorianCalendar();
            GregorianCalendar yesterday = new GregorianCalendar();
            yesterday.set(GregorianCalendar.HOUR_OF_DAY, 0);
            yesterday.set(GregorianCalendar.MINUTE, 0);
            yesterday.set(GregorianCalendar.SECOND, 0);

            long minTime = yesterday.getTime().getTime() - 86400000;
            yesterday.setTime(new Date(minTime));

            this.minTimestamp.setTime(yesterday.getTime());
        }
        return this.minTimestamp;
    }

    public boolean isPurgeData() {
        return purgeData;
    }

    public GregorianCalendar getRunDate() {
        return runDate;
    }

    public Integer getRunTimeHour() {
        if (runTimeHour == null) {
            if (getFormatID() == ExportFormatTypes.IONEVENTLOG_FORMAT) {
                GregorianCalendar cal = new GregorianCalendar();
                int nowHour = cal.get(GregorianCalendar.HOUR_OF_DAY);
                setRunTimeHour(nowHour);
            } else {
                runTimeHour = new Integer(1);
            }
        }
        return runTimeHour;
    }

    public boolean isShowColumnHeadings() {
        return showColumnHeadings;
    }

    public void setDaysToRetain(int daysToRetain) {
        this.daysToRetain = daysToRetain;
    }

    public void setDelimiter(Character delimiter) {
        this.delimiter = delimiter;
    }

    public void setMaxTimestamp(GregorianCalendar maxTimestamp) {
        this.maxTimestamp = maxTimestamp;
    }

    public void setMinTimestamp(GregorianCalendar minTimestamp) {
        this.minTimestamp = minTimestamp;
    }

    public void setPurgeData(boolean purgeData) {
        this.purgeData = purgeData;
    }

    public void setRunDate(GregorianCalendar runDate) {
        this.runDate = runDate;
    }

    public void setRunTimeHour(int runTimeHour) {
        this.runTimeHour = new Integer(runTimeHour);
    }

    public void setShowColumnHeadings(boolean showColumnHeadings) {
        this.showColumnHeadings = showColumnHeadings;
    }

}
