package com.cannontech.web.amr.meter;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole({YukonRole.METERING})
@RequestMapping("/virtual/*")
public class VirtualMeterController {

    @Autowired private CachingPointFormattingService cachingPointFormattingService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PointDao pointDao;

    @GetMapping("home")
    public String home(HttpServletRequest request, ModelMap model) throws ServletException {
        
        Integer deviceId = ServletRequestUtils.getIntParameter(request, "deviceId");
        if (deviceId != null) {
            SimpleDevice device = deviceDao.getYukonDevice(deviceId);
            model.addAttribute("id", deviceId);
            model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());
           
            // do some hinting to speed loading
            List<LitePoint> litePoints = pointDao.getLitePointsByPaObjectId(deviceId);
            cachingPointFormattingService.addLitePointsToCache(litePoints);
        }
        return "virtualMeterHome.jsp";
    }
}
