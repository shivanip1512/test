package com.cannontech.cbc.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.database.model.Season;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.PointQualityCheckable;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.CapControlConst;

public final class CapControlUtils {

    private static final CapControlCache ccCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
    private static final StateDao stateDao = YukonSpringHook.getBean("stateDao", StateDao.class);
    private static final RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
    private static final SeasonScheduleDao seasonScheduleDao = YukonSpringHook.getBean(SeasonScheduleDao.class);
    private static final YukonUserContextMessageSourceResolver messageResolver = YukonSpringHook.getBean(YukonUserContextMessageSourceResolver.class);
    private static Map<String, List<String>> kvarPropertiesAsLists = new HashMap<>();
    private final static Logger log = YukonLogManager.getLogger(CapControlUtils.class);

    public static final Comparator<SubBus> SUB_DISPLAY_COMPARATOR = new Comparator<SubBus>() {
        @Override
        public int compare(SubBus o1, SubBus o2) {
            Integer order1 = o1.getDisplayOrder();
            Integer order2 = o2.getDisplayOrder();
            return order1.compareTo(order2);
        }
    };

    public static final Comparator<StreamableCapObject> CCNAME_COMPARATOR = new Comparator<StreamableCapObject>() {
        @Override
        public int compare(StreamableCapObject o1, StreamableCapObject o2) {
            try {
                String strA = o1.getCcName();
                String strB = o2.getCcName();

                return strA.compareToIgnoreCase(strB);
            } catch (Exception e) {
                log.error("Something went wrong with sorting, ignoring sorting rules", e);
                return 0;
            }

        }
    };

    public static final Comparator<CCFeederBankList> BANK_DISPLAY_ORDER_COMPARATOR = new Comparator<CCFeederBankList>() {
        @Override
        public int compare(CCFeederBankList o1, CCFeederBankList o2) {
            Float order1 = o1.getControlOrder();
            Float order2 = o2.getControlOrder();
            int result = order1.compareTo(order2);
            if (result == 0) {
                result = o1.getDeviceID().compareTo(o2.getDeviceID());
            }

            return result;
        }
    };

    public static final Comparator<CCFeederBankList> BANK_CLOSE_ORDER_COMPARATOR = new Comparator<CCFeederBankList>() {
        @Override
        public int compare(CCFeederBankList o1, CCFeederBankList o2) {
            Float order1 = o1.getCloseOrder();
            Float order2 = o2.getCloseOrder();
            int result = order1.compareTo(order2);
            if (result == 0) {
                result = o1.getDeviceID().compareTo(o2.getDeviceID());
            }

            return result;
        }
    };

    public static final Comparator<CCFeederBankList> BANK_TRIP_ORDER_COMPARATOR = new Comparator<CCFeederBankList>() {
        @Override
        public int compare(CCFeederBankList o1, CCFeederBankList o2) {
            Float order1 = o1.getTripOrder();
            Float order2 = o2.getTripOrder();
            int result = order1.compareTo(order2);
            if (result == 0) {
                result = o1.getDeviceID().compareTo(o2.getDeviceID());
            }

            return result;
        }
    };

    /**
     * Calculates the average PowerFactor for an array of SubBuses that have
     * valid PowerFactor values.
     */
    public static final double calcAvgPF(List<SubStation> stations) {
        double retVal = 0.0;
        double sumOfVars = 0.0;
        double sumOfWatts = 0.0;

        if (stations == null) return CapControlConst.PF_INVALID_VALUE;

        if (stations.isEmpty()) return 0.0;

        for (SubStation station: stations) {
            List<SubBus> subBuses = ccCache.getSubBusesBySubStation(station);
            for (SubBus subBus : subBuses) {
                if (subBus != null) {
                    sumOfVars += subBus.getCurrentVarLoadPointValue();
                    sumOfWatts += Math.abs(subBus.getCurrentWattLoadPointValue());
                }
            }
        }
        retVal = sumOfWatts / (Math.sqrt(Math.pow(sumOfVars, 2.0) + Math.pow(sumOfWatts, 2.0)));

        if (sumOfVars < 0) {
            retVal = -retVal;
        }
        return Double.isNaN(retVal) ? 0.0 : retVal;
    }

    /**
     * Calculates the estimated average PowerFactor for an array of SubBuses
     * that have valid PowerFactor values.
     */
    public static final double calcAvgEstPF(List<SubStation> stations) {
        double retVal = 0.0;
        double sumOfVars = 0.0;
        double sumOfWatts = 0.0;

        if (stations == null) return CapControlConst.PF_INVALID_VALUE;

        if (stations.isEmpty()) return 0.0;

        for(SubStation station: stations) {
            List<SubBus> buses = ccCache.getSubBusesBySubStation(station);
            for (SubBus bus: buses) {
                if( bus != null) {
                    sumOfVars += bus.getEstimatedVarLoadPointValue();
                    sumOfWatts += Math.abs(bus.getCurrentWattLoadPointValue());
                }
            }
        }
        retVal = sumOfWatts / (Math.sqrt(Math.pow(sumOfVars, 2.0) + Math.pow(sumOfWatts, 2.0)));

        if (sumOfVars < 0) {
            retVal = -retVal;
        }
        return Double.isNaN(retVal) ? 0.0 : retVal;
    }

    public static boolean isCBAdditionalInfoAllowed(LiteYukonUser user) {
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        boolean showCapBankAddInfo = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_CB_ADDINFO, user);
        return showCapBankAddInfo;
    }

    private static String getCapControlStateText(final int index) throws NotFoundException {
        final List<LiteState> stateList = stateDao.getLiteStateGroup(CapControlConst.CAPBANKSTATUS_STATEGROUP_ID).getStatesList();

        for (LiteState liteState : stateList) {
            if (liteState.getStateRawState() == index) {
                return liteState.getStateText();
            }
        }
        // If no matching state found..Note: getLiteStateGroup doesn't return rawStates < 0
        throw new NotFoundException("State with value of " + index + " not found");
    }
    
    /**
     * returns the sum of VARS on switched capbanks for every ENABLED cap object
     * on a SubBus
     * @param sub
     * @return
     */
    private static double calcVarsAvailableForSubBus(SubBus subBus, List<String> availableStates) {
        double returnVal = 0.0;
        for (Feeder feeder : subBus.getCcFeeders()) {
            returnVal += calcVarsAvailableForFeeder(feeder, availableStates);
        }
        return returnVal;
    }

    private static double calcVarsAvailableForFeeder(Feeder feeder, List<String> availableStates) {
        double returnVal = 0.0;

        for (CapBankDevice capBank : feeder.getCcCapBanks()) {
            String controlStateText;
            try {
                int controlStatusIndex = capBank.getControlStatus();
                controlStateText = getCapControlStateText(controlStatusIndex);
            } catch (NotFoundException ignoreAndContinue) {
                // don't count cb's with states that aren't standard states
                continue;
            }

            String operationalState = capBank.getOperationalState();

            boolean disabled = capBank.getCcDisableFlag();
            if (disabled) operationalState = "Disabled";

            String typeAndState = operationalState + ":" + controlStateText;
            if (availableStates.contains(typeAndState)){
                returnVal += capBank.getBankSize();
            }
        }
        return returnVal;
    }

    private static double calcVarsUnavailableForFeeder(Feeder feeder, List<String> unavailableStates) {
        double returnVal = 0.0;

        for (CapBankDevice capBank : feeder.getCcCapBanks()) {
            String controlStateText;
            try {
                int controlStatusIndex = capBank.getControlStatus();
                controlStateText = getCapControlStateText(controlStatusIndex);
            } catch (NotFoundException ignoreAndContinue) {
                // don't count cb's with states that aren't standard states
                continue;
            }

            String operationalState = capBank.getOperationalState();

            boolean disabled = capBank.getCcDisableFlag();
            if (disabled) operationalState = "Disabled";

            String typeAndState = operationalState + ":" + controlStateText;
            if (unavailableStates.contains(typeAndState)){
                returnVal += capBank.getBankSize();
            }
        }
        return returnVal;
    }

    /**
     * Calculates the summation of VARS for an array of CapBankDevices that are
     * in a open state.
     */
    public static final int calcVarsTrippedForCapBanks(List<CapBankDevice> capBanks, LiteYukonUser user) {
        int returnVal = 0;
        if (capBanks == null) {
            return returnVal;
        }
        List<String> trippedStates = getTrippedStatesList(user);

        for (CapBankDevice capBank : capBanks) {
            String controlStateText;
            try {
                int controlStatusIndex = capBank.getControlStatus();
                controlStateText = getCapControlStateText(controlStatusIndex);
            } catch (NotFoundException ignoreAndContinue) {
                // don't count cb's with states that aren't standard states
                continue;
            }

            String operationalState = capBank.getOperationalState();

            boolean disabled = capBank.getCcDisableFlag();
            if (disabled) operationalState = "Disabled";

            String typeAndState = operationalState + ":" + controlStateText;
            if (trippedStates.contains(typeAndState)){
                returnVal += capBank.getBankSize();
            }
        }
        return returnVal;
    }

    /**
     * Calculates the summation of VARS for an array of CapBankDevices that are
     * in a closed state.
     */
    public static final int calcVarsClosedForCapBanks(List<CapBankDevice> capBanks, LiteYukonUser user) {
        int returnVal = 0;
        if (capBanks == null) {
            return returnVal;
        }
        List<String> closedStates = getClosedStatesList(user);

        for (CapBankDevice capBank : capBanks) {
            String controlStateText;
            try {
                int controlStatusIndex = capBank.getControlStatus();
                controlStateText = getCapControlStateText(controlStatusIndex);
            } catch (NotFoundException ignoreAndContinue) {
                // don't count cb's with states that aren't standard states
                continue;
            }

            String operationalState = capBank.getOperationalState();

            boolean disabled = capBank.getCcDisableFlag();
            if (disabled) operationalState = "Disabled";

            String typeAndState = operationalState + ":" + controlStateText;
            if (closedStates.contains(typeAndState)){
                returnVal += capBank.getBankSize();
            }
        }
        return returnVal;
    }

    // must be a better way to do this
    private static List<String> getAvailableStatesList(LiteYukonUser user){
        return getRolePropertyAsList(YukonRoleProperty.AVAILABLE_DEFINITION, user);
    }

    //  must be a better way to do this
    private static List<String> getUnavailableStatesList(LiteYukonUser user){
        return getRolePropertyAsList(YukonRoleProperty.UNAVAILABLE_DEFINITION, user);
    }

    //  must be a better way to do this
    private static List<String> getClosedStatesList(LiteYukonUser user){
        return getRolePropertyAsList(YukonRoleProperty.CLOSED_DEFINITION, user);
    }

    //  must be a better way to do this
    private static List<String> getTrippedStatesList(LiteYukonUser user){
        return getRolePropertyAsList(YukonRoleProperty.TRIPPED_DEFINITION, user);
    }

    private static List<String> getRolePropertyAsList(YukonRoleProperty definition, LiteYukonUser user) {
        String rolePropery = rolePropertyDao.getPropertyStringValue(definition, user);
        if (kvarPropertiesAsLists.containsKey(rolePropery)) {
            return kvarPropertiesAsLists.get(rolePropery);
        }

        String[] array = rolePropery.split(",");
        List<String> list = Arrays.asList(array);
        kvarPropertiesAsLists.put(rolePropery, list);
        return list;
    }

    /**
     * returns the sum of VARS on switched capbanks for every ENABLED cap object
     * on a SubStation
     * @param sub
     * @return
     */
    public static double calcVarsAvailableForSubStation(SubStation substation, LiteYukonUser user) {
        List<String> availableStates = getAvailableStatesList(user);
        return calcVarsAvailableForSubStation(substation, availableStates);
    }

    private static double calcVarsAvailableForSubStation(SubStation substation, List<String> availableStates) {
        double returnVal = 0.0;
        List<SubBus> subBuses = ccCache.getSubBusesBySubStation(substation);
        if( subBuses != null ) {
            for (SubBus subBus : subBuses) {
                returnVal += calcVarsAvailableForSubBus(subBus, availableStates);
            }
        }
        return returnVal;
    }

    /**
     * returns the sum of VARS on switched capbanks for every ENABLED cap object
     * for a List<SubStation>
     * @param subs
     * @return
     */
    public static double calcVarsAvailableForSubStations(List<SubStation> subs, LiteYukonUser user) {
        List<String> availableStates = getAvailableStatesList(user);
        double returnVal = 0.0;
        if( subs != null )
            for (SubStation sub : subs) {
                returnVal += calcVarsAvailableForSubStation(sub, availableStates);
            }
        return returnVal;
    }

    public static double calcVarsUnavailableForSubStations(List<SubStation> subs, LiteYukonUser user) {
        double returnVal = 0.0;
        List<String> unavailableStates = getUnavailableStatesList(user);
        if( subs != null)
            for (SubStation bus : subs) {
                returnVal+= calcVarsUnavailableForSubStation(bus, unavailableStates);
            }
        return returnVal;
    }

    /**
     * returns the sum of VARS on switched capbanks for every DISABLED cap object
     * on a SubBus
     * @param object
     * @return
     */
    private static double calcVarsUnavailableForSubBus(SubBus subBus, List<String> unavailableStates) {
        double returnVal = 0.0;
        for (Feeder feeder : subBus.getCcFeeders()) {
            returnVal += calcVarsUnavailableForFeeder(feeder, unavailableStates);
        }
        return returnVal;
    }

  /**
   * returns the sum of VARS on switched capbanks for every DISABLED cap object
   * on a SubStation
   * @param object
   * @return
   */
    public static double calcVarsUnavailableForSubStation(SubStation substation, LiteYukonUser user) {
        List<String> unavailableStates = getUnavailableStatesList(user);
        return calcVarsUnavailableForSubStation(substation, unavailableStates);
    }

    private static double calcVarsUnavailableForSubStation(SubStation substation, List<String> unavailableStates) {
        double returnVal = 0.0;
        List<SubBus> subBuses = ccCache.getSubBusesBySubStation(substation);
        if( subBuses != null ) {
            for (SubBus subBus : subBuses) {
                returnVal += calcVarsUnavailableForSubBus(subBus, unavailableStates);
            }
        }
        return returnVal;
    }

    public static String getAreaNameForSubBus(int subBusId) {
        int areaId = ccCache.getParentAreaId(subBusId);
        return ccCache.getArea(areaId).getCcName();
     }

    public static boolean signalQualityNormal(PointQualityCheckable checkable, UnitOfMeasure uom) {
        boolean pointQualNormal = isPointQualNormal(checkable, uom);
        if (uom.isCapControlVar() || uom.isCapControlWatt() || uom.isCapControlVolt()) {
            return pointQualNormal;
        } else {
            return false;
        }
    }

    private static boolean isPointQualNormal(PointQualityCheckable checkable, UnitOfMeasure uom) {
        return checkable.getCurrentPtQuality(uom) == PointQuality.Normal.getQuality();
    }

    public static boolean checkControllerByType(PaoType paoType) {
        switch (paoType) {
            case CBC_7010:
            case CBC_7011:
            case CBC_7012:
            case CBC_7020:
            case CBC_7022:
            case CBC_7023:
            case CBC_7024:
            case CBC_8020:
            case CBC_8024:
            case CBC_EXPRESSCOM:
            case CAPBANKCONTROLLER:
            case CBC_DNP:
            case CBC_FP_2800:
                return true;
        default:
            return false;
        }
    }

    public static boolean isCapBankController802X(PaoType paoType) {
        return (paoType == PaoType.CBC_8020 || paoType == PaoType.CBC_8024);
    }

    public static boolean isController(int id) {
        LiteYukonPAObject lite = null;
        try{

            lite = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(id);
            return checkControllerByType(lite.getPaoType());
        }
        catch(NotFoundException nfe)
        {
            return false;
        }
    }

    /**
     * The text of capbanks states. This can change since is based on a state
     * group
     */
    public static LiteState[] getCBCStateNames() {
        return stateDao.getLiteStates(StateGroupUtils.STATEGROUPID_CAPBANK);
    }

    /**
     * Get the LiteState for the raw state value of of the CapBank state group.
     * @param rawState
     * @return The LiteState corresponding to this raw state or null;
     */
    public static LiteState getCapBankState(int rawState) {
        return stateDao.findLiteState(StateGroupUtils.STATEGROUPID_CAPBANK, rawState);
    }

    public static boolean isStrategyAttachedToSubBusOrSubBusParentArea(SubBus subBus) {
        SubStation subBusSubstation = ccCache.getSubstation(subBus.getParentID());
        int parentAreaId;
        if (subBusSubstation.getSpecialAreaEnabled()) {
            parentAreaId = subBusSubstation.getSpecialAreaId();
        } else {
            parentAreaId = ccCache.getParentAreaId(subBus.getCcId());
        }
        StreamableCapObject area = ccCache.getStreamableArea(parentAreaId);
        Map<Season, Integer> areaSeasonSchedule = seasonScheduleDao.getSeasonStrategyAssignments(area.getCcId());
        if (!isSeasonStrategyAssigned(areaSeasonSchedule)) {
            Map<Season, Integer> subBusSeasonSchedule = seasonScheduleDao.getSeasonStrategyAssignments(subBus.getCcId());
            if (!isSeasonStrategyAssigned(subBusSeasonSchedule)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSeasonStrategyAssigned(Map<Season, Integer> seasonSchedule) {
        for (Entry<Season, Integer> entry : seasonSchedule.entrySet()) {
            if (entry.getValue() != -1) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDualBusEnabled(SubBus subBus) {
        DBPersistent pao = PAOFactory.createPAObject(subBus.getCcId()
                                                     .intValue());
        Connection conn = null;

        try {
            conn = PoolManager.getInstance()
            .getConnection(CtiUtilities.getDatabaseAlias());
            pao.setDbConnection(conn);
            pao.retrieve();
        } catch (SQLException sql) {
            log.error("Unable to retrieve DB Object", sql);
        } finally {
            pao.setDbConnection(null);

            try {
                if (conn != null)
                    conn.close();
            } catch (java.sql.SQLException e2) {}
        }
        CapControlSubBus capControlSubBus1 = ((CapControlSubBus) pao);
        CapControlSubBus capControlSubBus = capControlSubBus1;
        String dualBusEnabled = capControlSubBus.getCapControlSubstationBus().getDualBusEnabled();
        return (dualBusEnabled.equalsIgnoreCase("Y")) ? true : false;
    }

    /**
     * Discovers if the given Feeder is in any Pending state
     */
    public static String getFeederPendingState(Feeder feeder) {
        for (CapBankDevice capBank : feeder.getCcCapBanks()) {

            if (capBank.getControlStatus() == CapControlConst.BANK_CLOSE_PENDING)
                return CapControlUtils.getCBCStateNames()[CapControlConst.BANK_CLOSE_PENDING].getStateText();

            if (capBank.getControlStatus() == CapControlConst.BANK_OPEN_PENDING)
                return CapControlUtils.getCBCStateNames()[CapControlConst.BANK_OPEN_PENDING].getStateText();
        }

        // we are not pending
        return null;
    }

    /**
     * Retrieves the Capbank fixed/static text property for a LiteYukonUser.  If the user is not in the CAP_BANK_FIXED_TEXT
     * role, the default value of "Fixed" is returned.
     */
    public static String getFixedText(LiteYukonUser yukonUser) {
        String fixedText = null;

        try {
            fixedText = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.CAP_BANK_FIXED_TEXT, yukonUser);
        } catch(UserNotInRoleException e) {
            log.warn("User not in Cap Bank Display role, using default Fixed text.");
        }

        if(StringUtils.isBlank(fixedText)) fixedText = "Fixed";

        return fixedText;
    }

    /**
     * This is used in CapBankDetailsController as a format
     */
    public static String convertNeutralCurrent(Double value) {
        int pvalue = value.intValue();
        String neutralCurrent = "No";

        if ((pvalue & 0x08) == 0x08){
            neutralCurrent = "Yes";
        }

        return neutralCurrent;
    }

    /**
     * This is used in CapBankDetailsController as a format
     */
    public static String convertToOctalIp(Double value) {
        long ipvalue = value.longValue();

        StringBuilder sb = new StringBuilder();
        int temp = (int) ((ipvalue >> 24) & 0xFF);
        sb.append(Integer.toString(temp, 10) + ".");
        temp = (int) ((ipvalue >> 16) & 0xFF);
        sb.append(Integer.toString(temp, 10) + ".");
        temp = (int) ((ipvalue >> 8) & 0xFF);
        sb.append(Integer.toString(temp, 10) + ".");
        temp = (int) (ipvalue & 0xFF);
        sb.append(Integer.toString(temp, 10));

        return sb.toString();
    }

    /**
     * This is used in CapBankDetailsController as a format
     */
    public static String convertToFirmwareVersion(Double value) {
        //  The firmware version is encoded as up to 8 six-bit ASCII characters
        //  http://nemesis.lonestar.org/reference/telecom/codes/sixbit.html
        long encodedValue = value.longValue();

        StringBuilder sb = new StringBuilder();

        while( encodedValue != 0 ) {
            sb.append((char)(' ' + encodedValue % 0x40));
            encodedValue /= 0x40;
        }

        return sb.reverse().toString();
    }

    /**
     * This is used in CapBankDetailsController as a format
     */
    public static String convertLong(Double value) {
        return Long.toString(value.longValue());
    }

    /**
     * This is used in CapBankDetailsController as a format
     */
    public static String convertControlReason(Double value) {

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
        int rawState = value.intValue();
        
        LiteState state = stateDao.findLiteState(StateGroupUtils.STATEGROUP_LASTCONTROL_STATE, rawState);
        
        if (state == null) {
            log.error("Unrecognized control state" + value);
            return accessor.getMessage("yukon.web.modules.capcontrol.unknownState", rawState);
        }

        return state.getStateText();
    }
}