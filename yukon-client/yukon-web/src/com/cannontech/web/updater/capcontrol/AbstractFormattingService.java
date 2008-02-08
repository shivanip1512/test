package com.cannontech.web.updater.capcontrol;

import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.cbc.StreamableCapObject;

public abstract class AbstractFormattingService<E extends StreamableCapObject> implements CapControlFormattingService<E> {
    protected FilterCacheFactory filterCacheFactory;

    @Override
    public String getValueString(final E latestValue, final Format format, final LiteYukonUser user) {
        final CBCDisplay cbcDisplay = new CBCDisplay(user);
        
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
            
            case KW_VOLTS : {
                value = getKWVolts(latestValue, cbcDisplay);
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
            
            default : throw new RuntimeException("Unsupported Format: " + format);
        }
        
        return value;
    }

    protected String getCBName(E latestValue, CBCDisplay cbcDisplay) {
        throw new UnsupportedOperationException("Not supported at this level");
    }
    
    protected String getCBStatus(E latestValue, CBCDisplay cbcDisplay) {
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
    
    protected String getKWVolts(E latestValue, CBCDisplay cbcDisplay) {
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
    
    public void setFilterCacheFactory(FilterCacheFactory filterCacheFactory) {
        this.filterCacheFactory = filterCacheFactory;
    }
    
}
