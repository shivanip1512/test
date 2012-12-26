package com.cannontech.web.contextualMenu.model.menuEntry;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.contextualMenu.CollectionCategory;
import com.cannontech.web.contextualMenu.MenuTypeApplicabilityDecider;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public abstract class BaseMenuEntry implements DeviceAction {

    protected String baseUrl;
    private YukonRole requiredRole;
    private Set<YukonRoleProperty> requiredRoleProperties;
    private MenuTypeApplicabilityDecider decider;
    private String beanName;
    
    private static final String BASE_KEY = "yukon.web.modules.common.contextualMenu.";

    public BaseMenuEntry() {
        super();
    }
    
    public BaseMenuEntry(String baseUrl, YukonRole requiredRole) {
        this.baseUrl = getBaseUrlWithappendedAmpersand(baseUrl);
        this.requiredRole = requiredRole;
    }

    public BaseMenuEntry(String baseUrl, YukonRole requiredRole,
                                YukonRoleProperty... requiredRoleProperties) {
        this.baseUrl = getBaseUrlWithappendedAmpersand(baseUrl);
        this.requiredRole = requiredRole;
        
        Builder<YukonRoleProperty> builder = ImmutableSet.builder();
        for (YukonRoleProperty roleProperty : requiredRoleProperties) {
            builder.add(roleProperty);
        }
        this.requiredRoleProperties = builder.build();
    }
    
    private String getBaseUrlWithappendedAmpersand(String baseUrl) {  
        return baseUrl.contains("?") ? (baseUrl + "&") : (baseUrl + "?");
    }

    @Override
    public YukonRole getRequiredRole() {
        return requiredRole;
    }

    @Override
    public Set<YukonRoleProperty> getRequiredRoleProperties() {
        return requiredRoleProperties;
    }
    
    public void setDecider(MenuTypeApplicabilityDecider decider) {
        this.decider = decider;
    }

    @Override
    public abstract String getUrl(CollectionCategory collectionCategory, Map<String, String> inputs);

    @Override
    public boolean supports(PaoIdentifier paoIdentifier) {
        if (decider != null) {
            return decider.isApplicable(paoIdentifier);
        }
        return true;
    }
    
    @Override
    public String getFormatKey() {
        return BASE_KEY + beanName;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
