package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.outagestate.OutageState;
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
    private CommandRequestExecutor commandRequestExecutor;

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
            CommandRequestExecutor commandRequestExecutor) {
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
        mav.addObject("state", getOutageState(result));

        mav.addObject("isRead", true);

        mav.addObject("errorsExist", result.isErrorsExist());
        
        mav.addObject("result", result);
        
        return mav;
    }
    
    private OutageState getOutageState(CommandResultHolder result) {
        if( result.getErrors().isEmpty())
            return OutageState.RESTORED;

        for(DeviceErrorDescription deviceError: result.getErrors()) {
            if( deviceError.getErrorCode() == 1 || 
                deviceError.getErrorCode() == 17 || 
                deviceError.getErrorCode() == 74 || 
                deviceError.getErrorCode() == 0 ) {
                return OutageState.RESTORED;
            }
        }
        return OutageState.OUTAGE;
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
