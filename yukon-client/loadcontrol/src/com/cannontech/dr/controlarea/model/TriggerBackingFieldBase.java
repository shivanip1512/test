package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.user.YukonUserContext;

/**
 * Abstract Base Class for trigger backing fields 
 */
public abstract class TriggerBackingFieldBase implements 
                DemandResponseBackingField<LMControlAreaTrigger> {

    private final static String baseKey = "yukon.web.modules.dr.controlAreaTrigger.value";
    protected final static MessageSourceResolvable blankFieldResolvable = 
        new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");
    
    /**
     * Method to get the field value from the given trigger
     * @param trigger - Trigger to get value for
     * @param userContext - Current userContext
     * @return Value of this field for the given trigger
     */
    public abstract Object getTriggerValue(LMControlAreaTrigger trigger, 
                                           YukonUserContext userContext);

    @Override
    public Object getValue(LMControlAreaTrigger trigger, YukonUserContext userContext) {
        if(trigger != null) {
            return getTriggerValue((LMControlAreaTrigger) trigger, userContext);
        } else {
            // This can happen if a trigger is deleted.  In the future, we'll
            // have to deal with this better.
            return "";
        }
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(boolean isDescending, YukonUserContext userContext) {
        // Default implementation to return NO sorter
        return null;
    }
    
    protected MessageSourceResolvable buildResolvable(String name, Object... args) {
        return new YukonMessageSourceResolvable(getKey(name), args);
    }
    
    protected String getKey(String suffix) {
        return baseKey + "." + suffix;
    }
    
}
