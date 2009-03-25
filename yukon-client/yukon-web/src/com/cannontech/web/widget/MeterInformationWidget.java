package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class MeterInformationWidget extends WidgetControllerBase {

    private MeterDao meterDao = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;
    private CommandRequestDeviceExecutor commandRequestExecutor;

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Required
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }
    
    @Required
    public void setCommandRequestExecutor(
            CommandRequestDeviceExecutor commandRequestExecutor) {
        this.commandRequestExecutor = commandRequestExecutor;
    }
    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        ModelAndView mav = getMeterInformationModelAndView(deviceId);
        
        return mav;
    }
    
    public ModelAndView ping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");

        Meter meter = meterDao.getForId(deviceId);
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        CommandResultHolder result = commandRequestExecutor.execute(meter, "ping", user);
        mav.addObject("isRead", true);

        mav.addObject("result", result);
        
        return mav;
    }
    
    private ModelAndView getMeterInformationModelAndView(int deviceId) {
        
        ModelAndView mav = new ModelAndView("meterInformationWidget/render.jsp");
        
        Meter meter = meterDao.getForId(deviceId);
        String type = paoGroupsWrapper.getPAOTypeString(meter.getType());
        
        mav.addObject("meter", meter);
        mav.addObject("deviceType", type);
        
        return mav;
    }
    
}
