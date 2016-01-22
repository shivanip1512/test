package com.cannontech.web.widget;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@RequestMapping("/touScheduleWidget/*")
public class TouScheduleWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private MeterDao meterDao;
    @Autowired private CommandRequestDeviceExecutor commandRequestExecutor;
    @Autowired private CommandDao commandDao;
    @Autowired private IDatabaseCache databaseCache;
    
    @Autowired
    public TouScheduleWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        String checkRole = YukonRole.METERING.name();
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, Integer deviceId) throws Exception {

        LiteYukonPAObject litePao = databaseCache.getAllPaosMap().get(deviceId);
        if (DeviceTypesFuncs.isMCT4XX(litePao.getPaoType())) {
            List<LiteTOUSchedule> schedules = databaseCache.getAllTOUSchedules();
            model.put("schedules", schedules);
        }
        return "touScheduleWidget/render.jsp";
    }
    
    @RequestMapping("downloadTouSchedule")
    public String downloadTouSchedule(ModelMap model, Integer deviceId, Integer scheduleId, YukonUserContext userContext) throws Exception {
        
        String command = commandDao.buildTOUScheduleCommand(scheduleId);
        PlcMeter meter = meterDao.getPlcMeterForId(deviceId);
        CommandResultHolder result = commandRequestExecutor.execute(meter, command, DeviceRequestType.TOU_SCHEDULE_COMMAND, userContext.getYukonUser());
        model.put("result", result);
        
        return "common/meterReadingsResult.jsp";
    }
}