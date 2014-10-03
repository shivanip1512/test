package com.cannontech.web.widget.meterInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.widget.meterInfo.model.PlcMeterModel;
import com.cannontech.web.widget.meterInfo.model.RfMeterModel;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

/**
 * Widget used to display basic device information
 */
public class MeterInformationWidget extends AdvancedWidgetControllerBase {

    @Autowired private MeterDao meterDao;
    @Autowired private PaoDao paoDao;
    @Autowired private CommandRequestDeviceExecutor cre;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @RequestMapping("render")
    public String render(ModelMap model, int deviceId) {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        model.addAttribute("meter", meter);
        
        if (meter instanceof RfnMeter) {
            /* Show RFMESH settings such as serial number, model, and manufacturer*/
            model.addAttribute("showRFMeshSettings", true);
        } else if (meter instanceof PlcMeter) {
            /* Show PLC settings such as route and physcal address */
            model.addAttribute("showCarrierSettings", true);
        }
        
        if (paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS)) {
            model.addAttribute("supportsPing", true);
        }
        
        return "meterInformationWidget/render.jsp";
    }
    
    @RequestMapping("ping")
    public String ping(ModelMap model, LiteYukonUser user, int deviceId) throws Exception {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        CommandResultHolder result = cre.execute(meter, "ping", DeviceRequestType.METER_INFORMATION_PING_COMMAND, user);
        model.addAttribute("isRead", true);
        model.addAttribute("result", result);
        
        return "common/meterReadingsResult.jsp";
    }
    
    @RequestMapping(value="edit", method=RequestMethod.GET)
    public String edit(ModelMap model, LiteYukonUser user, int deviceId) throws Exception {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        
        if (meter instanceof RfnMeter) {
            model.addAttribute("meter", RfMeterModel.of((RfnMeter)meter));
            model.addAttribute("showRFMeshSettings", true);
        } else if (meter instanceof PlcMeter) {
            model.addAttribute("meter", PlcMeterModel.of((PlcMeter)meter));
            model.addAttribute("showCarrierSettings", true);
            if (meter.getPaoType().isRoutable()) {
                model.addAttribute("routable", true);
                LiteYukonPAObject[] routes = paoDao.getRoutesByType(PaoType.ROUTE_CCU, PaoType.ROUTE_MACRO);
                model.addAttribute("routes", routes);
            }
        }
        
        return "meterInformationWidget/edit.jsp";
    }
    
    @RequestMapping(value="edit-plc", method=RequestMethod.POST)
    public String editPlc(ModelMap model, LiteYukonUser user, @ModelAttribute("meter") PlcMeter meter) {
        
        return null;
    }
    
    @RequestMapping(value="edit-rf", method=RequestMethod.POST)
    public String editRf(ModelMap model, LiteYukonUser user, @ModelAttribute("meter") RfnMeter meter) {
        
        return null;
    }
    
}