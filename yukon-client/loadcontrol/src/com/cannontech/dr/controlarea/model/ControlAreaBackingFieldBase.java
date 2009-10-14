package com.cannontech.dr.controlarea.model;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;

/**
 * Abstract Base Class for control area backing fields 
 */
public abstract class ControlAreaBackingFieldBase implements DemandResponseBackingField<LMControlArea> {

    private final static String baseKey = "yukon.web.modules.dr.controlArea.value";
    protected final static MessageSourceResolvable blankFieldResolvable = 
        new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");
    
    private ControlAreaService controlAreaService;
    
    /**
     * Method to get the field value from the given control area
     * @param controlArea - Control area to get value for
     * @param userContext - Current userContext
     * @return Value of this field for the given control area(Should be one of: String, 
     *                                                  MessageSourceResolvable, ResolvableTemplate)
     */
    public abstract Object getControlAreaValue(LMControlArea controlArea, 
                                               YukonUserContext userContext);

    @Override
    public Object getValue(LMControlArea controlArea, YukonUserContext userContext) {
        if(controlArea != null || handleNull()) {
            return getControlAreaValue((LMControlArea) controlArea, userContext);
        } else {
            return blankFieldResolvable;
        }
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(boolean isDescending, YukonUserContext userContext) {
        // Default implementation to return NO sorter
        return null;
    }
    
    protected LMControlArea getControlAreaFromYukonPao(YukonPao from){
        return controlAreaService.getControlAreaForPao(from);
    }
    
    protected MessageSourceResolvable buildResolvable(String name, Object... args) {
        return new YukonMessageSourceResolvable(getKey(name), args);
    }
    
    protected String getKey(String suffix) {
        return baseKey + "." + suffix;
    }
    
    protected boolean handleNull() {
        return false;
    }
    
    @Autowired
    public void setControlAreaService(ControlAreaService controlAreaService) {
        this.controlAreaService = controlAreaService;
    }
    
}
