package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.cannontech.yc.bean.YCBean;
import com.cannontech.yukon.IDatabaseCache;

public class TouScheduleWidget extends WidgetControllerBase {
    
    private MeterDao meterDao;
    private CommandRequestDeviceExecutor commandRequestExecutor;
    private IDatabaseCache databaseCache;
    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("touScheduleWidget/render.jsp");
        
        List<LiteTOUSchedule> schedules = databaseCache.getAllTOUSchedules();
        mav.addObject("schedules", schedules);

        return mav;
    }
    
    public ModelAndView downloadTouSchedule(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        int scheduleId = WidgetParameterHelper.getRequiredIntParameter(request, "scheduleId");
        
        YCBean ycBean = getYcBean(request, deviceId);
        String command = ycBean.buildTOUScheduleCommand(scheduleId);
        
        Meter meter = meterDao.getForId(deviceId);
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

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Required
    public void setCommandRequestExecutor(CommandRequestDeviceExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }
    
    @Required
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
}
