package com.cannontech.web.updater.capcontrol;

import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.cbc.StreamableCapObject;

public abstract class AbstractFormattingService<E extends StreamableCapObject> implements CapControlFormattingService<E> {
    protected FilterCacheFactory filterCacheFactory;

    @Override
    public String getValueString(final E latestValue, final Format format, final YukonUserContext userContext) {
        final CBCDisplay cbcDisplay = new CBCDisplay(userContext);
        
        String value;
        switch (format) {
            
            case CB_NAME : {
                value = getCBName(latestValue, cbcDisplay);
                break;
            }
            
            case CB_STATUS : {
                value = getCBStatus(latestValue, cbcDisplay);
                break;
            }
            
            case CB_PHASEA_BEFORE : {
                value = getCBPhaseABefore(latestValue, cbcDisplay);
                break;
            }
            
            case CB_PHASEB_BEFORE : {
                value = getCBPhaseBBefore(latestValue, cbcDisplay);
                break;
            }
            
            case CB_PHASEC_BEFORE : {
                value = getCBPhaseCBefore(latestValue, cbcDisplay);
                break;
            }
            
            case CB_BEFORE_TOTAL : {
                value = getCBBeforeTotal(latestValue, cbcDisplay);
                break;
            }
            
            case CB_PHASEA_AFTER : {
                value = getCBPhaseAAfter(latestValue, cbcDisplay);
                break;
            }
            
            case CB_PHASEB_AFTER : {
                value = getCBPhaseBAfter(latestValue, cbcDisplay);
                break;
            }
            
            case CB_PHASEC_AFTER : {
                value = getCBPhaseCAfter(latestValue, cbcDisplay);
                break;
            }
            
            case CB_AFTER_TOTAL : {
                value = getCBAfterTotal(latestValue, cbcDisplay);
                break;
            }
            
            case CB_PHASEA_PERCENTCHANGE : {
                value = getCBPhaseAPercentChange(latestValue, cbcDisplay);
                break;
            }
            
            case CB_PHASEB_PERCENTCHANGE : {
                value = getCBPhaseBPercentChange(latestValue, cbcDisplay);
                break;
            }
            
            case CB_PHASEC_PERCENTCHANGE : {
                value = getCBPhaseCPercentChange(latestValue, cbcDisplay);
                break;
            }
            
            case CB_PERCENTCHANGE_TOTAL : {
                value = getCBPercentChangeTotal(latestValue, cbcDisplay);
                break;
            }
            
            case CB_STATUS_COLOR : {
                value = getCBStatusColor(latestValue, cbcDisplay);
                break;
            }
            
            case CB_STATUS_MESSAGE : {
                value = getCBStatusMessage(latestValue, cbcDisplay);
                break;
            }
            case CB_SIZE : {
                value = getCBSize(latestValue, cbcDisplay);
                break;
            }
            case CB_PARENT : {
                value = getCBParent(latestValue, cbcDisplay);
                break;
            }
            case DAILY_MAX_OPS : {
                value = getDailyMaxOps(latestValue, cbcDisplay);
                break;
            }
            
            case DATE_TIME : {
                value = getDateTime(latestValue, cbcDisplay);
                break;
            }
        
            case KVAR_LOAD : {
                value = getKVarLoad(latestValue, cbcDisplay);
                break;    
            }
            
            case KVAR_LOAD_EST : {
                value = getKVarLoadEst(latestValue, cbcDisplay);
                break;    
            }
            
            case KVAR_LOAD_QUALITY : {
                value = getKVarLoadQuality(latestValue, cbcDisplay);
                break;    
            }
            
            case WATT_QUALITY : {
                value = getWattQuality(latestValue, cbcDisplay);
                break;    
            }
            
            case VOLT_QUALITY : {
                value = getVoltQuality(latestValue, cbcDisplay);
                break;    
            }
            
            case KVAR_LOAD_MESSAGE : {
                value = getKVarLoadMessage(latestValue, cbcDisplay);
                break;
            }
            
            case KVARS_AVAILABLE : {
                value = getKVarsAvailable(latestValue, cbcDisplay);
                break;    
            }
            
            case KVARS_CLOSED : {
                value = getKVarsClosed(latestValue, cbcDisplay);
                break;      
            }
            
            case KVARS_TRIPPED : {
                value = getKVarsTripped(latestValue, cbcDisplay);
                break;     
            }
            
            case KVARS_UNAVAILABLE : {
                value = getKVarsUnavailable(latestValue, cbcDisplay);
                break; 
            }
            
            case KW : {
                value = getKw(latestValue, cbcDisplay);
                break; 
            }

            case VOLTS : {
                value = getVolts(latestValue, cbcDisplay);
                break; 
            }
            
            case NAME : {
                value = getName(latestValue, cbcDisplay);
                break; 
            }
            
            case PFACTOR : {
                value = getPFactor(latestValue, cbcDisplay);
                break;
            }
            
            case SETUP : {
                value = getSetup(latestValue, cbcDisplay);
                break;
            }
            
            case STATE : {
                value = getState(latestValue, cbcDisplay);
                break;
            }
            
            case TARGET : {
                value = getTarget(latestValue, cbcDisplay);
                break;
            }
            case TARGET_PEAKLAG: {
                value = getTargetPeakLag(latestValue, cbcDisplay);
                break;
            }
            case TARGET_PEAKLEAD: {
                value = getTargetPeakLead(latestValue, cbcDisplay);
                break;
            }
            case TARGET_CLOSEOPENPERCENT: {
                value = getTargetCloseOpenPercent(latestValue, cbcDisplay);
                break;
            }
            case TARGET_MESSAGE : {
                value = getTargetMessage(latestValue, cbcDisplay);
                break;    
            }
            
            case WARNING_FLAG : {
                value = getWarningFlag(latestValue, cbcDisplay);
                break; 
            }
            
            case WARNING_FLAG_MESSAGE : {
                value = getWarningFlagMessage(latestValue, cbcDisplay);
                break;     
            }
            
            case VERIFICATION_FLAG : {
                value = getVerificationFlag(latestValue, cbcDisplay);
                break;
            }
            
            case SA_ENABLED : {
                value = getSpecialAreaEnabled(latestValue, cbcDisplay);
                break;
            }
            
            case DUALBUS : {
                value = getDualBus(latestValue, cbcDisplay);
                break;
            }
            
            case DUALBUS_MESSAGE : {
                value = getDualBusMessage(latestValue, cbcDisplay);
                break;
            }
            
            default : throw new RuntimeException("Unsupported Format: " + format);
        }
        
        return value;
    }

    protected String getDualBus(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getDualBusMessage(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBName(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBStatus(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseABefore(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseBBefore(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseCBefore(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBBeforeTotal(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBPhaseAAfter(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseBAfter(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseCAfter(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBAfterTotal(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBPhaseAPercentChange(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseBPercentChange(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPhaseCPercentChange(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBPercentChangeTotal(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBStatusColor(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBStatusMessage(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBSize(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }

    protected String getCBParent(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getDailyMaxOps(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getDateTime(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarLoad(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarLoadEst(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarLoadQuality(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarLoadMessage(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarsAvailable(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarsClosed(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarsTripped(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKVarsUnavailable(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getWattQuality(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getVoltQuality(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getKw(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getVolts(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getName(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getPFactor(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getSetup(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getState(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getTargetPeakLead(E latestValue, final CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level"); 
    }
    
    protected String getTargetPeakLag(E latestValue, final CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level"); 
    }
    
    protected String getTargetCloseOpenPercent(E latestValue, final CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getTarget(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getTargetMessage(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getWarningFlag(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getWarningFlagMessage(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getVerificationFlag(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getSpecialAreaEnabled(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    public void setFilterCacheFactory(FilterCacheFactory filterCacheFactory) {
        this.filterCacheFactory = filterCacheFactory;
    }
    
}
