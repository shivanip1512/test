package com.cannontech.web.widget;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class VoltageWidget extends WidgetControllerBase {
    
    private AttributeService attributeService = null;
    private MeterReadService meterReadService = null;
    private DeviceDao deviceDao;
    private MeterDao meterDao;
    private CommandRequestDeviceExecutor commandRequestExecutor;
    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("voltageWidget/render.jsp");
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        // device
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        YukonDevice device = deviceDao.getYukonDevice(deviceId);
        mav.addObject("device", device);
        
        // current voltage info
        mav.addObject("voltageAttribute", BuiltInAttribute.VOLTAGE);
        mav.addObject("minimumVoltageAttribute", BuiltInAttribute.MINIMUM_VOLTAGE);
        mav.addObject("maximumVoltageAttribute", BuiltInAttribute.MAXIMUM_VOLTAGE);
        
        // readable?
        Meter meter = meterDao.getForId(deviceId);  
        Set<Attribute> existingAttributes = attributeService.getAllExistingAttributes(meter);
        boolean isReadable = meterReadService.isReadable(meter, existingAttributes, user);
        mav.addObject("isReadable", isReadable);

        return mav;
    }
    
    public ModelAndView readVoltage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        
        Meter meter = meterDao.getForId(deviceId);
        CommandResultHolder result = commandRequestExecutor.execute(meter, "getvalue voltage & getvalue demand", user);
        
        mav.addObject("result", result);
        mav.addObject("successMsg", "Successful Voltage Read");
        
        return mav;
    }

    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Required
    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
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
