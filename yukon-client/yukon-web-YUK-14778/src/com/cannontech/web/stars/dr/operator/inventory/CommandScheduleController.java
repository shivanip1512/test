package com.cannontech.web.stars.dr.operator.inventory;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.CommandScheduleEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.stars.dr.operator.inventory.model.CommandScheduleWrapper;

@Controller
public class CommandScheduleController {
    
    @Autowired private CommandScheduleDao commandScheduleDao;
    @Autowired private CronExpressionTagService cronService;
    @Autowired private CommandScheduleValidator validator;
    @Autowired private CommandScheduleEventLogService logService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private static final String baseKey = "yukon.web.modules.operator.commandSchedule.";
    
    /* Command Schedule Edit/Creation page */
    @RequestMapping(value = "/operator/inventory/commandSchedule", method = RequestMethod.GET)
    public String commandSchedule(ModelMap modelMap, YukonUserContext userContext, Integer scheduleId) {
        
        CommandScheduleWrapper schedule = new CommandScheduleWrapper();
        CommandSchedule commandSchedule = new CommandSchedule();
        if (scheduleId > 0) {
            commandSchedule = commandScheduleDao.getById(scheduleId);
            modelMap.addAttribute("mode", PageEditMode.EDIT);
            String cronDescription = cronService.getDescription(commandSchedule.getStartTimeCronString(), userContext);
            modelMap.addAttribute("displayName", cronDescription);
        } else {
            modelMap.addAttribute("mode", PageEditMode.CREATE);
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String displayName = messageSourceAccessor.getMessage(baseKey + "createSchedule");
            modelMap.addAttribute("displayName", displayName);
        }
        
        schedule.setCommandSchedule(commandSchedule);
        
        CronExpressionTagState cron = cronService.parse(commandSchedule.getStartTimeCronString(), userContext);
        modelMap.addAttribute("cronExpressionTagState", cron);
        modelMap.addAttribute("schedule", schedule);
        
        return "operator/inventory/commandSchedule.jsp";
    }
    
    @RequestMapping(value="/operator/inventory/updateSchedule", method=RequestMethod.POST, params={"!delete","!cancel"})
    public String updateSchedule(@ModelAttribute("schedule") CommandScheduleWrapper schedule, BindingResult bindingResult,
                                 ModelMap modelMap, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 String formUniqueId) 
    throws ServletRequestBindingException, IllegalArgumentException, ParseException {
        
        try {
            /* Set Cron */
            String cronExpression = null;
            cronExpression = cronService.build(formUniqueId, request, userContext);
            schedule.getCommandSchedule().setStartTimeCronString(cronExpression);
        } catch (CronException CronException) {
            MessageSourceResolvable invalidCronMsg = new YukonMessageSourceResolvable("yukon.common.invalidCron");
            flashScope.setError(invalidCronMsg);
            return "operator/inventory/commandSchedule.jsp";
        }
        
        validator.validate(schedule, bindingResult);
        
        int scheduleId = schedule.getCommandSchedule().getCommandScheduleId();
        
        /* Return with errors */
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            String cron = schedule.getCommandSchedule().getStartTimeCronString();
            CronExpressionTagState cronState = cronService.parse(cron, userContext);
            modelMap.addAttribute("cronExpressionTagState", cronState);
            modelMap.addAttribute("schedule", schedule);
            if (schedule.getCommandSchedule().getCommandScheduleId() > 0) {
                modelMap.addAttribute("mode", PageEditMode.EDIT);
            } else {
                modelMap.addAttribute("mode", PageEditMode.CREATE);
            }
            
            return "operator/inventory/commandSchedule.jsp";
        }
        

        
        /* Set Duration and Delay */
        Period duration = Period.hours(schedule.getRunPeriodHours()).withMinutes(schedule.getRunPeriodMinutes());
        Period delay = Period.seconds(schedule.getDelayPeriodSeconds());
        
        schedule.getCommandSchedule().setRunPeriod(duration);
        schedule.getCommandSchedule().setDelayPeriod(delay);
        int energyCompanyId = ServletUtils.getStarsYukonUser(request).getEnergyCompanyID();
        schedule.getCommandSchedule().setEnergyCompanyId(energyCompanyId);

        commandScheduleDao.save(schedule.getCommandSchedule());
        
        if (scheduleId > 0) {
            logService.scheduleCreated(userContext.getYukonUser(), schedule.getCommandSchedule().getCommandScheduleId());
        } else {
            logService.scheduleUpdated(userContext.getYukonUser(), scheduleId);
        }
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "created"));
        
        return "redirect:home";
    }
       
    @RequestMapping(value = "/operator/inventory/updateSchedule", method = RequestMethod.POST, params="delete")
    public String delete(@ModelAttribute("schedule") CommandScheduleWrapper schedule, BindingResult bindingResult,
                                 FlashScope flashScope) {
        
        commandScheduleDao.delete(schedule.getCommandSchedule().getCommandScheduleId());
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "deleted"));
        
        return "redirect:home";
    }
    
    /* INIT BINDER */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver(baseKey + "");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }
    
}