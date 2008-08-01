package com.cannontech.web.amr.profile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.MeteringRole;
import com.cannontech.util.ServletUtil;

/**
 * Spring controller class for profile
 */
public class ProfileController extends MultiActionController {

    private AuthDao authDao = null;
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
        
        boolean profileCollection = Boolean.parseBoolean(authDao.getRolePropertyValue(user, MeteringRole.PROFILE_COLLECTION));
        boolean profileCollectionScanning = Boolean.parseBoolean(authDao.getRolePropertyValue(user, MeteringRole.PROFILE_COLLECTION_SCANNING));
        mav.addObject("profileCollection", profileCollection);
        mav.addObject("profileCollectionScanning", profileCollectionScanning);

        boolean peakReportSupported = DeviceTypesFuncs.isMCT410(device.getType());
        mav.addObject("peakReportSupported", peakReportSupported);
        
        return mav;
    }

    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

}
