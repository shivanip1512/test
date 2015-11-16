package com.cannontech.web.amr.meter;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole({YukonRole.METERING})
@RequestMapping("/water/*")
public class WaterMeterController {

    @Autowired private DeviceDao deviceDao = null;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private PointDao pointDao = null;
    @Autowired private PaoLoadingService paoLoadingService = null;
    @Autowired private CachingPointFormattingService cachingPointFormattingService = null;
    @Autowired private PaoDefinitionDao paoDefinitionDao = null;

    @RequestMapping("home")
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
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        String cisInfoWidgetName = multispeakFuncs.getCisDetailWidget(user);
        mav.addObject("cisInfoWidgetName", cisInfoWidgetName);
        
        boolean isRfMesh = device.getDeviceType().getPaoClass() == PaoClass.RFMESH;
        if (isRfMesh) {
            mav.addObject("isRFMesh", true);
            mav.addObject("showRfMetadata", true);
        }
        
        if (paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.PORTER_COMMAND_REQUESTS)) {
            mav.addObject("porterCommandRequestsSupported", true);
        }
        
        return mav;
    }
}