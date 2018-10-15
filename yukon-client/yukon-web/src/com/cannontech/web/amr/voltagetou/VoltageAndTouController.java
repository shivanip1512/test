package com.cannontech.web.amr.voltagetou;

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
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@RequestMapping("/voltageAndTou/*")
public class VoltageAndTouController  {

    @Autowired private IDatabaseCache databaseCache;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("voltageAndTou.jsp");
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);

        boolean threePhaseVoltageOrCurrentSupported = (paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(),
                                                                                       PaoTag.THREE_PHASE_VOLTAGE) ||
                                                       paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(),
                                                                                       PaoTag.THREE_PHASE_CURRENT));

        mav.addObject("threePhaseVoltageOrCurrentSupported", threePhaseVoltageOrCurrentSupported);
        mav.addObject("deviceId", deviceId);
        mav.addObject("deviceName", paoLoadingService.getDisplayablePao(device).getName());

        // Schedules
        if (DeviceTypesFuncs.isMCT4XX(device.getDeviceType())) {
            List<LiteTOUSchedule> schedules = databaseCache.getAllTOUSchedules();
            mav.addObject("schedules", schedules);
        }

        return mav;
    }
}