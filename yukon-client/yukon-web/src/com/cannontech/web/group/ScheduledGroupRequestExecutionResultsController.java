package com.cannontech.web.group;

import java.beans.PropertyEditor;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory.ScheduledGroupRequestExecutionJobWrapper;
import com.cannontech.web.common.scheduledGroupRequestExecution.model.ScheduledJobsFilter;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

@CheckRole(YukonRole.SCHEDULER)
@Controller
@RequestMapping("/scheduledGroupRequestExecutionResults/*")
public class ScheduledGroupRequestExecutionResultsController {
    
    @Autowired private ScheduledGroupRequestExecutionDao scheduledGreDao;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private JobManager jobManager;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private Map<Column, Comparator<ScheduledGroupRequestExecutionJobWrapper>> sorters;
    
    @PostConstruct
    public void initialize() {
        Builder<Column, Comparator<ScheduledGroupRequestExecutionJobWrapper>> builder = ImmutableMap.builder();
        builder.put(Column.NAME, ScheduledGroupRequestExecutionJobWrapperFactory.getJobNameComparator());
        builder.put(Column.DEVICE_GROUP, ScheduledGroupRequestExecutionJobWrapperFactory.getDeviceGroupNameComparator());
        builder.put(Column.ATTR_OR_COMM, ScheduledGroupRequestExecutionJobWrapperFactory.getAttributeCommandComparator());
        builder.put(Column.SCHED_DESC, ScheduledGroupRequestExecutionJobWrapperFactory.getRunScheduleComparator());
        builder.put(Column.NEXT_RUN, ScheduledGroupRequestExecutionJobWrapperFactory.getNextRunComparator());
        builder.put(Column.ENABLED_STATUS, ScheduledGroupRequestExecutionJobWrapperFactory.getStatusComparator());
        sorters = builder.build();
    }
    
    @RequestMapping("jobs")
    public String jobs(@ModelAttribute("filter") ScheduledJobsFilter filter,
                     BindingResult bindingResult, FlashScope flashScope,
                     YukonUserContext userContext, ModelMap model,
                     @DefaultItemsPerPage(10) PagingParameters paging,
                     @DefaultSort(dir=Direction.desc, sort="NEXT_RUN") SortingParameters sorting) {
        
        setupCommonJobsPageAttributes(bindingResult, flashScope, userContext, model);
        setupJobsFromFilter(filter, userContext, model, paging, sorting);
        
        return "scheduledGroupRequestExecution/results/jobs.jsp";
    }

    @RequestMapping("clear")
    public String clear() {
        return "redirect:jobs";
    }
    
    @RequestMapping("cancelJob")
    public @ResponseBody Map<String, Boolean> cancelJob(int jobId) {
        YukonJob job = jobManager.getJob(jobId);
        boolean isJobStopped = jobManager.abortJob(job);
        return Collections.singletonMap("isJobStopped", isJobStopped);
    }
    
    @RequestMapping("toggleEnabled")
    public @ResponseBody Map<String, Boolean> toggleEnabled(YukonUserContext userContext, int jobId) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.MANAGE_SCHEDULES, userContext.getYukonUser());
        YukonJob job = jobManager.getJob(jobId);
        boolean enabled = false;
        if (job.isDisabled()) {
            jobManager.enableJob(job);
            enabled = true;
        } else {
            jobManager.disableJob(job);
        }
        return Collections.singletonMap("jobEnabled", enabled);
    }

    @RequestMapping("detail")
    public String detail(int jobId, ModelMap model, YukonUserContext userContext) {
        
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        ScheduledGroupRequestExecutionJobWrapper jobWrapper = scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(job, null, null, userContext);
        model.addAttribute("jobWrapper", jobWrapper);
        
        CommandRequestExecution lastCre = scheduledGreDao.findLatestCommandRequestExecutionForJobId(jobId, null);
        model.addAttribute("lastCre", lastCre);
        
        boolean canManage = rolePropertyDao.checkProperty(YukonRoleProperty.MANAGE_SCHEDULES, userContext.getYukonUser());
        model.addAttribute("canManage", canManage);
        
        return "scheduledGroupRequestExecution/results/jobDetail.jsp";
    }

    // Note: this url should only be hit if it is know the job has a last cre, no protection for null lastCre here
    @RequestMapping("viewLastRun")
    public String viewLastRun(int jobId, ModelMap model) {
        CommandRequestExecution lastCre = scheduledGreDao.findLatestCommandRequestExecutionForJobId(jobId, null);
        model.addAttribute("commandRequestExecutionId", lastCre.getId());
        return "redirect:/common/commandRequestExecutionResults/detail";
    }
    
    private void setupJobsFromFilter(ScheduledJobsFilter filter,
                                     YukonUserContext userContext, 
                                     ModelMap model,
                                     PagingParameters paging,
                                     SortingParameters sorting) {
        List<ScheduledRepeatingJob> jobs =
              scheduledGreDao.getJobs(0,
                                    filter.getFromDate(),
                                    filter.getToDate(),
                                    filter.getTypeFilterAsList(),
                                    filter.getStatusFilter(),
                                    filter.getExcludePendingFilter(),
                                    false);
        
        List<ScheduledGroupRequestExecutionJobWrapper> jobWrappers = Lists.newArrayListWithCapacity(jobs.size());
        for (ScheduledRepeatingJob job : jobs) {
            ScheduledGroupRequestExecutionJobWrapper jobWrapper =
                  scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(job, 
                           filter.getFromDate(), filter.getToDate(), userContext);
            jobWrappers.add(jobWrapper);
        }
        
        Direction dir = sorting.getDirection();
        Column sortBy = Column.valueOf(sorting.getSort());
        Comparator<ScheduledGroupRequestExecutionJobWrapper> comparator = sorters.get(sortBy);
        if (dir == Direction.desc) {
            Collections.sort(jobWrappers, Collections.reverseOrder(comparator));
        } else {
            Collections.sort(jobWrappers, comparator);
        }

        SearchResults<ScheduledGroupRequestExecutionJobWrapper> filterResult =
                SearchResults.pageBasedForWholeList(paging, jobWrappers);

        model.addAttribute("filterResult", filterResult);
        model.addAttribute("filter", filter);
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (Column column : Column.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
    }

    private void setupCommonJobsPageAttributes(BindingResult bindingResult, FlashScope flashScope,
                                               YukonUserContext userContext, ModelMap model) {
        boolean hasFilterErrors = false;
        if (bindingResult.hasErrors()) {
            hasFilterErrors = true;
            List<MessageSourceResolvable> messages =
                  YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }
        model.addAttribute("hasFilterErrors", hasFilterErrors);

        List<DeviceRequestType> scheduledCommandRequestExecutionTypes = Lists.newArrayList();
        for (DeviceRequestType commandRequestExecutionType : DeviceRequestType.values()) {
            if (commandRequestExecutionType.isScheduled()) {
                scheduledCommandRequestExecutionTypes.add(commandRequestExecutionType);
            }
        }
        model.addAttribute("scheduledCommandRequestExecutionTypes",
                             scheduledCommandRequestExecutionTypes);
        boolean canManage = rolePropertyDao.checkProperty(YukonRoleProperty.MANAGE_SCHEDULES,
                                                          userContext.getYukonUser());
        model.addAttribute("canManage", canManage);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        PropertyEditor fromDateEditor = datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.START_OF_DAY, userContext);
        PropertyEditor toDateEditor = datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.END_OF_DAY, userContext);
        binder.registerCustomEditor(Date.class, "fromDate", fromDateEditor);
        binder.registerCustomEditor(Date.class, "toDate", toDateEditor);
        EnumPropertyEditor.register(binder, ScheduleGroupRequestExecutionDaoEnabledFilter.class);
        EnumPropertyEditor.register(binder, ScheduleGroupRequestExecutionDaoPendingFilter.class);
        EnumPropertyEditor.register(binder, DeviceRequestType.class);
    }
    
    public enum Column implements DisplayableEnum {
        
        NAME,
        DEVICE_GROUP,
        ATTR_OR_COMM,
        SCHED_DESC,
        NEXT_RUN,
        ENABLED_STATUS;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.schedules.all.executions.tableHeader." + name();
        }
    }
}