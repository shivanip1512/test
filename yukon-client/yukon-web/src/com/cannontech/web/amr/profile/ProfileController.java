package com.cannontech.web.amr.profile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

/**
 * Spring controller class for profile
 */
public class ProfileController extends MultiActionController {

    private RolePropertyDao rolePropertyDao;
    private DeviceDao deviceDao = null;
    
    public ModelAndView home(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("profile.jsp");

        LiteYukonUser user = ServletUtil.getYukonUser(request);
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        YukonDevice device = deviceDao.getYukonDevice(deviceId);
        
        mav.addObject("deviceId", deviceId);
        
        boolean lpSupported = DeviceTypesFuncs.isLoadProfile4Channel(device.getType());
        mav.addObject("lpSupported", lpSupported);
        
        boolean profileCollection = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.PROFILE_COLLECTION, user);
        mav.addObject("profileCollection", profileCollection);

        boolean peakReportSupported = DeviceTypesFuncs.isMCT410(device.getType());
        mav.addObject("peakReportSupported", peakReportSupported);
        
        return mav;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
}
