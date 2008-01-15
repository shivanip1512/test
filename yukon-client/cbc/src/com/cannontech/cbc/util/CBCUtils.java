package com.cannontech.cbc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController701x;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.cbc.CBCArea;
import com.cannontech.yukon.cbc.CBCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlConst;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.PointQualityCheckable;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

/**
 * @author ryan Generic utility classes for CapControl
 */
public final class CBCUtils {
    public static final int TEMP_MOVE_REFRESH = 1000;
    // responsible for how to render data for CBC displays
    public static final CBCDisplay CBC_DISPLAY = new CBCDisplay();
    public static CapControlCache ccCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
    public static AuthDao authDao = DaoFactory.getAuthDao();

    public static final Comparator<SubBus> SUB_DISPLAY_COMPARATOR = new Comparator<SubBus>() {
        @Override
        public int compare(SubBus o1, SubBus o2) {
            Integer order1 = o1.getDisplayOrder();
            Integer order2 = o2.getDisplayOrder();
            int result = order1.compareTo(order2);
            return result;
        }
    };
    
    public static final Comparator<CBCArea> CBC_AREA_COMPARATOR = new Comparator<CBCArea>() {
        public int compare(CBCArea o1, CBCArea o2) {
            try {
                String thisArea = o1.getPaoName();
                String anotherArea = o2.getPaoName();

                return (thisArea.compareToIgnoreCase(anotherArea));

            } catch (Exception e) {
                CTILogger.error("Something went wrong with sorting, ignoring sorting rules", e);
                return 0;
            }

        }
    };
    
    public static final Comparator<CBCSpecialArea> CBC_SPECIAL_AREA_COMPARATOR = new Comparator<CBCSpecialArea>() {
        public int compare(CBCSpecialArea o1, CBCSpecialArea o2) {
            try {
                String thisArea = o1.getPaoName();
                String anotherArea = o2.getPaoName();

                return (thisArea.compareToIgnoreCase(anotherArea));

            } catch (Exception e) {
                CTILogger.error("Something went wrong with sorting, ignoring sorting rules", e);
                return 0;
            }

        }
    };

    public static final Comparator<SubBus> SUB_AREA_COMPARATOR = new Comparator<SubBus>() {
        public int compare(SubBus o1, SubBus o2) {
            try {
                String thisArea = o1.getCcArea();
                String anotherArea = o2.getCcArea();

                if (!thisArea.equalsIgnoreCase(anotherArea))
                    return (thisArea.compareToIgnoreCase(anotherArea));

                // if the Area Names are equal, we need to sort by SubName
                String thisName = o1.getCcName();
                String anotherName = o2.getCcName();

                return (thisName.compareToIgnoreCase(anotherName));
            } catch (Exception e) {
                CTILogger.error("Something went wrong with sorting, ignoring sorting rules", e);
                return 0;
            }

        }
    };

    public static final Comparator<StreamableCapObject> CCNAME_COMPARATOR = new Comparator<StreamableCapObject>() {
        public int compare(StreamableCapObject o1, StreamableCapObject o2) {
            try {
                String strA = o1.getCcName();
                String strB = o2.getCcName();

                return strA.compareToIgnoreCase(strB);
            } catch (Exception e) {
                CTILogger.error("Something went wrong with sorting, ignoring sorting rules", e);
                return 0;
            }

        }
    };

    public static final boolean isPowerFactorControlled(String controlUnits) {
        return (CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION.equalsIgnoreCase(controlUnits) || CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION.equalsIgnoreCase(controlUnits));
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
     * Calculates the average PowerFactor for an array of SubBuses that have
     * valid PowerFactor values.
     */
    public static final double calcAvgPF(List<SubStation> subs) {
        double retVal = 0.0;
        double sumOfVars = 0.0;
        double sumOfWatts = 0.0;

        if (subs == null) {
            return CapControlConst.PF_INVALID_VALUE;
        }else {
            for (SubStation sub: subs) {
                List<SubBus> subBuses = ccCache.getSubBusesBySubStation(sub);
                for (SubBus subBus : subBuses) {
                    if (subBus != null) {
                        sumOfVars += subBus.getCurrentVarLoadPointValue().doubleValue();
                        sumOfWatts += Math.abs(subBus.getCurrentWattLoadPointValue().doubleValue());
                    }
                }
            }
            retVal = sumOfWatts / (Math.sqrt(Math.pow(sumOfVars, 2.0) + Math.pow(sumOfWatts, 2.0)));
            if (sumOfVars < 0) {
                retVal = retVal * (-1);
            }
            return retVal;
        }
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
            if(subs[i] != null) {
                sumOfVars += subs[i].getEstimatedVarLoadPointValue().doubleValue();
                sumOfWatts += Math.abs(subs[i].getCurrentWattLoadPointValue().doubleValue());
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
    public static final double calcAvgEstPF(List<SubStation> subs) {
        double retVal = 0.0;
        double sumOfVars = 0.0;
        double sumOfWatts = 0.0;

        if (subs == null) {
            return CapControlConst.PF_INVALID_VALUE;
        } else {
            for(SubStation sub: subs) {
                List<SubBus> buses = ccCache.getSubBusesBySubStation(sub);
                for (SubBus bus: buses) {
                    if( bus != null) {
                        sumOfVars += bus.getEstimatedVarLoadPointValue().doubleValue();
                        sumOfWatts += Math.abs(bus.getCurrentWattLoadPointValue().doubleValue());
                    }
                }
            }
            retVal = sumOfWatts / (Math.sqrt(Math.pow(sumOfVars, 2.0) + Math.pow(sumOfWatts, 2.0)));
            if (sumOfVars < 0) {
                retVal = retVal * (-1);
            }
            return retVal;
        }
    }

    public static String format(int val) {
        return NumberFormat.getInstance().format(val);
    }

    public static String format(double val) {
        return NumberFormat.getInstance().format(val);
    }

    public static boolean isTwoWay(int type) {
        int[] cbcTwoWay = DeviceTypes.CBC_TWOWAY;
        for (int x = 0; x < cbcTwoWay.length; x++) {
            if (type == cbcTwoWay[x]) return true;
        }
        return false;
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

    public static String getAreaNameFromSubStationId(int subID) {	
    	String ret = "(none)";
    	SubStation station  = ccCache.getSubstation(subID);
    	if( station != null ) {
    		int areaId = station.getParentID();
    		CBCArea area = ccCache.getCBCArea(areaId);
    		if( area != null ) {
    			ret = area.getCcName();
    		}
    	}
        return ret;
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
        boolean showCapBankAddInfo = Boolean.valueOf(authDao.getRolePropertyValue(user, CBCSettingsRole.SHOW_CB_ADDINFO)).booleanValue();
        return showCapBankAddInfo;
    }

    /**
     * returns the sum of VARS on switched capbanks for every ENABLED cap object 
     * on a SubBus
     * @param sub
     * @return
     */
    public static double calcVarsAvailableForSubBus(SubBus subBus, LiteYukonUser user) {
        double returnVal = 0.0;
        List<Feeder> feeders = new ArrayList<Feeder>(subBus.getCcFeeders());
        for (Feeder feeder : feeders) {
            returnVal += calcVarsAvailableForFeeder(feeder, user);
        }
        return returnVal;
    }
    
    public static double calcVarsAvailableForFeeder(Feeder feeder, LiteYukonUser user) {
        double returnVal = 0.0;
        List<CapBankDevice> capBanks = new ArrayList<CapBankDevice>(feeder.getCcCapBanks());
        List<String> availableStates = getAvailableStatesList(user);
        String[] controlStatusStrings = CapControlConst.CONTROL_STATUS_STRINGS_ARRAY;
        for (CapBankDevice capBank : capBanks) {
            Integer controlStatus = capBank.getControlStatus();
            String operationalState = capBank.getOperationalState();
            Boolean disabled = capBank.getCcDisableFlag();
            if(disabled) {
                operationalState = "Disabled";
            }
            String typeAndState = operationalState + ":" + controlStatusStrings[controlStatus];
            if(availableStates.contains(typeAndState)){
                returnVal += capBank.getBankSize();
            }
        }
        return returnVal;
    }
    
    public static double calcVarsUnavailableForFeeder(Feeder feeder, LiteYukonUser user) {
        double returnVal = 0.0;
        List<CapBankDevice> capBanks = new ArrayList<CapBankDevice>(feeder.getCcCapBanks());
        List<String> unavailableStates = getUnavailableStatesList(user); 
        String[] controlStatusStrings = CapControlConst.CONTROL_STATUS_STRINGS_ARRAY;
        for (CapBankDevice capBank : capBanks) {
            Integer controlStatus = capBank.getControlStatus();
            String operationalState = capBank.getOperationalState();
            Boolean disabled = capBank.getCcDisableFlag();
            if(disabled) {
                operationalState = "Disabled";
            }
            String typeAndState = operationalState + ":" + controlStatusStrings[controlStatus];
            if(unavailableStates.contains(typeAndState)){
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
        String[] controlStatusStrings = CapControlConst.CONTROL_STATUS_STRINGS_ARRAY;
        for (CapBankDevice capBank : capBanks) {
            Integer controlStatus = capBank.getControlStatus();
            String operationalState = capBank.getOperationalState();
            Boolean disabled = capBank.getCcDisableFlag();
            if(disabled) {
                operationalState = "Disabled";
            }
            String typeAndState = operationalState + ":" + controlStatusStrings[controlStatus];
            if(trippedStates.contains(typeAndState)){
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
        String[] controlStatusStrings = CapControlConst.CONTROL_STATUS_STRINGS_ARRAY;
        for (CapBankDevice capBank : capBanks) {
            Integer controlStatus = capBank.getControlStatus();
            String operationalState = capBank.getOperationalState();
            Boolean disabled = capBank.getCcDisableFlag();
            if(disabled) {
                operationalState = "Disabled";
            }
            String typeAndState = operationalState + ":" + controlStatusStrings[controlStatus];
            if(closedStates.contains(typeAndState)){
                returnVal += capBank.getBankSize();
            }
        }
        return returnVal;
    }
    
    // must be a better way to do this
    public static List<String> getAvailableStatesList(LiteYukonUser user){
        String availableStatesString = authDao.getRolePropertyValue(user, CBCSettingsRole.AVAILABLE_DEFINITION);
        String[] array = availableStatesString.split(",");
        List<String> list = Arrays.asList(array);
        return list;
    }
    
//  must be a better way to do this
    public static List<String> getUnavailableStatesList(LiteYukonUser user){
        String unavailableStatesString = authDao.getRolePropertyValue(user, CBCSettingsRole.UNAVAILABLE_DEFINITION);
        String[] array = unavailableStatesString.split(",");
        List<String> list = Arrays.asList(array);
        return list;
    }
    
//  must be a better way to do this
    public static List<String> getClosedStatesList(LiteYukonUser user){
        String closedStatesString = authDao.getRolePropertyValue(user, CBCSettingsRole.CLOSED_DEFINITION);
        String[] array = closedStatesString.split(",");
        List<String> list = Arrays.asList(array);
        return list;
    }
    
//  must be a better way to do this
    public static List<String> getTrippedStatesList(LiteYukonUser user){
        String trippedStatesString = authDao.getRolePropertyValue(user, CBCSettingsRole.TRIPPED_DEFINITION);
        String[] array = trippedStatesString.split(",");
        List<String> list = Arrays.asList(array);
        return list;
    }
    
    /**
     * returns the sum of VARS on switched capbanks for every ENABLED cap object 
     * on a SubStation
     * @param sub
     * @return
     */
    public static double calcVarsAvailableForSubStation(SubStation substation, LiteYukonUser user) {
        double returnVal = 0.0;
        List<SubBus> subBuses = ccCache.getSubBusesBySubStation(substation);
        if( subBuses != null ) {
            for (SubBus subBus : subBuses) {
                returnVal += calcVarsAvailableForSubBus(subBus, user);
            }
        }
        return returnVal;
    }
    
    /**
     * returns the sum of VARS on switched capbanks for every ENABLED cap object 
     * for a List<SubBus>
     * @param subs
     * @return
     */
    public static double calcVarsAvailableForSubBuses(List<SubBus> subs, LiteYukonUser user){
        double returnVal = 0.0;
        for (SubBus bus : subs) {
            returnVal += calcVarsAvailableForSubBus(bus, user);
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
        double returnVal = 0.0;
        if( subs != null )
            for (SubStation sub : subs) {
                returnVal += calcVarsAvailableForSubStation(sub, user);
            }
        return returnVal;
    }
    
    public static double calcVarsUnavailableForSubBuses(List<SubBus> subs, LiteYukonUser user) {
        double returnVal = 0.0;
        for (SubBus bus : subs) {
            returnVal+= calcVarsUnavailableForSubBus(bus, user);
        }
        return returnVal;
    }
    
    public static double calcVarsUnavailableForSubStations(List<SubStation> subs, LiteYukonUser user) {
        double returnVal = 0.0;
        if( subs != null)
            for (SubStation bus : subs) {
                returnVal+= calcVarsUnavailableForSubStation(bus, user);
            }
        return returnVal;
    }
    
    /**
     * returns the sum of VARS on switched capbanks for every DISABLED cap object 
     * on a SubBus
     * @param object
     * @return
     */
    public static double calcVarsUnavailableForSubBus(SubBus subBus, LiteYukonUser user) {
        double returnVal = 0.0;
        List<Feeder> feeders = new ArrayList<Feeder>(subBus.getCcFeeders());
        for (Feeder feeder : feeders) {
            returnVal += calcVarsUnavailableForFeeder(feeder, user);
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
        double returnVal = 0.0;
        List<SubBus> subBuses = ccCache.getSubBusesBySubStation(substation);
        if( subBuses != null ) {
            for (SubBus subBus : subBuses) {
                returnVal += calcVarsUnavailableForSubBus(subBus, user);
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
        return capBank.getOperationalState().equalsIgnoreCase(CapBank.SWITCHED_OPSTATE);
    }

    public static String getAreaNameForSubStation(Integer substationId) {
       Integer areaID = CCSubAreaAssignment.getAreaIDForSubStation(substationId);
       return DaoFactory.getPaoDao().getYukonPAOName(areaID);
    }
    
    public static String getAreaNameForSubBus(Integer subBusId) {
        Integer subStationId = CCSubstationSubBusList.getSubStationForSubBus(subBusId);
        return getAreaNameForSubStation(subStationId);
     }

    public static boolean signalQualityNormal(PointQualityCheckable checkable, Integer type) {
        boolean pointQualNormal = isPointQualNormal(checkable, type);

        if (Arrays.asList(PointUnits.CAP_CONTROL_VAR_UOMIDS).contains(type)) 
            return (pointQualNormal);
        if (Arrays.asList(PointUnits.CAP_CONTROL_WATTS_UOMIDS).contains(type))
            return pointQualNormal;
        if (Arrays.asList(PointUnits.CAP_CONTROL_VOLTS_UOMIDS).contains(type))
            return pointQualNormal;
        return false;
    }

    public static boolean isPointQualNormal(PointQualityCheckable checkable, Integer type) {
        return checkable.getCurrentPtQuality(type.intValue()) == PointQualityCheckable.PointQuality.NormalQuality.value();
    }

    public static boolean isController(int id) {
        LiteYukonPAObject lite = null;
        try{
        
            lite = DaoFactory.getPaoDao().getLiteYukonPAO(id);
        }
        catch(NotFoundException nfe)
        {
            return false;
        }
        int type = lite.getType();
        switch (type) {
            case PAOGroups.CBC_7010:
            case PAOGroups.CBC_7011:
            case PAOGroups.CBC_7012:
            case PAOGroups.CBC_7020:
            case PAOGroups.CBC_7022:
            case PAOGroups.CBC_7023:
            case PAOGroups.CBC_7024:
            case PAOGroups.CBC_EXPRESSCOM:
            case PAOGroups.CAPBANKCONTROLLER:
                return true;
        default:
            return false;
            
        }
    }

    public static boolean isPoint(int id) {
        try
       {
            DaoFactory.getPointDao().getLitePoint(id);
       }
       catch(NotFoundException nfe)
       {
           return false;
       }
        return true;
    }

}