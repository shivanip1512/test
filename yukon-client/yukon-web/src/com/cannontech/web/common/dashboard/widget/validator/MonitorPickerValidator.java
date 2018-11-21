package com.cannontech.web.common.dashboard.widget.validator;

import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

import com.cannontech.amr.monitors.PointMonitor;
import com.cannontech.amr.monitors.impl.MonitorCacheServiceImpl;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.common.dashboard.exception.WidgetMissingParameterException;
import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;
import com.cannontech.web.common.dashboard.model.WidgetInputType;

public class MonitorPickerValidator implements WidgetInputValidator {

    private static final MonitorPickerValidator singletonInstance = new MonitorPickerValidator();
    private MonitorCacheServiceImpl monitorCacheService;

    private MonitorPickerValidator() {
        monitorCacheService = YukonSpringHook.getBean("monitorCacheService", MonitorCacheServiceImpl.class);
    }

    public static MonitorPickerValidator get() {
        return singletonInstance;
    }

    @Override
    public void validate(String inputName, Object inputValue, WidgetInputType type)
            throws WidgetParameterValidationException, WidgetMissingParameterException {
        StringTokenizer tokenizer = new StringTokenizer(inputValue.toString(), ",");
        while (tokenizer.hasMoreElements()) {
            String monitorId = tokenizer.nextElement().toString();
            if (!isValidMonitorId(monitorId)) {
                throw new WidgetParameterValidationException("Valid Monitor Id is required", inputName,
                    "invalidMonitorId", inputValue);
            }
        }
    }

    /**
     * This method takes monitorId with prefix. The first character determine the Monitor Type
     * and the rest part consist of monitorId.
     */
    private boolean isValidMonitorId(String monitorId) {
        if (!StringUtils.isNumeric(monitorId)) {
            return false;
        }
        PointMonitor pointMonitor = null;
        int prefix = Integer.parseInt(monitorId.substring(0, 1));
        int id = Integer.parseInt(monitorId.substring(1));
        switch (prefix) {
        case 1: // Device data Monitor
            pointMonitor = monitorCacheService.getDeviceMonitor(id);
            break;
        case 2: // Outage Monitor
            pointMonitor = monitorCacheService.getOutageMonitor(id);
            break;
        case 3: // Tamper Flat Monitor
            pointMonitor = monitorCacheService.getTamperFlagMonitor(id);
            break;
        case 4: // Status Point Monitor
            pointMonitor = monitorCacheService.getStatusPointMonitor(id);
            break;
        case 5: // Porter Response Monitor
            pointMonitor = monitorCacheService.getPorterResponseMonitor(id);
            break;
        case 6: // Validation Monitor
            pointMonitor = monitorCacheService.getValidationMonitor(id);
            break;
        }
        return pointMonitor != null;
    }

}
