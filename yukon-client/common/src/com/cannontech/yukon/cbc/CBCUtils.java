package com.cannontech.yukon.cbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController701x;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.roles.capcontrol.CBCSettingsRole;

/**
 * @author ryan Generic utility classes for CapControl
 */
public final class CBCUtils {
    public static final int TEMP_MOVE_REFRESH = 1000;
    // responsible for how to render data for CBC displays
    public static final CBCDisplay CBC_DISPLAY = new CBCDisplay();

    public static final Comparator CBC_AREA_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            try {
                String thisArea = ((CBCArea) o1).getPaoName();
                String anotherArea = ((CBCArea) o2).getPaoName();

                return (thisArea.compareToIgnoreCase(anotherArea));

            } catch (Exception e) {
                CTILogger.error("Something went wrong with sorting, ignoring sorting rules",
                                e);
                return 0;
            }

        }
    };

    public static final Comparator SUB_AREA_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            try {
                String thisArea = ((SubBus) o1).getCcArea();
                String anotherArea = ((SubBus) o2).getCcArea();

                if (!thisArea.equalsIgnoreCase(anotherArea))
                    return (thisArea.compareToIgnoreCase(anotherArea));

                // if the Area Names are equal, we need to sort by SubName
                String thisName = ((SubBus) o1).getCcName();
                String anotherName = ((SubBus) o2).getCcName();

                return (thisName.compareToIgnoreCase(anotherName));
            } catch (Exception e) {
                CTILogger.error("Something went wrong with sorting, ignoring sorting rules",
                                e);
                return 0;
            }

        }
    };

    public static final Comparator CCNAME_COMPARATOR = new Comparator() {
        public int compare(Object o1, Object o2) {
            try {
                String strA = ((StreamableCapObject) o1).getCcName();
                String strB = ((StreamableCapObject) o2).getCcName();

                return strA.compareToIgnoreCase(strB);
            } catch (Exception e) {
                CTILogger.error("Something went wrong with sorting, ignoring sorting rules",
                                e);
                return 0;
            }

        }
    };

    public static final boolean isPowerFactorControlled(String controlUnits) {
        return (CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION.equalsIgnoreCase(controlUnits) || CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION.equalsIgnoreCase(controlUnits));
    }

    /**
     * Calculates the summation of VARS for an array of CapBankDevices that are
     * in a open state.
     */
    public static final int calcTrippedVARS(CapBankDevice[] capBanks) {
        int retVal = 0;
        if (capBanks == null)
            return retVal;

        for (int i = 0; i < capBanks.length; i++) {
            CapBankDevice capBank = capBanks[i];
            if (CapBankDevice.isInAnyOpenState(capBank))
                retVal += capBanks[i].getBankSize().intValue();
        }

        return retVal;
    }

    /**
     * Calculates the summation of VARS for an array of CapBankDevices that are
     * in a closed state.
     */
    public static final int calcClosedVARS(CapBankDevice[] capBanks) {
        int retVal = 0;
        if (capBanks == null)
            return retVal;

        for (int i = 0; i < capBanks.length; i++) {
            CapBankDevice capBank = capBanks[i];
            if (CapBankDevice.isInAnyCloseState(capBank))
                retVal += capBanks[i].getBankSize().intValue();
        }

        return retVal;
    }

    /**
     * Calculates the average PowerFactor for an array of SubBuses that have
     * valid PowerFactor values.
     */
    public static final double calcAvgPF(SubBus[] subs) {
        double retVal = 0.0;
        // temp variables
        double sumOfVars = 0.0;
        double sumOfWatts = 0.0;

        if (subs == null)
            return CapControlConst.PF_INVALID_VALUE;

        int numberOfSubs = subs.length;

        for (int i = 0; i < numberOfSubs; i++) {
            SubBus subBus = subs[i];
            if (subBus != null) {
                sumOfVars += subBus.getCurrentVarLoadPointValue().doubleValue();
                sumOfWatts += Math.abs(subBus.getCurrentWattLoadPointValue()
                                             .doubleValue());
            }
        }
        retVal = sumOfWatts / (Math.sqrt(Math.pow(sumOfVars, 2.0) + Math.pow(sumOfWatts,
                                                                             2.0)));
        if (sumOfVars < 0) {
            retVal = retVal * (-1);
        }

        return retVal;
    }

    /**
     * Calculates the estimaged average PowerFactor for an array of SubBuses
     * that have valid PowerFactor values.
     */
    public static final double calcAvgEstPF(SubBus[] subs) {
        double retVal = 0.0;
        // temp variables
        double sumOfVars = 0.0;
        double sumOfWatts = 0.0;

        if (subs == null)
            return CapControlConst.PF_INVALID_VALUE;

        int numberOfSubs = subs.length;

        for (int i = 0; i < numberOfSubs; i++) {
            sumOfVars += subs[i].getEstimatedVarLoadPointValue().doubleValue();
            sumOfWatts += Math.abs(subs[i].getCurrentWattLoadPointValue()
                                          .doubleValue());

        }
        retVal = sumOfWatts / (Math.sqrt(Math.pow(sumOfVars, 2.0) + Math.pow(sumOfWatts,
                                                                             2.0)));
        if (sumOfVars < 0) {
            retVal = retVal * (-1);
        }
        return retVal;
    }

    public static String format(int val) {
        return NumberFormat.getInstance().format(val);
    }

    public static String format(double val) {
        return NumberFormat.getInstance().format(val);
    }

    public static boolean isTwoWay(int type) {
        switch (type) {
        case PAOGroups.DNP_CBC_6510:
        case PAOGroups.CBC_7020:
        case PAOGroups.CBC_7022:
        case PAOGroups.CBC_7023:
        case PAOGroups.CBC_7024:
            return true;
        default:
            return false;
        }
    }

    public static boolean isTwoWay(LiteYukonPAObject obj) {
        DBPersistent dbPers = LiteFactory.convertLiteToDBPersAndRetrieve(obj);
        if (dbPers instanceof TwoWayDevice)
            return true;
        else
            return false;
    }

    public static boolean is701xDevice(LiteYukonPAObject obj) {
        DBPersistent dbPers = LiteFactory.convertLiteToDBPersAndRetrieve(obj);
        if (dbPers instanceof CapBankController701x)
            return true;
        else
            return false;

    }

    // default date for DB is 1990-01-01 00:00:00.000
    public static Date getDefaultStartTime() {
        Calendar cal = new GregorianCalendar(1990, Calendar.JANUARY, 1, 0, 0, 0);
        return cal.getTime();
    }

    public static String getAreaNameFromSubId(int subID) {

        String sqlStmt1 = "SELECT AreaID FROM CCSubAreaAssignment WHERE SubstationBusID = ?";
        String sqlStmt2 = "SELECT PAOName FROM YukonPAObject WHERE PAObjectID = ?";
        String areaName = null;
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        try {
            int areaid = yukonTemplate.queryForInt(sqlStmt1,
                                                   new Integer[] { new Integer(subID) });
            areaName = (String) yukonTemplate.queryForObject(sqlStmt2,
                                                             new Integer[] { new Integer(areaid) },
                                                             new RowMapper() {
                                                                 public Object mapRow(
                                                                         ResultSet rs,
                                                                         int rowNum)
                                                                         throws SQLException {
                                                                     return rs.getString(1);
                                                                 }

                                                             });
            return areaName;
        } catch (IncorrectResultSizeDataAccessException e) {
            areaName = new String("(none)");
        }
        return areaName;
    }

    /**
     * @param paoID
     * @return CapControlStrategy for Paobject. If no strategy exists returns a
     *         strategy with stratID = 0, stratName = "(none)"
     */
    public static SubSnapshotParams getSubSnapshot(int subID) {
        if (subID >= CtiUtilities.NONE_ZERO_ID) {

            String sqlStmt = "SELECT SubstationbusID, " + "ControlUnits, ControlMethod FROM CapControlSubstationBus, " + "CapControlStrategy WHERE CapControlSubstationBus.StrategyID = CapControlStrategy.StrategyID " + "AND SubStationBusID = ?";

            JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
            SubSnapshotParams snapshot = null;
            try {
                snapshot = (SubSnapshotParams) yukonTemplate.queryForObject(sqlStmt,
                                                                            new Integer[] { new Integer(subID) },
                                                                            new RowMapper() {
                                                                                public Object mapRow(
                                                                                        ResultSet rs,
                                                                                        int rowNum)
                                                                                        throws SQLException {
                                                                                    SubSnapshotParams snapParam = new SubSnapshotParams();
                                                                                    snapParam.setBusID(rs.getInt(1));
                                                                                    snapParam.setAlgorithm((rs.getString(2)));
                                                                                    snapParam.setControlMethod(rs.getString(3));
                                                                                    return snapParam;
                                                                                }

                                                                            });
            } catch (IncorrectResultSizeDataAccessException e) {
                snapshot = new SubSnapshotParams();
            }
            return snapshot;
        }
        return new SubSnapshotParams();
    }

    public static Integer getStateGroupIDByGroupName(String groupName) {
        LiteStateGroup[] allStateGroups = DaoFactory.getStateDao()
                                                    .getAllStateGroups();
        for (int i = 0; i < allStateGroups.length; i++) {
            LiteStateGroup group = allStateGroups[i];
            if (group.getStateGroupName().equalsIgnoreCase(groupName)) {
                return new Integer(group.getStateGroupID());
            }
        }
        return null;

    }

    public static String getAllManualCapStates() {
        String liteStates = "";
        LiteState[] cbcStates = CBCDisplay.getCBCStateNames();
        // create a comma separated string of all states
        // "Any:-1,Open:0,Close:1"
        for (int i = 0; i < cbcStates.length; i++) {
            LiteState state = cbcStates[i];
            liteStates += state.toString() + ":" + state.getStateRawState();
            if (i < (cbcStates.length - 1))
                liteStates += ",";
        }
        return liteStates;
    }

    public static boolean isCBAdditionalInfoAllowed(LiteYukonUser user) {
        boolean showCapBankAddInfo = Boolean.valueOf(DaoFactory.getAuthDao()
                                                               .getRolePropertyValue(user,
                                                                                     CBCSettingsRole.SHOW_CB_ADDINFO))
                                            .booleanValue();
        return showCapBankAddInfo;
    }

    /**
     * returns the sum of VARS on switched capbanks for every ENABLED cap object
     * @param object
     * @return
     */
    public  static double calcVarsAvailable(Object object) {
        double returnVal = 0.0;
        if (object instanceof List) {
            List<SubBus> subs = (List<SubBus>) object;
            for (SubBus bus : subs) {
                returnVal += calcVarsAvailable(bus);
            }
        } else if (object instanceof SubBus) {
            SubBus sub = (SubBus) object;
            return (calcTotalSwitchedVars (sub) - calcVarsDisabled (sub));
        }
        return returnVal;
    }

    private static double calcTotalSwitchedVars(SubBus sub) {
        double retVal = addAllSwitchedCapsSub(sub, true);
        retVal += addAllSwitchedCapsSub(sub, false);
        return retVal;
    }

    /**
     * returns the sum of VARS on switched capbanks for every DISABLED cap object
     * @param object
     * @return
     */
    public  static double calcVarsDisabled(Object object) {
        double returnVal = 0.0;
        if (object instanceof List) {
            List<SubBus> subs = (List<SubBus>) object;
            for (SubBus bus : subs) {
                returnVal+= calcVarsDisabled(bus);
            }
        } else if (object instanceof SubBus) {
            SubBus sub = (SubBus) object;
            if (isDisabled(sub)) {
                returnVal = addAllSwitchedCapsSub(sub, true);
                returnVal += addAllSwitchedCapsSub(sub, false);
            }
            else
            {
                boolean isFeederDis = true;
                return addAllSwitchedCapsSub(sub, isFeederDis);                
                
            }
        }
        return returnVal;
    }
    
    private static double addAllSwitchedCapsSub(SubBus sub, boolean disFlagAssert) {
        Vector ccFeeders = sub.getCcFeeders();
        double returnVal = 0.0;
        for (Iterator iter = ccFeeders.iterator(); iter.hasNext();) {
            Feeder feeder = (Feeder) iter.next();
            if (feeder.getCcDisableFlag().booleanValue() == disFlagAssert) {
                returnVal  += addAllSwitchedBankSizes(feeder);
            }
        }
        return returnVal;
    }



    private static double addAllSwitchedBankSizes(Feeder feeder) {
        double returnVal = 0.0;
        Vector ccCapBanks = feeder.getCcCapBanks();
        for (Iterator iterator = ccCapBanks.iterator(); iterator.hasNext();) {
            CapBankDevice capBank = (CapBankDevice) iterator.next();
            if (isSwitched(capBank)) {
                returnVal += capBank.getBankSize();
            }
        }
        return returnVal;
    }

    public static boolean isEnabled(StreamableCapObject object) {
        return object.getCcDisableFlag().equals(Boolean.FALSE);
    }

    public static boolean isDisabled(StreamableCapObject object) {
        return object.getCcDisableFlag().equals(Boolean.TRUE);
    }

    public static boolean isSwitched(CapBankDevice capBank) {
        return capBank.getOperationalState()
                      .equalsIgnoreCase(CapBank.SWITCHED_OPSTATE);
    }

}