package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.search.result.UltraLightMonitor;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.search.searcher.MonitorLuceneSearcher;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetParameterHelper;


@Controller
@RequestMapping("/subscribedMonitorsWidget/*")
@CheckRoleProperty({YukonRoleProperty.DEVICE_DATA_MONITORING,
    YukonRoleProperty.OUTAGE_PROCESSING,
    YukonRoleProperty.TAMPER_FLAG_PROCESSING,
    YukonRoleProperty.STATUS_POINT_MONITORING,
    YukonRoleProperty.PORTER_RESPONSE_MONITORING,
    YukonRoleProperty.VALIDATION_ENGINE,
    })
public class SubscribedMonitorsWidget extends AllMonitorsWidget {

    @Autowired private MonitorLuceneSearcher monitorLuceneSearcher;
    private static final Logger log = YukonLogManager.getLogger(SubscribedMonitorsWidget.class);

    public SubscribedMonitorsWidget() {
    }
    
    @Autowired
    public SubscribedMonitorsWidget(
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        super(roleAndPropertyDescriptionService);
    }

    @Override
    protected void putMonitorsInModel(ModelMap model, HttpServletRequest request) {
        List<Integer> monitorIds = new ArrayList<Integer>();
        try {
            String monitors = WidgetParameterHelper.getStringParameter(request, "selectMonitors");
            if (monitors == null) {
                super.putMonitorsInModel(model, request);
                model.addAttribute("isSubscribedWidget", true);
                return;
            }
            Pattern pattern = Pattern.compile(",");
            monitorIds = pattern.splitAsStream(monitors)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        } catch (ServletRequestBindingException e) {
            log.error(e.getMessage());
        }
        if (monitorIds == null || monitorIds.size() == 0) {
            super.putMonitorsInModel(model, request);
            model.addAttribute("isSubscribedWidget", true);
            return;
        }
        List<DeviceDataMonitor> deviceDataMonitors = new ArrayList<>();
        List<OutageMonitor> outageMonitors = new ArrayList<>();
        List<TamperFlagMonitor> tamperFlagMonitors = new ArrayList<>();
        List<StatusPointMonitor> statusPointMonitors = new ArrayList<>();
        List<PorterResponseMonitor> porterResponseMonitors = new ArrayList<>();
        List<ValidationMonitor> validationMonitors = new ArrayList<>();

        SearchResults<UltraLightMonitor> sr = monitorLuceneSearcher.all(null, 0, Integer.MAX_VALUE);
        for(UltraLightMonitor monitor: sr.getResultList()) {
            if (monitorIds.contains(monitor.getId())) {
                switch (monitor.getType()) {
                case "Device Data":
                    deviceDataMonitors.add(deviceDataMonitorDao.getMonitorById(monitor.getSubId()));
                    break;
                case "Outage":
                    outageMonitors.add(outageMonitorDao.getById(monitor.getSubId()));
                    break;
                case "Tamper Flag":
                    tamperFlagMonitors.add(tamperFlagMonitorDao.getById(monitor.getSubId()));
                    break;
                case "Status Point":
                    statusPointMonitors.add(statusPointMonitorDao.getStatusPointMonitorById(monitor.getSubId()));
                    break;
                case "Porter Response":
                    porterResponseMonitors.add(porterResponseMonitorDao.getMonitorById(monitor.getSubId()));
                    break;
                case "Validation":
                    validationMonitors.add(validationMonitorDao.getById(monitor.getSubId()));
                    break;
                default:
                    //Non-monitor subscription
                break;
                }
            }
        }

        model.addAttribute("isSubscribedWidget", true);
        model.addAttribute("deviceDataMonitors", deviceDataMonitors);
        model.addAttribute("outageMonitors", outageMonitors);
        model.addAttribute("tamperFlagMonitors", tamperFlagMonitors);
        model.addAttribute("statusPointMonitors", statusPointMonitors);
        model.addAttribute("porterResponseMonitors", porterResponseMonitors);
        model.addAttribute("validationMonitors", validationMonitors);
    }
}