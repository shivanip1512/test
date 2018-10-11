package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Logger;
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
import com.cannontech.common.search.result.UltraLightMonitor;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
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
        log.info("start");
        List<Integer> monitorIds = new ArrayList<>();
        model.addAttribute("isSubscribedWidget", true);
        try {
            String monitors = WidgetParameterHelper.getStringParameter(request, "selectMonitors");
            if (monitors == null || monitors.trim().isEmpty()) {
                super.putMonitorsInModel(model, request);
                return;
            }
            Pattern pattern = Pattern.compile(",");
            monitorIds = pattern.splitAsStream(monitors)
                                .map( monitor -> {
                                    if (NumberUtils.isNumber(monitor)) {
                                        return Integer.valueOf (monitor);
                                    } else {
                                        return null;
                                    }})
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
        } catch (ServletRequestBindingException e) {
            log.error(e.getMessage());
        }
        if (monitorIds == null || monitorIds.size() == 0) {
            super.putMonitorsInModel(model, request);
            return;
        }
        List<DeviceDataMonitor> deviceDataMonitors = new ArrayList<>();
        List<OutageMonitor> outageMonitors = new ArrayList<>();
        List<TamperFlagMonitor> tamperFlagMonitors = new ArrayList<>();
        List<StatusPointMonitor> statusPointMonitors = new ArrayList<>();
        List<PorterResponseMonitor> porterResponseMonitors = new ArrayList<>();
        List<ValidationMonitor> validationMonitors = new ArrayList<>();

        Map<Integer, UltraLightMonitor> monitors = Optional.ofNullable(monitorLuceneSearcher.all(null, 0, Integer.MAX_VALUE))
                .map(results -> results.getResultList())
                .orElse(new ArrayList<>())
                .stream()
                .collect(Collectors.toMap(UltraLightMonitor::getId, Function.identity()));
        
        for(int id: monitorIds){
            UltraLightMonitor monitor = monitors.get(id);
            if(monitor == null){
                continue;
            }
            int monitorId = monitor.getSubId();
            switch (monitor.getType()) {
            case "Device Data":
                deviceDataMonitors.add(monitorCacheService.getDeviceMonitor(monitorId));
                break;
            case "Outage":
                outageMonitors.add(monitorCacheService.getOutageMonitor(monitorId));
                break;
            case "Tamper Flag":
                tamperFlagMonitors.add(monitorCacheService.getTamperFlagMonitor(monitorId));
                break;
            case "Status Point":
                statusPointMonitors.add(monitorCacheService.getStatusPointMonitor(monitorId));
                break;
            case "Porter Response":
                porterResponseMonitors.add(monitorCacheService.getPorterResponseMonitor(monitorId));
                break;
            case "Validation":
                validationMonitors.add(monitorCacheService.getValidationMonitor(monitorId));
                break;
            default:
                //Non-monitor subscription
            break;
            }
        }

        if (CollectionUtils.isNotEmpty(deviceDataMonitors)) {
            setSmartNotificationsEvent(SmartNotificationEventType.DEVICE_DATA_MONITOR);
        } else {
            setSmartNotificationsEvent(null);
        }

        sortMonitorsAndAddToModel(deviceDataMonitors, outageMonitors, tamperFlagMonitors, statusPointMonitors,
            porterResponseMonitors, validationMonitors, model);
        log.info("end");
    }
}