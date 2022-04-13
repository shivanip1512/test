package com.cannontech.multispeak.dao.v4;

import java.util.Date;
import com.cannontech.multispeak.data.v4.MspMeterReadingReturnList;


public interface MspRawPointHistoryDao {
    public enum ReadBy {
        NONE, // all
        METER_NUMBER,
    }

    /**
     * Returns a MspMeterReading object with kW and kWh MeterReading readings for ReadBy value.
     * The readings are not grouped together such that there may be multiple MeterReads per meter per Attribute
     * @param readBy - readings collected for ReadBy value (ReadBy = NONE then ReadBy option not used).
     * @param readByValue
     * @param startDate - rph timestamp >= (inclusive)
     * @param endDate - rph timestamp <= (inclusive)
     * @param lastReceived - Results are retrieved for meterNumber > lastReceived. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for
     *                  that meterNumber will still be included. Meaning meterRead[].length > maxRecords is possible).
     * @return MspMeterReading
     */
    public MspMeterReadingReturnList retrieveMeterReading(ReadBy readBy, String readByValue, Date startDate,
                                              Date endDate, String lastReceived, int maxRecords);

    /**
     * Returns a MspMeterReading object with kW and kWh MeterReadings for ReadBy value.
     * Similar to retrieveMeterReading except that it returns only the latest archived reading for kWh
     * and kW elements of MeterRead.
     * The readings will be grouped together such that there is only one MeterRead per meter
     * @param readBy - readings collected for ReadBy value (ReadBy = NONE then ReadBy option not used).
     * @param readByValue
     * @param lastReceived - Results are retrieved for meterNumber > lastReceived. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for
     *            that meterNumber will still be included. Meaning meterReading[].length > maxRecordsis possible).
     * @return MspMeterReading
     */
    public MspMeterReadingReturnList retrieveLatestMeterReading(ReadBy readBy, String readByValue,
                                                    String lastReceived, int maxRecords);

 }