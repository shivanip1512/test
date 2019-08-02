package com.cannontech.web.amr.outageProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.RetryStrategy;
import com.cannontech.common.device.groups.DeviceGroupInUse;
import com.cannontech.common.device.groups.DeviceGroupInUseException;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.monitor.validators.OutageMonitorValidator;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeListType;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@RequestMapping("/outageProcessing/monitorEditor/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.OUTAGE_PROCESSING)
public class OutageMonitorEditorController {

    @Autowired private OutageMonitorDao outageMonitorDao;
    @Autowired private ScheduledGroupRequestExecutionService scheduledGroupRequestExecutionService;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private OutageMonitorService outageMonitorService;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private OutageMonitorValidator validator;

    private static final String CRON_TAG_ID = "outageMonitor";
    private static final Attribute BLINK_COUNT_ATTRIBUTE = BuiltInAttribute.BLINK_COUNT;
    private static final String baseKey = "yukon.web.modules.amr.outageMonitorConfig";
    private Logger log = YukonLogManager.getLogger(OutageMonitorEditorController.class);

    private final int DEFAULT_NUMBER_OF_OUTAGES = 2;
    private final int DEFAULT_TIME_PERIOD = 28;

    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String edit(ModelMap model, HttpServletRequest request, Integer outageMonitorId,
            YukonUserContext userContext) throws Exception, ServletException {
        OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
        model.addAttribute("mode", PageEditMode.EDIT);

        if (model.containsAttribute("outageMonitor")) {
            outageMonitor = (OutageMonitor) model.get("outageMonitor");
        }
        String basePath = deviceGroupService.getFullPath(SystemGroupEnum.OUTAGE);
        model.addAttribute("outageGroupBase", basePath);
        model.addAttribute("outageMonitor", outageMonitor);

        // cron tag setup
        model.addAttribute("cronExpressionTagId", CRON_TAG_ID);
        CronExpressionTagState cronExpressionTagState =
            cronExpressionTagService.parse(outageMonitor.getExpression(), userContext);
        model.addAttribute("cronExpressionTagState", cronExpressionTagState);
        model.addAttribute("outageMonitor", outageMonitor);
        return "outageProcessing/edit.jsp";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(ModelMap model, YukonUserContext userContext) {

        OutageMonitor outageMonitor = null;
        model.addAttribute("mode", PageEditMode.CREATE);
        if (model.containsAttribute("outageMonitor")) {
            outageMonitor = (OutageMonitor) model.get("outageMonitor");
        } else {
            outageMonitor = new OutageMonitor();
            outageMonitor.setTimePeriodDays(DEFAULT_TIME_PERIOD);
            outageMonitor.setNumberOfOutages(DEFAULT_NUMBER_OF_OUTAGES);
            outageMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);
        }
        String basePath = deviceGroupService.getFullPath(SystemGroupEnum.OUTAGE);
        model.addAttribute("outageGroupBase", basePath);
        model.addAttribute("outageMonitor", outageMonitor);

        // cron tag setup
        model.addAttribute("cronExpressionTagId", CRON_TAG_ID);
        CronExpressionTagState cronExpressionTagState =
            cronExpressionTagService.parse(outageMonitor.getExpression(), userContext);
        model.addAttribute("cronExpressionTagState", cronExpressionTagState);
        return "outageProcessing/edit.jsp";
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(@ModelAttribute OutageMonitor outageMonitor, BindingResult result, FlashScope flash,
            RedirectAttributes attrs, YukonUserContext userContext, HttpServletRequest request)
            throws Exception, ServletException {

        boolean isNewMonitor = outageMonitor.getOutageMonitorId() == null ? true : false;

        validator.validate(outageMonitor, result);

        String expression = "";
        if (isNewMonitor && outageMonitor.isScheduleGroupCommand()) {
            try {
                expression = cronExpressionTagService.build(CRON_TAG_ID, request, userContext);
            } catch (Exception e) {
                result.reject(baseKey + ".invalidSchedule");
                expression = null;
            }
        }

        if (result.hasErrors()) {
            /* Editing error. Redirect to edit page with error. */
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            return bindAndForward(outageMonitor, result, attrs);
        }

        // SCHEDULED BLINK COUNT REQUEST JOB
        if (isNewMonitor && outageMonitor.isScheduleGroupCommand()) {
            rolePropertyDao.verifyProperty(YukonRoleProperty.MANAGE_SCHEDULES, userContext.getYukonUser());
            scheduledGroupRequestExecutionService.schedule(outageMonitor.getScheduleName(),
                outageMonitor.getGroupName(), Collections.singleton(BLINK_COUNT_ATTRIBUTE),
                DeviceRequestType.SCHEDULED_GROUP_ATTRIBUTE_READ, expression, userContext,
                RetryStrategy.noRetryStrategy());
        }

        // OUTAGE GROUP
        if (isNewMonitor) {
            // create new group
            outageMonitorService.getOutageGroup(outageMonitor.getName());
        } else {
            // outage group needs new name
            OutageMonitor existingOutageMonitor = outageMonitorDao.getById(outageMonitor.getOutageMonitorId());
            String currentProcessorName = existingOutageMonitor.getName();
            if (!currentProcessorName.equals(outageMonitor.getName())) {
                // try to retrieve group by new name (possible it could exist)
                // if does not exist, get old group, give it new name
                try {
                    deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.OUTAGE, outageMonitor.getName(),
                        false);
                } catch (NotFoundException e) {
                    // ok, it doesn't yet exist
                    StoredDeviceGroup outageGroup = outageMonitorService.getOutageGroup(currentProcessorName);
                    outageGroup.setName(outageMonitor.getName());
                    deviceGroupEditorDao.updateGroup(outageGroup);
                }
            }
        }
        // ENABLE MONITORING
        if (isNewMonitor) {
            outageMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);
        }

        log.debug("Saving outageMonitor: isNewMonitor=" + isNewMonitor + ", outageMonitor=" + outageMonitor.toString());

        if (isNewMonitor) {
            outageMonitorService.create(outageMonitor);
        } else {
            outageMonitorService.update(outageMonitor);
        }
        // redirect to edit page with processor
        return "redirect:/amr/outageProcessing/process/process?outageMonitorId=" + outageMonitor.getOutageMonitorId();
    }

    // DELETE
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public String delete(@ModelAttribute OutageMonitor outageMonitor,ModelMap model, FlashScope flashScope)
            throws Exception, ServletException {
        try {
            model.addAttribute("outageMonitorId", outageMonitor.getOutageMonitorId());
            outageMonitorService.delete(outageMonitor.getOutageMonitorId());
            flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + ".deleted", outageMonitor.getName()));
        } catch (OutageMonitorNotFoundException e) {
            log.error("Could not delete outage monitor : ", e);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".notFound"));
        } catch (DeviceGroupInUseException e) {
            log.error("Could not delete outage monitor : ", e);
            List<MessageSourceResolvable> messages = new ArrayList<>();
            messages.add(new WebMessageSourceResolvable(baseKey + ".delete.error", outageMonitor.getName()));
            for (DeviceGroupInUse deviceGroupInUse : e.getReferences()) {
                MessageSourceResolvable message = new WebMessageSourceResolvable(e.getMessageKey(), deviceGroupInUse.getGroupName(), deviceGroupInUse.getReferenceType(), 
                                                                                   deviceGroupInUse.getName(), deviceGroupInUse.getOwner());
                messages.add(message);
            }
            flashScope.setError(messages, FlashScopeListType.NONE);
        }
        return "redirect:/meter/start";
    }

    // TOGGLE MONITOR EVALUATION SERVICE ENABLED/DISABLED
    @RequestMapping(value = "toggleEnabled", method = RequestMethod.POST)
    public String toggleEnabled(ModelMap model, FlashScope flashScope, int outageMonitorId)
            throws Exception, ServletException {
        
        try {
            model.addAttribute("outageMonitorId", outageMonitorId);
            outageMonitorService.toggleEnabled(outageMonitorId);
            return "redirect:edit";
        } catch (OutageMonitorNotFoundException e) {
            log.error("Could not enable/disable outage monitor :", e);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + ".notFound"));
            return "redirect:/meter/start";
        }
    }

    private String bindAndForward(OutageMonitor outageMonitor, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("outageMonitor", outageMonitor);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.outageMonitor", result);
        if (outageMonitor.getOutageMonitorId() == null) {
            return "redirect:create";
        }
        attrs.addAttribute("outageMonitorId", outageMonitor.getOutageMonitorId());
        return "redirect:edit";
    }
}