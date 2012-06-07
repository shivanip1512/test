package com.cannontech.stars.util.task;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.util.ServletUtil;

public class ConfigSNRangeTaskDTO {
    private boolean configNow;
    private boolean useConfig;
    private boolean useHardwareAddressing;
    private StarsLMConfiguration hwConfig;
    private Integer group;
    private Integer route;
    private Boolean confirmOnMessagePage;
    private String redirect;
    
    public boolean isConfigNow() {
        return configNow;
    }

    public void setConfigNow(boolean configNow) {
        this.configNow = configNow;
    }

    public boolean getUseConfig() {
        return useConfig;
    }

    public void setUseConfig(boolean useConfig) {
        this.useConfig = useConfig;
    }

    public boolean isUseHardwareAddressing() {
        return useHardwareAddressing;
    }

    public void setUseHardwareAddressing(boolean useHardwareAddressing) {
        this.useHardwareAddressing = useHardwareAddressing;
    }

    public StarsLMConfiguration getHwConfig() {
        return hwConfig;
    }

    public void setHwConfig(StarsLMConfiguration hwConfig) {
        this.hwConfig = hwConfig;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public Integer getRoute() {
        return route;
    }

    public void setRoute(Integer route) {
        this.route = route;
    }

    public Boolean getConfirmOnMessagePage() {
        return confirmOnMessagePage;
    }

    public void setConfirmOnMessagePage(Boolean confirmOnMessagePage) {
        this.confirmOnMessagePage = confirmOnMessagePage;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public static final ConfigSNRangeTaskDTO valueOf(HttpServletRequest request) 
        throws ServletRequestBindingException, WebClientException {
        
        ConfigSNRangeTaskDTO dto = new ConfigSNRangeTaskDTO();
        
        boolean configNow = request.getParameter("ConfigNow") != null;
        dto.setConfigNow(configNow);
        
        boolean useConfig = ServletRequestUtils.getBooleanParameter(request, "UseConfig", false);
        dto.setUseConfig(useConfig);
        
        boolean useHardwareAddressing = request.getParameter("UseHardwareAddressing") != null;
        dto.setUseHardwareAddressing(useHardwareAddressing);
        
        if (useConfig && useHardwareAddressing) {
            StarsLMConfiguration hwConfig = new StarsLMConfiguration();
            UpdateLMHardwareConfigAction.setStarsLMConfiguration( hwConfig, request );
            dto.setHwConfig(hwConfig);
        }
        
        Integer group = ServletRequestUtils.getIntParameter(request, "Group");
        dto.setGroup(group);
        
        Integer route = ServletRequestUtils.getIntParameter(request, "Route");
        dto.setRoute(route);
        
        boolean confirmOnMessagePage = request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null;
        dto.setConfirmOnMessagePage(confirmOnMessagePage);
        
        String resultSetRedirect = ServletUtil.createSafeRedirectUrl(request, "/operator/Hardware/ResultSet.jsp");
        dto.setRedirect(resultSetRedirect);
        
        return dto;
    }
    
}
