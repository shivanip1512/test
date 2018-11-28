package com.cannontech.web.common.dashboard.widget.validator;

import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

import com.cannontech.amr.monitors.PointMonitor;
import com.cannontech.amr.monitors.impl.MonitorCacheServiceImpl;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.common.dashboard.exception.WidgetMissingParameterException;
import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;
import com.cannontech.web.common.dashboard.model.WidgetInputType;
import com.cannontech.web.search.lucene.index.MonitorTypePrefixEnum;

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
     * This method takes monitorId with prefix. The first character determines the Monitor Type
     * and the rest part consist of monitorId.
     */
    private boolean isValidMonitorId(String monitorId) {
        if (!StringUtils.isNumeric(monitorId)) {
            return false;
        }
        PointMonitor pointMonitor = null;
        MonitorTypePrefixEnum monitor = MonitorTypePrefixEnum.fromName(monitorId.substring(0, 1));
        int id = Integer.parseInt(monitorId.substring(1));
        switch (monitor) {
        case DEVICE_DATA_MONITOR:
            pointMonitor = monitorCacheService.getDeviceMonitor(id);
            break;
        case OUTAGE_MONITOR:
            pointMonitor = monitorCacheService.getOutageMonitor(id);
            break;
        case TAMPER_FLAG_MONITOR:
            pointMonitor = monitorCacheService.getTamperFlagMonitor(id);
            break;
        case STATUS_POINT_MONITOR:
            pointMonitor = monitorCacheService.getStatusPointMonitor(id);
            break;
        case PORTER_RESPONSE_MONITOR:
            pointMonitor = monitorCacheService.getPorterResponseMonitor(id);
            break;
        case VALIDATION_MONITOR:
            pointMonitor = monitorCacheService.getValidationMonitor(id);
            break;
        }
        return pointMonitor != null;
    }

}
