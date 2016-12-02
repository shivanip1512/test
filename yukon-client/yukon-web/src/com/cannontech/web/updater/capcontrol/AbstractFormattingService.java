package com.cannontech.web.updater.capcontrol;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class AbstractFormattingService<E extends StreamableCapObject> implements CapControlFormattingService<E> {
    
    protected FilterCacheFactory filterCacheFactory;
    protected YukonUserContextMessageSourceResolver resolver;
    protected UpdaterHelper updaterHelper;

    @Override
    public String getValueString(final E latestValue, final Format format, final YukonUserContext context) {
        
        String value;
        switch (format) {
            
            case CB_NAME : {
                value = getCBName(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_STATUS : {
                value = getCBStatus(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_PHASEA_BEFORE : {
                value = getCBPhaseABefore(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_PHASEB_BEFORE : {
                value = getCBPhaseBBefore(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_PHASEC_BEFORE : {
                value = getCBPhaseCBefore(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_BEFORE_TOTAL : {
                value = getCBBeforeTotal(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_PHASEA_AFTER : {
                value = getCBPhaseAAfter(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_PHASEB_AFTER : {
                value = getCBPhaseBAfter(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_PHASEC_AFTER : {
                value = getCBPhaseCAfter(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_AFTER_TOTAL : {
                value = getCBAfterTotal(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_PHASEA_PERCENTCHANGE : {
                value = getCBPhaseAPercentChange(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_PHASEB_PERCENTCHANGE : {
                value = getCBPhaseBPercentChange(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_PHASEC_PERCENTCHANGE : {
                value = getCBPhaseCPercentChange(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_PERCENTCHANGE_TOTAL : {
                value = getCBPercentChangeTotal(latestValue, updaterHelper, context);
                break;
            }
            
            case CB_STATUS_COLOR : {
                value = getCBStatusColor(latestValue, updaterHelper);
                break;
            }
            
            case CB_SIZE : {
                value = getCBSize(latestValue, updaterHelper, context);
                break;
            }
            case CB_PARENT : {
                value = getCBParent(latestValue, updaterHelper, context);
                break;
            }
            case DAILY_MAX_OPS : {
                value = getDailyMaxOps(latestValue, updaterHelper, context);
                break;
            }
            
            case DATE_TIME : {
                value = getDateTime(latestValue, updaterHelper, context);
                break;
            }
        
            case KVAR_LOAD : {
                value = getKVarLoad(latestValue, updaterHelper, context);
                break;    
            }
            
            case KVAR_LOAD_EST : {
                value = getKVarLoadEst(latestValue, updaterHelper, context);
                break;    
            }
            
            case KVAR_LOAD_QUALITY : {
                value = getKVarLoadQuality(latestValue, updaterHelper, context);
                break;    
            }
            
            case WATT_QUALITY : {
                value = getWattQuality(latestValue, updaterHelper, context);
                break;    
            }
            
            case VOLT_QUALITY : {
                value = getVoltQuality(latestValue, updaterHelper, context);
                break;    
            }
            
            case KVAR_LOAD_MESSAGE : {
                value = getKVarLoadMessage(latestValue, updaterHelper, context);
                break;
            }
            
            case KVARS_AVAILABLE : {
                value = getKVarsAvailable(latestValue, updaterHelper, context);
                break;    
            }
            
            case KVARS_CLOSED : {
                value = getKVarsClosed(latestValue, updaterHelper, context);
                break;      
            }
            
            case KVARS_TRIPPED : {
                value = getKVarsTripped(latestValue, updaterHelper, context);
                break;     
            }
            
            case KVARS_UNAVAILABLE : {
                value = getKVarsUnavailable(latestValue, updaterHelper, context);
                break; 
            }
            
            case KW : {
                value = getKw(latestValue, updaterHelper, context);
                break; 
            }

            case VOLTS : {
                value = getVolts(latestValue, updaterHelper, context);
                break; 
            }
            
            case NAME : {
                value = getName(latestValue, updaterHelper);
                break; 
            }
            
            case PFACTOR : {
                value = getPFactor(latestValue, updaterHelper, context);
                break;
            }
            
            case CHILD_COUNT : {
                value = getChildCount(latestValue, updaterHelper, context);
                break;
            }
            
            case STATE : {
                value = getState(latestValue, updaterHelper, context);
                break;
            }
            
            case STATE_FLAGS : {
                Map<String, Object> response = getStateFlags(latestValue, updaterHelper, context);
                value = jsonPayloadFrom(latestValue, response);
                break; 
            }

            case TARGET : {
                value = getTarget(latestValue, updaterHelper, context);
                break;
            }
            case TARGET_PEAKLAG: {
                value = getTargetPeakLag(latestValue, updaterHelper, context);
                break;
            }
            case TARGET_PEAKLEAD: {
                value = getTargetPeakLead(latestValue, updaterHelper, context);
                break;
            }
            case TARGET_CLOSEOPENPERCENT: {
                value = getTargetCloseOpenPercent(latestValue, updaterHelper, context);
                break;
            }
            case TARGET_MESSAGE : {
                value = getTargetMessage(latestValue, updaterHelper, context);
                break;    
            }
            
            case WARNING_FLAG : {
                boolean response = getWarningFlag(latestValue, updaterHelper, context);
                value = jsonPayloadFrom(latestValue, response);
                break; 
            }
            
            case LOCAL_FLAG : {
                value = getLocalFlag(latestValue, updaterHelper, context);
                break; 
            }
            
            case WARNING_FLAG_MESSAGE : {
                value = getWarningFlagMessage(latestValue, updaterHelper, context);
                break;     
            }
            
            case VERIFICATION_FLAG : {
                boolean response = getVerificationFlag(latestValue, updaterHelper, context);
                value = jsonPayloadFrom(latestValue, response);
                break;
            }
            
            case SA_ENABLED_MSG : {
                value = getSpecialAreaEnabledMsg(latestValue, updaterHelper, context);
                break;
            }
            
        case SA_ENABLED_ALL_MSG: {
            value = getSpecialAreaEnabledMsg(latestValue, updaterHelper, context);
            // If there are many special areas assigned to a substation only one of them will be in the enabled
            // state If none of them are enabled the value will contain only a space in the string, which means none
            // of the special area assigned is enabled.Test to see if we have received a blank and display a
            // "No Special Area active" message
            if (StringUtils.isBlank(value)) {
                MessageSourceAccessor messageAccessor = resolver.getMessageSourceAccessor(context);
                value = messageAccessor.getMessage("yukon.web.modules.capcontrol.noSpecialAreaEnabled");
            }
            break;
        }
  
            case SA_ENABLED: {
                value = getSpecialAreaEnabled(latestValue, updaterHelper, context);
                break;
            }
            
            case DUALBUS : {
                boolean response = getDualBus(latestValue, updaterHelper, context);
                value = jsonPayloadFrom(latestValue, response);
                break;
            }
            
            case DUALBUS_MESSAGE : {
                value = getDualBusMessage(latestValue, updaterHelper, context);
                break;
            }
            
            default : throw new RuntimeException("Unsupported Format: " + format);
        }
        
        return value;
    }

    private static String jsonPayloadFrom(StreamableCapObject latestValue, Object value) {
        Map<String, Object> data = new HashMap<>();
        data.put("paoId", latestValue.getCcId());
        data.put("value", value);

        try {
            return JsonUtils.toJson(data);
        } catch (JsonProcessingException e) {
            //Writing this simple object to JSON should not throw an exception.
            throw new RuntimeException(e);
        }
    }

    protected boolean getDualBus(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getDualBusMessage(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBName(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBStatus(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseABefore(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseBBefore(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseCBefore(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBBeforeTotal(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBPhaseAAfter(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseBAfter(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseCAfter(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBAfterTotal(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBPhaseAPercentChange(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseBPercentChange(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseCPercentChange(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPercentChangeTotal(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBStatusColor(E latestValue, UpdaterHelper updaterHelper) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBStatusMessage(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBSize(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBParent(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getDailyMaxOps(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getDateTime(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarLoad(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarLoadEst(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarLoadQuality(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarLoadMessage(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarsAvailable(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarsClosed(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarsTripped(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarsUnavailable(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getWattQuality(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getVoltQuality(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKw(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getVolts(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getName(E latestValue, UpdaterHelper updaterHelper) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getPFactor(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getChildCount(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getState(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected Map<String, Object> getStateFlags(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level"); 
    }

    protected String getTargetPeakLead(E latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level"); 
    }
    
    protected String getTargetPeakLag(E latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level"); 
    }
    
    protected String getTargetCloseOpenPercent(E latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getTarget(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getTargetMessage(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected boolean getWarningFlag(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getLocalFlag(E latestValue, UpdaterHelper updaterHelper2, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getWarningFlagMessage(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected boolean getVerificationFlag(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getSpecialAreaEnabledMsg(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getSpecialAreaEnabled(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    public void setFilterCacheFactory(FilterCacheFactory filterCacheFactory) {
        this.filterCacheFactory = filterCacheFactory;
    }
    
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.resolver = messageSourceResolver;
    }
    
    public void setUpdaterHelper(UpdaterHelper updaterHelper) {
        this.updaterHelper = updaterHelper;
    }
    
}