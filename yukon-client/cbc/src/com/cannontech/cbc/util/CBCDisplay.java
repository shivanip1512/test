package com.cannontech.cbc.util;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.UnknownRolePropertyException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ColorUtil;
import com.cannontech.yukon.cbc.CCArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlConst;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

/**
 * @author rneuharth
 */
public class CBCDisplay {

    public static final String SYMBOL_SIGNAL_QUALITY = "(*)";
    public static final String STR_NA = "NA";
    public static final String DASH_LINE = "----";
    public static final String STR_UNKNOWN = "Unknown";

    // Column numbers for the CabBank display
    public static final int CB_NAME_COLUMN = 0;
    public static final int CB_CONTROLLER = 1;
    public static final int CB_BANK_SIZE_COLUMN = 2;
    public static final int CB_STATUS_COLUMN = 3;
    public static final int CB_TIME_STAMP_COLUMN = 4;
    public static final int CB_OP_COUNT_COLUMN = 5;
    public static final int CB_PARENT_COLUMN = 6;
    public static final int CB_CURRENT_DAILY_OP_COLUMN = 7;
    public static final int CB_DAILY_TOTAL_OP_COLUMN = 8;
    public static final int CB_SHORT_TIME_STAMP_COLUMN = 9;
    public static final int CB_STATUS_POPUP = 10;
    public static final int CB_WARNING_IMAGE_COLOR = 11;
    public static final int CB_WARNING_IMAGE_TEXT = 12;
    public static final int CB_WARNING_POPUP = 13;
    public static final int CB_DAILY_MAX_TOTAL_OP_COLUMN = 14;
    public static final int CB_PHASEA_BEFORE = 15;
    public static final int CB_PHASEB_BEFORE = 16;
    public static final int CB_PHASEC_BEFORE = 17;
    public static final int CB_PHASEA_AFTER = 18;
    public static final int CB_PHASEB_AFTER = 19;
    public static final int CB_PHASEC_AFTER = 20;
    public static final int CB_PHASEA_PERCENTCHANGE = 21;
    public static final int CB_PHASEB_PERCENTCHANGE = 22;
    public static final int CB_PHASEC_PERCENTCHANGE = 23;
    public static final int CB_PERCENTCHANGE_TOTAL = 24;
    public static final int CB_AFTER_TOTAL = 25;
    public static final int CB_BEFORE_TOTAL = 26;
    
    // Column numbers for the Feeder display
    public static final int FDR_NAME_COLUMN = 0;
    public static final int FDR_CURRENT_STATE_COLUMN = 1;
    public static final int FDR_TARGET_COLUMN = 2;
    public static final int FDR_VAR_LOAD_COLUMN = 3;
    public static final int FDR_WATTS_COLUMN = 4;
    public static final int FDR_POWER_FACTOR_COLUMN = 5;
    public static final int FDR_TIME_STAMP_COLUMN = 6;
    public static final int FDR_DAILY_OPERATIONS_COLUMN = 7;
    // used by oneline ONLY!!!
    public static final int FDR_ONELINE_WATTS_COLUMN = 8;
    public static final int FDR_ONELINE_VOLTS_COLUMN = 9;
    public static final int FDR_ONELINE_VAR_LOAD_COLUMN = 10;
    public static final int FDR_SHORT_TIME_STAMP_COLUMN = 11;
    public static final int FDR_TARGET_POPUP = 12;
    public static final int FDR_VAR_LOAD_POPUP = 13;
    public static final int FDR_WARNING_IMAGE = 14;
    public static final int FDR_WARNING_POPUP = 15;
    public static final int FDR_ONELINE_WATTS_VOLTS_COLUMN = 16;
    public static final int FDR_ONELINE_THREE_PHASE_COLUMN = 17;
    // More Column numbers for the Feeder display
    public static final int FDR_TARGET_COLUMN_PEAKLEAD = 18;
    public static final int FDR_TARGET_COLUMN_PEAKLAG = 19;
    public static final int FDR_TARGET_COLUMN_CLOSEOPENPERCENT = 20;
    public static final int FDR_VAR_LOAD_QUALITY = 21;
    public static final int FDR_WATT_QUALITY = 22;
    public static final int FDR_VOLT_QUALITY = 23;
    public static final int FDR_VAR_EST_LOAD_COLUMN = 24;
    public static final int FDR_VOLTS_COLUMN = 25;
    
    // Column numbers for the SubBus display
    public static final int SUB_AREA_NAME_COLUMN = 0;
    public static final int SUB_NAME_COLUMN = 1;
    public static final int SUB_CURRENT_STATE_COLUMN = 2;
    public static final int SUB_TARGET_COLUMN = 3;
    public static final int SUB_VAR_LOAD_COLUMN = 4;
    public static final int SUB_WATTS_COLUMN = 5;
    public static final int SUB_POWER_FACTOR_COLUMN = 6;
    public static final int SUB_TIME_STAMP_COLUMN = 7;
    public static final int SUB_DAILY_OPERATIONS_COLUMN = 8;
    public static final int SUB_SP_AREA_ENABLED = 9;
    public static final int SUB_VOLT_REDUCTION = 10;

    public static final int SUB_ONELINE_CONTROL_METHOD_COLUMN = 9;
    public static final int SUB_ONELINE_KVAR_LOAD_COLUMN = 10;
    public static final int SUB_ONELINE_KVAR_ESTMATED_COLUMN = 11;
    public static final int SUB_ONELINE_PF_COLUMN = 12;
    public static final int SUB_ONELINE_EST_PF_COLUMN = 13;
    public static final int SUB_ONELINE_WATT_COLUMN = 14;
    public static final int SUB_ONELINE_VOLT_COLUMN = 15;
    public static final int SUB_ONELINE_DAILY_OPCNT_COLUMN = 16;
    public static final int SUB_ONELINE_MAX_OPCNT_COLUMN = 17;
    public static final int SUB_ONELINE_AREANAME_COLUMN = 18;
    public static final int SUB_ONELINE_CTL_METHOD_COLUMN = 19;
    public static final int SUB_ONELINE_DAILY_MAX_OPCNT_COLUMN = 20;
    public static final int SUB_SHORT_TIME_STAMP_COLUMN = 21;
    public static final int SUB_TARGET_POPUP = 22;
    public static final int SUB_VAR_LOAD_POPUP = 23;
    public static final int SUB_WARNING_IMAGE = 24;
    public static final int SUB_WARNING_POPUP = 25;
    public static final int SUB_ONELINE_THREE_PHASE_COLUMN = 26;
    // More Column numbers for the Feeder display
    public static final int SUB_TARGET_COLUMN_PEAKLEAD = 27;
    public static final int SUB_TARGET_COLUMN_PEAKLAG = 28;
    public static final int SUB_TARGET_COLUMN_CLOSEOPENPERCENT = 29;
    public static final int SUB_VAR_LOAD_QUALITY = 30;
    public static final int SUB_WATT_QUALITY = 31;
    public static final int SUB_VOLT_QUALITY = 32;
    public static final int SUB_VAR_EST_LOAD_COLUMN = 33;
    public static final int SUB_VOLTS_COLUMN = 34;
    
    public static final int AREA_POWER_FACTOR_COLUMN = 0;
    public static final int AREA_VOLT_REDUCTION = 1;

    public static final String WARNING_LIKE_DAY = "Operating in like-day history control.";
    public static final String WARNING_VOLT_REDUCTION = "Volt Reduction is active.";
    public static final String WARNING_MAX_DAILY_OPS = "Max Daily Operations";
    public static final String WARNING_OVUV_SITUATION = "OVUV Situation";
    
    // The color schemes - based on the schedule status
    private static final Color[] _DEFAULT_COLORS = {
        // Enabled subbus (Green like color)
        new Color(60, 130, 66),
        // Disabled subbus
        Color.RED,
        // Pending subbus (Yellow like color)
        new Color(240, 145, 0) };

    private final YukonUserContext userContext;
    private DateFormattingService dateFormattingService;

    public CBCDisplay(final YukonUserContext userContext) {
        this.userContext = userContext;
        dateFormattingService = YukonSpringHook.getBean("dateFormattingService", DateFormattingService.class);
    }

    public YukonUserContext getYukonUserContext() {
        return userContext;
    }
        
    /**
     * getValueAt method for CapBanks.
     */
    public Object getCapBankValueAt(CapBankDevice capBank, int col) {
        if (capBank == null) return "";
        
        Integer controlDeviceID = capBank.getControlDeviceID();
        String controllerName = (controlDeviceID != 0) ? DaoFactory.getPaoDao().getYukonPAOName(controlDeviceID) : DASH_LINE;

        switch (col) {
        case CB_NAME_COLUMN: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(2);
            num.setMinimumFractionDigits(0);
            return capBank.getCcName() + " (" + num.format(capBank.getControlOrder()) + ")";
        }

        case CB_STATUS_COLUMN: {
            CbcHelperService cbcHelperService = YukonSpringHook.getBean("cbcHelperService", CbcHelperService.class);
            String fixedCapbankLabel = cbcHelperService.getFixedText(userContext.getYukonUser());
            
            boolean capBankInUnknownState = capBank.getControlStatus().intValue() < 0 
                || capBank.getControlStatus().intValue() >= CBCUtils.getCBCStateNames().length;
            int controlStatus = capBank.getControlStatus().intValue();                                                                    
            if (capBankInUnknownState) {
                CTILogger.info("*** A CapBank state was found that has no corresponding status.");
                return STR_UNKNOWN + " (" + controlStatus + ")";
            }    

            boolean isCapBankDisabled = capBank.getCcDisableFlag().booleanValue() == true;
            boolean isFixedState = capBank.getOperationalState().equalsIgnoreCase(CapBank.FIXED_OPSTATE);
            boolean isStandaloneState = capBank.getOperationalState().equalsIgnoreCase(CapBank.STANDALONE_OPSTATE);
            String currentState = CBCUtils.getCBCStateNames()[controlStatus].getStateText();
            boolean showIgnoreReason = capBank.isIgnoreFlag();

            if (isCapBankDisabled) {
                String disStateString = "DISABLED : " + (isFixedState ? fixedCapbankLabel : currentState);
                disStateString += capBank.getControlStatusQualityString();                    
                if (capBank.getOvUVDisabled()){
                    disStateString += "-V";
                }

                disStateString += (showIgnoreReason ? "<br/>" + CapBankDevice.getIgnoreReason( capBank.getIgnoreReason()) : "");
                return disStateString;
            } 

            String enStateString = "";
            if (isFixedState){
                enStateString = fixedCapbankLabel + " : ";
            }
            else if (isStandaloneState ){
                enStateString = CapBank.STANDALONE_OPSTATE + " : ";
            }

            enStateString += currentState;
            enStateString += capBank.getControlStatusQualityString();           

            if (capBank.getOvUVDisabled()){
                enStateString += "-V";
            }
            enStateString += (showIgnoreReason ? "<br/>" + CapBankDevice.getIgnoreReason( capBank.getIgnoreReason()) : "");
            return enStateString;
        }

        case CB_OP_COUNT_COLUMN: {
            return capBank.getTotalOperations();
        }

        case CB_BANK_SIZE_COLUMN: {
            return capBank.getBankSize();
        }

        case CB_TIME_STAMP_COLUMN: {
            Date date = capBank.getLastStatusChangeTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime())
                return DASH_LINE;
            return dateFormattingService.format(date, DateFormatEnum.BOTH, userContext);
        }

        case CB_SHORT_TIME_STAMP_COLUMN: {
            Date date = capBank.getLastStatusChangeTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime())
                return DASH_LINE;

            return dateFormattingService.format(date, DateFormatEnum.VERY_SHORT, userContext);
        }

        case CB_PARENT_COLUMN: {
            LiteYukonPAObject paoParent = DaoFactory.getPaoDao()
            .getLiteYukonPAO(capBank.getParentID());
            if (paoParent != null) return paoParent;

            return DASH_LINE;
        }

        case CB_CURRENT_DAILY_OP_COLUMN: {
            return capBank.getCurrentDailyOperations();
        }

        case CB_DAILY_TOTAL_OP_COLUMN: {
            return new String(capBank.getCurrentDailyOperations() + " / " + capBank.getTotalOperations());
        }

        case CB_DAILY_MAX_TOTAL_OP_COLUMN: {
            return new String(capBank.getCurrentDailyOperations() +" / " + capBank.getMaxDailyOperation() + " / " + capBank.getTotalOperations());
        }

        case CB_CONTROLLER:{
            return controllerName;
        }

        case CB_WARNING_IMAGE_COLOR:{
            String color = "Green";
            if (capBank.getMaxDailyOperationHitFlag() || capBank.getOvuvSituationFlag()) {
                color = "Yellow";
            } else {
                color = "Green";
            }
            
            return color;
        }
            
        case CB_WARNING_IMAGE_TEXT:{
            String text = "Remote";
            
            if(capBank.getLocalControlFlag()) {
                text = "Local";
            }
            return text;
        }

        case CB_WARNING_POPUP:{
            String warningText = "Alerts:";

            if (capBank.getMaxDailyOperationHitFlag())
                warningText += " " + WARNING_MAX_DAILY_OPS ;

            if (capBank.getOvuvSituationFlag())
                warningText += " " + WARNING_OVUV_SITUATION ;

            return warningText;
        }
        case CB_PHASEA_BEFORE:
        case CB_PHASEB_BEFORE:
        case CB_PHASEC_BEFORE:
        case CB_BEFORE_TOTAL:
        case CB_PHASEA_AFTER:
        case CB_PHASEB_AFTER:
        case CB_PHASEC_AFTER:
        case CB_AFTER_TOTAL:   
        case CB_PHASEA_PERCENTCHANGE:
        case CB_PHASEB_PERCENTCHANGE:
        case CB_PHASEC_PERCENTCHANGE:
        case CB_PERCENTCHANGE_TOTAL: {        	
        	String value = getPhaseValueFromBank(capBank,col);
        	return value;
        }

        default: return null;
        }
    }

    private String getPhaseValueFromBank(CapBankDevice bank, int col) {
        String value = "";
        String parseString = "";
        int parsePosition = 1;
        boolean phaseA = false;
        
        switch (col) {
            case CB_PHASEA_BEFORE:
            case CB_PHASEB_BEFORE:
            case CB_PHASEC_BEFORE:
            case CB_BEFORE_TOTAL: {
                parseString = bank.getBeforeVars().trim();
                break;
            }
                
            case CB_PHASEA_AFTER:
            case CB_PHASEB_AFTER:
            case CB_PHASEC_AFTER:
            case CB_AFTER_TOTAL: {
                parseString = bank.getAfterVars().trim();
                break;
            }
            
            case CB_PHASEA_PERCENTCHANGE:
            case CB_PHASEB_PERCENTCHANGE:
            case CB_PHASEC_PERCENTCHANGE:
            case CB_PERCENTCHANGE_TOTAL: {
                parseString = bank.getPercentChange().trim();
                break;
            }
            default:
                return value;
        }

        switch (col) {
            case CB_PHASEA_BEFORE:
            case CB_PHASEA_PERCENTCHANGE:
            case CB_PHASEA_AFTER: {
                phaseA = true;
                parsePosition = 1;
                break;
            }
            case CB_PHASEB_BEFORE:
            case CB_PHASEB_AFTER:
            case CB_PHASEB_PERCENTCHANGE: {
                parsePosition = 2;
                break;
            }
            case CB_PHASEC_BEFORE:
            case CB_PHASEC_AFTER:
            case CB_PHASEC_PERCENTCHANGE:
            {
                parsePosition = 3;
                break;
            }
            case CB_BEFORE_TOTAL:
            case CB_AFTER_TOTAL:
            case CB_PERCENTCHANGE_TOTAL: {
                parsePosition = 4;
                break;
            }
            default:
                return value;
        }
        
        if (parseString.contains(":")) {
            StringTokenizer st = new StringTokenizer(parseString, ":");
            
            try {
                String temp = "";
                
                for( int i = 1; i <= parsePosition; i++) {
                    temp = st.nextToken();
                }

                value = temp;
            } catch (NoSuchElementException e) {
                return "";
            }
        } else if (phaseA) {
            value = parseString;
        }
        
        return value;
    }
    
    /**
     * getValueAt method for Substations
     */
    public Object getSubstationValueAt(SubStation substation, int col) {
        if (substation == null) return "";

        switch (col) {
        case SUB_NAME_COLUMN: {
            return substation.getCcName();
        }

        case SUB_AREA_NAME_COLUMN: {
            return substation.getCcArea();
        }

        case SUB_CURRENT_STATE_COLUMN: {
            boolean isDisabled = substation.getCcDisableFlag().booleanValue();
            String state = (isDisabled) ? "DISABLED" : "ENABLED";
            if (substation.getRecentlyControlledFlag()) state += " Pending";
            if (substation.getOvuvDisableFlag()) state += "-V";
            return state;
        }

        case SUB_POWER_FACTOR_COLUMN: {
            String pf =  getPowerFactorText(substation.getPowerFactorValue().doubleValue(), true)     
            + " / " + getPowerFactorText(substation.getEstimatedPFValue().doubleValue(), true);
            return pf;
        }
        
        case SUB_SP_AREA_ENABLED: {
            boolean saEnabled = substation.getSpecialAreaEnabled();
            if (saEnabled) {
                return " SA Enabled: " + DaoFactory.getPaoDao().getYukonPAOName(substation.getSpecialAreaId());
                
            } else {
                return " ";
            }
        }
        
        case SUB_VOLT_REDUCTION: {
            Boolean flag = substation.getVoltReductionFlag();
            Boolean childFlag = substation.getChildVoltReductionFlag();
            if(flag || childFlag) {
                return "true";
            }else {
                return "false";
            }
        }

        default: return null;
        }
    }

    public String getAreaValueAt(CCArea area, int col) {
        if (area == null) return "";

        switch (col) {
        case AREA_POWER_FACTOR_COLUMN: {
            String text = getPowerFactorText(area.getPowerFactorValue().doubleValue(), true) +
            " / " + getPowerFactorText(area.getEstimatedPFValue().doubleValue(), true); 
            return text;
        }
        
        case AREA_VOLT_REDUCTION: { 
            Boolean flag = area.getVoltReductionFlag();
            Boolean childFlag = area.getChildVoltReductionFlag();
            if(flag || childFlag) {
                return "true";
            }else {
                return "false";
            }
        }

        default: return null;
        }
    }

    /**
     * getValueAt method for SubBuses
     */
    public Object getSubBusValueAt(SubBus subBus, int col) {
        if (subBus == null) return "";

        int decPlaces = subBus.getDecimalPlaces().intValue();

        switch (col) {
        case SUB_NAME_COLUMN: {
            return subBus.getCcName();
        }

        case SUB_AREA_NAME_COLUMN: {
            return subBus.getCcArea();
        }

        case SUB_CURRENT_STATE_COLUMN: {
            String state = "";
            if(subBus.getCcDisableFlag()) {
                state = "DISABLED";
            } else {
                state = "ENABLED";
            }
            
            if(subBus.getRecentlyControlledFlag()) {
                state += " Pending";
            }
            
            if (subBus.getDualBusEnabled() && subBus.getSwitchOverStatus().booleanValue()) {
                state += "-ALT BUS";
            }

            if (subBus.getPrimaryBusFlag().booleanValue()) {
                state += "-PRIMARY BUS";
            }
            
            if (subBus.getOvUvDisabledFlag().booleanValue()) {
                state += "-V";
            }
            return state;
        }
        case SUB_TARGET_COLUMN_PEAKLEAD: {
            return CommonUtils.formatDecimalPlaces(subBus.getPeakLead().doubleValue(),0);
        }
        case SUB_TARGET_COLUMN_PEAKLAG: {
            return CommonUtils.formatDecimalPlaces(subBus.getPeakLag().doubleValue(),0);
        }
        case SUB_TARGET_COLUMN_CLOSEOPENPERCENT: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);
            
            String closePercent = CommonUtils.formatDecimalPlaces(subBus.getOffPkLag().doubleValue(),0) + "%C | ";
            String target = num.format(subBus.getPeakPFSetPoint() ) + " | ";
            String openPercent = CommonUtils.formatDecimalPlaces(subBus.getOffPkLead().doubleValue(),0) + "%O ";
            
            String closeOpenPercent = closePercent + target + openPercent;
            return closeOpenPercent;
        }
        case SUB_TARGET_COLUMN: {
            // decide which set Point we are to use
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);
            
            if(subBus.getControlMethod() == ControlMethod.TIME_OF_DAY) {
                return "TOD";
            } else if (subBus.getControlMethod() == ControlMethod.NONE) {
                return CtiUtilities.STRING_NONE;
            } 
            
            /* Treat peak and off peak normally */
            if (subBus.getPeakTimeFlag().booleanValue()) { /* Currently in peak operating hours */

                if (subBus.isPowerFactorControlled()) {

                    return CommonUtils.formatDecimalPlaces(subBus.getPeakLag().doubleValue(),0) + "%C | " 
                    + num.format(subBus.getPeakPFSetPoint() ) + " | " 
                    + CommonUtils.formatDecimalPlaces(subBus.getPeakLead().doubleValue(),0) + "%O";
                } else
                    return CommonUtils.formatDecimalPlaces(subBus.getPeakLead().doubleValue(),0) 
                    + " to " + CommonUtils.formatDecimalPlaces(subBus.getPeakLag().doubleValue(),0) + " Pk";
            } else {
                /* Currently in off peak operating hours */
                if (subBus.isPowerFactorControlled()) {

                    return CommonUtils.formatDecimalPlaces(subBus.getOffPkLag().doubleValue(),0) 
                    + "%C | " + num.format(subBus.getOffpeakPFSetPoint()) 
                    + " | " + CommonUtils.formatDecimalPlaces(subBus.getOffPkLead().doubleValue(),0) + "%O";
                } else
                    return CommonUtils.formatDecimalPlaces(subBus.getOffPkLead().doubleValue(),0) 
                    + " to " + CommonUtils.formatDecimalPlaces(subBus.getOffPkLag().doubleValue(),0) + " OffPk";
            }
        }

        case SUB_TARGET_POPUP: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(3);
            num.setMinimumFractionDigits(0);

            return "Target Var: " + num.format( subBus.getTargetvarvalue() );
        }

        case SUB_VAR_LOAD_POPUP: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(2);
            num.setMinimumFractionDigits(0);
            String str = "Phase A:" + num.format( subBus.getPhaseA() ) 
            + " Phase B:" + num.format( subBus.getPhaseB() )
            + " Phase C:" + num.format( subBus.getPhaseC() );
            return str;
        }

        case SUB_DAILY_OPERATIONS_COLUMN: {
            return new String(subBus.getCurrentDailyOperations() + " / " + (subBus.getMaxDailyOperation()
                    .intValue() <= 0 ? STR_NA
                            : subBus.getMaxDailyOperation().toString()));
        }

        case SUB_VAR_LOAD_COLUMN: {
            String retVal = DASH_LINE; // default just in case

            if (subBus.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal = CommonUtils.formatDecimalPlaces(subBus.getCurrentVarLoadPointValue()
                                                         .doubleValue(),
                                                         decPlaces);
            }
            
            return retVal;
        }
        case SUB_VAR_EST_LOAD_COLUMN: {
            String retVal;

            if (subBus.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal = CommonUtils.formatDecimalPlaces(subBus.getEstimatedVarLoadPointValue()
                                                          .doubleValue(),
                                                          decPlaces);
            }

            return retVal;
        }
        case SUB_POWER_FACTOR_COLUMN: {
            return getPowerFactorText(subBus.getPowerFactorValue()
                                      .doubleValue(), true) + " / " + getPowerFactorText(subBus.getEstimatedPFValue()
                                                                                         .doubleValue(),
                                                                                         true);
        }
        // This is returning a css class to be updated, no data.
        case SUB_VAR_LOAD_QUALITY: {
            if (!CBCUtils.signalQualityNormal(subBus, PointUnits.UOMID_KVAR)) {
                return "";
            } else {
                return "hideElement";
            }
        }
        // This is returning a css class to be updated, no data.
        case SUB_WATT_QUALITY: {
            if (!CBCUtils.signalQualityNormal(subBus, PointUnits.UOMID_KW)) {
                return "";
            } else {
                return "hideElement";
            }
        }
        // This is returning a css class to be updated, no data.
        case SUB_VOLT_QUALITY: {
            if (!CBCUtils.signalQualityNormal(subBus, PointUnits.UOMID_KVOLTS)) {
                return "";
            } else {
                return "hideElement";
            }
        }    
        case SUB_WATTS_COLUMN: {
            String retVal;

            if (subBus.getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal = CommonUtils.formatDecimalPlaces(subBus.getCurrentWattLoadPointValue()
                                                         .doubleValue(),
                                                         decPlaces);
            }

            return retVal;
        }
        case SUB_VOLTS_COLUMN: {
            String retVal;

            if (subBus.getCurrentVoltLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal = CommonUtils.formatDecimalPlaces(subBus.getCurrentVoltLoadPointValue()
                                                          .doubleValue(),
                                                          decPlaces);
            }

            return retVal;
        }
        case SUB_TIME_STAMP_COLUMN: {
            Date date = subBus.getLastCurrentVarPointUpdateTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime())
                return DASH_LINE;
            else
                return dateFormattingService.format(date, DateFormatEnum.BOTH, userContext);
        }

        case SUB_SHORT_TIME_STAMP_COLUMN: {
            Date date = subBus.getLastCurrentVarPointUpdateTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime())
                return DASH_LINE;
            else
                return dateFormattingService.format(date, DateFormatEnum.VERY_SHORT, userContext);
        }

        case SUB_WARNING_POPUP: {
            String warningMessage = "";
            if( subBus.getLikeDayControlFlag()) {
                warningMessage += WARNING_LIKE_DAY;
            } 
            
            if ( subBus.getVoltReductionFlag() ) {
                if(subBus.getLikeDayControlFlag()) warningMessage +="<br>";
                warningMessage += WARNING_VOLT_REDUCTION;
            }
            return warningMessage;
        }

        case SUB_WARNING_IMAGE:{

            if( subBus.getLikeDayControlFlag() || subBus.getVoltReductionFlag() ) {
                return "true";
            } else {
                return "false";
            }
        }

        default:
            return "---";
        }

    }

    /**
     * getValueAt method for Feeders
     */
    public Object getFeederValueAt(Feeder feeder, int col) {
        if (feeder == null)
            return "";

        int decPlaces = feeder.getDecimalPlaces();
        switch (col) {
        case FDR_NAME_COLUMN: {
            return feeder.getCcName();
        }

        case FDR_CURRENT_STATE_COLUMN: {
            String state = null;

            if (feeder.getCcDisableFlag().booleanValue()) {
                state = "DISABLED";
            } else if (feeder.getRecentlyControlledFlag().booleanValue()) {
                state = CBCUtils.getFeederPendingState(feeder);

                if (state == null) {
                    state = "PENDING"; // we dont know what Pending state its
                    // in
                }

            } else
                state = "ENABLED";

            // show waived with a W at the end of the state
            if (feeder.getWaiveControlFlag().booleanValue())
                state += "-W";
            if (feeder.getOvUvDisabledFlag().booleanValue())
                state += "-V";

            return state;
        }
        case FDR_TARGET_COLUMN_PEAKLEAD: {
            return CommonUtils.formatDecimalPlaces(feeder.getPeakLead().doubleValue(),0);
        }
        case FDR_TARGET_COLUMN_PEAKLAG: {
            return CommonUtils.formatDecimalPlaces(feeder.getPeakLag().doubleValue(),0);
        }
        case FDR_TARGET_COLUMN_CLOSEOPENPERCENT: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);
            
            String closePercent = CommonUtils.formatDecimalPlaces(feeder.getOffPkLag().doubleValue(),0) + "%C | ";
            String target = num.format(feeder.getPeakPFSetPoint() ) + " | ";
            String openPercent = CommonUtils.formatDecimalPlaces(feeder.getOffPkLead().doubleValue(),0) + "%O ";
            
            String closeOpenPercent = closePercent + target + openPercent;
            return closeOpenPercent;
        }
        case FDR_TARGET_COLUMN: {
            // decide which set Point we are to use
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);
            if(feeder.getControlmethod() == ControlMethod.TIME_OF_DAY) {
                return "TOD";
            } else if (feeder.getControlmethod() == ControlMethod.NONE) {
                return CtiUtilities.STRING_NONE;
            } 

            if (feeder.getPeakTimeFlag().booleanValue()) {
                if (feeder.isPowerFactorControlled()) {
                    return CommonUtils.formatDecimalPlaces(feeder.getPeakLag().doubleValue(), 0) 
                    + "%C | " 
                    + num.format( feeder.getPeakPFSetPoint() ) 
                    + " | " 
                    + CommonUtils.formatDecimalPlaces(feeder.getPeakLead().doubleValue(), 0) 
                    + "%O";
                } else
                    return CommonUtils.formatDecimalPlaces(feeder.getPeakLead().doubleValue(), 0) 
                    + " to " 
                    + CommonUtils.formatDecimalPlaces(feeder.getPeakLag().doubleValue(), 0) 
                    + " Pk";
            } else {
                if (feeder.isPowerFactorControlled()) {
                    return CommonUtils.formatDecimalPlaces(feeder.getOffPkLag().doubleValue(), 0) 
                    + "%C | " 
                    + num.format( feeder.getOffpeakPFSetPoint() ) 
                    + " | " 
                    + CommonUtils.formatDecimalPlaces(feeder.getOffPkLead().doubleValue(), 0) 
                    + "%O";
                } else
                    return CommonUtils.formatDecimalPlaces(feeder.getOffPkLead().doubleValue(), 0) 
                    + " to " 
                    + CommonUtils.formatDecimalPlaces(feeder.getOffPkLag().doubleValue(), 0) 
                    + " OffPk";
            }
        }
        case FDR_TARGET_POPUP: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(3);
            num.setMinimumFractionDigits(0);

            return "Target Var: " + num.format( feeder.getTargetvarvalue() );
        }
        case FDR_VAR_LOAD_POPUP: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(2);
            num.setMinimumFractionDigits(0);
            String str = "Phase A:" + num.format( feeder.getPhaseA() ) 
            + " Phase B:" + num.format( feeder.getPhaseB() )
            + " Phase C:" + num.format( feeder.getPhaseC() );
            return str;
        }
        case FDR_POWER_FACTOR_COLUMN: {
            return getPowerFactorText(feeder.getPowerFactorValue()
                                      .doubleValue(), true) + " / " + getPowerFactorText(feeder.getEstimatedPFValue()
                                                                                         .doubleValue(),
                                                                                         true);
        }

        case FDR_DAILY_OPERATIONS_COLUMN: {
            return new String(feeder.getCurrentDailyOperations() + " / " + (feeder.getMaxDailyOperation()
                    .intValue() <= 0 ? STR_NA
                            : feeder.getMaxDailyOperation().toString()));
        }

        case FDR_VAR_LOAD_COLUMN: {
            String retVal = "";
            
            if (feeder.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else
                retVal = CommonUtils.formatDecimalPlaces(feeder.getCurrentVarLoadPointValue()
                                                         .doubleValue(),
                                                         decPlaces);
            
            return retVal;
        }
        case FDR_VAR_EST_LOAD_COLUMN: {
            String retVal = "";

            if (feeder.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal += DASH_LINE;
            else
                retVal += CommonUtils.formatDecimalPlaces(feeder.getEstimatedVarLoadPointValue()
                                                          .doubleValue(),
                                                          decPlaces);

            return retVal;
        }
        // This is returning a css class to be updated, no data.
        case FDR_VAR_LOAD_QUALITY: {
            if (!CBCUtils.signalQualityNormal(feeder, PointUnits.UOMID_KVAR)) {
                return "";
            } else {
                return "hideElement";
            }
        }
        // This is returning a css class to be updated, no data.
        case FDR_WATT_QUALITY: {
            if (!CBCUtils.signalQualityNormal(feeder, PointUnits.UOMID_KW)) {
                return "";
            } else {
                return "hideElement";
            }
        }
        // This is returning a css class to be updated, no data.
        case FDR_VOLT_QUALITY: {
            if (!CBCUtils.signalQualityNormal(feeder, PointUnits.UOMID_KVOLTS)) {
                return "";
            } else {
                return "hideElement";
            }
        }        
        case FDR_WATTS_COLUMN: {
            String retVal = DASH_LINE; // default just in case

            if (feeder.getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal = CommonUtils.formatDecimalPlaces(feeder.getCurrentWattLoadPointValue()
                                                         .doubleValue(),
                                                         decPlaces);
            }

            return retVal;
        }
        case FDR_VOLTS_COLUMN: {
            String retVal = "";

            if (feeder.getCurrentVoltLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal += CommonUtils.formatDecimalPlaces(feeder.getCurrentVoltLoadPointValue()
                                                          .doubleValue(),
                                                          decPlaces);
            }
            return retVal;
        }
        case FDR_TIME_STAMP_COLUMN: {
            Date date = feeder.getLastCurrentVarPointUpdateTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime())
                return DASH_LINE;
            else
                return dateFormattingService.format(date, DateFormatEnum.BOTH, userContext);
        }
        case FDR_SHORT_TIME_STAMP_COLUMN: {
            Date date = feeder.getLastCurrentVarPointUpdateTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime() )
                return DASH_LINE;
            else
                return dateFormattingService.format(date, DateFormatEnum.VERY_SHORT, userContext);
        }        

        case FDR_ONELINE_WATTS_COLUMN: {
            String retVal = DASH_LINE; // default just in case

            if (feeder.getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal = CommonUtils.formatDecimalPlaces(feeder.getCurrentWattLoadPointValue()
                                                         .doubleValue(),
                                                         decPlaces);
            }
            return retVal;

        }
        case FDR_ONELINE_VOLTS_COLUMN: {
            String retVal = DASH_LINE; // default just in case

            if (feeder.getCurrentVoltLoadPointID().intValue() > PointTypes.SYS_PID_SYSTEM) {
                retVal = CommonUtils.formatDecimalPlaces(feeder.getCurrentVoltLoadPointValue()
                                                         .doubleValue(),
                                                         decPlaces);
            }

            return retVal;
        }
        case FDR_ONELINE_VAR_LOAD_COLUMN: {
            String retVal = DASH_LINE; // default just in case

            if (feeder.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else
                retVal = CommonUtils.formatDecimalPlaces(feeder.getCurrentVarLoadPointValue()
                                                         .doubleValue(),
                                                         decPlaces);

            return retVal;
        }

        case FDR_WARNING_POPUP: {
            return WARNING_LIKE_DAY;
        }

        case FDR_WARNING_IMAGE:{
            if( feeder.getLikeDayControlFlag() ) {
                return "true";
            } else {
                return "false";
            }
        }
        case FDR_ONELINE_THREE_PHASE_COLUMN: {
        	StringBuilder str = new StringBuilder();
        	
        	str.append(CommonUtils.formatDecimalPlaces(feeder.getPhaseA(),decPlaces) + "/");
        	str.append(CommonUtils.formatDecimalPlaces(feeder.getPhaseB(),decPlaces) + "/");
        	str.append(CommonUtils.formatDecimalPlaces(feeder.getPhaseC(),decPlaces));
        	
        	return str.toString();
        }
        case FDR_ONELINE_WATTS_VOLTS_COLUMN: {
           StringBuilder retVal = new StringBuilder();
           
        	if (feeder.getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM) {
                retVal.append("---");
        	} else {
                retVal.append(CommonUtils.formatDecimalPlaces(feeder.getCurrentWattLoadPointValue().doubleValue(), decPlaces));
            }
        	
        	if (feeder.getCurrentVoltLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM) {
        		retVal.append("/---");
            } else {
        		retVal.append("/" + CommonUtils.formatDecimalPlaces(feeder.getCurrentVoltLoadPointValue().doubleValue(), decPlaces));
        	}
        	return retVal.toString();
        }

        default:
            return "---";
        }
    }

    /**
     * Gets the powerfactor as a percent
     * @param value
     * @param compute
     * @return
     */
    public String getPowerFactorText(double value, boolean compute) {
        int decPlaces;
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        try {
            String propertyValue = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.PFACTOR_DECIMAL_PLACES, userContext.getYukonUser());
            decPlaces = Integer.parseInt(propertyValue);
        } catch (UnknownRolePropertyException e) {
            CTILogger.warn(e);
            decPlaces = 1;
        }

        if (value <= CapControlConst.PF_INVALID_VALUE) {
            return STR_NA;
        }

        return CommonUtils.formatDecimalPlaces(value * (compute ? 100 : 1), decPlaces) + "%";
    }

    public String getHTMLFgColor(SubStation subBus) {
        Color rc = getSubstationFgColor(subBus, Color.BLACK);
        return ColorUtil.getHex(new int[] { rc.getRed(), rc.getGreen(),
                rc.getBlue() });
    }

    public String getHTMLFgColor(SubBus subBus) {
        Color rc = getSubFgColor(subBus, Color.BLACK);
        return ColorUtil.getHex(new int[] { rc.getRed(), rc.getGreen(),
                rc.getBlue() });
    }

    public String getHTMLFgColor(Feeder feeder) {
        Color rc = getFeederFgColor(feeder, Color.BLACK);
        return ColorUtil.getHex(new int[] { rc.getRed(), rc.getGreen(),
                rc.getBlue() });
    }

    public String getHTMLFgColor(CapBankDevice capBank) {
        Color rc = getCapBankFGColor(capBank, Color.BLACK);
        return ColorUtil.getHex(new int[] { rc.getRed(), rc.getGreen(),
                rc.getBlue() });
    }

    public Color getSubstationFgColor(SubStation substation, Color defColor) {
        Color retColor = defColor;

        if (substation != null) {
            if (substation.getCcDisableFlag().booleanValue()) {
                retColor = _DEFAULT_COLORS[1]; // disabled color
            } else {
                retColor = _DEFAULT_COLORS[0];
            }
        }

        return retColor;
    }

    public Color getSubFgColor(SubBus subBus, Color defColor) {
        Color retColor = defColor;

        if (subBus != null) {
            if (subBus.getCcDisableFlag().booleanValue()) {
                retColor = _DEFAULT_COLORS[1]; // disabled color
            } else if (subBus.getRecentlyControlledFlag().booleanValue()) {
                retColor = _DEFAULT_COLORS[2]; // pending color
            } else {
                retColor = _DEFAULT_COLORS[0];
            }
        }

        return retColor;
    }

    public Color getCapBankFGColor(CapBankDevice capBank, Color defColor) {
        Color retColor = defColor;
        int status = capBank.getControlStatus().intValue();

        if (status >= 0 && status < CBCUtils.getCBCStateNames().length) {
            retColor = Colors.getColor(CBCUtils.getCBCStateNames()[status].getFgColor());
            if(retColor.equals(Colors.getColor(Colors.GREEN_ID))) retColor = _DEFAULT_COLORS[0];
        }
        
        return retColor;
    }

    public Color getFeederFgColor(Feeder feeder, Color defColor) {
        Color retColor = defColor;

        if (feeder != null) {
            if (feeder.getCcDisableFlag().booleanValue()) {
                retColor = _DEFAULT_COLORS[1]; // disabled color
            } else if (feeder.getRecentlyControlledFlag().booleanValue()) {
                retColor = _DEFAULT_COLORS[2]; // pending color
            } else {
                retColor = _DEFAULT_COLORS[0];
            }
        }

        return retColor;
    }

    /**
     * does the job of getSubBusValueAt only for oneline display. The reason for
     * that is because 3-tier display returns real and estimated values together
     * we need to return them separately for oneline display
     * @param bus
     * @param dispCol
     * @return
     */

    public String getOnelineSubBusValueAt(SubBus subBus, Integer col) 
    {

        if (subBus == null)
            return "";

        int decPlaces = subBus.getDecimalPlaces().intValue();
        switch (col) {

        case SUB_ONELINE_AREANAME_COLUMN: 
        {
            return CBCUtils.getAreaNameForSubBus(subBus.getCcId());
        }
        case SUB_ONELINE_CTL_METHOD_COLUMN: {
            return subBus.getControlMethod().getDisplayName();
        }

        case SUB_TARGET_COLUMN: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);

            if (subBus.getPeakTimeFlag().booleanValue()) {

                if (subBus.isPowerFactorControlled()) {

                    return CommonUtils.formatDecimalPlaces(subBus.getPeakLag().doubleValue(),0) 
                    + "%C | " + num.format(subBus.getPeakPFSetPoint()) + " | " 
                    + CommonUtils.formatDecimalPlaces(subBus.getPeakLead().doubleValue(),1) + "%O";
                } else
                    return CommonUtils.formatDecimalPlaces(subBus.getPeakLead().doubleValue(),0) 
                    + " to " 
                    + CommonUtils.formatDecimalPlaces(subBus.getPeakLag().doubleValue(),0) + " Pk";
            } else {
                if (subBus.isPowerFactorControlled()) {

                    return CommonUtils.formatDecimalPlaces(subBus.getOffPkLag().doubleValue(),0) 
                    + "%C | " + num.format(subBus.getOffpeakPFSetPoint()) + " | " 
                    + CommonUtils.formatDecimalPlaces(subBus.getOffPkLead().doubleValue(),0) + "%O";
                } else
                    return CommonUtils.formatDecimalPlaces(subBus.getOffPkLead().doubleValue(), 0)
                    + " to " 
                    + CommonUtils.formatDecimalPlaces(subBus.getOffPkLag().doubleValue(),0) + " OffPk";
            }

        }

        case SUB_ONELINE_DAILY_OPCNT_COLUMN: {
            return (subBus.getCurrentDailyOperations()).toString();
        }

        case SUB_ONELINE_MAX_OPCNT_COLUMN: {
            String retStr = (subBus.getMaxDailyOperation().intValue() <= 0 ? STR_NA
                    : subBus.getMaxDailyOperation().toString());
            return retStr;
        }
        case SUB_ONELINE_DAILY_MAX_OPCNT_COLUMN: {
            String retStr = (subBus.getCurrentDailyOperations().toString() + " / " + (subBus.getMaxDailyOperation().intValue() <= 0 ? STR_NA
                    : subBus.getMaxDailyOperation().toString()) );

            return retStr;
        }
        
        case SUB_ONELINE_THREE_PHASE_COLUMN: {
            StringBuilder str = new StringBuilder();
            str.append(CommonUtils.formatDecimalPlaces(subBus.getPhaseA(),decPlaces) + "/");
            str.append(CommonUtils.formatDecimalPlaces(subBus.getPhaseB(),decPlaces) + "/");
            str.append(CommonUtils.formatDecimalPlaces(subBus.getPhaseC(),decPlaces));
            return str.toString();
        }
        
        case SUB_ONELINE_KVAR_LOAD_COLUMN: {
            String retVal = DASH_LINE; //default just in case

            if (subBus.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal = CommonUtils.formatDecimalPlaces(subBus.getCurrentVarLoadPointValue()
                                                         .doubleValue(),
                                                         decPlaces);
            }
            return retVal;
        }
        case SUB_ONELINE_KVAR_ESTMATED_COLUMN: {
            String retVal = DASH_LINE; //default just in case

            if (subBus.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal = CommonUtils.formatDecimalPlaces(subBus.getEstimatedVarLoadPointValue()
                                                         .doubleValue(),
                                                         decPlaces);
            }

            return retVal;
        }

        case SUB_ONELINE_PF_COLUMN: {
            return getPowerFactorText(subBus.getPowerFactorValue()
                                      .doubleValue(), true);
        }
        case SUB_ONELINE_EST_PF_COLUMN: {
            return getPowerFactorText(subBus.getEstimatedPFValue()
                                      .doubleValue(), true);
        }

        case SUB_ONELINE_WATT_COLUMN: {
            String retVal = DASH_LINE; //default just in case

            if (subBus.getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal = CommonUtils.formatDecimalPlaces(subBus.getCurrentWattLoadPointValue()
                                                         .doubleValue(),
                                                         decPlaces);
            }
            return retVal;
        }
        case SUB_ONELINE_VOLT_COLUMN: {
            String retVal = DASH_LINE; //default just in case

            if (subBus.getCurrentVoltLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM)
                retVal = DASH_LINE;
            else {
                retVal = CommonUtils.formatDecimalPlaces(subBus.getCurrentVoltLoadPointValue().doubleValue(),decPlaces);
            }

            return retVal;
        }
        case SUB_TIME_STAMP_COLUMN: {
            Date date = subBus.getLastCurrentVarPointUpdateTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime())
                return DASH_LINE;
            else
                return dateFormattingService.format(date, DateFormatEnum.BOTH, userContext);
        }
        case SUB_SHORT_TIME_STAMP_COLUMN: {
            Date date = subBus.getLastCurrentVarPointUpdateTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime())
                return DASH_LINE;
            else
                return dateFormattingService.format(date, DateFormatEnum.VERY_SHORT, userContext);
        }
        default:
            return "---";
        }

    }

}