package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
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

    @Autowired private MeterDao meterDao = null;
    @Autowired private CommandRequestDeviceExecutor commandRequestExecutor;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        ModelAndView mav = getMeterInformationModelAndView(deviceId);
        
        return mav;
    }
    
    public ModelAndView ping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");

        YukonMeter meter = meterDao.getForId(deviceId);
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        CommandResultHolder result = commandRequestExecutor.execute(meter, "ping", DeviceRequestType.METER_INFORMATION_PING_COMMAND, user);
        mav.addObject("isRead", true);

        mav.addObject("result", result);
        
        return mav;
    }
    
    private ModelAndView getMeterInformationModelAndView(int deviceId) {
        
        ModelAndView mav = new ModelAndView("meterInformationWidget/render.jsp");
        YukonMeter meter = meterDao.getForId(deviceId);
        mav.addObject("meter", meter);
        
        if (meter instanceof RfnMeter) {
            /* Show RFMESH settings such as serial number, model, and manufacturer*/
            mav.addObject("showRFMeshSettings", true);
        } else if (meter instanceof PlcMeter) {
            /* Show PLC settings such as route and physcal address */
            mav.addObject("showCarrierSettings", true);
        }
        
        if(paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS)) {
            mav.addObject("supportsPing", true);
        }
        return mav;
    }
    
}
