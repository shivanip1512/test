package com.cannontech.cbc.util;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dao.UnknownRolePropertyException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.CapControlConst;

/**
 * @author rneuharth
 */
public class UpdaterHelper {

    public enum UpdaterDataType {
        
        CB_NAME_COLUMN,
        CB_CONTROLLER,
        CB_BANK_SIZE_COLUMN,
        CB_STATUS_COLUMN,
        CB_TIME_STAMP_COLUMN,
        CB_OP_COUNT_COLUMN,
        CB_PARENT_COLUMN,
        CB_CURRENT_DAILY_OP_COLUMN,
        CB_DAILY_TOTAL_OP_COLUMN,
        CB_SHORT_TIME_STAMP_COLUMN,
        CB_STATUS_POPUP,
        CB_WARNING_FLAG,
        CB_LOCAL_REMOTE_TEXT,
        CB_WARNING_POPUP,
        CB_DAILY_MAX_TOTAL_OP_COLUMN,
        CB_PHASEA_BEFORE,
        CB_PHASEB_BEFORE,
        CB_PHASEC_BEFORE,
        CB_PHASEA_AFTER,
        CB_PHASEB_AFTER,
        CB_PHASEC_AFTER,
        CB_PHASEA_PERCENTCHANGE,
        CB_PHASEB_PERCENTCHANGE,
        CB_PHASEC_PERCENTCHANGE,
        CB_PERCENTCHANGE_TOTAL,
        CB_AFTER_TOTAL,
        CB_BEFORE_TOTAL,
        
        FDR_NAME_COLUMN,
        FDR_CURRENT_STATE_COLUMN,
        FDR_TARGET_COLUMN,
        FDR_VAR_LOAD_COLUMN,
        FDR_WATTS_COLUMN,
        FDR_POWER_FACTOR_COLUMN,
        FDR_TIME_STAMP_COLUMN,
        FDR_DAILY_OPERATIONS_COLUMN,
        FDR_ONELINE_WATTS_COLUMN,
        FDR_ONELINE_VOLTS_COLUMN,
        FDR_ONELINE_VAR_LOAD_COLUMN,
        FDR_SHORT_TIME_STAMP_COLUMN,
        FDR_TARGET_POPUP,
        FDR_VAR_LOAD_POPUP,
        FDR_WARNING_FLAG,
        FDR_WARNING_POPUP,
        FDR_ONELINE_WATTS_VOLTS_COLUMN,
        FDR_ONELINE_THREE_PHASE_COLUMN,
        FDR_TARGET_COLUMN_PEAKLEAD,
        FDR_TARGET_COLUMN_PEAKLAG,
        FDR_TARGET_COLUMN_CLOSEOPENPERCENT,
        FDR_TARGET_KVAR,
        FDR_VAR_LOAD_QUALITY,
        FDR_WATT_QUALITY,
        FDR_VOLT_QUALITY,
        FDR_VAR_EST_LOAD_COLUMN,
        FDR_VOLTS_COLUMN,
        
        SUB_AREA_NAME_COLUMN,
        SUB_NAME_COLUMN,
        SUB_CURRENT_STATE_COLUMN,
        STATE_FLAGS,
        SUB_TARGET_COLUMN,
        SUB_VAR_LOAD_COLUMN,
        SUB_WATTS_COLUMN,
        SUB_POWER_FACTOR_COLUMN,
        SUB_TIME_STAMP_COLUMN,
        SUB_DAILY_OPERATIONS_COLUMN,
        SUB_SP_AREA_ENABLED,
        SUB_SP_AREA_ENABLED_MSG,
        SUB_VOLT_REDUCTION,

        SUB_ONELINE_CONTROL_METHOD_COLUMN,
        SUB_SHORT_TIME_STAMP_COLUMN,
        SUB_TARGET_POPUP,
        SUB_VAR_LOAD_POPUP,
        SUB_WARNING_FLAG,
        SUB_WARNING_POPUP,
        SUB_TARGET_COLUMN_PEAKLEAD,
        SUB_TARGET_COLUMN_PEAKLAG,
        SUB_TARGET_COLUMN_CLOSEOPENPERCENT,
        SUB_TARGET_KVAR,
        SUB_VAR_LOAD_QUALITY,
        SUB_WATT_QUALITY,
        SUB_VOLT_QUALITY,
        SUB_VAR_EST_LOAD_COLUMN,
        SUB_VOLTS_COLUMN,
        
        AREA_POWER_FACTOR_COLUMN,
        AREA_VOLT_REDUCTION;
    }
    
    public static final String keyPrefix = "yukon.web.modules.capcontrol.";
    
    // The color schemes - based on the schedule status
    private static final YukonColorPalette[] _DEFAULT_COLORS = {
        // Enabled subbus
        YukonColorPalette.GREEN,
        // Disabled subbus
        YukonColorPalette.RED,
        // Pending subbus
        YukonColorPalette.ORANGE
    };
    
    @Autowired private AttributeService attributeService;
    @Autowired private StateGroupDao stateGroupDao;
    private DateFormattingService dateFormattingService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private PaoLoadingService paoLoadingService;
    private RolePropertyDao rolePropertyDao;
    private static final Logger log = YukonLogManager.getLogger(UpdaterHelper.class);
    
    public Object getCapBankValueAt(CapBankDevice capBank, UpdaterDataType dataType, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        
        if (capBank == null) return "";
        
        Integer controlDeviceId = capBank.getControlDeviceID();

        switch (dataType) {
        
        case CB_NAME_COLUMN: {
            return accessor.getMessage(keyPrefix + "capBankName", capBank.getCcName(), capBank.getControlOrder());
        }
        
        case CB_STATUS_COLUMN: {
            String fixedLabel = CapControlUtils.getFixedText(context.getYukonUser());
            
            int controlStatus = capBank.getControlStatus();
            LiteState capBankState = CapControlUtils.getCapBankState(controlStatus);
            if (capBankState == null) {
                log.error("A CapBank state was found that has no corresponding status.");
                return accessor.getMessage(keyPrefix + "unknownState", controlStatus);
            }
            
            boolean isCapBankDisabled = capBank.getCcDisableFlag() == true;
            boolean isFixedState = capBank.getOperationalState().equalsIgnoreCase(CapBank.FIXED_OPSTATE);
            boolean isStandaloneState = capBank.getOperationalState().equalsIgnoreCase(CapBank.STANDALONE_OPSTATE);
            String currentState = capBankState.getStateText();
            boolean showIgnoreReason = capBank.isIgnoreFlag();
            
            String ignoreReasonStr = null;
            // Load the ignoreReason value. Use the IGNORED_CONTROL_REASON attribute, if exists AND not the system state. Else, use CapBankDevice.IGNORE_REASON enum.
            if (controlDeviceId != 0) {
                PaoIdentifier pao = new PaoIdentifier(controlDeviceId, PaoType.getForDbString(capBank.getControlDeviceType()));
                LitePoint ignoredControlReasonPoint = attributeService.findPointForAttribute(pao, BuiltInAttribute.IGNORED_CONTROL_REASON);
                if (ignoredControlReasonPoint != null && ignoredControlReasonPoint.getStateGroupID() != StateGroupUtils.SYSTEM_STATEGROUPID) {
                    LiteState liteState = stateGroupDao.findLiteState(ignoredControlReasonPoint.getStateGroupID(), capBank.getIgnoreReason());
                    if (liteState != null) {
                        ignoreReasonStr = liteState.getStateText();
                    }
                }
            }
            if (ignoreReasonStr == null) { // if we haven't loaded anything yet, try this default
                ignoreReasonStr = CapBankDevice.getIgnoreReason(capBank.getIgnoreReason());
            }

            if (isCapBankDisabled) {
                String disStateString = accessor.getMessage(keyPrefix + "disabled") + " : " + (isFixedState ? fixedLabel : currentState);
                disStateString += capBank.getControlStatusQualityString();
                if (capBank.getOvUVDisabled()) {
                    disStateString += accessor.getMessage(keyPrefix + "ovuvDisabled");
                }
                
                disStateString += (showIgnoreReason ? " " + ignoreReasonStr : "");
                return disStateString;
            } 
            
            String enStateString = "";
            if (isFixedState) {
                enStateString = fixedLabel + " : ";
            } else if (isStandaloneState ) {
                enStateString = CapBank.STANDALONE_OPSTATE + " : ";
            }
            
            enStateString += currentState;
            enStateString += capBank.getControlStatusQualityString();
            
            if (capBank.getOvUVDisabled()) {
                enStateString += accessor.getMessage(keyPrefix + "ovuvDisabled");
            }
            enStateString += (showIgnoreReason ? " " + ignoreReasonStr : "");
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
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime()) {
                return accessor.getMessage("yukon.common.dashes");
            }
            return dateFormattingService.format(date, DateFormatEnum.BOTH, context);
        }
        
        case CB_SHORT_TIME_STAMP_COLUMN: {
            Date date = capBank.getLastStatusChangeTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime()) {
                return accessor.getMessage("yukon.common.dashes");
            }
            return dateFormattingService.format(date, DateFormatEnum.VERY_SHORT, context);
        }

        case CB_PARENT_COLUMN: {
            int parentId = capBank.getParentID();
            if (parentId > 0 ) {
                return paoLoadingService.getDisplayablePao(PaoIdentifier.of(parentId, PaoType.CAP_CONTROL_FEEDER)).getName();
            } else {
                return accessor.getMessage("yukon.common.dashes");
            }
        }
        
        case CB_CURRENT_DAILY_OP_COLUMN: {
            return capBank.getCurrentDailyOperations();
        }

        case CB_DAILY_TOTAL_OP_COLUMN: {
            int daily = capBank.getCurrentDailyOperations();
            int total = capBank.getTotalOperations();
            return accessor.getMessage(keyPrefix + "dailyOps", daily, total);
        }

        case CB_DAILY_MAX_TOTAL_OP_COLUMN: {
            int daily = capBank.getCurrentDailyOperations();
            int max = capBank.getMaxDailyOperation();
            int total = capBank.getTotalOperations();
            return accessor.getMessage(keyPrefix + "dailyMaxTotalOps", daily, max, total);
        }

        case CB_CONTROLLER:{
            String controllerName = null;
            
            if (controlDeviceId != 0) {
                PaoIdentifier pao = new PaoIdentifier(controlDeviceId, PaoType.getForDbString(capBank.getControlDeviceType()));
                controllerName = paoLoadingService.getDisplayablePao(pao).getName();
            } else {
                controllerName = accessor.getMessage("yukon.common.dashes");
            }
            
            return controllerName;
        }

        case CB_WARNING_FLAG:{
            return capBank.getMaxDailyOperationHitFlag() || capBank.getOvuvSituationFlag();
        }
            
        case CB_LOCAL_REMOTE_TEXT:{
            if (capBank.getLocalControlFlag()) {
                return accessor.getMessage(keyPrefix + "local");
            } else {
                return accessor.getMessage(keyPrefix + "remote");
            }
        }

        case CB_WARNING_POPUP:{
            String warningText = accessor.getMessage(keyPrefix + "alerts");

            if (capBank.getMaxDailyOperationHitFlag()) {
                warningText += " " + accessor.getMessage(keyPrefix + "maxDailyOps");
            }

            if (capBank.getOvuvSituationFlag()) {
                warningText += " " + accessor.getMessage(keyPrefix + "ovuvSituation");
            }
            
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
        	String value = getPhaseValueFromBank(capBank, dataType);
        	return value;
        }

        default: return null;
        }
        
    }

    private String getPhaseValueFromBank(CapBankDevice bank, UpdaterDataType dataType) {
        String value = "";
        String parseString = "";
        int parsePosition = 1;
        boolean totalColumn = false;
        
        switch (dataType) {
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

        switch (dataType) {
            case CB_PHASEA_BEFORE:
            case CB_PHASEA_PERCENTCHANGE:
            case CB_PHASEA_AFTER: {
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
                totalColumn = true;
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
        } else if (totalColumn) {
            value = parseString;
        }
        
        return value;
    }
    
    /**
     * getValueAt method for Substations
     */
    public Object getSubstationValueAt(SubStation substation, UpdaterDataType dataType, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        if (substation == null) return "";

        switch (dataType) {
        case SUB_NAME_COLUMN: {
            return substation.getCcName();
        }

        case SUB_AREA_NAME_COLUMN: {
            return substation.getCcArea();
        }

        case SUB_CURRENT_STATE_COLUMN: {
            boolean isDisabled = substation.getCcDisableFlag();
            String state = "";
            if (isDisabled) {
                state = accessor.getMessage(keyPrefix + "disabled");
            } else {
                state = accessor.getMessage(keyPrefix + "enabled");
            }
            
            if (substation.getRecentlyControlledFlag()) {
                state += " " + accessor.getMessage(keyPrefix + "pending");
            }
            
            if (substation.getOvuvDisableFlag()) {
                state += accessor.getMessage(keyPrefix + "ovuvDisabled");
            }
            
            return state;
        }

        case STATE_FLAGS: {
            Map<String, Object> flags = new HashMap<>();

            flags.put("enabled", !substation.getCcDisableFlag());
            flags.put("pending", substation.getRecentlyControlledFlag());
            flags.put("ovuvDisabled", substation.getOvuvDisableFlag());

            return flags;
        }

        case SUB_POWER_FACTOR_COLUMN: {
            String pf =  getPowerFactorText(substation.getPowerFactorValue() , true, context.getYukonUser(), accessor);     
            String estPf = getPowerFactorText(substation.getEstimatedPFValue(), true, context.getYukonUser(), accessor);
            return accessor.getMessage(keyPrefix + "pfEstPf", pf, estPf);
        }
        
        case SUB_SP_AREA_ENABLED: {
            if (substation.getSpecialAreaEnabled()) {
                return "error";
            } else {
                return "";
            }
        }
        
        case SUB_SP_AREA_ENABLED_MSG: {
            boolean saEnabled = substation.getSpecialAreaEnabled();
            if (saEnabled) {
                String specialAreaName = paoLoadingService.getDisplayablePao(new PaoIdentifier(substation.getSpecialAreaId(), PaoType.CAP_CONTROL_SPECIAL_AREA)).getName();
                return accessor.getMessage(keyPrefix + "saEnabled", specialAreaName);
            } else {
                return " ";
            }
        }
        
        case SUB_VOLT_REDUCTION: {
            boolean flag = substation.getVoltReductionFlag();
            boolean childFlag = substation.getChildVoltReductionFlag();

            return flag || childFlag;
        }

        default: return null;
        }
        
    }

    public Object getAreaValueAt(Area area, UpdaterDataType dataType, YukonUserContext context) {
        if (area == null) return "";
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

        switch (dataType) {
        
        case AREA_POWER_FACTOR_COLUMN: {
            String pf =  getPowerFactorText(area.getPowerFactorValue() , true, context.getYukonUser(), accessor);     
            String estPf = getPowerFactorText(area.getEstimatedPFValue(), true, context.getYukonUser(), accessor);
            return accessor.getMessage(keyPrefix + "pfEstPf", pf, estPf);
        }
        
        case AREA_VOLT_REDUCTION: { 
            boolean flag = area.getVoltReductionFlag();
            boolean childFlag = area.getChildVoltReductionFlag();
            
            return flag || childFlag;
        }

        default: return null;
        }
    }

    /**
     * getValueAt method for SubBuses
     */
    public Object getSubBusValueAt(SubBus subBus, UpdaterDataType dataType, YukonUserContext context) {
        if (subBus == null) return "";
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

        int decPlaces = subBus.getDecimalPlaces();
        NumberFormat voltagePointFormatter = NumberFormat.getNumberInstance();
        voltagePointFormatter.setMinimumFractionDigits(1);
        voltagePointFormatter.setMaximumFractionDigits(3);

        switch (dataType) {
        
        case SUB_NAME_COLUMN: {
            return subBus.getCcName();
        }

        case SUB_AREA_NAME_COLUMN: {
            return subBus.getCcArea();
        }

        case SUB_CURRENT_STATE_COLUMN: {
            String state = "";
            if (subBus.getCcDisableFlag()) {
                state = accessor.getMessage(keyPrefix + "disabled");
            } else {
                state = accessor.getMessage(keyPrefix + "enabled");
            }
            
            if (subBus.getRecentlyControlledFlag()) {
                state += " " + accessor.getMessage(keyPrefix + "pending");
            }
            
            if (subBus.getDualBusEnabled() && subBus.getSwitchOverStatus()) {
                state += accessor.getMessage(keyPrefix + "altBus");
            }

            if (subBus.getPrimaryBusFlag()) {
                state += accessor.getMessage(keyPrefix + "primaryBus");
            }
            
            if (subBus.getOvUvDisabledFlag()) {
                state += accessor.getMessage(keyPrefix + "ovuvDisabled");
            }
            return state;
        }
        
        case STATE_FLAGS: {
            Map<String, Object> flags = new HashMap<>();

            flags.put("enabled", !subBus.getCcDisableFlag());
            flags.put("pending", subBus.getRecentlyControlledFlag());
            flags.put("altBus", subBus.getDualBusEnabled() && subBus.getSwitchOverStatus());
            flags.put("primaryBus", subBus.getPrimaryBusFlag());
            flags.put("ovuvDisabled", subBus.getPrimaryBusFlag());

            return flags;
        }

        case SUB_TARGET_COLUMN_PEAKLEAD: {
            return voltagePointFormatter.format(subBus.getPeakLead());
        }
        
        case SUB_TARGET_COLUMN_PEAKLAG: {
            return voltagePointFormatter.format(subBus.getPeakLag());
        }
        
        case SUB_TARGET_COLUMN_CLOSEOPENPERCENT: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);
            
            String close = CommonUtils.formatDecimalPlaces(subBus.getOffPkLag(), 0);
            String target = num.format(subBus.getPeakPFSetPoint());
            String open = CommonUtils.formatDecimalPlaces(subBus.getOffPkLead(), 0);
            
            return accessor.getMessage(keyPrefix + "closeTargetOpen", close, target, open);
        }
        case SUB_TARGET_COLUMN: {
            // decide which set Point we are to use
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);
            
            if (subBus.getControlMethod() == ControlMethod.TIME_OF_DAY) {
                return accessor.getMessage(keyPrefix + "tod");
            } else if (subBus.getControlMethod() == ControlMethod.MANUAL_ONLY) {
                return accessor.getMessage(keyPrefix + "manual");
            } else if (subBus.getControlMethod() == null) {
                return accessor.getMessage("yukon.common.none.choice");
            } 
            
            /* Treat peak and off peak normally */
            if (subBus.getPeakTimeFlag()) { /* Currently in peak operating hours */

                if (subBus.isPowerFactorControlled()) {
                    String close = CommonUtils.formatDecimalPlaces(subBus.getPeakLag(), 0); 
                    String target = num.format(subBus.getPeakPFSetPoint());
                    String open = CommonUtils.formatDecimalPlaces(subBus.getPeakLead(), 0);
                    
                    return accessor.getMessage(keyPrefix + "closeTargetOpen", close, target, open);
                } else {
                    double peakLead = subBus.getPeakLead();
                    double peakLag = subBus.getPeakLag();
                    String lead = voltagePointFormatter.format(peakLead);
                    String lag = voltagePointFormatter.format(peakLag);
                    // In case of ControlAlgo = KVar : Lag > Lead 
                    // when ControlAlgo = Volt : Lead > Lag
                    if (peakLead <= peakLag) {
                        return accessor.getMessage(keyPrefix + "peakLeadLag", lead, lag);
                    } else {
                        return accessor.getMessage(keyPrefix + "peakLeadLag", lag, lead);
                    }
                }
                
            } else {
                /* Currently in off peak operating hours */
                if (subBus.isPowerFactorControlled()) {
                    String close = CommonUtils.formatDecimalPlaces(subBus.getOffPkLag(), 0); 
                    String target = num.format(subBus.getOffpeakPFSetPoint()); 
                    String open = CommonUtils.formatDecimalPlaces(subBus.getOffPkLead(), 0);
                    
                    return accessor.getMessage(keyPrefix + "closeTargetOpen", close, target, open);
                } else {
                    double offPeakLead = subBus.getOffPkLead();
                    double offPeakLag = subBus.getOffPkLag();
                    String lead = voltagePointFormatter.format(offPeakLead);
                    String lag = voltagePointFormatter.format(offPeakLag);
                    // In case of ControlAlgo = KVar : Lag > Lead 
                    // when ControlAlgo = Volt : Lead > Lag
                    if (offPeakLead <= offPeakLag) {
                        return accessor.getMessage(keyPrefix + "offPeakLeadLag", lead, lag);
                    } else {
                        return accessor.getMessage(keyPrefix + "offPeakLeadLag", lag, lead);
                    }
                }
            }
        }

        case SUB_TARGET_POPUP: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(3);
            num.setMinimumFractionDigits(0);

            return accessor.getMessage(keyPrefix + "targetVar", num.format(subBus.getTargetvarvalue()));
        }
        
        case SUB_TARGET_KVAR: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);
            
            if (subBus.getControlMethod() == ControlMethod.TIME_OF_DAY) {
                return accessor.getMessage(keyPrefix + "tod");
            } else if (subBus.getControlMethod() == null) {
                return accessor.getMessage("yukon.common.none.choice");
            } 
            
            //Decided to re-use these fields from the Cap Control Message so we did not need to add additional fields
            String lead = num.format(subBus.getPeakPFSetPoint());
            String lag = num.format(subBus.getOffpeakPFSetPoint());
            
            if (subBus.getPeakTimeFlag()) {
                return accessor.getMessage(keyPrefix + "peakLeadLag", lead, lag);
            } else {
                return accessor.getMessage(keyPrefix + "offPeakLeadLag", lead, lag);
            }
        }

        case SUB_VAR_LOAD_POPUP: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(2);
            num.setMinimumFractionDigits(0);
            
            String a = num.format(subBus.getPhaseA()); 
            String b = num.format(subBus.getPhaseB()); 
            String c = num.format(subBus.getPhaseC()); 
            return accessor.getMessage(keyPrefix + "varLoad", a, b, c);
        }

        case SUB_DAILY_OPERATIONS_COLUMN: {
            int current = subBus.getCurrentDailyOperations();
            int max = subBus.getMaxDailyOperation();
            
            if (max <= 0) {
                String na = accessor.getMessage("yukon.common.na");
                return accessor.getMessage(keyPrefix + "dailyOps", current, na);
            } else {
                return accessor.getMessage(keyPrefix + "dailyOps", current, max);
            }
        }

        case SUB_VAR_LOAD_COLUMN: {
            if (subBus.getCurrentVarLoadPointID() <= PointTypes.SYS_PID_SYSTEM)
                return accessor.getMessage("yukon.common.dashes");
            else {
                return CommonUtils.formatDecimalPlaces(subBus.getCurrentVarLoadPointValue(), decPlaces);
            }
        }
        
        case SUB_VAR_EST_LOAD_COLUMN: {
            if (subBus.getCurrentVarLoadPointID() <= PointTypes.SYS_PID_SYSTEM)
                return accessor.getMessage("yukon.common.dashes");
            else {
                return CommonUtils.formatDecimalPlaces(subBus.getEstimatedVarLoadPointValue(), decPlaces);
            }
        }
        
        case SUB_POWER_FACTOR_COLUMN: {
            String pf = getPowerFactorText(subBus.getPowerFactorValue(), true, context.getYukonUser(), accessor);
            String estPf = getPowerFactorText(subBus.getEstimatedPFValue(), true, context.getYukonUser(), accessor);
            
            return accessor.getMessage(keyPrefix + "pfEstPf", pf, estPf);
        }
        
        // This is returning a css class to be updated, no data.
        case SUB_VAR_LOAD_QUALITY: {
            if (!CapControlUtils.signalQualityNormal(subBus, UnitOfMeasure.KVAR)) {
                return "";
            } else {
                return "dn";
            }
        }
        
        // This is returning a css class to be updated, no data.
        case SUB_WATT_QUALITY: {
            if (!CapControlUtils.signalQualityNormal(subBus, UnitOfMeasure.KW)) {
                return "";
            } else {
                return "dn";
            }
        }
        
        // This is returning a css class to be updated, no data.
        case SUB_VOLT_QUALITY: {
            if (!CapControlUtils.signalQualityNormal(subBus, UnitOfMeasure.KVOLTS)) {
                return "";
            } else {
                return "dn";
            }
        }
        
        case SUB_WATTS_COLUMN: {
            if (subBus.getCurrentWattLoadPointID() <= PointTypes.SYS_PID_SYSTEM)
                return accessor.getMessage("yukon.common.dashes");
            else {
                return CommonUtils.formatDecimalPlaces(subBus.getCurrentWattLoadPointValue(), decPlaces);
            }
        }
        
        case SUB_VOLTS_COLUMN: {
            if (subBus.getCurrentVoltLoadPointID() <= PointTypes.SYS_PID_SYSTEM)
                return accessor.getMessage("yukon.common.dashes");
            else {
                return CommonUtils.formatDecimalPlaces(subBus.getCurrentVoltLoadPointValue(), decPlaces);
            }
        }
        
        case SUB_TIME_STAMP_COLUMN: {
            Date date = subBus.getLastCurrentVarPointUpdateTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime()) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return dateFormattingService.format(date, DateFormatEnum.BOTH, context);
            }
        }

        case SUB_SHORT_TIME_STAMP_COLUMN: {
            Date date = subBus.getLastCurrentVarPointUpdateTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime()) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return dateFormattingService.format(date, DateFormatEnum.VERY_SHORT, context);
            }
        }

        case SUB_WARNING_POPUP: {
            String warningMessage = "";
            if (subBus.getLikeDayControlFlag()) {
                warningMessage += accessor.getMessage(keyPrefix + "likeDay");
            } 
            
            if (subBus.getVoltReductionFlag()) {
                if (subBus.getLikeDayControlFlag()) {
                    warningMessage += " ";
                }
                warningMessage += accessor.getMessage(keyPrefix + "voltReduction");
            }
            
            return warningMessage;
        }

        case SUB_WARNING_FLAG:{
            return subBus.getLikeDayControlFlag() || subBus.getVoltReductionFlag();
        }

        default:
            return accessor.getMessage("yukon.common.dashes");
        }

    }

    /**
     * getValueAt method for Feeders
     */
    public Object getFeederValueAt(Feeder feeder, UpdaterDataType dataType, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        if (feeder == null) {
            return "";
        }
        
        int decPlaces = feeder.getDecimalPlaces();
        NumberFormat voltagePointFormatter = NumberFormat.getNumberInstance();
        voltagePointFormatter.setMinimumFractionDigits(1);
        voltagePointFormatter.setMaximumFractionDigits(3);
        switch (dataType) {
        
        case FDR_NAME_COLUMN: {
            return feeder.getCcName();
        }

        case FDR_CURRENT_STATE_COLUMN: {
            String state = null;

            if (feeder.getCcDisableFlag()) {
                state = accessor.getMessage(keyPrefix + "disabled");
            } else if (feeder.getRecentlyControlledFlag()) {
                state = CapControlUtils.getFeederPendingState(feeder);

                if (state == null) {
                    state = accessor.getMessage(keyPrefix + "pending"); // we dont know what Pending state its in
                }

            } else {
                state = accessor.getMessage(keyPrefix + "enabled");
            }
            
            // show waived with a W at the end of the state
            if (feeder.getWaiveControlFlag()) {
                state += accessor.getMessage(keyPrefix + "waive");
            }
            
            if (feeder.getOvUvDisabledFlag()) {
                state += accessor.getMessage(keyPrefix + "ovuvDisabled");
            }
            
            return state;
        }
        
        case STATE_FLAGS: {

            Map<String, Object> flags = new HashMap<>();

            flags.put("enabled", !feeder.getCcDisableFlag());
            flags.put("pending", feeder.getRecentlyControlledFlag());
            flags.put("waive", feeder.getWaiveControlFlag());
            flags.put("ovuvDisabled", feeder.getOvUvDisabledFlag());

            return flags;
        }

        case FDR_TARGET_COLUMN_PEAKLEAD: {
            return voltagePointFormatter.format(feeder.getPeakLead());
        }
        
        case FDR_TARGET_COLUMN_PEAKLAG: {
            return voltagePointFormatter.format(feeder.getPeakLag());
        }
        
        case FDR_TARGET_COLUMN_CLOSEOPENPERCENT: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);
            
            String close = CommonUtils.formatDecimalPlaces(feeder.getOffPkLag(), 0);
            String target = num.format(feeder.getPeakPFSetPoint());
            String open = CommonUtils.formatDecimalPlaces(feeder.getOffPkLead(), 0);
            
            return accessor.getMessage(keyPrefix + "closeTargetOpen", close, target, open);
        }
        
        case FDR_TARGET_COLUMN: {
            // decide which set Point we are to use
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);
            
            if (feeder.getControlmethod() == ControlMethod.TIME_OF_DAY) {
                return accessor.getMessage(keyPrefix + "tod");
            } else if (feeder.getControlmethod() == ControlMethod.MANUAL_ONLY) {
                return accessor.getMessage(keyPrefix + "manual");  
            } else if (feeder.getControlmethod() == null) {
                return accessor.getMessage("yukon.common.none.choice");
            } 

            if (feeder.getPeakTimeFlag()) {
                if (feeder.isPowerFactorControlled()) {
                    String close = CommonUtils.formatDecimalPlaces(feeder.getPeakLag(), 0); 
                    String target = num.format(feeder.getPeakPFSetPoint()); 
                    String open = CommonUtils.formatDecimalPlaces(feeder.getPeakLead(), 0);
                    return accessor.getMessage(keyPrefix + "closeTargetOpen", close, target, open);
                } else {
                    double peakLead = feeder.getPeakLead();
                    double peakLag = feeder.getPeakLag();
                    String lead = voltagePointFormatter.format(peakLead);
                    String lag = voltagePointFormatter.format(peakLag);
                    // In case of ControlAlgo = KVar : Lag > Lead 
                    // when ControlAlgo = Volt : Lead > Lag
                    if (peakLead <= peakLag) {
                        return accessor.getMessage(keyPrefix + "peakLeadLag", lead, lag);
                    } else {
                        return accessor.getMessage(keyPrefix + "peakLeadLag", lag, lead);
                    }
                }
                
            } else {
                
                if (feeder.isPowerFactorControlled()) {
                    String close = CommonUtils.formatDecimalPlaces(feeder.getOffPkLag(), 0); 
                    String target = num.format(feeder.getOffpeakPFSetPoint()); 
                    String open = CommonUtils.formatDecimalPlaces(feeder.getOffPkLead(), 0);
                    return accessor.getMessage(keyPrefix + "closeTargetOpen", close, target, open);
                } else {
                    double offPeakLead = feeder.getOffPkLead();
                    double offPeakLag = feeder.getOffPkLag();
                    String lead = voltagePointFormatter.format(offPeakLead);
                    String lag = voltagePointFormatter.format(offPeakLag);
                    // In case of ControlAlgo = KVar : Lag > Lead 
                    // when ControlAlgo = Volt : Lead > Lag
                    if (offPeakLead <= offPeakLag) {
                        return accessor.getMessage(keyPrefix + "offPeakLeadLag", lead, lag);
                    } else {
                        return accessor.getMessage(keyPrefix + "offPeakLeadLag", lag, lead);
                    }
                }
                
            }
            
        }
        
        case FDR_TARGET_POPUP: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(3);
            num.setMinimumFractionDigits(0);
            
            return accessor.getMessage(keyPrefix + "targetVar", num.format(feeder.getTargetvarvalue()));
        }
        
        case FDR_TARGET_KVAR: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(1);
            num.setMinimumFractionDigits(1);
            
            if (feeder.getAlgorithm() == ControlAlgorithm.TIME_OF_DAY) {
                return accessor.getMessage(keyPrefix + "tod");
            } else if (feeder.getAlgorithm() == null) {
                return accessor.getMessage("yukon.common.none.choice");
            } 
            
            //Decided to re-use these fields from the Cap Control Message so we did not need to add additional fields
            String lead = num.format(feeder.getPeakPFSetPoint());
            String lag = num.format(feeder.getOffpeakPFSetPoint());

            if (feeder.getPeakTimeFlag()) {
                return accessor.getMessage(keyPrefix + "peakLeadLag", lead, lag);
            } else {
                return accessor.getMessage(keyPrefix + "offPeakLeadLag", lead, lag);
            }
        }
        
        case FDR_VAR_LOAD_POPUP: {
            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(2);
            num.setMinimumFractionDigits(0);
            
            String a = num.format(feeder.getPhaseA()); 
            String b = num.format(feeder.getPhaseB()); 
            String c = num.format(feeder.getPhaseC()); 
            return accessor.getMessage(keyPrefix + "varLoad", a, b, c);
        }
        
        case FDR_POWER_FACTOR_COLUMN: {
            String pf =  getPowerFactorText(feeder.getPowerFactorValue() , true, context.getYukonUser(), accessor);     
            String estPf = getPowerFactorText(feeder.getEstimatedPFValue(), true, context.getYukonUser(), accessor);
            return accessor.getMessage(keyPrefix + "pfEstPf", pf, estPf);
        }

        case FDR_DAILY_OPERATIONS_COLUMN: {
            int current = feeder.getCurrentDailyOperations();
            int max = feeder.getMaxDailyOperation();
            
            if (max <= 0) {
                String na = accessor.getMessage("yukon.common.na");
                return accessor.getMessage(keyPrefix + "dailyOps", current, na);
            } else {
                return accessor.getMessage(keyPrefix + "dailyOps", current, max);
            }
        }

        case FDR_VAR_LOAD_COLUMN: {
            if (feeder.getCurrentVarLoadPointID() <= PointTypes.SYS_PID_SYSTEM) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return CommonUtils.formatDecimalPlaces(feeder.getCurrentVarLoadPointValue(), decPlaces);
            }
        }
        
        case FDR_VAR_EST_LOAD_COLUMN: {
            if (feeder.getCurrentVarLoadPointID() <= PointTypes.SYS_PID_SYSTEM) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return CommonUtils.formatDecimalPlaces(feeder.getEstimatedVarLoadPointValue(), decPlaces);
            }
        }
        
        // This is returning a css class to be updated, no data.
        case FDR_VAR_LOAD_QUALITY: {
            if (!CapControlUtils.signalQualityNormal(feeder, UnitOfMeasure.KVAR)) {
                return "";
            } else {
                return "dn";
            }
        }
        
        // This is returning a css class to be updated, no data.
        case FDR_WATT_QUALITY: {
            if (!CapControlUtils.signalQualityNormal(feeder, UnitOfMeasure.KW)) {
                return "";
            } else {
                return "dn";
            }
        }
        
        // This is returning a css class to be updated, no data.
        case FDR_VOLT_QUALITY: {
            if (!CapControlUtils.signalQualityNormal(feeder, UnitOfMeasure.KVOLTS)) {
                return "";
            } else {
                return "dn";
            }
        }
        
        case FDR_WATTS_COLUMN: {
            if (feeder.getCurrentWattLoadPointID() <= PointTypes.SYS_PID_SYSTEM) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return CommonUtils.formatDecimalPlaces(feeder.getCurrentWattLoadPointValue(), decPlaces);
            }
        }
        
        case FDR_VOLTS_COLUMN: {
            if (feeder.getCurrentVoltLoadPointID() <= PointTypes.SYS_PID_SYSTEM) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return CommonUtils.formatDecimalPlaces(feeder.getCurrentVoltLoadPointValue(), decPlaces);
            }
        }
        
        case FDR_TIME_STAMP_COLUMN: {
            Date date = feeder.getLastCurrentVarPointUpdateTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime()) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return dateFormattingService.format(date, DateFormatEnum.BOTH, context);
            }
        }
        
        case FDR_SHORT_TIME_STAMP_COLUMN: {
            Date date = feeder.getLastCurrentVarPointUpdateTime();
            if (date.getTime() <= CtiUtilities.get1990GregCalendar().getTime().getTime() ) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return dateFormattingService.format(date, DateFormatEnum.VERY_SHORT, context);
            }
        }        

        case FDR_ONELINE_WATTS_COLUMN: {
            if (feeder.getCurrentWattLoadPointID() <= PointTypes.SYS_PID_SYSTEM) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return CommonUtils.formatDecimalPlaces(feeder.getCurrentWattLoadPointValue(), decPlaces);
            }
        }
        
        case FDR_ONELINE_VOLTS_COLUMN: {
            if (feeder.getCurrentVoltLoadPointID() <= PointTypes.SYS_PID_SYSTEM) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return CommonUtils.formatDecimalPlaces(feeder.getCurrentVoltLoadPointValue(), decPlaces);
            }
        }
        
        case FDR_ONELINE_VAR_LOAD_COLUMN: {
            if (feeder.getCurrentVarLoadPointID() <= PointTypes.SYS_PID_SYSTEM) {
                return accessor.getMessage("yukon.common.dashes");
            } else {
                return CommonUtils.formatDecimalPlaces(feeder.getCurrentVarLoadPointValue(), decPlaces);
            }
        }

        case FDR_WARNING_POPUP: {
            return accessor.getMessage(keyPrefix + "likeDay");
        }

        case FDR_WARNING_FLAG:{
           return feeder.getLikeDayControlFlag();
        }
        
        case FDR_ONELINE_THREE_PHASE_COLUMN: {
        	String a = CommonUtils.formatDecimalPlaces(feeder.getPhaseA(),decPlaces);
        	String b = CommonUtils.formatDecimalPlaces(feeder.getPhaseB(),decPlaces);
        	String c = CommonUtils.formatDecimalPlaces(feeder.getPhaseC(),decPlaces);
        	
        	return accessor.getMessage(keyPrefix + "oneline.threePhase", a, b, c);
        }
        
        case FDR_ONELINE_WATTS_VOLTS_COLUMN: {
            String watts = "";
        	if (feeder.getCurrentWattLoadPointID() <= PointTypes.SYS_PID_SYSTEM) {
                watts = accessor.getMessage("yukon.common.dashes");
        	} else {
        	    watts = CommonUtils.formatDecimalPlaces(feeder.getCurrentWattLoadPointValue(), decPlaces);
            }
        	
        	String volts = "";
        	if (feeder.getCurrentVoltLoadPointID() <= PointTypes.SYS_PID_SYSTEM) {
        	    volts = accessor.getMessage("yukon.common.dashes");
            } else {
        		volts = CommonUtils.formatDecimalPlaces(feeder.getCurrentVoltLoadPointValue(), decPlaces);
        	}
        	return accessor.getMessage(keyPrefix + "oneline.wattsVolts", watts, volts);
        }

        default:
            return accessor.getMessage("yukon.common.dashes");
        }
    }

    /**
     * Gets the powerfactor as a percent
     * @param value
     * @param compute
     * @return 
     */
    public String getPowerFactorText(double value, boolean compute, LiteYukonUser user, MessageSourceAccessor accessor) {
        if (value <= CapControlConst.PF_INVALID_VALUE) {
            return accessor.getMessage("yukon.common.na");
        }

        int decPlaces;
        try {
            decPlaces = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.PFACTOR_DECIMAL_PLACES, user);
        } catch (UnknownRolePropertyException e) {
            log.error("Unknown RP: " + YukonRoleProperty.PFACTOR_DECIMAL_PLACES, e);
            decPlaces = 1;
        }

        String formatted = CommonUtils.formatDecimalPlaces(value * (compute ? 100 : 1), decPlaces);
        return accessor.getMessage("yukon.common.percent", formatted);
    }

    public String getHTMLFgColor(SubStation subBus) {
        YukonColorPalette rc = getSubstationFgColor(subBus, YukonColorPalette.BLACK);
        return rc.getHexValue();
    }

    public String getHTMLFgColor(SubBus subBus) {
        YukonColorPalette rc = getSubFgColor(subBus, YukonColorPalette.BLACK);
        return rc.getHexValue();
    }

    public String getHTMLFgColor(Feeder feeder) {
        YukonColorPalette rc = getFeederFgColor(feeder, YukonColorPalette.BLACK);
        return rc.getHexValue();
    }

    public String getHTMLFgColor(CapBankDevice capBank) {
        YukonColorPalette rc = getCapBankFGColor(capBank, YukonColorPalette.BLACK);
        return rc.getHexValue();
    }

    public YukonColorPalette getSubstationFgColor(SubStation substation, YukonColorPalette defColor) {
        YukonColorPalette retColor = defColor;

        if (substation != null) {
            if (substation.getCcDisableFlag()) {
                retColor = _DEFAULT_COLORS[1]; // disabled color
            } else {
                retColor = _DEFAULT_COLORS[0];
            }
        }

        return retColor;
    }

    public YukonColorPalette getSubFgColor(SubBus subBus, YukonColorPalette defColor) {
        YukonColorPalette retColor = defColor;

        if (subBus != null) {
            if (subBus.getCcDisableFlag()) {
                retColor = _DEFAULT_COLORS[1]; // disabled color
            } else if (subBus.getRecentlyControlledFlag()) {
                retColor = _DEFAULT_COLORS[2]; // pending color
            } else {
                retColor = _DEFAULT_COLORS[0];
            }
        }

        return retColor;
    }

    public YukonColorPalette getCapBankFGColor(CapBankDevice capBank, YukonColorPalette defColor) {
        YukonColorPalette retColor = defColor;
        int status = capBank.getControlStatus();
        LiteState state = CapControlUtils.getCapBankState(status);
        
        if (state != null) {
            retColor = YukonColorPalette.getColor(state.getFgColor());
            if (retColor.equals(YukonColorPalette.GREEN)){
                retColor = _DEFAULT_COLORS[0];
            }
        }
        
        return retColor;
    }

    public YukonColorPalette getFeederFgColor(Feeder feeder, YukonColorPalette defColor) {
        YukonColorPalette retColor = defColor;

        if (feeder != null) {
            if (feeder.getCcDisableFlag()) {
                retColor = _DEFAULT_COLORS[1]; // disabled color
            } else if (feeder.getRecentlyControlledFlag()) {
                retColor = _DEFAULT_COLORS[2]; // pending color
            } else {
                retColor = _DEFAULT_COLORS[0];
            }
        }

        return retColor;
    }
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
}