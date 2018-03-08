package com.cannontech.web.widget.support;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.user.checker.UserChecker;

public abstract class AdvancedWidgetControllerBase implements WidgetDefinitionBean, BeanNameAware {
    
    private String shortName;
    private String title;
    private final String keyPrefix = "yukon.web.widgets.";
    private String identityPath;
    private boolean lazyLoad = false;
    private Set<WidgetInput> inputs;
    private UserChecker roleAndPropertiesChecker;
    private SmartNotificationEventType smartNotificationsEvent;
    
    final public String getShortName() {
        return shortName;
    }
    
    public void setBeanName(String name) {
        this.shortName = name;
    }
    
    public String getTitleKey() {
    	return this.keyPrefix + this.shortName;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setLazyLoad(boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }
    
    public void setIdentityPath(String identityPath) {
        this.identityPath = identityPath;
    }
    
    public String getIdentityPath() {
        return identityPath;
    }
    
    public boolean isHasIdentity() {
        return identityPath != null;
    }
    
    public boolean isLazyLoad() {
        return lazyLoad;
    }
    
    public Set<WidgetInput> getInputs() {
        return inputs;
    }
    
    public void setInputs(Set<WidgetInput> inputs) {
        this.inputs = inputs;
    }
    
    public UserChecker getRoleAndPropertiesChecker() {
        return roleAndPropertiesChecker;
    }
    
    public void setRoleAndPropertiesChecker(UserChecker roleAndPropertiesChecker) {
        this.roleAndPropertiesChecker = roleAndPropertiesChecker;
    }
    
    public Object getActionTarget() {
        return this;
    }
    
    @RequestMapping(value="identity", method=RequestMethod.GET)
    public String identity() throws Exception {
        return identityPath;
    }

    /**
     * Adds the WidgetInput to the set (inputs)
     * 
     * @param input - the WidgetInput to add to the set.
     */
    public void addInput(WidgetInput input) {
        if (inputs == null) {
            inputs = new HashSet<>();
        }
        inputs.add(input);
    }

    public SmartNotificationEventType getSmartNotificationsEvent() {
        return smartNotificationsEvent;
    }

    public void setSmartNotificationsEvent(SmartNotificationEventType smartNotificationsEvent) {
        this.smartNotificationsEvent = smartNotificationsEvent;
    }
    
}