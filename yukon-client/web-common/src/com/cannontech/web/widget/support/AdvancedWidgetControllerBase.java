package com.cannontech.web.widget.support;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.user.checker.UserChecker;

public abstract class AdvancedWidgetControllerBase implements WidgetDefinitionBean, BeanNameAware {
    
    private String shortName;
    private String title;
    private final String keyPrefix = "yukon.web.widgets.";
    private String identityPath;
    private boolean lazyLoad = false;
    private Set<WidgetInput> inputs;
    private UserChecker roleAndPropertiesChecker;
    
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
    
    public ModelAndView identity(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView(identityPath);
        return mav;
    }
    
}