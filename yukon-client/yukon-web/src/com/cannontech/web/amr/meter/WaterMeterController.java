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
import com.cannontech.web.updater.point.PointUpdateBackingService;

@Controller
@CheckRole({YukonRole.METERING,YukonRole.APPLICATION_BILLING,YukonRole.SCHEDULER,YukonRole.DEVICE_ACTIONS})
@RequestMapping("/water/*")
public class WaterMeterController {

    private DeviceDao deviceDao = null;
    private MultispeakFuncs multispeakFuncs;
    private PointDao pointDao = null;
    private PaoLoadingService paoLoadingService = null;
    private CachingPointFormattingService cachingPointFormattingService = null;
    private PointUpdateBackingService pointUpdateBackingService = null;
    private PaoDefinitionDao paoDefinitionDao = null;

    @RequestMapping
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
        pointUpdateBackingService.notifyOfImminentPoints(litePoints);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        String cisInfoWidgetName = multispeakFuncs.getCisDetailWidget(user);
        mav.addObject("cisInfoWidgetName", cisInfoWidgetName);
        
        if(device.getDeviceType().getPaoClass() == PaoClass.RFMESH) {
            mav.addObject("isRFMesh", true);
        }
        
        if(paoDefinitionDao.isTagSupported(device.getDeviceType(), PaoTag.PORTER_COMMAND_REQUESTS)) {
            mav.addObject("porterCommandRequestsSupported", true);
        }
        
        return mav;
    }

    // DI Setters
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    @Autowired
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Autowired
    public void setCachingPointFormattingService(CachingPointFormattingService cachingPointFormattingService) {
        this.cachingPointFormattingService = cachingPointFormattingService;
    }
    
    @Autowired
    public void setPointUpdateBackingService(PointUpdateBackingService pointUpdateBackingService) {
        this.pointUpdateBackingService = pointUpdateBackingService;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
}
