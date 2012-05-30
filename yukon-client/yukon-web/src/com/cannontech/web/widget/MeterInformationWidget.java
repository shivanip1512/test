package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class MeterInformationWidget extends WidgetControllerBase {

    private MeterDao meterDao = null;
    private RfnDeviceDao rfnDeviceDao;
    private CommandRequestDeviceExecutor commandRequestExecutor;
    private PaoDefinitionDao paoDefinitionDao;

    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Autowired
    public void setRfnDeviceDao(RfnDeviceDao rfnDeviceDao) {
        this.rfnDeviceDao = rfnDeviceDao;
    }
    
    @Required
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
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
        CommandResultHolder result = commandRequestExecutor.execute(meter, "ping", DeviceRequestType.METER_INFORMATION_PING_COMMAND, user);
        mav.addObject("isRead", true);

        mav.addObject("result", result);
        
        return mav;
    }
    
    private ModelAndView getMeterInformationModelAndView(int deviceId) {
        
        ModelAndView mav = new ModelAndView("meterInformationWidget/render.jsp");
        
        Meter meter = meterDao.getForId(deviceId);
        PaoType paoType = meter.getPaoType();
                
        /* Show CARRIER settings such as route and physcal address */
        if(paoType.getPaoClass() == PaoClass.CARRIER) {
            mav.addObject("showCarrierSettings", true);
        }
        
        /* Show RFMESH settings such as serial number, model, and manufacturer*/
        if(paoType.getPaoClass() == PaoClass.RFMESH) {
            mav.addObject("showRFMeshSettings", true);
            mav.addObject("rfnMeter", rfnDeviceDao.getMeter(meter));
        }
        
        if(paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS)) {
            mav.addObject("supportsPing", true);
        }
        
        mav.addObject("meter", meter);
        mav.addObject("deviceType", paoType.getPaoTypeName());
        
        return mav;
    }
    
}
