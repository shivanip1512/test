package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.DeviceReadJobLogDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.widget.support.WidgetControllerBase;

public class PointValueDisplayWidget extends WidgetControllerBase {
    private PointDao pointDao;
    private DeviceReadJobLogDao deviceReadJobLogDao;

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return new ModelAndView();
    }

    
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    public void setDeviceReadJobLogDao(DeviceReadJobLogDao deviceReadJobLogDao) {
        this.deviceReadJobLogDao = deviceReadJobLogDao;
    }
}
