package com.cannontech.web.widget;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.cannontech.yc.bean.YCBean;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@RequestMapping("/touScheduleWidget/*")
public class TouScheduleWidget extends WidgetControllerBase {
    
    @Autowired private MeterDao meterDao;
    @Autowired private CommandRequestDeviceExecutor commandRequestExecutor;
    @Autowired private IDatabaseCache databaseCache;
    
    @Autowired
    public TouScheduleWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        Set<WidgetInput> simpleWidgetInputSet = new HashSet<WidgetInput>();
        simpleWidgetInputSet.add(simpleWidgetInput);
        
        this.setIdentityPath("common/deviceIdentity.jsp");
        this.setInputs(simpleWidgetInputSet);
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile("METERING"));
    }
    
    @Override
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("touScheduleWidget/render.jsp");
        
        List<LiteTOUSchedule> schedules = databaseCache.getAllTOUSchedules();
        mav.addObject("schedules", schedules);

        return mav;
    }
    
    @RequestMapping("downloadTouSchedule")
    public ModelAndView downloadTouSchedule(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        int scheduleId = WidgetParameterHelper.getRequiredIntParameter(request, "scheduleId");
        
        YCBean ycBean = getYcBean(request, deviceId);
        String command = ycBean.buildTOUScheduleCommand(scheduleId);
        
        PlcMeter meter = meterDao.getPlcMeterForId(deviceId);
        CommandResultHolder result = commandRequestExecutor.execute(meter, command, DeviceRequestType.TOU_SCHEDULE_COMMAND, user);
        
        mav.addObject("result", result);
        
        return mav;
    }
    
    private YCBean getYcBean(HttpServletRequest request, int deviceId) {

        YCBean ycBean = (YCBean) request.getSession().getAttribute("YC_BEAN");
        if (ycBean == null) {
            ycBean = new YCBean();
        }
        request.getSession().setAttribute("YC_BEAN", ycBean);

        LiteYukonUser user = ServletUtil.getYukonUser(request);
        ycBean.setUserID(user.getUserID());
        ycBean.setLiteYukonPao(deviceId);

        return ycBean;
    }
}
