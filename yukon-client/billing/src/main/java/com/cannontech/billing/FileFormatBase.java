package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:07:05 PM)
 * 
 * @author:
 */
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import com.cannontech.billing.format.simple.SimpleBillingFormatBase;
import com.cannontech.billing.record.BillingRecordBase;
import com.cannontech.billing.record.MVRSRecord;
import com.cannontech.billing.record.MVRS_KetchikanRecord;
import com.cannontech.billing.record.StringRecord;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

public abstract class FileFormatBase extends SimpleBillingFormatBase {
    // number of records
    private int count = 0;

    // used to store every line of output that will be written to the file
    // it holds Objects of type BillingRecordBase
    private Vector<BillingRecordBase> recordVector = null;
    private Hashtable<Integer, Double> pointIDMultiplierHashTable = null;

    /**
     * FileFormatBase constructor comment.
     */
    public FileFormatBase() {
        super();
    }

    /**
     * Returns the record vector as a string buffer formatted
     * by each BillingRecordBase dataToString() format.
     * Creation date: (11/29/00)
     * 
     * @return java.lang.StringBuffer
     */
    private StringBuffer getOutputAsStringBuffer() {
        StringBuffer returnBuffer = new StringBuffer();
        this.count = 0; // reset the count

        if (getBillingFileDefaults().getFormatID() == FileFormatTypes.MVRS
            || getBillingFileDefaults().getFormatID() == FileFormatTypes.MVRS_KETCHIKAN)// special case!!!
        {
            MVRSRecord mvrsRecord;
            // create an instance of the record and call the dataToString, this reads a file instead
            // of being a preloaded vector of records from the database.
            if (getBillingFileDefaults().getFormatID() == FileFormatTypes.MVRS_KETCHIKAN) {
                mvrsRecord = new MVRS_KetchikanRecord();
            } else {
                mvrsRecord = new MVRSRecord();
            }

            mvrsRecord.setInputFile(getBillingFileDefaults().getInputFileDir());
            returnBuffer.append(mvrsRecord.dataToString());
            // set the record format's record count, based on the number of meter records in the file
            this.count = mvrsRecord.getNumberMeters();
        } else {
            for (int i = 0; i < getRecordVector().size(); i++) {
                String dataString = getRecordVector().get(i).dataToString();
                if (dataString != null) {
                    returnBuffer.append(dataString);
                }
            }
        }

        return returnBuffer;
    }

    /**
     * Returns a hashtable of pointid as key and multiplier as value.
     * 
     * @return java.util.Hashtable
     */
    public Hashtable<Integer, Double> getPointIDMultiplierHashTable() {
        if (pointIDMultiplierHashTable == null) {
            pointIDMultiplierHashTable = retrievePointIDMultiplierHashTable();
        }
        return pointIDMultiplierHashTable;
    }

    /**
     * Returns a vector of (billingRecordBase) records.
     * Creation date: (11/29/00)
     * 
     * @return java.util.Vector
     */
    public Vector<BillingRecordBase> getRecordVector() {
        if (recordVector == null) {
            recordVector = new Vector<>(150);
        }
        return recordVector;
    }

    /**
     * Returns true if the billing data retrieval was successfull
     * Retrieves values from the database and inserts them in a FileFormatBase object.
     * 
     * @param dbAlias the database name string alias
     * @return boolean
     */
    abstract public boolean retrieveBillingData();

    /**
     * Returns the number of records collected.
     * 
     * @return int
     */
    @Override
    public int getReadingCount() {
        if (count == 0) {
            if (getRecordVector() != null) {
                for (int i = 0; i < getRecordVector().size(); i++) {
                    if (!(getRecordVector().get(i) instanceof StringRecord)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Returns a hashtable of pointid as key and multiplier as value.
     * Collects the pointid/multiplier from the database.
     * 
     * @return java.util.Hashtable
     */
    private Hashtable<Integer, Double> retrievePointIDMultiplierHashTable() {
        Hashtable<Integer, Double> multiplierHashTable = new Hashtable<>();

        String sql = new String("SELECT ACC.POINTID, ACC.MULTIPLIER FROM POINTACCUMULATOR ACC");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if (conn == null) {
                CTILogger.info(":  Error getting database connection.");
                return null;
            }
            pstmt = conn.prepareStatement(sql.toString());
            rset = pstmt.executeQuery();

            while (rset.next()) {
                Integer pointID = new Integer(rset.getInt(1));
                Double multiplier = new Double(rset.getDouble(2));
                multiplierHashTable.put(pointID, multiplier);
            }

            sql = new String("SELECT ANA.POINTID, ANA.MULTIPLIER FROM POINTANALOG ANA");
            pstmt = conn.prepareStatement(sql.toString());
            rset = pstmt.executeQuery();

            while (rset.next()) {
                Integer pointID = new Integer(rset.getInt(1));
                Double multiplier = new Double(rset.getDouble(2));
                multiplierHashTable.put(pointID, multiplier);
            }
        } catch (SQLException e) {
            CTILogger.error(e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }

        return multiplierHashTable;
    }

    /**
     * Writes the record string to the output stream.
     * 
     * @param out java.io.OutputStream
     */
    @Override
    public boolean writeToFile(OutputStream out) throws java.io.IOException {

        if (getBillingFileDefaults().getDeviceGroups().isEmpty()) {
            return false;
        }

        boolean success = retrieveBillingData();
        if (success) {
            out.write(getOutputAsStringBuffer().toString().getBytes());
        }
        return success;
    }

    @Override
    public String toString() {
        return FileFormatTypes.getFormatType(billingFileDefaults.getFormatID());
    }
}
