package com.cannontech.web.stars.dr.operator.inventoryOperations;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.ReadablePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.CommandScheduleEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SimplePeriodFormat;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.CommandScheduleDao;
import com.cannontech.stars.dr.hardware.model.CommandSchedule;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.CommandScheduleWrapper;

@Controller
public class CommandScheduleController {
    
    private CommandScheduleDao commandScheduleDao;
    private CronExpressionTagService cronExpressionTagService;
    private CommandScheduleValidator validator;
    private CommandScheduleEventLogService commandScheduleEventLogService;
    private EnergyCompanyDao energyCompanyDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    /* Command Schedule Edit/Creation page */
    @RequestMapping(value = "/operator/inventory/inventoryOperations/commandSchedule", method = RequestMethod.GET)
    public String commandSchedule(ModelMap modelMap, YukonUserContext userContext, Integer scheduleId) {
        CommandScheduleWrapper schedule = new CommandScheduleWrapper();
        CommandSchedule commandSchedule = new CommandSchedule();
        if (scheduleId > 0) {
            commandSchedule = commandScheduleDao.getById(scheduleId);
            modelMap.addAttribute("mode", PageEditMode.EDIT);
            String cronDescription = cronExpressionTagService.getDescription(commandSchedule.getStartTimeCronString(), userContext);
            modelMap.addAttribute("displayName", cronDescription);
        } else {
            modelMap.addAttribute("mode", PageEditMode.CREATE);
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String displayName = messageSourceAccessor.getMessage("yukon.web.modules.operator.commandSchedule.createSchedule");
            modelMap.addAttribute("displayName", displayName);
        }
        
        schedule.setCommandSchedule(commandSchedule);
        
        CronExpressionTagState cronExpressionTagState = cronExpressionTagService.parse(commandSchedule.getStartTimeCronString(), userContext);
        modelMap.addAttribute("cronExpressionTagState", cronExpressionTagState);
        modelMap.addAttribute("schedule", schedule);
        
        return "operator/inventory/inventoryOperations/commandSchedule.jsp";
    }

    @RequestMapping(value = "/operator/inventory/inventoryOperations/updateSchedule", method = RequestMethod.POST, params={"!delete","!cancel"})
    public String updateSchedule(@ModelAttribute("schedule") CommandScheduleWrapper schedule, BindingResult bindingResult,
                                 ModelMap modelMap, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 String formUniqueId) throws ServletRequestBindingException, IllegalArgumentException, ParseException {
        
        validator.validate(schedule, bindingResult);
        
        int scheduleId = schedule.getCommandSchedule().getCommandScheduleId();
        
        /* Return with errors */
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            CronExpressionTagState cronExpressionTagState = cronExpressionTagService.parse(schedule.getCommandSchedule().getStartTimeCronString(), userContext);
            modelMap.addAttribute("cronExpressionTagState", cronExpressionTagState);
            modelMap.addAttribute("schedule", schedule);
            if(schedule.getCommandSchedule().getCommandScheduleId() > 0) {
                modelMap.addAttribute("mode", PageEditMode.EDIT);
            } else {
                modelMap.addAttribute("mode", PageEditMode.CREATE);
            }
            
            return "operator/inventory/inventoryOperations/commandSchedule.jsp";
        }
        
        /* Set Cron */
        String cronExpression = null;
        cronExpression = cronExpressionTagService.build(formUniqueId, request, userContext);
        schedule.getCommandSchedule().setStartTimeCronString(cronExpression);
        
        /* Set Duration and Delay */
        ReadablePeriod duration = SimplePeriodFormat.getConfigPeriodFormatter().parsePeriod(schedule.getHours() + "h" + schedule.getMinutes() + "m");
        ReadablePeriod delay = SimplePeriodFormat.getConfigPeriodFormatter().parsePeriod(schedule.getSeconds() + "s");
        schedule.getCommandSchedule().setRunPeriod(duration);
        schedule.getCommandSchedule().setDelayPeriod(delay);
        int energyCompanyId = energyCompanyDao.getEnergyCompany(userContext.getYukonUser()).getEnergyCompanyID();
        schedule.getCommandSchedule().setEnergyCompanyId(energyCompanyId);

        commandScheduleDao.save(schedule.getCommandSchedule());
        
        if (scheduleId > 0) {
            commandScheduleEventLogService.scheduleCreated(userContext.getYukonUser(), schedule.getCommandSchedule().getCommandScheduleId());
        } else {
            commandScheduleEventLogService.scheduleUpdated(userContext.getYukonUser(), scheduleId);
        }
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.commandSchedule.created"));
        
        return "redirect:home";
    }
    
    @RequestMapping(value = "/operator/inventory/inventoryOperations/updateSchedule", method = RequestMethod.POST, params="cancel")
    public String cancel(@ModelAttribute("schedule") CommandScheduleWrapper schedule, BindingResult bindingResult,
                                 ModelMap modelMap, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 String formUniqueId) {
        return "redirect:home";
    }
    
    @RequestMapping(value = "/operator/inventory/inventoryOperations/updateSchedule", method = RequestMethod.POST, params="delete")
    public String delete(@ModelAttribute("schedule") CommandScheduleWrapper schedule, BindingResult bindingResult,
                                 ModelMap modelMap, 
                                 YukonUserContext userContext,
                                 HttpServletRequest request,
                                 FlashScope flashScope,
                                 String formUniqueId) {
        
        commandScheduleDao.delete(schedule.getCommandSchedule().getCommandScheduleId());
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.commandSchedule.deleted"));
        
        return "redirect:/spring/stars/operator/inventory/inventoryOperations/home";
    }
    
    @Autowired
    public void setCommandScheduleDao(CommandScheduleDao commandScheduleDao) {
        this.commandScheduleDao = commandScheduleDao;
    }

    @Autowired
    public void setCronExpressionTagService(CronExpressionTagService cronExpressionTagService) {
        this.cronExpressionTagService = cronExpressionTagService;
    }
    
    @Autowired
    public void setCommandScheduleValidator(CommandScheduleValidator validator) {
        this.validator = validator;
    }
    
    @Autowired
    public void setCommandScheduleEventLogService(CommandScheduleEventLogService commandScheduleEventLogService) {
        this.commandScheduleEventLogService = commandScheduleEventLogService;
    }

    @Autowired
    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}