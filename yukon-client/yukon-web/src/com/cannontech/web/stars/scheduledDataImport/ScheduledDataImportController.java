package com.cannontech.web.stars.scheduledDataImport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.scheduledFileImport.ScheduleImportHistoryEntry;
import com.cannontech.common.scheduledFileImport.ScheduledDataImport;
import com.cannontech.common.scheduledFileImport.ScheduledImportType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.schedule.ScheduleControllerHelper;
import com.cannontech.web.common.scheduledDataImportTask.ScheduledDataImportTaskJobWrapperFactory.ScheduledDataImportTaskJobWrapper;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.scheduledDataImport.service.ScheduledDataImportService;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("scheduledDataImport")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT)
public class ScheduledDataImportController {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private CronExpressionTagService cronExpressionTagService;
    @Autowired private ScheduledDataImportService scheduledDataImportService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ScheduleControllerHelper scheduleControllerHelper;
    
    private static final String baseKey = "yukon.web.modules.operator.scheduledDataImportDetail.";
    
    private Validator scheduledDataImportValidator = new SimpleValidator<ScheduledDataImport>(ScheduledDataImport.class) {

        @Override
        protected void doValidation(ScheduledDataImport importData, Errors errors) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "scheduleName", "yukon.web.error.isBlank");
            if (!errors.hasFieldErrors("scheduleName")) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "scheduleName", importData.getScheduleName(), 100);
            }
        }
    };

    @GetMapping("list")
    public String list(YukonUserContext userContext, ModelMap model,
            @DefaultItemsPerPage(10) PagingParameters paging,
            @DefaultSort(dir=Direction.desc, sort="NEXT_RUN") SortingParameters sorting) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        Direction dir = sorting.getDirection();
        Column sortBy = Column.valueOf(sorting.getSort());
        for (Column column : Column.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        SearchResults<ScheduledDataImportTaskJobWrapper> filterResult =
                            scheduledDataImportService.getScheduledFileImportJobData(paging, sorting, userContext);
        model.addAttribute("filterResult", filterResult);
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
        try {
            String cronExpression = cronExpressionTagService.build("cronExpression", request, userContext);
            scheduledImportData.setCronString(cronExpression);
        } catch (CronException e) {
            result.rejectValue("cronString", "yukon.common.invalidCron");
        }
        
        scheduledDataImportValidator.validate(scheduledImportData, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setError(messages);
            return bindAndForward(scheduledImportData, result, redirectAttributes);
        }
        
        YukonJob savedJob = null;
        if (scheduledImportData.getJobId() == null) {
            savedJob = scheduledDataImportService.scheduleDataImport(scheduledImportData, userContext);
        } else {
            JobState currentJobState = scheduleControllerHelper.getJobState(scheduledImportData.getJobId());
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
        JobState currentJobState = scheduleControllerHelper.getJobState(jobId);
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

    @GetMapping("toggleJob")
    public @ResponseBody Map<String, Object> toggleEnabled(HttpServletRequest request) throws ServletException {
        int toggleJobId = ServletRequestUtils.getRequiredIntParameter(request, "toggleJobId");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        return scheduleControllerHelper.toggleJob(toggleJobId, userContext);
    }

    @GetMapping("startJob")
    public @ResponseBody Map<String, Object> startJob(HttpServletRequest request) throws ServletException {
        int jobId = ServletRequestUtils.getRequiredIntParameter(request, "toggleJobId");
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String cronExpression = null;
        try {
            cronExpression = cronExpressionTagService.build(Integer.toString(jobId), request, userContext);
        } catch (CronException e) {}
        return scheduleControllerHelper.startJob(jobId, cronExpression, userContext);
    }

    @GetMapping("{jobGroupId}/viewHistory")
    public String viewHistory(ModelMap model, @PathVariable int jobGroupId) throws ServletException {
        List<ScheduleImportHistoryEntry> results = scheduledDataImportService.getImportHistory(jobGroupId);
        model.addAttribute("results", results);
        model.addAttribute("jobGroupId", jobGroupId);
        return "/scheduledDataImport/history.jsp";
    }

    @GetMapping("downloadArchivedFile")
    public String downloadArchivedFile(HttpServletResponse response, FlashScope flashScope, String originalFileName,
            String fileName, boolean isSuccessFile, String failedFilePath, int jobGroupId) {
        String baseKey = "yukon.web.modules.operator.fileImportHistory.";
        try (InputStream input = new FileInputStream(scheduledDataImportService.downloadArchivedFile(fileName, isSuccessFile, failedFilePath));
             OutputStream output = response.getOutputStream();) {
            // set up the response
            response.setContentType("text/csv");
            String safeFileName = ServletUtil.makeWindowsSafeFileName(originalFileName != null ? originalFileName : fileName);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + safeFileName + "\"");
            // pull data from the file and push it to the browser
            IOUtils.copy(input, output);
        } catch (FileNotFoundException e) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + "fileNotFound"));
            return "redirect:/stars/scheduledDataImport/" + jobGroupId + "/viewHistory";
        } catch (Exception e) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + "ioError"));
            return "redirect:/stars/scheduledDataImport/" + jobGroupId + "/viewHistory";
        }
        return null;
    }

    private void setupModel(ModelMap model, ScheduledDataImport scheduledDataImport,
            YukonUserContext userContext) {
        if (model.containsAttribute("scheduledImportData")) {
            scheduledDataImport = (ScheduledDataImport) model.get("scheduledImportData");
        }
        
        String globalImportPaths = globalSettingDao.getString(GlobalSettingType.SCHEDULE_PARAMETERS_IMPORT_PATH);
        model.addAttribute("importPaths", StringUtils.parseStringsForList(globalImportPaths));

        String globalErrorFileOutputPaths =
            globalSettingDao.getString(GlobalSettingType.SCHEDULE_PARAMETERS_EXPORT_PATH);
        model.addAttribute("errorFileOutputPaths", StringUtils.parseStringsForList(globalErrorFileOutputPaths));

        CronExpressionTagState cronExpressionTagState =
            cronExpressionTagService.parse(scheduledDataImport.getCronString(), userContext);
        if (model.containsAttribute("org.springframework.validation.BindingResult.scheduledImportData")) {
            BindingResult bindingResult = (BindingResult) model.get("org.springframework.validation.BindingResult.scheduledImportData");
            if (bindingResult.hasFieldErrors("cronString")) {
                cronExpressionTagState.setCronTagStyleType(CronTagStyleType.CUSTOM);
                model.addAttribute("invalidCronString", true);
            }
        }
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
