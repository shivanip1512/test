package com.cannontech.web.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.dao.ScheduledOneTimeJobDao;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.dao.YukonJobDao;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

@CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
@Controller
@RequestMapping("/jobsscheduler/*")
public class JobsController {
    @Autowired private JobManager jobManager;
    @Autowired private YukonJobDao yukonJobDao;
    @Autowired private JobStatusDao jobStatusDao;
    @Autowired private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
    @Autowired private ScheduledOneTimeJobDao scheduledOneTimeJobDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private Map<RepeatingSortBy, Comparator<YukonJob>> repeatingSorters;
    private Map<OneTimeSortBy, Comparator<YukonJob>> oneTimeSorters;
    private Map<StatusSortBy, Comparator<JobStatus<YukonJob>>> statusSorters;

    @PostConstruct
    public void initialize() {
        Builder<RepeatingSortBy, Comparator<YukonJob>> builder = ImmutableMap.builder();
        builder.put(RepeatingSortBy.jobName, getJobNameComparator());
        builder.put(RepeatingSortBy.beanName, getBeanNameComparator());
        builder.put(RepeatingSortBy.username, getUserNameComparator());
        builder.put(RepeatingSortBy.cronString, getCronStringComparator());
        builder.put(RepeatingSortBy.enabled, getEnabledComparator());
        repeatingSorters = builder.build();
        
        Builder<OneTimeSortBy, Comparator<YukonJob>> oneTimeBuilder = ImmutableMap.builder();
        oneTimeBuilder.put(OneTimeSortBy.jobName, getJobNameComparator());
        oneTimeBuilder.put(OneTimeSortBy.beanName, getBeanNameComparator());
        oneTimeBuilder.put(OneTimeSortBy.username, getUserNameComparator());
        oneTimeBuilder.put(OneTimeSortBy.scheduledStart, getScheduledStartComparator());
        oneTimeBuilder.put(OneTimeSortBy.enabled, getEnabledComparator());
        oneTimeSorters = oneTimeBuilder.build();
        
        Builder<StatusSortBy, Comparator<JobStatus<YukonJob>>> statusBuilder = ImmutableMap.builder();
        statusBuilder.put(StatusSortBy.jobName, getStatusJobNameComparator());
        statusBuilder.put(StatusSortBy.start, getStartComparator());
        statusBuilder.put(StatusSortBy.stop, getStopComparator());
        statusBuilder.put(StatusSortBy.state, getStateComparator());
        statusBuilder.put(StatusSortBy.enabled, getStatusEnabledComparator());
        statusBuilder.put(StatusSortBy.errorMessage, getErrorMessageComparator());
        statusSorters = statusBuilder.build();
    }

    @RequestMapping("active")
    public String active(ModelMap model) {
        Collection<YukonJob> scheduledJobs = jobManager.getCurrentlyExecuting();
        Collection<JobStatus<YukonJob>> jobList = new ArrayList<JobStatus<YukonJob>>();
        for(YukonJob job : scheduledJobs){
            JobStatus<YukonJob> status = jobStatusDao.findLatestStatusByJobId(job.getId());
            jobList.add(status);
        }
        model.addAttribute("activeJobs", jobList);
        return "jobs/active.jsp";
    }
    
    @RequestMapping("status")
    public String status(ModelMap model, YukonUserContext userContext, @DefaultSort(dir=Direction.desc, sort="start") SortingParameters sorting, 
                         @DefaultItemsPerPage(10) PagingParameters paging) {
        getStatusJobs(model, sorting, paging, userContext);
        return "jobs/status.jsp";
    }
    
    @RequestMapping("all")
    public String all(ModelMap model, YukonUserContext userContext, @DefaultSort(dir=Direction.asc, sort="jobName") SortingParameters sorting, 
                      @DefaultItemsPerPage(10) PagingParameters paging) {

        getOneTimeJobs(model, sorting, paging, userContext);
        getRepeatingJobs(model, sorting, paging, userContext);
        return "jobs/all.jsp";
    }
    
    private void getStatusJobs(ModelMap model, SortingParameters sorting, PagingParameters paging, YukonUserContext userContext) {
        Date now = new Date();
        Date oneWeekAgo = DateUtils.addWeeks(now, -1);
        
        List<JobStatus<YukonJob>> allStatus = jobStatusDao.getAllStatus(now, oneWeekAgo);
        
        SearchResults<JobStatus<YukonJob>> searchResult = new SearchResults<JobStatus<YukonJob>>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, allStatus.size());

        StatusSortBy sortBy = StatusSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<JobStatus<YukonJob>>itemList = Lists.newArrayList(allStatus);
        Comparator<JobStatus<YukonJob>> comparator = statusSorters.get(sortBy);
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);
        
        List<SortableColumn> columns = new ArrayList<SortableColumn>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (StatusSortBy column : StatusSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
        }
        model.addAttribute("columns", columns);
        
        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, allStatus.size());
        searchResult.setResultList(itemList);
        model.addAttribute("jobStatusList", searchResult);
    }
    
    private void getRepeatingJobs(ModelMap model, SortingParameters sorting, PagingParameters paging, YukonUserContext userContext){
        Set<ScheduledRepeatingJob> allRepeating = scheduledRepeatingJobDao.getAll();
        SearchResults<ScheduledRepeatingJob> searchResult = new SearchResults<ScheduledRepeatingJob>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, allRepeating.size());

        RepeatingSortBy sortBy = RepeatingSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<ScheduledRepeatingJob>itemList = Lists.newArrayList(allRepeating);
        Comparator<YukonJob> comparator = repeatingSorters.get(sortBy);
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);
        
        List<SortableColumn> columns = new ArrayList<SortableColumn>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (RepeatingSortBy column : RepeatingSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
        }
        model.addAttribute("columns", columns);
        
        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, allRepeating.size());
        searchResult.setResultList(itemList);
        model.addAttribute("allRepeating", searchResult);
    }
    
    private void getOneTimeJobs(ModelMap model, SortingParameters sorting, PagingParameters paging, YukonUserContext userContext){
        Set<ScheduledOneTimeJob> allOneTime = scheduledOneTimeJobDao.getAll();
        
        SearchResults<ScheduledOneTimeJob> searchResult = new SearchResults<ScheduledOneTimeJob>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, allOneTime.size());

        OneTimeSortBy sortBy = OneTimeSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<ScheduledOneTimeJob>itemList = Lists.newArrayList(allOneTime);
        Comparator<YukonJob> comparator = oneTimeSorters.get(sortBy);
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);
        
        List<SortableColumn> columns = new ArrayList<SortableColumn>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (OneTimeSortBy column : OneTimeSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
        }
        model.addAttribute("columns", columns);
        
        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, allOneTime.size());
        searchResult.setResultList(itemList);
        model.addAttribute("allOneTime", searchResult);
    }
    
    @RequestMapping("oneTimeJobs")
    public String oneTimeJobs(ModelMap model, YukonUserContext userContext, @DefaultSort(dir=Direction.asc, sort="jobName") SortingParameters sorting, 
                      @DefaultItemsPerPage(10) PagingParameters paging) {
        getOneTimeJobs(model, sorting, paging, userContext);
        return "jobs/onetimejobs.jsp";
    }
    
    @RequestMapping("repeatingJobs")
    public String repeatingJobs(ModelMap model, YukonUserContext userContext, @DefaultSort(dir=Direction.asc, sort="jobName") SortingParameters sorting, 
                      @DefaultItemsPerPage(10) PagingParameters paging) {
        getRepeatingJobs(model, sorting, paging, userContext);
        return "jobs/repeatingjobs.jsp";
    }
    
    @RequestMapping("statusJobs")
    public String statusJobs(ModelMap model, YukonUserContext userContext, @DefaultSort(dir=Direction.desc, sort="start") SortingParameters sorting, 
                      @DefaultItemsPerPage(10) PagingParameters paging) {
        getStatusJobs(model, sorting, paging, userContext);
        return "jobs/statusjobs.jsp";
    }
    
    @RequestMapping("toggleState")
    public void toggleState(HttpServletResponse resp, int jobId) {
        YukonJob job = yukonJobDao.getById(jobId);
        if (job.isDisabled())
            jobManager.enableJob(job);
        else
            jobManager.disableJob(job);
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @RequestMapping("abortJob")
    public String abortJob(int jobId) throws ServletException {
        YukonJob job = yukonJobDao.getById(jobId);
        jobManager.abortJob(job);
        
        return "redirect:active";
    }

    
    public enum RepeatingSortBy implements DisplayableEnum {
        
        jobName,
        beanName,
        username,
        cronString,
        enabled;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.adminSetup.jobsscheduler.all.column." + name();
        }
    }
    
    public enum OneTimeSortBy implements DisplayableEnum {
        
        jobName,
        beanName,
        username,
        scheduledStart,
        enabled;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.adminSetup.jobsscheduler.all.column." + name();
        }
    }
    
    public enum StatusSortBy implements DisplayableEnum {
        
        jobName,
        start,
        stop,
        state,
        enabled,
        errorMessage;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.adminSetup.jobsscheduler.status.column." + name();
        }
    }
    
    private Comparator<YukonJob> getJobNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<YukonJob> nameOrdering = normalStringComparer
            .onResultOf(new Function<YukonJob, String>() {
                @Override
                public String apply(YukonJob from) {
                    return from.getJobDefinition().getName();
                }
            });
        return nameOrdering;
    }


    private Comparator<YukonJob> getBeanNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<YukonJob> nameOrdering = normalStringComparer
            .onResultOf(new Function<YukonJob, String>() {
                @Override
                public String apply(YukonJob from) {
                    return from.getBeanName();
                }
            });
        return nameOrdering;
    }
    
    private Comparator<YukonJob> getUserNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<YukonJob> nameOrdering = normalStringComparer
            .onResultOf(new Function<YukonJob, String>() {
                @Override
                public String apply(YukonJob from) {
                    return from.getUserContext().getYukonUser().getUsername();
                }
            });
        return nameOrdering;
    }
    
    private Comparator<YukonJob> getCronStringComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<YukonJob> cronOrdering = normalStringComparer
            .onResultOf(new Function<YukonJob, String>() {
                @Override
                public String apply(YukonJob from) {
                    if(from instanceof ScheduledRepeatingJob){
                        ScheduledRepeatingJob sch = (ScheduledRepeatingJob)from;
                        return sch.getCronString();
                    } else
                        return "";
                }
            });
        return cronOrdering;
    }
    
    private Comparator<YukonJob> getScheduledStartComparator() {
        Ordering<Date> normalDateComparer = Ordering.natural();
        Ordering<YukonJob> dateOrdering = normalDateComparer
            .onResultOf(new Function<YukonJob, Date>() {
                @Override
                public Date apply(YukonJob from) {
                    if(from instanceof ScheduledOneTimeJob){
                        ScheduledOneTimeJob sch = (ScheduledOneTimeJob)from;
                        return sch.getStartTime();
                    } else
                        return null;
                }
            });
        return dateOrdering;
    }
    
    private Comparator<YukonJob> getEnabledComparator() {
        Ordering<Boolean> normalBooleanComparer = Ordering.natural();
        Ordering<YukonJob> booleanOrdering = normalBooleanComparer
            .onResultOf(new Function<YukonJob, Boolean>() {
                @Override
                public Boolean apply(YukonJob from) {
                    return from.isDisabled();
                }
            });
        return booleanOrdering;
    }
    
    private Comparator<JobStatus<YukonJob>> getStatusJobNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<JobStatus<YukonJob>> nameOrdering = normalStringComparer
            .onResultOf(new Function<JobStatus<YukonJob>, String>() {
                @Override
                public String apply(JobStatus<YukonJob> from) {
                    return from.getJob().getJobDefinition().getName();
                }
            });
        return nameOrdering;
    }
    
    private Comparator<JobStatus<YukonJob>> getStartComparator() {
        Ordering<Date> normalDateComparer = Ordering.natural();
        Ordering<JobStatus<YukonJob>> dateOrdering = normalDateComparer
            .onResultOf(new Function<JobStatus<YukonJob>, Date>() {
                @Override
                public Date apply(JobStatus<YukonJob> from) {
                    return from.getStartTime();
                }
            });
        return dateOrdering;
    }
    
    private Comparator<JobStatus<YukonJob>> getStopComparator() {
        Ordering<Date> normalDateComparer = Ordering.natural();
        Ordering<JobStatus<YukonJob>> dateOrdering = normalDateComparer
            .onResultOf(new Function<JobStatus<YukonJob>, Date>() {
                @Override
                public Date apply(JobStatus<YukonJob> from) {
                    return from.getStopTime();
                }
            });
        return dateOrdering;
    }
    
    private Comparator<JobStatus<YukonJob>> getStateComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<JobStatus<YukonJob>> stateOrdering = normalStringComparer
            .onResultOf(new Function<JobStatus<YukonJob>, String>() {
                @Override
                public String apply(JobStatus<YukonJob> from) {
                    return from.getJobRunStatus().toString();
                }
            });
        return stateOrdering;
    }
    
    private Comparator<JobStatus<YukonJob>> getStatusEnabledComparator() {
        Ordering<Boolean> normalBooleanComparer = Ordering.natural();
        Ordering<JobStatus<YukonJob>> disabledOrdering = normalBooleanComparer
            .onResultOf(new Function<JobStatus<YukonJob>, Boolean>() {
                @Override
                public Boolean apply(JobStatus<YukonJob> from) {
                    return from.getJob().isDisabled();
                }
            });
        return disabledOrdering;
    }
    
    private Comparator<JobStatus<YukonJob>> getErrorMessageComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<JobStatus<YukonJob>> errorOrdering = normalStringComparer
            .onResultOf(new Function<JobStatus<YukonJob>, String>() {
                @Override
                public String apply(JobStatus<YukonJob> from) {
                    return from.getMessage();
                }
            });
        return errorOrdering;
    }
}
