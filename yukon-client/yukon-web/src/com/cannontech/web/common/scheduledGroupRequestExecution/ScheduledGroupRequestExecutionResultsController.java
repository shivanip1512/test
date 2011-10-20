package com.cannontech.web.common.scheduledGroupRequestExecution;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoOnetimeFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory.ScheduledGroupRequestExecutionJobWrapper;
import com.cannontech.web.common.scheduledGroupRequestExecution.model.ScheduledJobsFilterBackingBean;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

@CheckRole(YukonRole.SCHEDULER)
@Controller
@RequestMapping("/scheduledGroupRequestExecution/results/*")
public class ScheduledGroupRequestExecutionResultsController extends MultiActionController {
	
	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	private ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory;
	private RolePropertyDao rolePropertyDao;
	private DatePropertyEditorFactory datePropertyEditorFactory;
	private JobManager jobManager;
	private Map<String, Comparator<ScheduledGroupRequestExecutionJobWrapper>> sorters;
	
    @PostConstruct
    public void initialize() {
        Builder<String, Comparator<ScheduledGroupRequestExecutionJobWrapper>> builder = ImmutableMap.builder();
        builder.put("NAME", ScheduledGroupRequestExecutionJobWrapperFactory.getJobNameComparator());
        builder.put("DEVICE_GROUP", ScheduledGroupRequestExecutionJobWrapperFactory.getDeviceGroupNameComparator());
        builder.put("ATTR_OR_COMM", ScheduledGroupRequestExecutionJobWrapperFactory.getAttributeCommandComparator());
        builder.put("SCHED_DESC", ScheduledGroupRequestExecutionJobWrapperFactory.getRunScheduleComparator());
        builder.put("NEXT_RUN", ScheduledGroupRequestExecutionJobWrapperFactory.getNextRunComparator());
        builder.put("ENABLED_STATUS", ScheduledGroupRequestExecutionJobWrapperFactory.getStatusComparator());
        sorters = builder.build();
    }
	
    @RequestMapping
    public String jobs(@ModelAttribute("scheduledJobsFilterBackingBean") ScheduledJobsFilterBackingBean backingBean,
                     BindingResult bindingResult, FlashScope flashScope,
                     YukonUserContext userContext, ModelMap model) {
        setupCommonJobsPageAttributes(bindingResult, flashScope, userContext, model);
        setupJobsFromFilter(backingBean, userContext, model);
		return "scheduledGroupRequestExecution/results/jobs.jsp";
	}

    @RequestMapping
    public String clear() {
        return "redirect:jobs";
    }
    
    @RequestMapping
    public void toggleEnabled(HttpServletResponse response, YukonUserContext userContext, int jobId)
            throws IOException {
        rolePropertyDao.verifyProperty(YukonRoleProperty.MANAGE_SCHEDULES, userContext.getYukonUser());
        YukonJob job = jobManager.getJob(jobId);
        JSONObject object = new JSONObject();
        boolean enabled = false;
        if (job.isDisabled()) {
            jobManager.enableJob(job);
            enabled = true;
        } else {
            jobManager.disableJob(job);
        }
        object.put("jobEnabled", enabled);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(object.toString());
        out.close();
    }
	
    @RequestMapping
    public String detail(int jobId, ModelMap model, YukonUserContext userContext) {
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(jobId);
        ScheduledGroupRequestExecutionJobWrapper jobWrapper = scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(job, null, null, userContext);
        model.addAttribute("jobWrapper", jobWrapper);
        
        CommandRequestExecution lastCre = scheduledGroupRequestExecutionDao.findLatestCommandRequestExecutionForJobId(jobId, null);
        model.addAttribute("lastCre", lastCre);
        
        boolean canManage = rolePropertyDao.checkProperty(YukonRoleProperty.MANAGE_SCHEDULES, userContext.getYukonUser());
        model.addAttribute("canManage", canManage);
        return "scheduledGroupRequestExecution/results/jobDetail.jsp";
	}

	// Note: this url should only be hit if it is know the job has a last cre, no protection for null lastCre here
    @RequestMapping
	public String viewLastRun(int jobId, ModelMap model) {
		CommandRequestExecution lastCre = scheduledGroupRequestExecutionDao.findLatestCommandRequestExecutionForJobId(jobId, null);
		model.addAttribute("commandRequestExecutionId", lastCre.getId());
		return "redirect:/spring/common/commandRequestExecutionResults/detail";
	}

    private void setupJobsFromFilter(ScheduledJobsFilterBackingBean backingBean,
                                     YukonUserContext userContext, ModelMap model) {
        List<ScheduledRepeatingJob> jobs =
              scheduledGroupRequestExecutionDao.getJobs(0,
                                                        backingBean.getFromDate(),
                                                        backingBean.getToDate(),
                                                        backingBean.getTypeFilterAsList(),
                                                        backingBean.getStatusFilter(),
                                                        backingBean.getExcludePendingFilter(),
                                                        backingBean.getIncludeOnetimeFilter(),
                                                        false);

        SearchResult<ScheduledGroupRequestExecutionJobWrapper> filterResult =
              new SearchResult<ScheduledGroupRequestExecutionJobWrapper>();
        filterResult.setBounds(backingBean.getStartIndex(),
                               backingBean.getItemsPerPage(),
                               jobs.size());

        List<ScheduledGroupRequestExecutionJobWrapper> jobWrappers = Lists.newArrayListWithCapacity(jobs.size());
        for (ScheduledRepeatingJob job : jobs) {
            ScheduledGroupRequestExecutionJobWrapper jobWrapper =
                  scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(job, 
                           backingBean.getFromDate(), backingBean.getToDate(), userContext);
            jobWrappers.add(jobWrapper);
        }

        if (backingBean.getSort() != null) {
            if (backingBean.getDescending()) {
                Collections.sort(jobWrappers, Collections.reverseOrder(sorters.get(backingBean.getSort())));
            } else {
                Collections.sort(jobWrappers, sorters.get(backingBean.getSort()));
            }
        } else {
            Collections.sort(jobWrappers, ScheduledGroupRequestExecutionJobWrapperFactory.getNextRunAndNameComparator());
        }

        jobWrappers = jobWrappers.subList(backingBean.getStartIndex(),
                                          backingBean.getStartIndex() + 
                                          backingBean.getItemsPerPage() > jobs.size() ?
                                          jobs.size() : backingBean.getStartIndex() +
                                          backingBean.getItemsPerPage());
        filterResult.setResultList(jobWrappers);
        model.addAttribute("filterResult", filterResult);
        model.addAttribute("backingBean", backingBean);
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
        EnumPropertyEditor.register(binder, ScheduleGroupRequestExecutionDaoOnetimeFilter.class);
        EnumPropertyEditor.register(binder, DeviceRequestType.class);
    }
    
	@Autowired
	public void setScheduledGroupRequestExecutionDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
	
	@Autowired
	public void setScheduledRepeatingJobDao(
			ScheduledRepeatingJobDao scheduledRepeatingJobDao) {
		this.scheduledRepeatingJobDao = scheduledRepeatingJobDao;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionJobWrapperFactory(ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory) {
		this.scheduledGroupRequestExecutionJobWrapperFactory = scheduledGroupRequestExecutionJobWrapperFactory;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
	@Autowired
	public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
	
	@Autowired
	public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }
}