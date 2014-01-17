package com.cannontech.web.component;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;

@Controller
@RequestMapping("/groupSelect")
public class GroupSelectComponent {
    private DeviceGroupProviderDao deviceGroupDao;

    @RequestMapping("display")
    public void display() throws JspException {
    }
    
    @ModelAttribute("groupList")
    public List<DeviceGroup> getGroupList() {
        List<DeviceGroup> groupList = deviceGroupDao.getAllGroups();
        return groupList;
    }
    
    @Autowired
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }
    
    
}
