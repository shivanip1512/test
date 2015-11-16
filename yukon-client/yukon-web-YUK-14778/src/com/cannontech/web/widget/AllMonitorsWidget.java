package com.cannontech.web.widget;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.google.common.base.Joiner;


@Controller
@RequestMapping("/allMonitorsWidget/*")
@CheckRoleProperty({YukonRoleProperty.DEVICE_DATA_MONITORING,
                    YukonRoleProperty.OUTAGE_PROCESSING,
                    YukonRoleProperty.TAMPER_FLAG_PROCESSING,
                    YukonRoleProperty.STATUS_POINT_MONITORING,
                    YukonRoleProperty.PORTER_RESPONSE_MONITORING,
                    YukonRoleProperty.VALIDATION_ENGINE,
                    })

public class AllMonitorsWidget extends AdvancedWidgetControllerBase {

    @Autowired protected DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired protected OutageMonitorDao outageMonitorDao;
    @Autowired protected TamperFlagMonitorDao tamperFlagMonitorDao;
    @Autowired protected StatusPointMonitorDao statusPointMonitorDao;
    @Autowired protected PorterResponseMonitorDao porterResponseMonitorDao;
    @Autowired protected ValidationMonitorDao validationMonitorDao;

    public AllMonitorsWidget() {
    }
    
    @Autowired
    public AllMonitorsWidget(RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        String checkRole = Joiner.on(" ").join(YukonRoleProperty.DEVICE_DATA_MONITORING.name(),
                                               YukonRoleProperty.OUTAGE_PROCESSING.name(),
                                               YukonRoleProperty.TAMPER_FLAG_PROCESSING.name(),
                                               YukonRoleProperty.STATUS_POINT_MONITORING.name(),
                                               YukonRoleProperty.PORTER_RESPONSE_MONITORING.name(),
                                               YukonRoleProperty.VALIDATION_ENGINE.name());
        
        setIdentityPath("common/deviceIdentity.jsp");
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext context) {
        putMonitorsInModel(model, context);
        return "allMonitorsWidget/render.jsp";
    }

    protected void putMonitorsInModel(ModelMap model, YukonUserContext context) {
        List<DeviceDataMonitor> deviceDataMonitors = deviceDataMonitorDao.getAllMonitors();
        Collections.sort(deviceDataMonitors);
        model.addAttribute("deviceDataMonitors", deviceDataMonitors);
        
        List<OutageMonitor> outageMonitors = outageMonitorDao.getAll();
        Collections.sort(outageMonitors);
        model.addAttribute("outageMonitors", outageMonitors);
        
        List<TamperFlagMonitor> tamperFlagMonitors = tamperFlagMonitorDao.getAll();
        Collections.sort(tamperFlagMonitors);
        model.addAttribute("tamperFlagMonitors", tamperFlagMonitors);
        
        List<StatusPointMonitor> statusPointMonitors = statusPointMonitorDao.getAllStatusPointMonitors();
        Collections.sort(statusPointMonitors);
        model.addAttribute("statusPointMonitors", statusPointMonitors);
        
        List<PorterResponseMonitor> porterResponseMonitors = porterResponseMonitorDao.getAllMonitors();
        Collections.sort(porterResponseMonitors);
        model.addAttribute("porterResponseMonitors", porterResponseMonitors);
        
        List<ValidationMonitor> validationMonitors = validationMonitorDao.getAll();
        Collections.sort(validationMonitors);
        model.addAttribute("validationMonitors", validationMonitors);
        
    }
}
