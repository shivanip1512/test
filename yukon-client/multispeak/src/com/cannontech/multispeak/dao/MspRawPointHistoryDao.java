package com.cannontech.multispeak.dao;

import java.util.Date;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.data.MspBlockReturnList;
import com.cannontech.multispeak.data.MspMeterReadReturnList;
import com.cannontech.multispeak.data.MspScadaAnalogReturnList;

public interface MspRawPointHistoryDao {
    public enum ReadBy {
        NONE, // all
        METER_NUMBER,
    }

    /**
     * Returns a MspMeterRead object with kW and kWh MeterRead readings for ReadBy value.
     * The readings are not grouped together such that there may be multiple MeterReads per meter per Attribute
     * @param readBy - readings collected for ReadBy value (ReadBy = NONE then ReadBy option not used).
     * @param readByValue
     * @param startDate - rph timestamp >= (inclusive)
     * @param endDate - rph timestamp <= (inclusive)
     * @param lastReceived - Results are retrieved for meterNumber > lastReceived. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for
     *                  that meterNumber will still be included. Meaning meterRead[].length > maxRecords is possible).
     * @return MspMeterRead
     */
    public MspMeterReadReturnList retrieveMeterReads(ReadBy readBy, String readByValue, Date startDate,
                                              Date endDate, String lastReceived, int maxRecords);

    /**
     * Returns a MspMeterRead object with kW and kWh MeterRead readings for ReadBy value.
     * Similar to retrieveMeterReads except that it returns only the latest archived reading for kWh
     * and kW elements of MeterRead.
     * The readings will be grouped together such that there is only one MeterRead per meter
     * @param readBy - readings collected for ReadBy value (ReadBy = NONE then ReadBy option not used).
     * @param readByValue
     * @param lastReceived - Results are retrieved for meterNumber > lastReceived. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for
     *            that meterNumber will still be included. Meaning meterRead[].length > maxRecordsis possible).
     * @return MspMeterRead
     */
    public MspMeterReadReturnList retrieveLatestMeterReads(ReadBy readBy, String readByValue,
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

    /**
     * Retrieves estimated load data for the SCADA_Server endpoint.  
     * It reads and returns the latest point values for all analog points attached to LM program paos, 
     * which includes the points: Connected Load, Diversified Load, Max Load Reduction, Available Load Reduction.
     * user must have LM_VISIBLE permission for the program to be included in the return
     * @return A list of SCADAAnalog data representing all analog point values for LM program paos. 
     */
    public MspScadaAnalogReturnList retrieveLatestScadaAnalogs(LiteYukonUser user) ;
}