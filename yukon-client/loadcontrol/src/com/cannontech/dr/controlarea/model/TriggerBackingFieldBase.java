package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaTriggerItem;
import com.cannontech.user.YukonUserContext;

/**
 * Abstract Base Class for trigger backing fields 
 */
public abstract class TriggerBackingFieldBase implements 
                DemandResponseBackingField<ControlAreaTriggerItem> {

    private final static String baseKey = "yukon.web.modules.dr.controlAreaTrigger.value";
    protected final static MessageSourceResolvable blankFieldResolvable = 
        new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");

    private ControlAreaService controlAreaService;

    protected abstract class TriggerComparator implements
            Comparator<DisplayablePao> {

        @Override
        public int compare(DisplayablePao pao1, DisplayablePao pao2) {
            ControlAreaItem controlArea1 = getControlAreaFromYukonPao(pao1);
            ControlAreaItem controlArea2 = getControlAreaFromYukonPao(pao2);

            if (controlArea1 == controlArea2) {
                return 0;
            }

            if (controlArea1 == null) {
                return -1;
            }
            if (controlArea2 == null) {
                return 1;
            }

            ControlAreaTriggerItem trigger1 = controlArea1.getTrigger(1);
            ControlAreaTriggerItem trigger2 = controlArea2.getTrigger(1);

            if (trigger1 == trigger2) {
                return 0;
            }

            if (trigger1 == null) {
                return -1;
            }
            if (trigger2 == null) {
                return 1;
            }

            TriggerType trigger1Type = trigger1.getTriggerType();
            TriggerType trigger2Type = trigger2.getTriggerType();
            if (trigger1Type != trigger2Type) {
                return trigger1Type == TriggerType.STATUS ? -1 : 1;
            }

            return triggerCompare(trigger1Type, trigger1, trigger2);
        }

        public abstract int triggerCompare(
                TriggerType triggerType,
                ControlAreaTriggerItem trigger1, ControlAreaTriggerItem trigger2);
    }

    /**
     * Method to get the field value from the given trigger
     * @param trigger - Trigger to get value for
     * @param userContext - Current userContext
     * @return Value of this field for the given trigger (Should be one of: String, 
     *                                                  MessageSourceResolvable, ResolvableTemplate)
     */
    public abstract Object getTriggerValue(ControlAreaTriggerItem trigger, 
                                           YukonUserContext userContext);

    @Override
    public Object getValue(ControlAreaTriggerItem trigger, YukonUserContext userContext) {
        if(trigger != null) {
            return getTriggerValue((ControlAreaTriggerItem) trigger, userContext);
        } else {
            // This can happen if a trigger is deleted.  In the future, we'll
            // have to deal with this better.
            return "";
        }
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        // Default implementation to return NO sorter
        return null;
    }

    protected ControlAreaItem getControlAreaFromYukonPao(YukonPao from){
        return controlAreaService.getControlAreaForPao(from);
    }

    protected MessageSourceResolvable buildResolvable(String name, Object... args) {
        return new YukonMessageSourceResolvable(getKey(name), args);
    }

    protected String getKey(String suffix) {
        return baseKey + "." + suffix;
    }

    @Autowired
    public void setControlAreaService(ControlAreaService controlAreaService) {
        this.controlAreaService = controlAreaService;
    }
}
