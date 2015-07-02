package com.cannontech.web.widget.support;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.user.checker.UserChecker;

public abstract class WidgetControllerBase implements WidgetDefinitionBean, BeanNameAware, WidgetController {
    private String shortName;
    private String identityPath;
    protected static final String keyPrefix = "yukon.web.widgets.";
    private boolean lazyLoad = false;
    private Set<WidgetInput> inputs;
    private UserChecker roleAndPropertiesChecker;

    public ModelAndView identity(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView(identityPath);
        return mav;
    }
    
    final public String getShortName() {
        return shortName;
    }
    
    public void setBeanName(String name) {
        this.shortName = name;
    }
    
    public String getTitleKey() {
    	return WidgetControllerBase.keyPrefix + this.shortName;
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
    
}
