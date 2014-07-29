package com.cannontech.dr.loadgroup.model;

import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;

/**
 * Abstract Base Class for load group backing fields 
 */
public abstract class LoadGroupBackingFieldBase implements DemandResponseBackingField<LMDirectGroupBase> {

    private final static String baseKey = "yukon.web.modules.dr.loadGroup.value";
    protected final static MessageSourceResolvable blankFieldResolvable = 
        new YukonMessageSourceResolvable("yukon.web.modules.dr.blankField");
    
    private LoadGroupService loadGroupService;
    
    /**
     * Method to get the field value from the given group
     * @param group - Group to get value for
     * @param userContext - Current userContext
     * @return Value of this field for the given group (Should be one of: String, 
     *                                                  MessageSourceResolvable, ResolvableTemplate)
     */
    public abstract Object getGroupValue(LMDirectGroupBase group, YukonUserContext userContext);

    @Override
    public Object getValue(LMDirectGroupBase group, YukonUserContext userContext) {
        if(group != null || handlesNull()) {
            return getGroupValue(group, userContext);
        } else {
            return blankFieldResolvable;
        }
    }
    
    @Override
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext) {
        // Default implementation to return NO sorter
        return null;
    }
    
    protected LMDirectGroupBase getGroupFromYukonPao(YukonPao from){
        return loadGroupService.getGroupForPao(from);
    }
    
    protected MessageSourceResolvable buildResolvable(String name, Object... args) {
        return new YukonMessageSourceResolvable(getKey(name), args);
    }
    
    protected String getKey(String suffix) {
        return baseKey + "." + suffix;
    }
    
    protected boolean handlesNull() {
        return false;
    }
    
    @Autowired
    public void setLoadGroupService(LoadGroupService loadGroupService) {
        this.loadGroupService = loadGroupService;
    }
    
}
