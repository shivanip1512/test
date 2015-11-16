package com.cannontech.web.contextualMenu.model.menuEntry;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.contextualMenu.CollectionCategory;
import com.cannontech.web.contextualMenu.MenuTypeApplicabilityDecider;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Contains the generic functionality and methods for both a single device and a device collection
 * for an individual MenuAction
 */
public abstract class DeviceMenuAction implements DeviceAction {

    protected String baseUrl;
    protected String inputParamName;
    protected String outputParamName;

    private YukonRole requiredRole;
    private Set<YukonRoleProperty> requiredRoleProperties;
    private MenuTypeApplicabilityDecider decider;
    private String beanName;
    
    protected final static String defaultDeviceIdParamName = "deviceId";
    private static final String BASE_KEY = "yukon.web.modules.common.contextualMenu.";

    public DeviceMenuAction() {
        super();
    }
    
    public DeviceMenuAction(String baseUrl, YukonRole requiredRole) {
        this.baseUrl = getBaseUrlWithAppendedAmpersand(baseUrl);
        this.requiredRole = requiredRole;
    }

    public DeviceMenuAction(String baseUrl, YukonRole requiredRole,
                                YukonRoleProperty... requiredRoleProperties) {
        this.baseUrl = getBaseUrlWithAppendedAmpersand(baseUrl);
        this.requiredRole = requiredRole;
        
        Builder<YukonRoleProperty> builder = ImmutableSet.builder();
        for (YukonRoleProperty roleProperty : requiredRoleProperties) {
            builder.add(roleProperty);
        }
        this.requiredRoleProperties = builder.build();
    }

    public DeviceMenuAction(String baseUrl, String inputParamName, String outputParamName,
                         YukonRole requiredRole,
                         YukonRoleProperty... requiredRoleProperties) {
        this(baseUrl, requiredRole, requiredRoleProperties);
        this.inputParamName = inputParamName;
        this.outputParamName = outputParamName;
    }
    
    private String getBaseUrlWithAppendedAmpersand(String baseUrl) {  
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
    public abstract String getUrl(CollectionCategory collectionCategory, HttpServletRequest req);

    @Override
    public boolean supports(PaoIdentifier paoIdentifier) {
        if (decider != null) {
            return decider.isApplicable(paoIdentifier);
        }
        return true;
    }
    
    public Integer getDeviceId(CollectionCategory collectionCategory, Map<String, String> inputs) {
        if (collectionCategory == CollectionCategory.PAO_ID) {
            return Integer.valueOf(inputs.get(inputParamName));
        }
        return null;
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
