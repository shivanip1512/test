package com.cannontech.multispeak.dao;

import java.util.Date;

import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.MeterRead;

public interface MspRawPointHistoryDao
{    
    public enum ReadBy {
        NONE,	//all
        METER_NUMBER,
    }

    /**
     * Returns a MeterRead[] with kW and kWh readings for ReadBy value.
     * Excludes point offsets 101 - 104.
     * @param readBy - readings collected for ReadBy value (ReadBy = NONE then ReadBy option not used).
     * @param readByValue
     * @param startDate - rph timestamp >= (inclusive)
     * @param endDate - rph timestamp <= (inclusive)
     * @param lastReceived - Results are retrieved for meterNumber > lastRecieved. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for 
     *  that meterNumber will still be included. Meaning meterRead[].length > maxRecords is possible).
     * @return MeterRead[]
     */
    public MeterRead[] retrieveMeterReads(ReadBy readBy, String readByValue, Date startDate, Date endDate, String lastReceived, int maxRecords);
    
    /**
     * Returns a MeterRead[] with kW and kWh readings.
     * RPH readings are for the latest timestamp archived.
     * Excludes point offsets 101 - 104.
     * List size is limited to maxRecords (NOTE: If maxRecords is exceeded over the same meterNumber, all results for 
     *  that meterNumber will still be included. Meaning meterRead[].length > maxRecords is possible).
     * Results are retrieved for meterNumber > lastRecieved. LastReceived == null means start from begining.
     * @param lastReceived - Results are retrieved for meterNumber > lastRecieved. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for 
     *  that meterNumber will still be included. Meaning meterRead[].length > maxRecords is possible).     * @return
     * @return MeterRead[]
     */
    public MeterRead[] retrieveLatestMeterReads(String lastReceived, int maxRecords);

    /**
     * Returns a FormattedBlock of type block.
     * @param block
	 * @param startDate - rph timestamp >= (inclusive)
     * @param endDate - rph timestamp <= (inclusive)
     * @param lastReceived - Results are retrieved for meterNumber > lastRecieved. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for 
     *  that meterNumber will still be included. Meaning meterRead[].length > maxRecords is possible).
     * @return FormattedBlock
     */
    public FormattedBlock retrieveBlock(FormattedBlockService<Block> block, Date startDate, Date endDate, String lastReceived, int maxRecords);

    /**
     * Returns a FormattedBlock of type block. 
     * @param block
     * @param lastReceived - Results are retrieved for meterNumber > lastRecieved. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for 
     *  that meterNumber will still be included. Meaning meterRead[].length > maxRecords is possible).
     * @return FormattedBlock
     */
    public FormattedBlock retrieveLatestBlock(FormattedBlockService<Block> block, String lastReceived, int maxRecords);

    /**
     * Returns a FormattedBlock of type block. 
     * @param block
	 * @param startDate - rph timestamp >= (inclusive)
     * @param endDate - rph timestamp <= (inclusive)
     * @param lastReceived - Results are retrieved for meterNumber > lastRecieved. LastReceived == null means start from beginning.
     * @param maxRecords - Max results returned (NOTE: If maxRecords is exceeded over the same meterNumber, all results for 
     *  that meterNumber will still be included. Meaning meterRead[].length > maxRecords is possible).
     * @return FormattedBlock
     */
    public FormattedBlock retrieveBlockByMeterNo(FormattedBlockService<Block> block, Date startDate, Date endDate, String meterNumber, int maxRecords);
}