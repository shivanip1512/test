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
    public static List<BillableDevice> retrieveBillingData(BillingFileDefaults defaults) {

        long timer = System.currentTimeMillis();

        List<BillableDevice> deviceList = null;

        String sql = "SELECT                                                            "
                + "     dmg.meternumber,                                                "
                + "     p.pointid,                                                      "
                + "     p.pointoffset,                                                  "
                + "     p.pointtype,                                                    "
                + "     p.pointname,                                                    "
                + "     rph.timestamp,                                                  "
                + "     rph.value,                                                      "
                + "     dmg.deviceid,                                                   "
                + "     ypo.type,                                                       "
                + "     ypo.category,                                                   "
                + "     uom.uomid,                                                      "
                + "     ypo.paoname,                                                    "
                + "     dcs.address                                                     "
                + " FROM                                                                "
                + "     devicemetergroup dmg,                                           "
                + "     point p,                                                        "
                + "     rawpointhistory rph,                                            "
                + "     yukonpaobject ypo                                               "
                + "     LEFT JOIN devicecarriersettings as dcs                          "
                + "         ON ypo.paobjectid = dcs.deviceid,                           "
                + "     unitmeasure uom,                                                "
                + "     pointunit pu,                                                   "
                + "     (SELECT                                                         "
                + "         r.pointid,                                                  "
                + "         max(r.timestamp) AS date                                    "
                + "     FROM                                                            "
                + "         (SELECT                                                     "
                + "             *                                                       "
                + "         FROM                                                        "
                + "             rawpointhistory                                         "
                + "         WHERE                                                       "
                + "             timestamp > ?                                           "
                + "             AND timestamp <= ?) r                                   "
                + "     GROUP BY                                                        "
                + "         pointid) irph                                               "
                + " WHERE                                                               "
                + "     p.pointid = irph.pointid                                        "
                + "     AND pu.pointid = p.pointid                                      "
                + "     AND uom.uomid = pu.uomid                                        "
                + "     AND rph.pointid = irph.pointid                                  "
                + "     AND rph.timestamp = irph.date                                   "
                + "     AND ypo.paobjectid = p.paobjectid                               "
                + "     AND dmg.deviceid = ypo.paobjectid                               "
                + getDeviceMeterGroupWhereClause(defaults)
                + " ORDER BY                                                            "
                + "     dmg.meternumber,                                                "
                + "     dmg.deviceid,                                                   "
                + "     p.pointoffset,                                                  "
                + "     rph.timestamp DESC                                              ";

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
                Map<String, String> accountNumberMap = null;
                if( defaults.getFormatID() == FileFormatTypes.CADP ||
               		defaults.getFormatID() == FileFormatTypes.CADPXL2 ) {
	                accountNumberMap = retrieveAccountNumbers();
                }

                Map<String, Integer> meterPositionNumberMap = new HashMap<String, Integer>();

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
                        multiplier = retrievePointIDMultiplierHashTable().get(new Integer(currentPointID)).doubleValue();
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
    private static String getDeviceMeterGroupWhereClause(BillingFileDefaults defaults) {
        DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);

        StringBuffer where = new StringBuffer(" AND ");
        
        List<String> deviceGroupNames = defaults.getDeviceGroups();
        Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(deviceGroupNames);
        String deviceGroupSqlWhereClause = deviceGroupService.getDeviceGroupSqlWhereClause(deviceGroups, "YPO.PAOBJECTID");
        where.append(deviceGroupSqlWhereClause);

        return where.toString();
    }

    /**
     * Helper method to get a meter number postition from a map based on the
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
}
