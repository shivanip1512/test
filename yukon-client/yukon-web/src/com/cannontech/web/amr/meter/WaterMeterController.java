package com.cannontech.web.amr.meter;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.CisDetailRolePropertyEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole({YukonRole.METERING})
@RequestMapping("/water/*")
public class WaterMeterController {

    @Autowired private CachingPointFormattingService cachingPointFormattingService = null;
    @Autowired private DeviceDao deviceDao = null;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao = null;
    @Autowired private PaoLoadingService paoLoadingService = null;
    @Autowired private PointDao pointDao = null;

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response)
    throws ServletException {
        
        ModelAndView mav = new ModelAndView("waterMeterHome.jsp");
        
        int deviceId = ServletRequestUtils.getIntParameter(request, "deviceId");
        
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        mav.addObject("deviceId", deviceId);
        mav.addObject("deviceName",  paoLoadingService.getDisplayablePao(device).getName());
        
        // do some hinting to speed loading
        List<LitePoint> litePoints = pointDao.getLitePointsByPaObjectId(deviceId);
        cachingPointFormattingService.addLitePointsToCache(litePoints);
        
        CisDetailRolePropertyEnum cisDetail = globalSettingDao.getEnum(GlobalSettingType.CIS_DETAIL_TYPE, CisDetailRolePropertyEnum.class);
        mav.addObject("cisInfoWidgetName", cisDetail.getWidgetName());
        
        PaoType deviceType = device.getDeviceType();
        boolean isRfMesh = deviceType.getPaoClass() == PaoClass.RFMESH;
        mav.addObject("showMapNetwork", isRfMesh);
        if (isRfMesh) {
            mav.addObject("isRFMesh", true);
            mav.addObject("showRfMetadata", true);
        }
        
        if (paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.PORTER_COMMAND_REQUESTS)) {
            mav.addObject("porterCommandRequestsSupported", true);
        }
        
        if (paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.RFN_EVENTS)) {
            mav.addObject("showEvents", true);
        }
        
        if (deviceType == PaoType.RFW201) {
        	mav.addObject("deviceConfigSupported", true);
        	mav.addObject("configurableDevice", true);
        }
        
        return mav;
    }
}