package com.cannontech.billing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.billing.device.BillableDeviceFactory;
import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.billing.device.base.DeviceData;
import com.cannontech.billing.format.ExtendedTOURecordFormatter;
import com.cannontech.billing.mainprograms.BillingFileDefaults;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.spring.YukonSpringHook;

/**
 * Class used to retrieve billing data
 */
public class BillingDao {

    /**
     * Retrieves billing data from the database and returns a list of
     * BillableDevices which contain all of the billing data
     * @param defaults - Information about the billing data to be retrieved
     * @param multiplier - Reading value multiplier
     * @return A list of BillableDevices
     */
    public static List<BillableDevice> retrieveBillingData(final BillingFileDefaults defaults) {

        if( defaults.getFormatID() == FileFormatTypes.NISC_INTERVAL_READINGS) {
            return retrieveIntervalReadingsData(defaults);
        }
        
        long timer = System.currentTimeMillis();


        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT");
        sql.append("     dmg.meternumber,");
        sql.append("     p.pointid,");
        sql.append("     p.pointoffset,");
        sql.append("     p.pointtype,");
        sql.append("     p.pointname,");
        sql.append("     rph.timestamp,");
        sql.append("     rph.value,");
        sql.append("     dmg.deviceid,");
        sql.append("     ypo.type,");
        sql.append("     ypo.category,");
        sql.append("     uom.uomid,");
        sql.append("     ypo.paoname,");
        sql.append("     dcs.address");
        sql.append(" FROM");
        sql.append("     devicemetergroup dmg,");
        sql.append("     point p,");
        sql.append("     rawpointhistory rph,");
        sql.append("     yukonpaobject ypo");
        sql.append("     LEFT JOIN devicecarriersettings dcs");
        sql.append("         ON ypo.paobjectid = dcs.deviceid,");
        sql.append("     unitmeasure uom,");
        sql.append("     pointunit pu,");
        sql.append("     (SELECT");
        sql.append("         r.pointid,");
        sql.append("         max(r.timestamp) AS rDate");
        sql.append("     FROM");
        sql.append("         (SELECT");
        sql.append("             *");
        sql.append("         FROM");
        sql.append("             rawpointhistory");
        sql.append("         WHERE");
        sql.append("             timestamp > ").appendArgument(defaults.getEarliestStartDate());
        sql.append("             AND timestamp <= ").appendArgument(defaults.getEndDate()).append(") r");
        sql.append("     GROUP BY");
        sql.append("         pointid) irph");
        sql.append(" WHERE");
        sql.append("     p.pointid = irph.pointid");
        sql.append("     AND pu.pointid = p.pointid");
        sql.append("     AND uom.uomid = pu.uomid");
        sql.append("     AND rph.pointid = irph.pointid");
        sql.append("     AND rph.timestamp = irph.rDate");
        sql.append("     AND ypo.paobjectid = p.paobjectid");
        sql.append("     AND dmg.deviceid = ypo.paobjectid");
        sql.appendFragment(getDeviceMeterGroupWhereClause(defaults));
        sql.append(" ORDER BY");
        sql.append("     dmg.meternumber,");
        sql.append("     dmg.deviceid,");
        sql.append("     p.pointoffset,");
        sql.append("     rph.timestamp DESC");

        CTILogger.info("SQL Statement: " + sql);

        JdbcTemplate jdbcTemplate = YukonSpringHook.getBean("jdbcTemplate", JdbcTemplate.class);


        final List<BillableDevice> deviceList = new ArrayList<BillableDevice>();
        jdbcTemplate.query(sql.getSql(), sql.getArguments(), new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rset) throws SQLException, DataAccessException {

                Map<String, String> accountNumberMap = null;
                if (defaults.getFormatID() == FileFormatTypes.CADP ||
                        defaults.getFormatID() == FileFormatTypes.CADPXL2 ) {
                    accountNumberMap = retrieveAccountNumbers();
                }

                Map<String, Integer> meterPositionNumberMap = new HashMap<String, Integer>();
                Hashtable<Integer, Double> pointIdMultiplierHashTable = new Hashtable<Integer, Double>();
                if (defaults.isRemoveMultiplier()) {
                    //Don't do all the work against the point tables if we don't need to 
                    pointIdMultiplierHashTable = retrievePointIDMultiplierHashTable();
                }

                int currentPointID = 0;
                int currentDeviceID = 0;
                int lastPointID = 0;
                int lastDeviceID = 0;

                BillableDevice device = null;
                while (rset.next()) {
                    String meterNumber = rset.getString(1);
                    currentPointID = rset.getInt(2);
                    int ptOffset = rset.getInt(3);
                    String ptType = rset.getString(4);
                    String pointName = rset.getString(5);
                    Timestamp ts = rset.getTimestamp(6);

                    double multiplier = 1;
                    if (defaults.isRemoveMultiplier()) {
                        Double pointIdMult = pointIdMultiplierHashTable.get(new Integer(currentPointID));
                        if ( pointIdMult != null) {	//Possibility that status points might be returned...which don't have multipliers!
                            multiplier = pointIdMult;
                        }
                    }

                    double reading = rset.getDouble(7) / multiplier;
                    currentDeviceID = rset.getInt(8);
                    String paoType = rset.getString(9);
                    String category = rset.getString(10);
                    int unitOfMeasure = rset.getInt(11);
                    String paoName = rset.getString(12);
                    int address = rset.getInt(13);

                    int pointType = PointTypes.getType(ptType);
                    DevicePointIdentifier devicePointIdentifier = new DevicePointIdentifier(pointType, ptOffset);
                    java.util.Date tsDate = new java.util.Date(ts.getTime());

                    String accountNumber = (accountNumberMap == null ? paoName : accountNumberMap.get(meterNumber));

                    if (currentPointID != lastPointID) {
                        lastPointID = currentPointID;

                        if (currentDeviceID == lastDeviceID) {

                            if (device != null) {

                                if( (device.isEnergy(devicePointIdentifier) &&  !tsDate.before(defaults.getEnergyStartDate()) )  || 
                                        (device.isDemand(devicePointIdentifier) &&  !tsDate.before(defaults.getDemandStartDate()) ) ) { 

                                    DeviceData meterData = new DeviceData(meterNumber,
                                                                          getMeterPositionNumber(meterPositionNumberMap,
                                                                                                 accountNumber),
                                                                                                 String.valueOf(address),
                                                                                                 accountNumber,
                                                                                                 paoName);
                                    device.populate(devicePointIdentifier,
                                                    ts,
                                                    reading,
                                                    unitOfMeasure,
                                                    pointName,
                                                    meterData);
                                }                                    

                            }

                        } else {
                            device = BillableDeviceFactory.createBillableDevice(category, paoType);

                            if (device != null) {

                                if( (device.isEnergy(devicePointIdentifier) &&  !tsDate.before(defaults.getEnergyStartDate()) )  || 
                                        (device.isDemand(devicePointIdentifier) &&  !tsDate.before(defaults.getDemandStartDate()) ) ) { 

                                    DeviceData meterData = new DeviceData(meterNumber,
                                                                          getMeterPositionNumber(meterPositionNumberMap,
                                                                                                 accountNumber),
                                                                                                 String.valueOf(address),
                                                                                                 accountNumber,
                                                                                                 paoName);
                                    device.populate(devicePointIdentifier,
                                                    ts,
                                                    reading,
                                                    unitOfMeasure,
                                                    pointName,
                                                    meterData);

                                    lastDeviceID = currentDeviceID;
                                    deviceList.add(device);
                                }
                            }
                        }
                    }
                }
                return null;
            }
        });

        CTILogger.info("@" + BillingDao.class.toString() + " Data Collection : Took "
                       + (System.currentTimeMillis() - timer));
        return deviceList;
    }

    /**
     * Returns a hashtable of pointid as key and multiplier as value. Collects
     * the pointid/multiplier from the database.
     * @return java.util.Hashtable
     */
    public static Hashtable<Integer, Double> retrievePointIDMultiplierHashTable() {

        Hashtable<Integer, Double> multiplierHashTable = new Hashtable<Integer, Double>();

        String sql = new String("SELECT ACC.POINTID, ACC.MULTIPLIER FROM POINTACCUMULATOR ACC");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if (conn == null) {
                com.cannontech.clientutils.CTILogger.info(":  Error getting database connection.");
                return null;
            } else {
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
            }
        } catch (SQLException e) {
            CTILogger.error(e);
        } finally {
            SqlUtils.close(rset, pstmt, conn );
        }

        return multiplierHashTable;
    }

    /**
     * Returns a hashtable of meternumber as key and account number as value.
     * Collects the meterNumber/Account number set from the file
     * meterAndAccountNumbers.txt
     * @return Hashtable (key: meterNumber, value: account number)
     */
    public static Map<String, String> retrieveAccountNumbers() {

        Vector<String> linesInFile = new Vector<String>();
        Map<String, String> accountNumberHashTable = null;

        try {
            FileReader meterAndAccountNumbersFileReader = new FileReader("../config/meterAndAccountNumbers.txt");

            BufferedReader readBuffer = new BufferedReader(meterAndAccountNumbersFileReader);

            try {
                String tempLineString = readBuffer.readLine();

                while (tempLineString != null) {
                    linesInFile.add(new String(tempLineString));
                    tempLineString = readBuffer.readLine();
                }
            } catch (IOException ioe) {
            	CTILogger.error(ioe);
            }
        } catch (FileNotFoundException fnfe) {

            CTILogger.info("***********************************************************************************************");
            CTILogger.info("Cannot find meterAndAccountNumbers.txt attempting to get account numbers from the device names.");
            CTILogger.info("***********************************************************************************************");

            return null;
        }

        if (linesInFile != null) {
            Collections.sort(linesInFile);
            int hashCapacity = (linesInFile.size() + 1);
            accountNumberHashTable = new HashMap<String, String>(hashCapacity);

            for (int i = 0; i < linesInFile.size(); i++) {
                String line = linesInFile.get(i);
                int commaIndex = line.indexOf(",");

                String keyMeterNumber = line.substring(0, commaIndex);
                String valueAccountNumber = line.substring(commaIndex + 1);
                accountNumberHashTable.put(keyMeterNumber, valueAccountNumber);
            }
        }
        return accountNumberHashTable;
    }

    /**
     * Helper method to get the where clase for device meter group
     * @param defaults - Information about the billing data to be retrieved
     * @return String where clause
     */
    private static SqlFragmentSource getDeviceMeterGroupWhereClause(BillingFileDefaults defaults) {
        DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);

        SqlStatementBuilder where = new SqlStatementBuilder(" AND ");
        
        List<String> deviceGroupNames = defaults.getDeviceGroups();
        Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(deviceGroupNames);
        SqlFragmentSource deviceGroupSqlWhereClause = deviceGroupService.getDeviceGroupSqlWhereClause(deviceGroups, "YPO.PAOBJECTID");
        where.appendFragment(deviceGroupSqlWhereClause);

        return where;
    }

    /**
     * Helper method to get a meter number position from a map based on the
     * account number. The meter number position is basically a count of meters
     * for the given account. If the map contains the account number, the value
     * is incremented and then returned, if the map does not contain the account
     * number, it is added and a value of one is inserted and returned.
     * @param meterPositionNumberMap - Map containing key: account number and
     *            value: meter position
     * @param accountNumber - Account number to get the meter position for
     * @return String representing the meter position number
     */
    private static String getMeterPositionNumber(Map<String, Integer> meterPositionNumberMap,
            String accountNumber) {

        Integer meterPositionNumber = new Integer(1);

        if (meterPositionNumberMap.containsKey(accountNumber)) {
            meterPositionNumber = meterPositionNumberMap.get(accountNumber) + 1;
        }

        meterPositionNumberMap.put(accountNumber, meterPositionNumber);

        return meterPositionNumber.toString();

    }

    public static void main(String[] args) {

        BillingFileDefaults bfd = new BillingFileDefaults(1,
                                                          30,
                                                          30,
                                                          "",
                                                          1,
                                                          "",
                                                          true,
                                                          "",
                                                          new Date(),
                                                          true);

        List deviceList = BillingDao.retrieveBillingData(bfd);

        System.out.println(deviceList.size());

        Iterator iter = deviceList.iterator();
        while (iter.hasNext()) {
            BillableDevice device = (BillableDevice) iter.next();
            // System.out.println(device.getMeterNumber()
            // + ", " + device.getTotalConsumption() + ", " +
            // device.getTotalDate());
            System.out.println(new ExtendedTOURecordFormatter().dataToString(device));
        }

        System.exit(0);

    }
    
    /**
     * Retrieves all readings data from the database and returns a list of
     * BillableDevices which contain all of the billing data
     * Data of interest for the NISC Interval Readings format is kW, peak kW, and blink count (for all rates/registers)
     * @param defaults - Information about the billing data to be retrieved
     * @param multiplier - Reading value multiplier
     * @return A list of BillableDevices
     */
    private static List<BillableDevice> retrieveIntervalReadingsData(BillingFileDefaults defaults) {

        long timer = System.currentTimeMillis();

        List<BillableDevice> deviceList = null;

        String sql = "SELECT distinct dmg.meternumber, p.pointid, p.pointoffset, p.pointtype, p.pointname, rph.timestamp, " +
                     " rph.value, dmg.deviceid, ypo.type, ypo.category, uom.uomid, ypo.paoname, dcs.address " +
                     " FROM devicemetergroup dmg, point p, rawpointhistory rph, yukonpaobject ypo " +
                     " LEFT JOIN devicecarriersettings dcs ON ypo.paobjectid = dcs.deviceid, " +
                     " unitmeasure uom, pointunit pu " +
                     " WHERE p.pointid = rph.pointid " +
                     " AND pu.pointid = p.pointid " +
                     " AND uom.uomid = pu.uomid " +
                     " AND timestamp > ? " +
                     " AND timestamp <= ? " +
                     " AND ypo.paobjectid = p.paobjectid " +
                     " AND dmg.deviceid = ypo.paobjectid " +
                     //Don't include the  load profile points
                     " AND (pointoffset < 101 or pointoffset > 104) " +
                     getDeviceMeterGroupWhereClause(defaults) +
                     " ORDER BY dmg.meternumber, dmg.deviceid, p.pointtype, p.pointoffset, rph.timestamp DESC";

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            CTILogger.info("SQL Statement: " + sql.replaceAll(" {2,}", " "));

            con = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if (con == null) {
                CTILogger.info(BillingDao.class + ":  Error getting database connection.");
                return null;
            } else {
                stmt = con.prepareStatement(sql);
                stmt.setTimestamp(1, new Timestamp(defaults.getEarliestStartDate().getTime()));
                stmt.setTimestamp(2, new Timestamp(defaults.getEndDate().getTime()));

                rset = stmt.executeQuery();

                deviceList = new ArrayList<BillableDevice>();
                Map<String, Integer> meterPositionNumberMap = new HashMap<String, Integer>();
                Hashtable<Integer, Double> pointIdMultiplierHashTable = new Hashtable<Integer, Double>();
                if (defaults.isRemoveMultiplier()) {
                	//Don't do all the work against the point tables if we don't need to 
                	pointIdMultiplierHashTable = retrievePointIDMultiplierHashTable();
                }
                
                BillableDevice device = null;
                while (rset.next()) {
                    String meterNumber = rset.getString(1);
                    int pointId = rset.getInt(2);
                    int ptOffset = rset.getInt(3);
                    String ptType = rset.getString(4);
                    String pointName = rset.getString(5);
                    Timestamp ts = rset.getTimestamp(6);

                    double multiplier = 1;
                    if (defaults.isRemoveMultiplier()) {
                    	Double pointIdMult = pointIdMultiplierHashTable.get(new Integer(pointId));
                    	if ( pointIdMult != null) {	//Possibility that status points might be returned...which don't have multipliers!
                    		multiplier = pointIdMult;
                    	}
                    }

                    double reading = rset.getDouble(7) / multiplier;
                    int deviceID = rset.getInt(8);
                    String paoType = rset.getString(9);
                    String category = rset.getString(10);
                    int unitOfMeasure = rset.getInt(11);
                    String paoName = rset.getString(12);
                    int address = rset.getInt(13);

                    int pointType = PointTypes.getType(ptType);
                    DevicePointIdentifier devicePointIdentifier = new DevicePointIdentifier(pointType, ptOffset);
                    java.util.Date tsDate = new java.util.Date(ts.getTime());
                    
                    //AccountNumber will always be the same as paoName for interval readings.
                    String accountNumber = paoName;

                    device = BillableDeviceFactory.createBillableDevice(category, paoType);
                    if (device != null) {
                        
                        if( (device.isEnergy(devicePointIdentifier) &&  !tsDate.before(defaults.getEnergyStartDate()) )  || 
                            (device.isDemand(devicePointIdentifier) &&  !tsDate.before(defaults.getDemandStartDate()) ) ) { 

                            DeviceData meterData = new DeviceData(meterNumber,
                                                                  getMeterPositionNumber(meterPositionNumberMap,
                                                                                         accountNumber),
                                                                  String.valueOf(address),
                                                                  accountNumber,
                                                                  paoName);
                            device.populate(devicePointIdentifier,
                                            ts,
                                            reading,
                                            unitOfMeasure,
                                            pointName,
                                            meterData);

                            deviceList.add(device);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            CTILogger.error(e);
        } finally {
            SqlUtils.close(rset, stmt, con );
        }
        CTILogger.info("@" + BillingDao.class.toString() + " Data Collection : Took "
                + (System.currentTimeMillis() - timer));
        return deviceList;
    }
}
