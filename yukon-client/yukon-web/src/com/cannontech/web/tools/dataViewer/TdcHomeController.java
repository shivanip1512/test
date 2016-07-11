package com.cannontech.web.tools.dataViewer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.tdc.dao.DisplayDao;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.common.tdc.model.IDisplay;
import com.cannontech.common.tdc.service.TdcService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

@Controller
@CheckRole(YukonRole.TABULAR_DISPLAY_CONSOLE)
public class TdcHomeController {
    
    @Autowired private DisplayDao displayDao;
    @Autowired private TdcService tdcService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private DeviceBehaviorDao deviceBehaviorDao;
    @Autowired private DataStreamingService dataStreamingService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @RequestMapping("data-viewer")
    public String home(ModelMap model, YukonUserContext userContext) {
             
      MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
      List<BuiltInAttribute> attributes = ImmutableList.of(BuiltInAttribute.KVAR,
          BuiltInAttribute.DEMAND, BuiltInAttribute.DELIVERED_KWH);
      DataStreamingConfig newConfig = new DataStreamingConfig(accessor);
      int i = 0;
      attributes.forEach(a -> {
          DataStreamingAttribute attribute = new DataStreamingAttribute();
          attribute.setAttribute(a);
          attribute.setInterval(7);
          attribute.setAttributeOn(Boolean.TRUE);
          newConfig.addAttribute(attribute);
      });
      System.out.println(newConfig.getName());

      
      //int id = dataStreamingService.saveConfig(newConfig);
      
      DataStreamingConfig config = dataStreamingService.findDataStreamingConfiguration(154);
      System.out.println();
  
     
  
     // List<LiteBehavior> b  = deviceBehaviorDao.getLiteBehaviorsByType(BehaviorType.DATA_STREAMING);
      
    
    /*  BehaviorReport report = new BehaviorReport();
      report.setDeviceId(6);
      report.setStatus(BehaviorReportStatus.PENDING);
      report.setType(BehaviorType.DATA_STREAMING);
      report.setTimestamp(new Instant());
      report.setValues(values);
      int id = deviceBehaviorDao.saveBehaviorReport(report);*/
        return "data-viewer/home.jsp";
    }
    
    
    @RequestMapping("data-viewer/refresh")
    public String refresh(ModelMap model) {
        
        // unacknowledged alarms
        List<DisplayData> unackAlarms = tdcService.getAlarms(false);
        // unacknowledged or active alarms
        List<DisplayData> alarms = tdcService.getAlarms(true);
        model.addAttribute("alarms", alarms);
        Map<DisplayData, DisplayData> mappedAlarms =
            Maps.uniqueIndex(unackAlarms, new Function<DisplayData, DisplayData>() {
                @Override
                public DisplayData apply(DisplayData data) {
                    return data;
                }
            });
        model.addAttribute("unackAlarms", mappedAlarms);
        model.addAttribute("colorStateBoxes", tdcService.getUnackAlarmColorStateBoxes(null, alarms));
        model.addAttribute("allAlarmsDislay", IDisplay.GLOBAL_ALARM_DISPLAY);
        return "data-viewer/alarming.jsp";
    }
     
    
    @RequestMapping(value="data-viewer/acknowledge-all", method=RequestMethod.POST)
    public @ResponseBody Map<String, String> acknowledgeAll(YukonUserContext context) {
        
        int alarms = tdcService.acknowledgeAllAlarms(context.getYukonUser());
        MessageSourceResolvable successMsg =
                new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.ack.success", alarms);
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        return Collections.singletonMap("success", accessor.getMessage(successMsg));
    }
    
    @RequestMapping(value="data-viewer/acknowledge", method=RequestMethod.POST)
    public @ResponseBody Map<String, String> acknowledge(YukonUserContext context, int pointId, int condition) {
        
        tdcService.acknowledgeAlarm(pointId, condition, context.getYukonUser());
        return Collections.singletonMap("success", "success");

    }
}