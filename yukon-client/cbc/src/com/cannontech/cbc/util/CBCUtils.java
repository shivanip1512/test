package com.cannontech.cbc.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController701x;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.yukon.cbc.CCArea;
import com.cannontech.yukon.cbc.CCSpecialArea;
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
    private static final CapControlCache ccCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
    private static final StateDao stateDao = YukonSpringHook.getBean("stateDao", StateDao.class);
    private static final RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);

    public static final Comparator<SubBus> SUB_DISPLAY_COMPARATOR = new Comparator<SubBus>() {
        @Override
        public int compare(SubBus o1, SubBus o2) {
            Integer order1 = o1.getDisplayOrder();
            Integer order2 = o2.getDisplayOrder();
            int result = order1.compareTo(order2);
            return result;
        }
    };
    
    public static final Comparator<CCArea> CBC_AREA_COMPARATOR = new Comparator<CCArea>() {
        public int compare(CCArea o1, CCArea o2) {
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
    
    public static final Comparator<CCSpecialArea> CBC_SPECIAL_AREA_COMPARATOR = new Comparator<CCSpecialArea>() {
        public int compare(CCSpecialArea o1, CCSpecialArea o2) {
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
    public static final double calcAvgPF(SubBus[] subs) {
        double retVal = 0.0;
        // temp variables
        double sumOfVars = 0.0;
        double sumOfWatts = 0.0;

        if (subs == null) return CapControlConst.PF_INVALID_VALUE;

        if (subs.length == 0) return 0.0;
        
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

        if (subs == null) return CapControlConst.PF_INVALID_VALUE;

        if (subs.isEmpty()) return 0.0;
        
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

    /**
     * Calculates the estimated average PowerFactor for an array of SubBuses
     * that have valid PowerFactor values.
     */
    public static final double calcAvgEstPF(SubBus[] subs) {
        double retVal = 0.0;
        // temp variables
        double sumOfVars = 0.0;
        double sumOfWatts = 0.0;

        if (subs == null) return CapControlConst.PF_INVALID_VALUE;
        
        if (subs.length == 0) return 0.0;

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
     * Calculates the estimated average PowerFactor for an array of SubBuses
     * that have valid PowerFactor values.
     */
    public static final double calcAvgEstPF(List<SubStation> subs) {
        double retVal = 0.0;
        double sumOfVars = 0.0;
        double sumOfWatts = 0.0;

        if (subs == null) return CapControlConst.PF_INVALID_VALUE;
        
        if (subs.isEmpty()) return 0.0;
        
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

    public static String format(int val) {
        return NumberFormat.getInstance().format(val);
    }

    public static String format(double val) {
        return NumberFormat.getInstance().format(val);
    }

    public static boolean isTwoWay(int type) {
    	return DeviceTypesFuncs.isCBCTwoWay(type);
    }   
    
    public static boolean isTwoWay(LiteYukonPAObject obj) {
        DBPersistent dbPers = LiteFactory.convertLiteToDBPersAndRetrieve(obj);
        if (dbPers instanceof TwoWayDevice) return true;
        
        return false;
    }

    public static boolean is701xDevice(LiteYukonPAObject obj) {
        DBPersistent dbPers = LiteFactory.convertLiteToDBPersAndRetrieve(obj);
        if (dbPers instanceof CapBankController701x) return true;
        
        return false;
    }

    public static boolean is702xDevice(int type) {
        return DeviceTypesFuncs.is702xDevice(type);
    }
    
    public static String getAreaNameForSubStationIdFromCache(int subID) {	
    	String ret = "(none)";
    	SubStation station  = ccCache.getSubstation(subID);
		int areaId = station.getParentID();
		CCArea area = ccCache.getCBCArea(areaId);
		if( area != null ) {
			ret = area.getPaoName();
		}
        return ret;
    }
    
    public static String getAreaNameForSubStationBusIdFromCache(int subID) {	
    	SubBus bus  = ccCache.getSubBus(subID);

		int stationId = bus.getParentID();
		return getAreaNameForSubStationIdFromCache(stationId);
    }
    public static Integer getStateGroupIDByGroupName(String groupName) {
        LiteStateGroup[] allStateGroups = stateDao.getAllStateGroups();
        for (int i = 0; i < allStateGroups.length; i++) {
            LiteStateGroup group = allStateGroups[i];
            if (group.getStateGroupName().equalsIgnoreCase(groupName)) {
                return new Integer(group.getStateGroupID());
            }
        }
        return null;

    }

    public static boolean isCBAdditionalInfoAllowed(LiteYukonUser user) {
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        boolean showCapBankAddInfo = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_CB_ADDINFO, user);
        return showCapBankAddInfo;
    }

    public static String getCapControlStateText(final int index) throws NotFoundException {
        final List<LiteState> stateList = stateDao.getLiteStateGroup(
            CapControlConst.CAPBANKSTATUS_STATEGROUP_ID).getStatesList();
        
        if (index >= stateList.size()) 
            throw new NotFoundException("State with index of " + index + " not found");
        
        LiteState state = stateList.get(index);
        String text = state.getStateText();
        return text;
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
        
        for (CapBankDevice capBank : capBanks) {
            String controlStateText;
            try {
                Integer controlStatusIndex = capBank.getControlStatus();
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
    
    public static double calcVarsUnavailableForFeeder(Feeder feeder, LiteYukonUser user) {
        double returnVal = 0.0;
        List<CapBankDevice> capBanks = new ArrayList<CapBankDevice>(feeder.getCcCapBanks());
        List<String> unavailableStates = getUnavailableStatesList(user); 
        
        for (CapBankDevice capBank : capBanks) {
            String controlStateText;
            try {
                Integer controlStatusIndex = capBank.getControlStatus();
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
                Integer controlStatusIndex = capBank.getControlStatus();
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
                Integer controlStatusIndex = capBank.getControlStatus();
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
    public static List<String> getAvailableStatesList(LiteYukonUser user){
        String availableStatesString = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.AVAILABLE_DEFINITION, user);
        String[] array = availableStatesString.split(",");
        List<String> list = Arrays.asList(array);
        return list;
    }
    
    //  must be a better way to do this
    public static List<String> getUnavailableStatesList(LiteYukonUser user){
    	String unavailableStatesString = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.UNAVAILABLE_DEFINITION, user);
        String[] array = unavailableStatesString.split(",");
        List<String> list = Arrays.asList(array);
        return list;
    }
    
    //  must be a better way to do this
    public static List<String> getClosedStatesList(LiteYukonUser user){
    	String closedStatesString = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.CLOSED_DEFINITION, user);
        String[] array = closedStatesString.split(",");
        List<String> list = Arrays.asList(array);
        return list;
    }
    
    //  must be a better way to do this
    public static List<String> getTrippedStatesList(LiteYukonUser user){
    	String trippedStatesString = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.TRIPPED_DEFINITION, user);
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
       if(areaID < 0 ) {
           return "(none)";
       }else {
           return DaoFactory.getPaoDao().getYukonPAOName(areaID);
       }
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
        return checkable.getCurrentPtQuality(type.intValue()) == PointQuality.Normal.getQuality();
    }
    
    public static boolean isController(LiteWrapper lite) {
        PaoType paoType = PaoType.getForId(lite.getRawType());
        return checkControllerByType(paoType);
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
        
            lite = DaoFactory.getPaoDao().getLiteYukonPAO(id);
        }
        catch(NotFoundException nfe)
        {
            return false;
        }
        return isController(new LiteWrapper(lite));
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
    
    /**
     * The text of capbanks states. This can change since is based on a state
     * group
     */
    public static LiteState[] getCBCStateNames() {
        return stateDao.getLiteStates(StateGroupUtils.STATEGROUPID_CAPBANK);
    }
    
    /**
     * @param subBus
     * @return
     */
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
            CTILogger.error("Unable to retrieve DB Object", sql);
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
        int size = feeder.getCcCapBanks().size();
        for (int j = 0; j < size; j++) {
            CapBankDevice capBank = feeder.getCcCapBanks().elementAt(j);

            if (capBank.getControlStatus().intValue() == CapControlConst.BANK_CLOSE_PENDING)
                return CBCUtils.getCBCStateNames()[CapControlConst.BANK_CLOSE_PENDING].getStateText();

            if (capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING)
                return CBCUtils.getCBCStateNames()[CapControlConst.BANK_OPEN_PENDING].getStateText();
        }

        // we are not pending
        return null;
    }
}