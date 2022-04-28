package com.cannontech.multispeak.dao.v4;

import java.util.Date;

import com.cannontech.multispeak.block.v4.Block;
import com.cannontech.multispeak.data.v4.MspBlockReturnList;
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
     * and kW elements of MeterReading.
     * The readings will be grouped together such that there is only one MeterReading per meter
     * @param readBy - readings collected for ReadBy value (ReadBy = NONE then ReadBy option not used).
     * @param readByValue
     * @param lastReceived - Results are retrieved for meterNumber > lastReceived. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for
     *            that meterNumber will still be included. Meaning meterReading[].length > maxRecordsis possible).
     * @return MspMeterReading
     */
    public MspMeterReadingReturnList retrieveLatestMeterReading(ReadBy readBy, String readByValue,
                                                    String lastReceived, int maxRecords);
    
    /**
     * Returns a list of Blocks.
     * @param readBy - readings collected for ReadBy value (ReadBy = NONE then ReadBy option not used).
     * @param readByValue
     * @param blockProcessService - service to use when defining type of block and how to load
     * @param startDate - rph timestamp >= (inclusive)
     * @param endDate - rph timestamp <= (inclusive)
     * @param lastReceived - Results are retrieved for meterNumber > lastReceived. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for
     *            that meterNumber will still be included. Meaning meterRead[].length > maxRecords
     *            is possible). More specifically, maxRecords is equivalent
     *            to the number of actual Meters that data is being returned for.
     * @return MspBlock
     */
    public MspBlockReturnList retrieveBlock(ReadBy readBy, String readByValue,
                                     FormattedBlockProcessingService<Block> blockProcessingService,
                                     Date startDate, Date endDate, String lastReceived,
                                     int maxRecords);

    /**
     * Returns a FormattedBlock of type block.
     * @param blockProcessService - service to use when defining type of block and how to load
     * @param lastReceived - Results are retrieved for meterNumber > lastReceived. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for
     *            that meterNumber will still be included. Meaning meterRead[].length > maxRecords
     *            is possible). More specifically, maxRecords is equivalent
     *            to the number of actual Meters that data is being returned for.
     * @return MspBlock
     */
    public MspBlockReturnList retrieveLatestBlock(FormattedBlockProcessingService<Block> blockProcessingService,
                                           String lastReceived, int maxRecords);

 }