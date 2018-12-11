package com.cannontech.web.stars.scheduledDataImport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.scheduledFileImport.ScheduledImportType;
import com.cannontech.common.scheduledFileImport.ScheduledDataImport;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.scheduledDataImport.service.ScheduledDataImportService;

@Controller
@RequestMapping("scheduledDataImport")
public class ScheduledDataImportController {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private ScheduledDataImportService scheduledDataImportService;
    
    private static final String baseKey = "yukon.web.modules.operator.scheduledDataImportDetail.";
    
    private Validator scheduledDataImportValidator = new SimpleValidator<ScheduledDataImport>(ScheduledDataImport.class) {

        @Override
        protected void doValidation(ScheduledDataImport importData, Errors errors) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "scheduleName", "yukon.web.error.isBlank");
            if (!errors.hasFieldErrors("scheduleName")) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "scheduleName", importData.getScheduleName(), 60);
            }
        }
    };

    @GetMapping("list")
    public String list() {
        return "/scheduledDataImport/list.jsp";
    }

    @GetMapping("create")
    public String create(ModelMap model, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.CREATE);
        ScheduledDataImport importData = new ScheduledDataImport();
        setupModel(model, importData, userContext);
        return "/scheduledDataImport/detail.jsp";
    }

    @PostMapping("save")
    public String save(@ModelAttribute("scheduledImportData") ScheduledDataImport scheduledImportData, BindingResult result,
             FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request, YukonUserContext userContext) {
        scheduledDataImportValidator.validate(scheduledImportData, result);
        
        if (result.hasErrors()) {
            return bindAndForward(scheduledImportData, result, redirectAttributes);
        }
        
        try {
            String cronExpression = cronExpressionTagService.build("cronExpression", request, userContext);
            scheduledImportData.setCronString(cronExpression);
        } catch (CronException e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "invalidCronExpression"));
            return bindAndForward(scheduledImportData, result, redirectAttributes);
        }
        YukonJob savedJob = null;
        if (scheduledImportData.getJobId() == null) {
            savedJob = scheduledDataImportService.scheduleDataImport(scheduledImportData, userContext);
        } else {
            JobState currentJobState = scheduledDataImportService.getJobState(scheduledImportData.getJobId());
            if (currentJobState == JobState.DELETED) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "editDeletedJob.error"));
                return "redirect:/stars/scheduledDataImport/" + scheduledImportData.getJobId() + "/view";
            } else if (currentJobState == JobState.RUNNING) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "editRunningJob.error"));
                return "redirect:/stars/scheduledDataImport/" + scheduledImportData.getJobId() + "/view";
            }
            savedJob = scheduledDataImportService.updateDataImport(scheduledImportData, userContext);
        }
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success", scheduledImportData.getScheduleName()));
        return "redirect:/stars/scheduledDataImport/" + savedJob.getId() + "/view";
    }

    private String bindAndForward(ScheduledDataImport scheduledImportData, BindingResult result,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("scheduledImportData", scheduledImportData);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.scheduledImportData", result);
        if (scheduledImportData.getJobId() == null) {
            return "redirect:/stars/scheduledDataImport/create";
        }
        return "redirect:/stars/scheduledDataImport/" + scheduledImportData.getJobId() + "/edit";
    }

    @GetMapping("{jobId}/edit")
    public String edit(ModelMap model, @PathVariable int jobId, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.EDIT);
        ScheduledDataImport importData = scheduledDataImportService.getJobById(jobId);
        setupModel(model, importData, userContext);
        return "/scheduledDataImport/detail.jsp";
    }

    @GetMapping("{jobId}/view")
    public String view(ModelMap model, @PathVariable int jobId, YukonUserContext userContext) {
        model.addAttribute("mode", PageEditMode.VIEW);
        ScheduledDataImport importData = scheduledDataImportService.getJobById(jobId);
        setupModel(model, importData, userContext);
        return "/scheduledDataImport/detail.jsp";
    }
    
    @DeleteMapping("{jobId}/delete")
    public String delete(@PathVariable int jobId, FlashScope flash) {
        JobState currentJobState = scheduledDataImportService.getJobState(jobId);
        if (currentJobState == JobState.DELETED) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "editDeletedJob.error"));
            return "redirect:/stars/scheduledDataImport/" + jobId + "/view";
        } else if (currentJobState == JobState.RUNNING) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "editRunningJob.error"));
            return "redirect:/stars/scheduledDataImport/" + jobId + "/view";
        }
        
        ScheduledDataImport deleteJob = scheduledDataImportService.deleteJobById(jobId);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", deleteJob.getScheduleName()));
        return "redirect:/stars/scheduledDataImport/list";
    }

    private void setupModel(ModelMap model, ScheduledDataImport scheduledDataImport,
            YukonUserContext userContext) {
        if (model.containsAttribute("scheduledImportData")) {
            scheduledDataImport = (ScheduledDataImport) model.get("scheduledImportData");
        }
        
        String globalImportPaths = globalSettingDao.getString(GlobalSettingType.SCHEDULE_PARAMETERS_IMPORT_PATH);
        StringUtils.parseStringsForList(globalImportPaths);
        model.addAttribute("importPaths", globalImportPaths);

        String globalErrorFileOutputPaths =
            globalSettingDao.getString(GlobalSettingType.SCHEDULE_PARAMETERS_EXPORT_PATH);
        StringUtils.parseStringsForList(globalErrorFileOutputPaths);
        model.addAttribute("errorFileOutputPaths", globalErrorFileOutputPaths);

        CronExpressionTagState cronExpressionTagState =
            cronExpressionTagService.parse(scheduledDataImport.getCronString(), userContext);
        model.addAttribute("cronExpressionTagState", cronExpressionTagState);
        
        scheduledDataImport.setImportType(ScheduledImportType.ASSET_IMPORT);
        model.addAttribute("scheduledImportData", scheduledDataImport);
    }

    public enum Column implements DisplayableEnum {
        NAME,
        TYPE,
        SCHEDULE,
        NEXT_RUN,
        STATUS;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.scheduledDataImportList." + name();
        }
    }
}
