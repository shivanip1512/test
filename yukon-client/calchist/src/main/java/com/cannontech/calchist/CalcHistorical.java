package com.cannontech.calchist;

/**
 * Insert the type's description here.
 * Creation date: (12/4/2000 2:04:30 PM)
 * 
 * @author:
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.util.ClientConnectionFactory;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public final class CalcHistorical {
    private Thread starter = null;

    private Integer aggregationInterval = null;// interval in seconds between calculations
    private DispatchClientConnection dispatchConnection = null;
    private GregorianCalendar nextCalcTime = null;

    private static boolean isService = true;
    private static LogWriter logger = null;

    private final int KW_UNITMEASURE = UnitOfMeasure.KW.getId();
    private final int KVA_UNITMEASURE = UnitOfMeasure.KVA.getId();
    private final int KVAR_UNITMEASURE = UnitOfMeasure.KVAR.getId();
    private final int KQ_UNITMEASURE = UnitOfMeasure.KQ.getId();

    private final int KW_KVAR_PFTYPE = 0;
    private final int KW_KQ_PFTYPE = 1;
    private final int KW_KVA_PFTYPE = 2;

    private class PF {
        final double SQRT3 = 1.7320508075688772935274463415059;
        private int pfType = 0;
        private double kw_value = 0.0;
        private double kvar_value = 0.0;
        private double kq_value = 0.0;
        private double kva_value = 0.0;
    }

    private void figureNextCalcTime() {
        if (this.nextCalcTime == null) {
            this.nextCalcTime = new GregorianCalendar();
        } else {
            GregorianCalendar tempCal = new GregorianCalendar();
            long nowInMilliSeconds = tempCal.getTime().getTime();
            long aggIntInMilliSeconds = getAggregationInterval().longValue() * 1000;
            long tempSeconds = (nowInMilliSeconds - (nowInMilliSeconds % aggIntInMilliSeconds)) + aggIntInMilliSeconds;

            /*
             * if it hasn't been at least one full aggregation interval since we last did a
             * calculation, wait until next scheduled calculation time
             */
            if (tempSeconds < (this.nextCalcTime.getTime().getTime() + aggIntInMilliSeconds)) {
                tempSeconds += aggIntInMilliSeconds;
            }

            this.nextCalcTime = new GregorianCalendar();
            this.nextCalcTime.setTime(new Date(tempSeconds));
        }
        logEvent(" ... Next Historical Calculation to occur at: " + nextCalcTime.getTime(), LogWriter.INFO);
        CTILogger.info(" ... Next Historical Calculation to occur at: " + nextCalcTime.getTime());
    }

    private Double figurePointDataMsgValue(Vector<CalcComponent> calcComponentVector,
            Vector<?> currentRawPointHistoryVector) {
        double returnValue = 0;
        PF powerFactor = null;

        PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
        
        for (int i = 0; i < calcComponentVector.size(); i++) {
            CalcComponent calcComponent = calcComponentVector.get(i);
            if (calcComponent.getComponentType().equalsIgnoreCase(CalcComponentTypes.OPERATION_COMP_TYPE)) {
                for (int j = 0; j < currentRawPointHistoryVector.size(); j++) {
                    if (calcComponent.getComponentPointID().intValue() == ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getPointID()) {
                        if (calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.ADDITION_OPERATION)) {
                            returnValue =
                                returnValue + ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue();
                        } else if (calcComponent.getOperation().equalsIgnoreCase(
                            CalcComponentTypes.SUBTRACTION_OPERATION)) {
                            returnValue =
                                returnValue - ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue();
                        } else if (calcComponent.getOperation().equalsIgnoreCase(
                            CalcComponentTypes.MULTIPLICATION_OPERATION)) {
                            returnValue =
                                returnValue * ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue();
                        } else if (calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.DIVISION_OPERATION)) {
                            if (((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue() != 0.0) {
                                returnValue =
                                    returnValue
                                        / ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue();
                            } else {
                                logEvent("Can not divide by zero CalcHistorical::figurePointDataMsgValue()",
                                    LogWriter.ERROR);
                                CTILogger.info("Can not divide by zero CalcHistorical::figurePointDataMsgValue()");
                                return null;
                            }
                        } else {
                            logEvent("Can not determine the Operation " + calcComponent.getOperation().toString()
                                + " in CalcHistorical::figurePointDataMsgValue()", LogWriter.ERROR);
                            CTILogger.info("Can not determine the Operation  "
                                + calcComponent.getOperation().toString()
                                + " in CalcHistorical::figurePointDataMsgValue()");
                            return null;
                        }
                    }
                }
            } else if (calcComponent.getComponentType().equalsIgnoreCase(CalcComponentTypes.CONSTANT_COMP_TYPE)) {
                if (calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.ADDITION_OPERATION)) {
                    returnValue = returnValue + calcComponent.getConstant().doubleValue();
                } else if (calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.SUBTRACTION_OPERATION)) {
                    returnValue = returnValue - calcComponent.getConstant().doubleValue();
                } else if (calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.MULTIPLICATION_OPERATION)) {
                    returnValue = returnValue * calcComponent.getConstant().doubleValue();
                } else if (calcComponent.getOperation().equalsIgnoreCase(CalcComponentTypes.DIVISION_OPERATION)) {
                    if (calcComponent.getConstant().doubleValue() != 0.0) {
                        returnValue = returnValue / calcComponent.getConstant().doubleValue();
                    } else {
                        logEvent("Can not divide by zero CalcHistorical::figurePointDataMsgValue()", LogWriter.ERROR);
                        CTILogger.info("Can not divide by zero CalcHistorical::figurePointDataMsgValue()");
                        return null;
                    }
                } else {
                    logEvent("Can not determine the Operation " + calcComponent.getOperation().toString()
                        + " in CalcHistorical::figurePointDataMsgValue()", LogWriter.ERROR);
                    CTILogger.info("Can not determine the Operation " + calcComponent.getOperation().toString()
                        + " in CalcHistorical::figurePointDataMsgValue()");
                    return null;
                }
            } else if (calcComponent.getComponentType().equalsIgnoreCase(CalcComponentTypes.FUNCTION_COMP_TYPE)) {

                if (calcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION)) {
                    if (powerFactor == null) {
                        powerFactor = new PF();
                    }
                    powerFactor.pfType = KW_KVAR_PFTYPE;

                    // Original way of processing Power Fail. It is done using push in calc-logic.
                    LitePointUnit ltPU = pointDao.getPointUnit(calcComponent.getComponentPointID());

                    for (int j = 0; j < currentRawPointHistoryVector.size(); j++) {
                        if (calcComponent.getComponentPointID().intValue() == ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getPointID()) {

                            if (ltPU.getUomID() == KW_UNITMEASURE) {
                                powerFactor.kw_value =
                                    ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue();
                            } else if (ltPU.getUomID() == KVAR_UNITMEASURE) {
                                powerFactor.kvar_value =
                                    ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue();
                            }
                        }
                    }
                }

                else if (calcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION)) {
                    if (powerFactor == null) {
                        powerFactor = new PF();
                    }
                    powerFactor.pfType = KW_KQ_PFTYPE;

                    LitePointUnit ltPU = pointDao.getPointUnit(calcComponent.getPointID());

                    for (int j = 0; j < currentRawPointHistoryVector.size(); j++) {
                        CTILogger.info(" Current RawPointHistoryVector.size() = " + currentRawPointHistoryVector.size());
                        if (ltPU.getUomID() == KW_UNITMEASURE) {
                            powerFactor.kw_value =
                                ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue();
                        } else if (ltPU.getUomID() == KQ_UNITMEASURE) {
                            powerFactor.kq_value =
                                ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue();
                        }
                    }
                } else if (calcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.PFACTOR_KW_KVA_FUNCTION)) {
                    if (powerFactor == null) {
                        powerFactor = new PF();
                    }
                    powerFactor.pfType = KW_KVA_PFTYPE;

                    LitePointUnit ltPU = pointDao.getPointUnit(calcComponent.getPointID());

                    for (int j = 0; j < currentRawPointHistoryVector.size(); j++) {
                        CTILogger.info(" Current RawPointHistoryVector.size() = " + currentRawPointHistoryVector.size());
                        if (ltPU.getUomID() == KW_UNITMEASURE) {
                            powerFactor.kw_value =
                                ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue();
                        } else if (ltPU.getUomID() == KVA_UNITMEASURE) {
                            powerFactor.kva_value =
                                ((LiteRawPointHistory) currentRawPointHistoryVector.get(j)).getValue();
                        }
                    }
                }
                // For this to ever work the query in getCalcComponentPoints(...)
                // must be changed at the spot of != 'Function'
                else if (calcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION)) {
                    // This is handled in main, not here!
                    return null;
                } else {
                    logEvent("Can not determine the Function " + calcComponent.getFunctionName().toString()
                        + " in CalcHistorical::figurePointDataMsgValue()", LogWriter.ERROR);
                    CTILogger.info("Can not determine the Function " + calcComponent.getFunctionName().toString()
                        + " in CalcHistorical::figurePointDataMsgValue()");
                    return null;
                }
            } else {
                CTILogger.info("Can not determine the ComponentType " + calcComponent.getComponentType().toString()
                    + " in CalcHistorical::figurePointDataMsgValue()");
                return null;
            }
        }
        if (powerFactor != null) {
            returnValue = calculatePowerFactor(powerFactor);
        }
        return new Double(returnValue);
    }

    public Integer getAggregationInterval() {
        if (aggregationInterval == null) {
            aggregationInterval =
                YukonSpringHook.getBean(GlobalSettingDao.class).getInteger(GlobalSettingType.INTERVAL);

            logEvent(" Aggregation interval = " + aggregationInterval + " seconds.", LogWriter.INFO);
            CTILogger.info("Aggregation interval from Global Properties is " + aggregationInterval + " seconds.");
        }
        return aggregationInterval;
    }

    public static GregorianCalendar getCalcHistoricalLastUpdateTimeStamp(int calcPointID) {
        // January 1, 1980
        GregorianCalendar returnTimeStamp = new GregorianCalendar(1980, 0, 1, 0, 0);

        java.sql.PreparedStatement preparedStatement = null;
        java.sql.Connection conn = null;
        java.sql.ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            preparedStatement =
                conn.prepareStatement("SELECT LASTUPDATE FROM DYNAMICCALCHISTORICAL WHERE POINTID = "
                    + String.valueOf(calcPointID));
            rset = preparedStatement.executeQuery();

            if (rset != null) {
                java.sql.Timestamp tempTimestamp = null;
                while (rset.next()) {
                    tempTimestamp = rset.getTimestamp(1);
                    if (tempTimestamp != null) {
                        returnTimeStamp.setTime(tempTimestamp);
                    }
                }
            } else {
                // Default lastUpdate to TODAY, insert lastupdate and pointid into DynamicCalcHistorical table
                // for future use.
                GregorianCalendar tempCal = new GregorianCalendar();
                returnTimeStamp.set(Calendar.YEAR, tempCal.get(Calendar.YEAR));
                returnTimeStamp.set(Calendar.DAY_OF_YEAR, tempCal.get(Calendar.DAY_OF_YEAR));

                if (preparedStatement != null) {
                    preparedStatement.close();
                }

                preparedStatement = conn.prepareStatement("INSERT INTO DYNAMICCALCHISTORICAL VALUES ( ?, ?)");
                preparedStatement.setInt(1, calcPointID);
                preparedStatement.setTimestamp(2, new java.sql.Timestamp(returnTimeStamp.getTime().getTime()));
                preparedStatement.executeUpdate();

                logEvent(
                    "POINTID: " + calcPointID
                        + "   Missing from DynamicCalcHistorical.  PointID was added with LastUpdate= "
                        + returnTimeStamp.getTime(), LogWriter.INFO);
                CTILogger.info("POINTID: " + calcPointID
                    + "   Missing from DynamicCalcHistorical.  PointID was added with LastUpdate= "
                    + returnTimeStamp.getTime());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            CTILogger.debug("CalcPointID:  " + calcPointID + "    Timestamp: " + returnTimeStamp.getTime()
                + "  Found in DynamicCalcHistorical");
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rset != null) {
                    rset.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                CTILogger.info(" *** DYNAMICCALCHISTORICAL TABLE may be missing:  DYNAMICCALCHISTORICAL");
                logEvent(" *** DYNAMICCALCHISTORICAL TABLE may be missing:  DYNAMICCALCHISTORICAL", LogWriter.ERROR);
                e.printStackTrace();
            }
        }
        return returnTimeStamp;
    }

    private Vector<Vector<LiteRawPointHistory>> getRawPointHistoryVectorOfVectors(
            Vector<CalcComponent> calcComponentVector, GregorianCalendar lastCalcPointRawPointHistoryTimeStamp) {

        Vector<LiteRawPointHistory> tempRawPointHistoryVector = null;
        Vector<Vector<LiteRawPointHistory>> rawPointHistoryVectorOfVectors = new Vector<>();

        java.sql.PreparedStatement preparedStatement = null;
        java.sql.Connection conn = null;
        java.sql.ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            for (int i = 0; i < calcComponentVector.size(); i++) {
                if (calcComponentVector.get(i).getComponentPointID().intValue() > 0) {
                    int compPointID = calcComponentVector.get(i).getComponentPointID().intValue();

                    CTILogger.debug("Starting RPH Retrieve loop for PointID: " + compPointID + " - Timestamp: "
                        + lastCalcPointRawPointHistoryTimeStamp.getTime());
                    preparedStatement =
                        conn.prepareStatement("SELECT DISTINCT POINTID, TIMESTAMP, QUALITY, VALUE FROM RAWPOINTHISTORY WHERE POINTID = ? AND TIMESTAMP > ? ORDER BY TIMESTAMP");
                    preparedStatement.setInt(1, compPointID);
                    preparedStatement.setTimestamp(2, new java.sql.Timestamp(
                        lastCalcPointRawPointHistoryTimeStamp.getTime().getTime()));

                    rset = preparedStatement.executeQuery();
                    tempRawPointHistoryVector = new Vector<>();

                    int count = 0;
                    CTILogger.debug(" ** RSET RETRIEVED * getCalcHistoricalPointDataMsgVector");
                    while (rset.next()) {
                        // long changeID = rset.getLong(1);
                        long changeID = -1; // HACK, we just need some value here, and we wanted to do
                                            // distinct in the query.
                        int pointID = rset.getInt(1);
                        long time = rset.getTimestamp(2).getTime();
                        int quality = rset.getInt(3);
                        double value = rset.getDouble(4);

                        LiteRawPointHistory lrph = new LiteRawPointHistory(changeID, pointID, time, quality, value);

                        tempRawPointHistoryVector.add(lrph);
                        count++;
                    }
                    CTILogger.debug(" ** RSET FINISHED *" + count + "* getCalcHistoricalPointDataMsgVector");
                    if (tempRawPointHistoryVector != null && tempRawPointHistoryVector.size() > 0) {
                        rawPointHistoryVectorOfVectors.add(tempRawPointHistoryVector);
                    } else {
                        rawPointHistoryVectorOfVectors.add(new Vector<LiteRawPointHistory>(0)); // add vector
                                                                                                // with size
                                                                                                // == 0
                    }

                    tempRawPointHistoryVector = null;
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rset != null) {
                    rset.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rawPointHistoryVectorOfVectors;
    }

    public DispatchClientConnection getDispatchConnection() {
        if (dispatchConnection == null || !dispatchConnection.isValid()) {
            dispatchConnection = ClientConnectionFactory.getInstance().createDispatchConn();

            Registration reg = new Registration();
            reg.setAppName(CtiUtilities.getAppRegistration());
            reg.setAppIsUnique(0);
            reg.setAppKnownPort(0);
            reg.setAppExpirationDelay(3600); // 1 hour should be OK

            dispatchConnection.setAutoReconnect(true);
            dispatchConnection.setRegistrationMsg(reg);

            try {
                dispatchConnection.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dispatchConnection;
    }

    private Vector<CalcComponent> retrieveHistoricalCalcComponents() {
        Vector<CalcComponent> calcComponents = new Vector<>();

        java.sql.PreparedStatement preparedStatement = null;
        java.sql.Connection conn = null;
        java.sql.ResultSet rset = null;
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            String statement =
                ("SELECT CC.POINTID, CC.COMPONENTORDER, CC.COMPONENTTYPE, CC.COMPONENTPOINTID, CC.OPERATION, CC.CONSTANT, CC.FUNCTIONNAME "
                    + "FROM CALCCOMPONENT CC, POINT P, YUKONPAOBJECT PAO, CALCBASE CB "
                    + "WHERE CC.COMPONENTPOINTID = P.POINTID "
                    + "AND CC.POINTID = CB.POINTID "
                    + "AND P.PAOBJECTID = PAO.PAOBJECTID "
                    + "AND PAO.DISABLEFLAG = 'N' "
                    + "AND CB.UPDATETYPE = 'Historical' " + "ORDER BY CC.POINTID, CC.COMPONENTORDER");

            preparedStatement = conn.prepareStatement(statement);
            rset = preparedStatement.executeQuery();

            while (rset.next()) {
                Integer pointID = new Integer(rset.getInt(1));
                Integer componentOrder = new Integer(rset.getInt(2));
                String componentType = rset.getString(3);
                Integer componentPointID = new Integer(rset.getInt(4));
                String operation = rset.getString(5);
                Double constant = new Double(rset.getDouble(6));
                String functionName = rset.getString(7);

                CalcComponent cc =
                    new CalcComponent(pointID, componentOrder, componentType, componentPointID, operation, constant,
                        functionName);

                calcComponents.add(cc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rset != null) {
                    rset.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return calcComponents;
    }

    public GregorianCalendar getNextCalcTime() {
        return this.nextCalcTime;
    }

    private double calculatePowerFactor(PF pFactor) {
        double pfValue = 1.0;
        double kva = pFactor.kva_value;
        double kw = pFactor.kw_value;
        double kvar = pFactor.kvar_value;

        if (pFactor.pfType == KW_KVAR_PFTYPE) {
            // KVA = sqrt( KW^2 + KVAR^2 )
            kva = Math.sqrt((kw * kw) + (kvar * kvar));
        } else if (pFactor.pfType == KW_KQ_PFTYPE) {
            // KVAR = ((2 * KQ) - KW) / SQRT3)
            kvar = (((2 * pFactor.kq_value) - kw) / pFactor.SQRT3);
            kva = Math.sqrt((kw * kw) + (kvar * kvar));
        } else if (pFactor.pfType == KW_KVA_PFTYPE) {
            // all values already stored from database.
        }

        // Calc PF=KW/KVA (based on DSM2 Calculations)
        if (kvar == 0.0) {
            pfValue = 1.0;
        } else if (kva != 0) {
            pfValue = kw / kva;
            // Check if this is leading
            if (kvar < 0.0 && pfValue != 1.0) {
                pfValue = -pfValue;
            }
        } else {
            CTILogger.info(" ERROR IN CALCULATEPOWERFACTOR::Cannot devide by 0");
        }
        return pfValue;
    }

    public static void logEvent(String event, int severity) {
        if (logger == null) {
            try {
                String dataDir = "../log/";
                File file = new File(dataDir);
                file.mkdirs();

                String className = "calchist";
                String filename = dataDir + className + ".log";
                FileOutputStream out = new FileOutputStream(filename, true);
                PrintWriter writer = new PrintWriter(out, true);
                logger = new LogWriter(className, LogWriter.DEBUG, writer);

                logger.log("Starting up " + className, LogWriter.INFO);
                logger.log("Version: " + VersionTools.getYUKON_VERSION() + ".", LogWriter.INFO);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.log(event, severity);
    }

    public void start() {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                Baseline baseLine = new Baseline();

                GregorianCalendar lastCalcPointRawPointHistoryTimeStamp;
                int calcComponentIndex = 0;

                Vector<PointData> tempPointDataMsgVector = null;
                Date now = null;

                figureNextCalcTime();
                baseLine.figureNextBaselineCalcTime();

                CTILogger.info("Calc Historical Version: " + VersionTools.getYUKON_VERSION() + " Started.");
                logEvent("Calc Historical (Version: " + VersionTools.getYUKON_VERSION() + ") Started.", LogWriter.INFO);

                new Thread();

                do {
                    now = new Date();

                    if (getNextCalcTime().getTime().compareTo(now) <= 0) {
                        CTILogger.info("Starting period calculating of historical points.");
                        logEvent("Starting period calculating of historical points.", LogWriter.INFO);

                        // Get a list of all 'Historical' CalcPoints and their fields from Point table in
                        // database.
                        // Get a list of all CalcComponents and their fields from calcComponent table in
                        // database
                        // contains com.cannontech.database.db.point.calculation.CalcComponent values.
                        Vector<CalcComponent> allHistoricalCalcComponentsList = retrieveHistoricalCalcComponents();
                        // contains
                        Vector<Integer> calcBasePoints = getCalcBasePoints(allHistoricalCalcComponentsList);

                        // calcBasePoints, Vector of Integer pointIds for distinct calcBase points.

                        for (int i = 0; i < calcBasePoints.size(); i++) {
                            if (isService) // Check for service exit
                            {
                                int pointID = calcBasePoints.get(i).intValue();

                                // contains CalcComponent values.
                                Vector<CalcComponent> currentCalcComponents = new Vector<>();
                                while (calcComponentIndex < allHistoricalCalcComponentsList.size()) {
                                    // Find the calcComponents entries with pointIds matching the LitePoint
                                    // (current CalcBase pointId).
                                    if (pointID == allHistoricalCalcComponentsList.get(calcComponentIndex).getPointID().intValue()) {
                                        currentCalcComponents.add(allHistoricalCalcComponentsList.get(calcComponentIndex));
                                        calcComponentIndex++;
                                    } else {
                                        break;
                                    }
                                }
                                if (!currentCalcComponents.isEmpty()) {
                                    lastCalcPointRawPointHistoryTimeStamp =
                                        getCalcHistoricalLastUpdateTimeStamp(pointID);

                                    Vector<Vector<LiteRawPointHistory>> rphDataVectorOfVectors =
                                        getRawPointHistoryVectorOfVectors(currentCalcComponents,
                                            lastCalcPointRawPointHistoryTimeStamp);
                                    tempPointDataMsgVector =
                                        parseAndCalculateRawPointHistories(rphDataVectorOfVectors, pointID,
                                            currentCalcComponents);
                                    writeMultiMessage(tempPointDataMsgVector, pointID);
                                }
                                // else
                                // CTILogger.info("Skipping point "+ litePoint.getPointID());
                            } else {
                                // Forcing the for loop exit
                                i = calcBasePoints.size();
                                break;
                            }
                        }

                        CTILogger.info("Done with period calculating of historical points.");
                        logEvent("Done with period calculating of historical points.", LogWriter.INFO);
                        figureNextCalcTime();

                        // Clear out the lists.
                        allHistoricalCalcComponentsList.clear();
                        calcComponentIndex = 0;
                    }

                    // CALCULATE BASELINE TOTALS.
                    if (baseLine.getNextBaselineCalcTime().getTime().compareTo(now) <= 0) {
                        CTILogger.info("Starting baseline calculation of baseline calc points.");
                        logEvent("Starting baseline calculation of baseline calc points.", LogWriter.INFO);

                        // Get a list of all 'Historical' & 'Baseline' CalcPoints and their fields from Point
                        // table in database.
                        // contains com.cannontech.database.db.point.calculation.CalcComponent values.
                        Vector<CalcComponent> allBaselineCalcComponents = retrieveHistoricalCalcComponents();
                        Vector<Integer> calcBasePoints = getCalcBasePoints(allBaselineCalcComponents);
                        baseLine.setHistoricalCalcComponents(allBaselineCalcComponents);

                        // Loop through each calcBase point(ID).
                        for (int i = 0; i < calcBasePoints.size(); i++) {
                            // Kind of hackery to init this thing here...but it works for now.
                            baseLine.returnPointDataMsgVector = new Vector<>();
                            Integer pointID = (calcBasePoints.get(i));
                            tempPointDataMsgVector = baseLine.main(pointID);
                            writeMultiMessage(tempPointDataMsgVector, pointID.intValue());
                        }

                        CTILogger.info("[" + new Date() + "]  Done with baseline calculation of baseline calc points.");
                        logEvent("Done with baseline calculation of baseline calc points.", LogWriter.INFO);
                        baseLine.figureNextBaselineCalcTime();
                    }

                    try {
                        System.gc();
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        CTILogger.info("Exiting Calc Historical");
                        logEvent("Exiting Calc Historical", LogWriter.ERROR);
                        if (getDispatchConnection().isValid()) {
                            getDispatchConnection().disconnect();
                        }
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (isService);

                getDispatchConnection().disconnect();
                dispatchConnection = null;

                logger.getPrintWriter().close();
                logger = null;

                CTILogger.info("Exiting Calc Historical...at end");
                logEvent("Exiting Calc Historical...at end", LogWriter.INFO);

                // be sure the runner thread is NULL
                starter = null;
            }
        };

        if (starter == null) {
            starter = new Thread(runner, "CalcHistorical");
            starter.start();
        }

    }

    public void stop() {
        try {
            Thread t = starter;
            starter = null;
            t.interrupt();
        } catch (Exception e) {
            // Ignore
        }
    }

    public boolean isRunning() {
        return starter != null;
    }

    public static void main(String[] args) {
        ClientSession session = ClientSession.getInstance();
        if (!session.establishSession()) {
            System.exit(-1);
        }

        System.setProperty("cti.app.name", "CalcHistorical");
        CalcHistorical calcHistorical = new CalcHistorical();

        calcHistorical.start();
    }

    private Vector<Integer> getCalcBasePoints(Vector<CalcComponent> calcComponents) {
        // Store just the distinct CalcPoint ID's
        Vector<Integer> calcBasePoints = new Vector<>();
        for (int i = 0; i < calcComponents.size(); i++) {
            Integer ptID = calcComponents.get(i).getPointID();
            if (!calcBasePoints.contains(ptID)) {
                calcBasePoints.add(ptID);
            }
        }
        return calcBasePoints;
    }

    private Vector<PointData> parseAndCalculateRawPointHistories(
            Vector<Vector<LiteRawPointHistory>> rawPointHistoryVectorOfVectors, int pointID,
            Vector<CalcComponent> calcComponents) {
        if (rawPointHistoryVectorOfVectors.size() == 0) {
            return null;
        }

        for (int i = 0; i < rawPointHistoryVectorOfVectors.size(); i++) {
            if (rawPointHistoryVectorOfVectors.get(i).size() == 0) {
                // if one of the rawpointhistory vectors is empty we can not matchup the timestamps
                return null;
            }
        }

        int arrayOfIndexes[] = new int[rawPointHistoryVectorOfVectors.size()];
        for (int i = 0; i < rawPointHistoryVectorOfVectors.size(); i++) {
            arrayOfIndexes[i] = 0;
        }

        Vector<Object> tempRawPointHistoryVector = null;
        Vector<PointData> returnVector = new Vector<>();

        boolean done = false;
        PointData pointDataMsg = null;
        // GregorianCalendar targetRawPointHistoryTimeStamp = null;
        long targetRPHTimeInMillis = 0;

        if (arrayOfIndexes[0] < rawPointHistoryVectorOfVectors.get(0).size()) {
            targetRPHTimeInMillis = rawPointHistoryVectorOfVectors.get(0).get(arrayOfIndexes[0]).getTimeStamp();
        } else {
            done = true;
        }

        while (!done) {
            tempRawPointHistoryVector = new Vector<>(rawPointHistoryVectorOfVectors.size());

            while (tempRawPointHistoryVector.size() < rawPointHistoryVectorOfVectors.size() && !done) {
                for (int i = 0; i < rawPointHistoryVectorOfVectors.size(); i++) {
                    Vector<LiteRawPointHistory> rawPointHistoryVectors = rawPointHistoryVectorOfVectors.get(i);
                    if (arrayOfIndexes[i] < rawPointHistoryVectors.size()) {
                        if (targetRPHTimeInMillis > rawPointHistoryVectors.get(arrayOfIndexes[i]).getTimeStamp()) {
                            while (arrayOfIndexes[i] < rawPointHistoryVectors.size()
                                && targetRPHTimeInMillis > rawPointHistoryVectors.get(arrayOfIndexes[i]).getTimeStamp()) {
                                arrayOfIndexes[i]++;
                            }

                            if (arrayOfIndexes[i] >= rawPointHistoryVectors.size()) {
                                if (tempRawPointHistoryVector != null) {
                                    tempRawPointHistoryVector.clear();
                                }

                                done = true;
                            }
                            break;
                        } else if (targetRPHTimeInMillis < rawPointHistoryVectors.get(arrayOfIndexes[i]).getTimeStamp()) {
                            if (tempRawPointHistoryVector != null) {
                                tempRawPointHistoryVector.clear();
                            }
                            targetRPHTimeInMillis = rawPointHistoryVectors.get(arrayOfIndexes[i]).getTimeStamp();
                            break;
                        } else if (targetRPHTimeInMillis == rawPointHistoryVectors.get(arrayOfIndexes[i]).getTimeStamp()) {
                            Object x = rawPointHistoryVectors.get(arrayOfIndexes[i]);
                            tempRawPointHistoryVector.addElement(x);
                        }
                    } else {
                        if (tempRawPointHistoryVector != null) {
                            tempRawPointHistoryVector.clear();
                        }
                        done = true;
                        break;
                    }
                }
            }

            if (tempRawPointHistoryVector != null && tempRawPointHistoryVector.size() > 0
                && tempRawPointHistoryVector.size() == rawPointHistoryVectorOfVectors.size()) {
                Double value = figurePointDataMsgValue(calcComponents, tempRawPointHistoryVector);
                if (value != null) {
                    pointDataMsg = new PointData();
                    pointDataMsg.setId(pointID);
                    pointDataMsg.setValue(value.doubleValue());
                    pointDataMsg.setTimeStamp(new Date(
                        ((LiteRawPointHistory) tempRawPointHistoryVector.get(0)).getTimeStamp()));
                    pointDataMsg.setTime(new Date(
                        ((LiteRawPointHistory) tempRawPointHistoryVector.get(0)).getTimeStamp()));
                    pointDataMsg.setPointQuality(PointQuality.Normal);
                    pointDataMsg.setType(PointTypes.CALCULATED_POINT);
                    pointDataMsg.setTagsLoadProfileData(true);
                    pointDataMsg.setStr("Calc Historical");

                    returnVector.addElement(pointDataMsg);
                    updateDynamicCalcHistorical(pointDataMsg.getTimeStamp(), pointID);

                }
                for (int i = 0; i < rawPointHistoryVectorOfVectors.size(); i++) {
                    arrayOfIndexes[i]++;
                }
            }

            tempRawPointHistoryVector = null;
        }

        for (int i = 0; i < returnVector.size(); i++) {
            PointData tempPD = returnVector.get(i);
            CTILogger.debug("POINTID: " + tempPD.getId() + " -  TAG: " + Long.toHexString(tempPD.getTags()));
        }
        return returnVector;
    }

    public static void updateDynamicCalcHistorical(Date lastUpdate, int pointID) {
        StringBuffer pSql =
            new StringBuffer("UPDATE DYNAMICCALCHISTORICAL SET LASTUPDATE =? WHERE POINTID = " + pointID);

        java.sql.PreparedStatement pstmt = null;
        java.sql.Connection conn = null;
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            pstmt = conn.prepareStatement(pSql.toString());
            pstmt.setTimestamp(1, new java.sql.Timestamp(lastUpdate.getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // throw e;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                CTILogger.info(" *** NEW DYNAMIC TABLE may be missing:  DYNAMICCALCHISTORICAL");
                logEvent(" *** NEW DYNAMIC TABLE may be missing:  DYNAMICCALCHISTORICAL", LogWriter.DEBUG);
                e.printStackTrace();

            }
        }
    }

    private void writeMultiMessage(Vector<PointData> pointDataMsgVector, int pointID) {
        if (pointDataMsgVector != null) {
            Multi<PointData> multiMsg = new Multi<>();
            multiMsg.getVector().addAll(pointDataMsgVector);

            if (pointDataMsgVector != null) {
                pointDataMsgVector.clear();
            }

            if (getDispatchConnection().isValid()) {
                if (multiMsg.getVector().size() > 0) {
                    CTILogger.info("Sending " + multiMsg.getVector().size()
                        + " point changes to Dispatch for pointID: " + pointID);
                    logEvent("Sending " + multiMsg.getVector().size() + " point changes to Dispatch for pointID: "
                        + pointID, LogWriter.INFO);
                    getDispatchConnection().write(multiMsg);
                } else {
                    CTILogger.info("Dispatch connection valid, but no Point Changes to send at this time for pointID: "
                        + pointID);
                    logEvent("Dispatch connection valid, but no Point Changes to send at this time for pointID: "
                        + pointID, LogWriter.INFO);
                }
            } else {
                CTILogger.info("Dispatch connection is not valid couldn't send point changes.");
                logEvent("Dispatch connection is not valid couldn't send point changes.", LogWriter.DEBUG);
            }
        }
    }
}
