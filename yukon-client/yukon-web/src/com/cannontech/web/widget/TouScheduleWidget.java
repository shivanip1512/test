package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.yc.bean.YCBean;

public class TouScheduleWidget extends WidgetControllerBase {
    
    private MeterDao meterDao;
    private CommandRequestDeviceExecutor commandRequestExecutor;
    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("touScheduleWidget/render.jsp");
        
        List<LiteTOUSchedule> schedules = DefaultDatabaseCache.getInstance().getAllTOUSchedules();
        mav.addObject("schedules", schedules);

        return mav;
    }
    
    public ModelAndView downloadTouSchedule(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        int scheduleId = ServletRequestUtils.getRequiredIntParameter(request, "scheduleId");
        
        YCBean ycBean = getYcBean(request, deviceId);
        String command = ycBean.buildTOUScheduleCommand(scheduleId);
        
        Meter meter = meterDao.getForId(deviceId);
        CommandResultHolder result = commandRequestExecutor.execute(meter, command, user);
        
        mav.addObject("result", result);
        mav.addObject("successMsg", "Successful Download");
        
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
        ycBean.setDeviceID(deviceId);

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
}
