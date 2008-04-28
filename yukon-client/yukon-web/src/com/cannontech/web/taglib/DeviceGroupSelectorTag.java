package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.AnyDeviceGroupPredicate;
import com.cannontech.common.device.groups.service.DeviceGroupService;

@Configurable("deviceGroupSelectorTagPrototype")
public class DeviceGroupSelectorTag extends YukonTagSupport {

    private DeviceGroupService deviceGroupService;
    
    private String selectorId = null;
    private String deviceGroupNameElement = null;
    private String currentDeviceGroup = "";
    private String rootGroupName = null;
    private Integer selectorSize = 6;
    
    public DeviceGroupSelectorTag() {
        super();
    }

    public void doTag() throws JspException, IOException {
        
        // out
        JspWriter out = getJspContext().getOut();
        
        // javascript to set group name value field
        out.println("<script>");
        out.println("function selectGroup(selector) {document.getElementById('" + deviceGroupNameElement + "').value = selector.options[selector.selectedIndex].value;}");
        out.println("</script> ");
        
        // get groups
        DeviceGroup rootGroup = null;
        if(rootGroupName != null) {
            rootGroup = deviceGroupService.resolveGroupName(rootGroupName);
        }
        else {
            rootGroup = deviceGroupService.getRootGroup();
        }
        DeviceGroupHierarchy deviceGroupHierarchy = deviceGroupService.getDeviceGroupHierarchy(rootGroup, new AnyDeviceGroupPredicate());
        
        // name container
        out.println("<input type=\"text\" id=\"" + deviceGroupNameElement + "\" name=\"" + deviceGroupNameElement + "\" style=\"width:250px;\" value=\"" + currentDeviceGroup + "\" readonly/>");
        out.println("<br><br>");
        
        
        // select tag
        out.println("<select id=\"" + selectorId + "\" name=\"" + selectorId + "\" size=\"" + selectorSize + "\" onchange=\"selectGroup(this);\" style=\"width:400px;\">");
        
        printGroupOptions(out, deviceGroupHierarchy);
        
        out.println("</select>");
        
        
    }
    
    private void printGroupOptions(JspWriter out, DeviceGroupHierarchy dgh) throws IOException{
        
        String groupFullName = dgh.getGroup().getFullName();
        
        String selected = "";
        if (groupFullName.equals(currentDeviceGroup)) {
            selected = " selected";
        }
        
        String groupFullNameEscaped = StringEscapeUtils.escapeHtml(groupFullName);
        
        out.println("<option value=\"" + groupFullNameEscaped + "\"" + selected + ">" + groupFullNameEscaped + "</option>");
        for (DeviceGroupHierarchy d : dgh.getChildGroupList()) {
            printGroupOptions(out, d);
        }
    }
    
    
    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    public void setDeviceGroupNameElement(String deviceGroupNameElement) {
        this.deviceGroupNameElement = deviceGroupNameElement;
    }

    public void setSelectorId(String selectorId) {
        this.selectorId = selectorId;
    }
    
    public void setSelectorSize(Integer selectorSize) {
        this.selectorSize = selectorSize;
    }

    public void setRootGroupName(String rootGroupName) {
        this.rootGroupName = rootGroupName;
    }

    public void setCurrentDeviceGroup(String currentDeviceGroup) {
        this.currentDeviceGroup = currentDeviceGroup;
    }

}
