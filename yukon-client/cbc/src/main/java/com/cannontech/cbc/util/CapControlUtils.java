package com.cannontech.cbc.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.database.model.Season;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.PointQualityCheckable;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.CapControlConst;
import com.cannontech.web.lite.LiteWrapper;

public final class CapControlUtils {
    public static final int TEMP_MOVE_REFRESH = 1000;
    // responsible for how to render data for CBC displays
    private static final CapControlCache ccCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
    private static final StateDao stateDao = YukonSpringHook.getBean("stateDao", StateDao.class);
    private static final RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
    private static final SeasonScheduleDao seasonScheduleDao = YukonSpringHook.getBean(SeasonScheduleDao.class);

    public static final Comparator<SubBus> SUB_DISPLAY_COMPARATOR = new Comparator<SubBus>() {
        @Override
        public int compare(SubBus o1, SubBus o2) {
            Integer order1 = o1.getDisplayOrder();
            Integer order2 = o2.getDisplayOrder();
            int result = order1.compareTo(order2);
            return result;
        }
    };
    
    public static final Comparator<Area> CBC_AREA_COMPARATOR = new Comparator<Area>() {
        @Override
        public int compare(Area o1, Area o2) {
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
    
    public static final Comparator<SpecialArea> CBC_SPECIAL_AREA_COMPARATOR = new Comparator<SpecialArea>() {
        @Override
        public int compare(SpecialArea o1, SpecialArea o2) {
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
        @Override
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
        @Override
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
        Double retVal = 0.0;
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
        return retVal.isNaN() ? 0.0 : retVal.doubleValue();
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
        Double retVal = 0.0;
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
        return retVal.isNaN() ? 0.0 : retVal.doubleValue();
    }

    public static boolean isTwoWay(PaoType type) {
    	return DeviceTypesFuncs.isCBCTwoWay(type);
    }   
    
    public static boolean isTwoWay(LiteYukonPAObject obj) {
        DBPersistent dbPers = LiteFactory.convertLiteToDBPersAndRetrieve(obj);
        if (dbPers instanceof TwoWayDevice) return true;
        
        return false;
    }

    public static boolean is701xDevice(LiteYukonPAObject obj) {
        if (obj.getPaoType() == PaoType.CBC_7010 ||
                obj.getPaoType() == PaoType.CBC_7011 ||
                obj.getPaoType() == PaoType.CBC_7012) {
            return true;
        }
        
        return false;
    }

    public static boolean is702xDevice(PaoType type) {
        return DeviceTypesFuncs.is702xDevice(type);
    }
    
    public static String getAreaNameForSubStationIdFromCache(int subID) {	
    	String ret = "(none)";
    	SubStation station  = ccCache.getSubstation(subID);
		int areaId = station.getParentID();
		Area area = ccCache.getArea(areaId);
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
           return YukonSpringHook.getBean(PaoDao.class).getYukonPAOName(areaID);
       }
    }
    
    public static String getAreaNameForSubBus(Integer subBusId) {
        Integer subStationId = CCSubstationSubBusList.getSubStationForSubBus(subBusId);
        return getAreaNameForSubStation(subStationId);
     }

    public static boolean signalQualityNormal(PointQualityCheckable checkable, Integer type) {
        boolean pointQualNormal = isPointQualNormal(checkable, type);
        UnitOfMeasure uom = UnitOfMeasure.getForId(type);
        if (uom.isCapControlVar() || uom.isCapControlWatt() || uom.isCapControlVolt()) { 
            return pointQualNormal;
        } else {
            return false;
        }
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
        
            lite = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(id);
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
            YukonSpringHook.getBean(PointDao.class).getLitePoint(id);
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
                return CapControlUtils.getCBCStateNames()[CapControlConst.BANK_CLOSE_PENDING].getStateText();

            if (capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING)
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
            CTILogger.warn("User not in Cap Bank Display role, using default Fixed text.");
        }
        
        if(StringUtils.isBlank(fixedText)) fixedText = "Fixed";
        
        return fixedText;
    }
    
    public static String convertNeutralCurrent(Double value) {        
        Integer pvalue = value.intValue();
        String neutralCurrent = "No";
        
        if ((pvalue & 0x08) == 0x08){
            neutralCurrent = "Yes";
        }
        
        return neutralCurrent;
    }
    
    public static String convertToOctalIp(Double value) {
        Long ipvalue = new Long(value.longValue());

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

    public static String convertLong(Double value) {
        return Long.toString(value.longValue());
    }

    public static String convertControlReason(Double value) {
        int rawState = value.intValue();

        LiteState state = stateDao.findLiteState(StateGroupUtils.STATEGROUP_LASTCONTROL_STATE, rawState);

        return state.getStateText();
    }
}