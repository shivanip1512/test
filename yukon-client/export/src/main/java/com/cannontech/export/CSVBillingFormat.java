package com.cannontech.export;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.KeysAndValues;
import com.cannontech.common.util.KeysAndValuesFile;
import com.cannontech.common.util.LogWriter;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.point.PointOffsets;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.export.record.CSVBillingCustomerRecord;
import com.cannontech.export.record.CSVBillingRecord;
import com.cannontech.export.record.StringRecord;

public class CSVBillingFormat extends ExportFormatBase {
    private Hashtable<Integer, CSVBillingCustomerRecord> customerHashtable;
    private Double[] baselineValues;
    private String filePrefix = "OfferBill";
    private String fileExtension = ".csv";

    private SimpleDateFormat COMMAND_LINE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    private SimpleDateFormat FILENAME_FORMAT = new SimpleDateFormat("-MM-dd-yyyy");

    /**
     * @see com.cannontech.export.ExportFormatBase#parseDatFile()
     */
    @Override
    public void parseDatFile() {
        KeysAndValuesFile kavFile = new KeysAndValuesFile(CtiUtilities.getConfigDirPath() + getDatFileName());

        KeysAndValues keysAndValues = kavFile.getKeysAndValues();

        if (keysAndValues != null) {
            boolean gotStart = false;
            boolean gotStop = false;

            String keys[] = keysAndValues.getKeys();
            String values[] = keysAndValues.getValues();
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equalsIgnoreCase("DIR")) {
                    setExportDirectory(values[i].toString());
                    File file = new File(getExportDirectory());
                    file.mkdirs();
                } else if (keys[i].equalsIgnoreCase("DELIMITER")) {
                    getExportProperties().setDelimiter(new Character(values[i].charAt(0)));
                } else if (keys[i].equalsIgnoreCase("HEADINGS")) {
                    getExportProperties().setShowColumnHeadings(Boolean.valueOf(values[i]).booleanValue());
                } else if (keys[i].equalsIgnoreCase("START")) {
                    Date date = null;
                    GregorianCalendar cal = new GregorianCalendar();
                    try {
                        date = COMMAND_LINE_FORMAT.parse(values[i]);
                    } catch (ParseException pe) {
                        date = new Date();
                        long time = date.getTime() - 84600000;
                        date.setTime(time);
                        cal.setTime(date);
                        cal.set(java.util.GregorianCalendar.HOUR_OF_DAY, 0);
                        cal.set(java.util.GregorianCalendar.MINUTE, 0);
                        cal.set(java.util.GregorianCalendar.SECOND, 0);
                    }
                    cal.setTime(date);

                    getExportProperties().setMinTimestamp(cal);
                    gotStart = true;
                } else if (keys[i].equalsIgnoreCase("STOP")) {
                    Date date = null;
                    GregorianCalendar cal = new GregorianCalendar();

                    try {
                        date = COMMAND_LINE_FORMAT.parse(values[i]);
                    } catch (ParseException pe) {
                        date = new Date();
                        cal.setTime(date);
                        cal.set(java.util.GregorianCalendar.HOUR_OF_DAY, 0);
                        cal.set(java.util.GregorianCalendar.MINUTE, 0);
                        cal.set(java.util.GregorianCalendar.SECOND, 0);
                    }
                    cal.setTime(date);

                    getExportProperties().setMaxTimestamp(cal);
                    gotStop = true;
                }
            }

            if (gotStop) {
                if (!gotStart) // Have stop but not start
                {
                    GregorianCalendar cal = new GregorianCalendar();
                    Date date = getExportProperties().getMaxTimestamp().getTime();
                    date.setTime(getExportProperties().getMaxTimestamp().getTime().getTime() - 86400000);
                    cal.setTime(date);
                    getExportProperties().setMinTimestamp(cal);
                }
                // else I'm good to go...already have both.
            } else {
                if (gotStart) // Have start but not stop
                {
                    GregorianCalendar cal = new GregorianCalendar();
                    Date date = getExportProperties().getMinTimestamp().getTime();
                    date.setTime(getExportProperties().getMinTimestamp().getTime().getTime() + 86400000);
                    cal.setTime(date);
                    getExportProperties().setMaxTimestamp(cal);
                }
            }
        } else {
            // MODIFY THE LOG EVENT HERE!!!
            logEvent(
                "Usage:  format=<formatID> dir=<exportfileDirectory> energyfile=<energyDataFile> delimiter=<delChar> headings=<boolean> start=<mm/dd/yyyy> stop=<mm/dd/yyyy>",
                com.cannontech.common.util.LogWriter.INFO);
            logEvent(
                "Ex.	  format=0 dir=c:/yukon/client/export/ energyfile=c:/yukon/client/config/nums.txt delimiter=',' headings=false start=mm/dd/yyyy stop=mm/dd/yyyy",
                com.cannontech.common.util.LogWriter.INFO);
            logEvent("** All parameters will be defaulted to the above if not specified",
                com.cannontech.common.util.LogWriter.INFO);
        }

    }

    /**
     * @see com.cannontech.export.ExportFormatBase#buildKeysAndValues()
     */
    @Override
    public String[][] buildKeysAndValues() {
        String[] keys = new String[7];
        String[] values = new String[7];

        int i = 0;
        keys[i] = "FORMAT";
        values[i++] = String.valueOf(ExportFormatTypes.CSVBILLING_FORMAT);

        keys[i] = "DIR";
        values[i++] = getExportDirectory();

        keys[i] = "START";
        values[i++] = COMMAND_LINE_FORMAT.format(getExportProperties().getMinTimestamp().getTime());

        keys[i] = "STOP";
        values[i++] = COMMAND_LINE_FORMAT.format(getExportProperties().getMaxTimestamp().getTime());

        keys[i] = "DELIMITER";
        values[i++] = getExportProperties().getDelimiter().toString();

        keys[i] = "HEADINGS";
        values[i++] = String.valueOf(getExportProperties().isShowColumnHeadings());

        return new String[][] { keys, values };
    }

    private void retrieveBaselineData(int baselinePointID, GregorianCalendar curtailDate) {
        GregorianCalendar cal = (GregorianCalendar) curtailDate.clone();
        long timer = System.currentTimeMillis();
        StringBuffer sql = new StringBuffer("SELECT RPH.VALUE, RPH.TIMESTAMP ");
        sql.append("FROM RAWPOINTHISTORY RPH");

        sql.append(" WHERE RPH.TIMESTAMP > ? AND RPH.TIMESTAMP <= ?");
        sql.append(" AND RPH.POINTID = " + baselinePointID);
        sql.append(" ORDER BY RPH.TIMESTAMP");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        // Initialize baselineValues for 24 vals and value = 0;
        baselineValues = new Double[24];
        for (int i = 0; i < baselineValues.length; i++) {
            baselineValues[i] = new Double(0);
        }

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if (conn == null) {
                logEvent(getClass() + ":  Error getting database connection.",
                    com.cannontech.common.util.LogWriter.INFO);
                return;
            }
            stmt = conn.prepareStatement(sql.toString());
            stmt.setTimestamp(1, new Timestamp(cal.getTime().getTime()));
            cal.add(Calendar.DATE, 1);
            stmt.setTimestamp(2, new Timestamp(cal.getTime().getTime()));

            rset = stmt.executeQuery();
            while (rset.next()) {
                Double value = new Double(rset.getDouble(1));
                GregorianCalendar offerDate = new GregorianCalendar();
                offerDate.setTime(rset.getTimestamp(2));

                int hour = offerDate.get(java.util.GregorianCalendar.HOUR_OF_DAY);
                baselineValues[hour] = value;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SqlUtils.close(rset, stmt, conn);
        }
        logEvent("...BASELINE DATA RETRIEVED: Took " + (System.currentTimeMillis() - timer) + " millis.",
            com.cannontech.common.util.LogWriter.INFO);
        return;
    }

    private Vector<CSVBillingRecord> retrieveCurtailHistory(CSVBillingCustomerRecord csvBillingCust) {
        // contains RecordBase values.
        Vector<CSVBillingRecord> curtailHistoryVector = new Vector<>();

        StringBuffer sql = new StringBuffer("SELECT LMEEHO.OFFERID, LMEEHO.REVISIONNUMBER");
        sql.append(", LMEEHO.PRICE, LMEEHC.AMOUNTCOMMITTED, LMEEHO.HOUR, LMEEPO.OFFERDATE ");
        sql.append("FROM LMENERGYEXCHANGEHOURLYCUSTOMER LMEEHC, ");
        sql.append("LMENERGYEXCHANGEPROGRAMOFFER LMEEPO, ");
        sql.append("LMENERGYEXCHANGEHOURLYOFFER LMEEHO ");
        sql.append(" WHERE LMEEHC.CUSTOMERID = " + csvBillingCust.getCustomerID().intValue());
        sql.append(" AND LMEEPO.OFFERID = LMEEHO.OFFERID");
        sql.append(" AND LMEEPO.OFFERID = LMEEHC.OFFERID");
        sql.append(" AND LMEEHO.REVISIONNUMBER = LMEEHC.REVISIONNUMBER");
        sql.append(" AND LMEEHO.HOUR = LMEEHC.HOUR");
        sql.append(" AND LMEEHC.AMOUNTCOMMITTED > 0");
        sql.append(" AND LMEEPO.OFFERDATE > ? AND LMEEPO.OFFERDATE <= ?");
        sql.append(" ORDER BY LMEEPO.OFFERID, LMEEPO.OFFERDATE, LMEEHO.HOUR ");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if (conn == null) {
                logEvent(getClass() + ":  Error getting database connection.",
                    com.cannontech.common.util.LogWriter.INFO);
                return null;
            }
            stmt = conn.prepareStatement(sql.toString());
            // Curtail Dates are stored with a 00:00 timestamp but we still want that actual date
            // so we have to subtract one day from each timestamp to get an accurate date for offer info
            // queries.
            GregorianCalendar tempCal = (GregorianCalendar) getExportProperties().getMinTimestamp().clone();
            tempCal.add(Calendar.DATE, -1);
            stmt.setTimestamp(1, new Timestamp(tempCal.getTime().getTime()));

            tempCal = (GregorianCalendar) getExportProperties().getMaxTimestamp().clone();
            tempCal.add(Calendar.DATE, -1);
            stmt.setTimestamp(2, new Timestamp(tempCal.getTime().getTime()));
            rset = stmt.executeQuery();
            while (rset.next()) {
                // 1LMEEHO.OFFERID, 2LMEEHO.REVISIONNUMBER, 3LMEEHO.PRICE,
                // 4LMEEHC.AMOUNTCOMMITTED, 5LMEEHO.HOUR, 6LMEEPO.OFFERDATE
                String offerID = rset.getString(1) + " - " + rset.getString(2);
                Double price = new Double(rset.getDouble(3));
                Double amtCommit = new Double(rset.getDouble(4));
                int hour = rset.getInt(5);
                GregorianCalendar offerDate = new GregorianCalendar();
                offerDate.setTime(rset.getTimestamp(6));

                GregorianCalendar curtailDate = (GregorianCalendar) offerDate.clone();
                curtailDate.set(Calendar.HOUR_OF_DAY, hour);

                CSVBillingRecord csvBillingRec = new CSVBillingRecord();
                csvBillingRec.setCurtailOffer(offerID);
                csvBillingRec.setCurtailDate(curtailDate);
                csvBillingRec.setCurtailRate(price);

                csvBillingRec.setCLR(amtCommit);
                csvBillingRec.setPDL(csvBillingCust.getPDL());
                csvBillingRec.setDelimiter(getExportProperties().getDelimiter());

                curtailHistoryVector.add(csvBillingRec);
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SqlUtils.close(rset, stmt, conn);
        }
        return curtailHistoryVector;
    }

    private void retrieveBillingData(Vector<CSVBillingRecord> recordVector, CSVBillingCustomerRecord csvBillingCust) {
        GregorianCalendar prevCurtailDate = new GregorianCalendar();
        HoursAndValues hoursAndValues = null;
        for (int i = 0; i < recordVector.size(); i++) {
            if (recordVector.get(i) instanceof CSVBillingRecord) {
                CSVBillingRecord record = recordVector.get(i);
                GregorianCalendar curtailDate = (GregorianCalendar) record.getCurtailDate().clone();
                curtailDate.set(Calendar.HOUR_OF_DAY, 0);
                curtailDate.set(Calendar.MINUTE, 0);
                curtailDate.set(Calendar.SECOND, 0);

                if (curtailDate.getTime().compareTo(prevCurtailDate.getTime()) != 0) {
                    // Only collect baseline data for curtailed dates.
                    retrieveBaselineData(csvBillingCust.getBaselinePointId().intValue(), curtailDate);
                    prevCurtailDate = (GregorianCalendar) curtailDate.clone();
                    Vector<Date> validTimestamps = new Vector<>(1);
                    validTimestamps.add(curtailDate.getTime());
                    hoursAndValues = retrieveData(csvBillingCust.getCurtailPointId(), validTimestamps);
                }
                int hourOfDay = record.getCurtailDate().get(Calendar.HOUR_OF_DAY);
                if (hoursAndValues != null) {
                    Double value = hoursAndValues.getValue(hourOfDay);
                    if (value != null) {
                        record.setADL(value);
                    }

                    // Change hour from 0-23 to 1-24 (the new 0 is midnight as in the latest reading, not the
                    // earliest)
                    hourOfDay++;
                    if (hourOfDay == 24) {
                        hourOfDay = 0;
                    }
                    record.setRLP(baselineValues[hourOfDay]);
                }
            }
        }
        return;
    }

    private void retrieveCustomerData() {
        System.currentTimeMillis();
        StringBuffer sql =
            new StringBuffer(
                "SELECT CCB.COMPANYNAME, CCB.CUSTOMERID, CC.POINTID, CC.COMPONENTPOINTID, DMG.METERNUMBER, CCB.CUSTOMERDEMANDLEVEL, DMG.DEVICEID, PAO.PAONAME ");
        sql.append(" FROM CICUSTOMERBASE CCB, ");
        sql.append(" DEVICECUSTOMERLIST DCL, ");
        sql.append(" CALCCOMPONENT CC, ");
        sql.append(" POINT PT, ");
        sql.append(" YUKONPAOBJECT PAO, ");
        sql.append(" DEVICEMETERGROUP DMG ");
        sql.append(" WHERE CCB.CUSTOMERID = DCL.CUSTOMERID ");
        sql.append(" AND DCL.DEVICEID = DMG.DEVICEID ");
        sql.append(" AND PAO.PAOBJECTID = PT.PAOBJECTID ");
        sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID ");
        sql.append(" AND CC.POINTID = PT.POINTID ");
        sql.append(" AND PT.POINTOFFSET = " + PointOffsets.PT_OFFSET_BILLING_BASELINE);
        sql.append(" AND FUNCTIONNAME = '" + CalcComponentTypes.BASELINE_FUNCTION + "'");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        customerHashtable = new Hashtable<>(10);

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if (conn == null) {
                logEvent(getClass() + ":  Error getting database connection.",
                    com.cannontech.common.util.LogWriter.INFO);
                return;
            }
            stmt = conn.prepareStatement(sql.toString());
            rset = stmt.executeQuery();
            while (rset.next()) {
                String custName = rset.getString(1);
                Integer custID = new Integer(rset.getInt(2));
                Integer baselinePtId = new Integer(rset.getInt(3));
                Integer curtailPtId = new Integer(rset.getInt(4));
                String meterLoc = rset.getString(5);
                Double pdl = new Double(rset.getDouble(6));
                Integer paoID = new Integer(rset.getInt(7));
                String paoName = new String(rset.getString(8));

                CSVBillingCustomerRecord csvBillingCust =
                    new CSVBillingCustomerRecord(custName, custID, meterLoc, paoName, baselinePtId, curtailPtId, pdl);

                csvBillingCust.setDelimiter(getExportProperties().getDelimiter());
                customerHashtable.put(paoID, csvBillingCust);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SqlUtils.close(rset, stmt, conn);
        }
        return;
    }

    @Override
    public void retrieveData() {
        String name = new String();
        name += filePrefix;
        name += FILENAME_FORMAT.format(getExportProperties().getMaxTimestamp().getTime());
        name += fileExtension;
        setExportFileName(name);

        long timer = System.currentTimeMillis();
        if (getExportProperties().isShowColumnHeadings()) {
            StringRecord stringRec =
                new StringRecord("Yukon Curtailment Settlement for "
                    + getExportProperties().getMinTimestamp().getTime() + " - "
                    + getExportProperties().getMaxTimestamp().getTime() + "\r\n");

            // Add a title record
            getRecordVector().add(stringRec);
        }

        logEvent("...Retrieving data for Date > " + getExportProperties().getMinTimestamp().getTime() + " AND <= "
            + getExportProperties().getMaxTimestamp().getTime(), com.cannontech.common.util.LogWriter.INFO);

        // Get a hashtable of records of all curtailment customers
        retrieveCustomerData();

        if (customerHashtable == null) {
            return;
        }
        // Loop through all customers and get all curtailment data for the timeframe.
        Set<Integer> keyset = customerHashtable.keySet();
        Iterator<Integer> iter = keyset.iterator();
        while (iter.hasNext()) {
            Vector<CSVBillingRecord> tempRecordVector = null;
            Integer keyid = iter.next();
            CSVBillingCustomerRecord custRec = customerHashtable.get(keyid);
            if (custRec != null) {
                getRecordVector().add(custRec);
                if (getExportProperties().isShowColumnHeadings()) {
                    StringRecord stringRec = new StringRecord(CSVBillingRecord.getColumnHeadingsString());
                    // Add a column headings record
                    getRecordVector().add(stringRec);
                }
                tempRecordVector = retrieveCurtailHistory(custRec);
                retrieveBillingData(tempRecordVector, custRec);
                getRecordVector().addAll(tempRecordVector);
            }
        }

        // Set timestamps for next day.
        getExportProperties().setMinTimestamp(getExportProperties().getMaxTimestamp());

        GregorianCalendar newCal = new GregorianCalendar();
        newCal.setTime(new Date(getExportProperties().getMaxTimestamp().getTime().getTime() + 86400000));
        getExportProperties().setMaxTimestamp(newCal);
        logEvent(" * Next TimePeriod: " + getExportProperties().getMinTimestamp().getTime() + " - "
            + getExportProperties().getMaxTimestamp().getTime(), com.cannontech.common.util.LogWriter.INFO);
        logEvent("@" + this.toString() + " Data Collection : Took " + (System.currentTimeMillis() - timer) + " millis",
            com.cannontech.common.util.LogWriter.INFO);
    }
    
    private HoursAndValues retrieveData(Integer pointID, Vector<Date> validTimestampsVector) {
        HoursAndValues returnData = null;
        StringBuffer sql = new StringBuffer("SELECT VALUE, TIMESTAMP FROM RAWPOINTHISTORY WHERE ");

        sql.append(" POINTID = ");
        sql.append(pointID);

        // ...for each entry in the baselineCalDatesVector....
        // Timestamps give us values > 00:00:00 and <= 00:00:00 of the last
        // day+1
        // contains java.util.Date values.
        Vector<Date> sqlQueryDates = new Vector<>();
        sqlQueryDates = appendTimestamps(sql, validTimestampsVector);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if (conn == null) {
                CTILogger.info("Error getting database connection.");
                logEvent("Error getting database connection.", LogWriter.ERROR);
                return null;
            }
            pstmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < sqlQueryDates.size(); i++) {
                pstmt.setTimestamp(i + 1, new Timestamp(sqlQueryDates.get(i).getTime()));
            }
            rset = pstmt.executeQuery();

            // contains Double values (rawPointHistory values).
            Vector<Double> values = new Vector<>();
            // contains Integer values (rawPointHistory timestamps.hour).
            Vector<Integer> hours = new Vector<>();

            while (rset.next()) {
                values.add(new Double(rset.getDouble(1)));

                Timestamp ts = rset.getTimestamp(2);
                int keyHour = getAdjustedHour(ts);
                hours.add(new Integer(keyHour));
            }

            if (!values.isEmpty() && !hours.isEmpty()) {
                PointUnit pUnit = new PointUnit();
                pUnit.setPointID(pointID);
                pUnit.setDbConnection(conn);
                pUnit.retrieve();

                TreeMap<?, ?> treeMap = buildTreeMap(values, hours);
                Set<?> keySet = treeMap.keySet();
                Collection<?> keyVals = treeMap.values();

                Object[] tempArray = new Double[keyVals.size()];
                returnData = new HoursAndValues(keySet.size(), keyVals.size());
                keySet.toArray(returnData.getHours());
                tempArray = keyVals.toArray();

                for (int i = 0; i < keyVals.size(); i++) {
                    Double[] v = (Double[]) tempArray[i];
                    double counter = v[0].doubleValue();
                    double totalVal = v[1].doubleValue();

                    UnitOfMeasure uom = UnitOfMeasure.getForId(pUnit.getUomID());
                    if (uom == UnitOfMeasure.KW) {
                        returnData.values[i] = new Double(totalVal / counter); // kW
                                                                               // values.
                    } else if (uom == UnitOfMeasure.KWH) {
                        returnData.values[i] = new Double(totalVal / validTimestampsVector.size()); // kWh
                                                                                                    // values
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rset != null) {
                    rset.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace(); // sometin is up
            }
        }

        return returnData; // hours and values of the retreived data.
    }

    private static int getAdjustedHour(Timestamp ts) {
        int min = (new Integer(new SimpleDateFormat("mm").format(ts).toString()).intValue());
        int hr = (new Integer(new SimpleDateFormat("HH").format(ts).toString()).intValue());

        // have to manipulate the hour associated with the value and timestamps.
        if (hr == 0) {
            if (min == 0) {
                return 23;
            }
        } else {
            if (min == 0) {
                return (hr - 1);
            }
        }
        return hr;
    }

    /**
     * @param sql
     * @param validTimestampsVector
     * @return Vector of java.util.Date values that will need to be set for the
     *         sql statement.
     */
    private static Vector<Date> appendTimestamps(StringBuffer sql,
            Vector<Date> validTimestampsVector) {
        Vector<Date> vectorOfQueryDates = new Vector<>();
        boolean newLine = false;
        for (int i = 0; i < validTimestampsVector.size(); i++) {
            GregorianCalendar endDate = new GregorianCalendar();
            endDate.setTime(validTimestampsVector.get(i));
            endDate.add(java.util.Calendar.DATE, 1);
            if (i == 0) {
                sql.append(" AND (( TIMESTAMP <= ?");
                vectorOfQueryDates.add(endDate.getTime());
                // System.out.println(" AND (( TIMESTAMP <= " +
                // endDate.getTime() );
            } else if (newLine) {
                sql.append(" OR ( TIMESTAMP <= ?");
                vectorOfQueryDates.add(endDate.getTime());
                // System.out.println(" OR ( TIMESTAMP <= " +
                // endDate.getTime());
                newLine = false;
            }

            // The last timestamp to check so we need to close the statement
            // too.
            if (i + 1 == validTimestampsVector.size()) {
                sql.append(" AND TIMESTAMP > ?)");
                sql.append(") ORDER BY TIMESTAMP");
                vectorOfQueryDates.add(validTimestampsVector.get(i));
                // System.out.println("  AND TIMESTAMP > " +
                // validTimestampsVector.get(i) +
                // ") ORDER BY TIMESTAMP");
                break;
            }
            // Compare i and i+1 values incremental dates. If they are in order
            // we may skip the date by making
            // it inclusive in the where clause
            GregorianCalendar yesterday = new GregorianCalendar();
            yesterday.setTime(validTimestampsVector.get(i));
            yesterday.add(java.util.Calendar.DATE, -1);
            if (yesterday.getTime().compareTo(validTimestampsVector.get(i + 1)) == 0) {
                // SKIP
            } else {
                sql.append(" AND TIMESTAMP > ?)");
                vectorOfQueryDates.add(validTimestampsVector.get(i));
                // System.out.println(" AND TIMESTAMP > " +
                // validTimestampsVector.get(i));
                newLine = true;
            }
        }
        return vectorOfQueryDates;
    }

    private static TreeMap<Integer, Double[]> buildTreeMap(Vector<Double> values,
            Vector<Integer> hours) {
        TreeMap<Integer, Double[]> tree = new TreeMap<>();
        if (!hours.isEmpty() && !values.isEmpty()) {
            for (int i = 0; i < hours.size(); i++) {
                Integer d = hours.get(i);
                Double[] objectValues = tree.get(d);

                if (objectValues == null) {
                    // objectValues is not in the key already
                    objectValues = new Double[2]; // 1 for counter, 1 for value
                    objectValues[0] = new Double(1);
                    objectValues[1] = values.get(i);
                    tree.put(d, objectValues);
                } else {
                    Double newVal = new Double(objectValues[1].doubleValue() + values.get(i)
                                                                                     .doubleValue());

                    objectValues[0].doubleValue();
                    objectValues[0] = new Double(objectValues[0].doubleValue() + 1);
                    objectValues[1] = newVal;

                    tree.put(d, objectValues);
                }
            }
        }
        return tree;
    }

}