package com.cannontech.web.amr.outageProcessing;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.OutageMonitorEvaluatorStatus;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagUtils;

public class OutageMonitorEditorController extends MultiActionController {
	
	private OutageMonitorDao outageMonitorDao;
	private ScheduledGroupRequestExecutionService scheduledGroupRequestExecutionService;
	private DeviceGroupEditorDao deviceGroupEditorDao;
	private OutageMonitorService outageMonitorService;
	
	private static final String CRON_TAG_ID = "outageMonitor";
	private static final Attribute BLINK_COUNT_ATTRIBUTE = BuiltInAttribute.BLINK_COUNT;
	private Logger log = YukonLogManager.getLogger(OutageMonitorEditorController.class);
	
	
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception, ServletException {
        
        ModelAndView mav = new ModelAndView("outageProcessing/edit.jsp");
        
        // pass through due to error
        String editError = ServletRequestUtils.getStringParameter(request, "editError", null);
        int outageMonitorId = ServletRequestUtils.getIntParameter(request, "outageMonitorId", 0);
        String name = ServletRequestUtils.getStringParameter(request, "name", null);
        String deviceGroupName = ServletRequestUtils.getStringParameter(request, "deviceGroupName", null);
        int numberOfOutages = ServletRequestUtils.getIntParameter(request, "numberOfOutages", 0);
        int timePeriod = ServletRequestUtils.getIntParameter(request, "timePeriod", 0);
        String expression = ServletRequestUtils.getStringParameter(request, "expression", null);
        boolean scheduleGroupCommand = ServletRequestUtils.getBooleanParameter(request, "scheduleGroupCommand", false);;
        
        OutageMonitor outageMonitor = null;
        try {
        	
	        // existing outage processor
	        if (outageMonitorId > 0) {
	        	
	        	outageMonitor = outageMonitorDao.getById(outageMonitorId);
	        	
	        	// use entered values instead of existing value if present
	        	if (name == null) {
	        		name = outageMonitor.getName();
	        	}
	        	if (deviceGroupName == null) {
	        		deviceGroupName = outageMonitor.getGroupName();
	        	}
	        	if (numberOfOutages <= 0) {
	        		numberOfOutages = outageMonitor.getNumberOfOutages();
	        	}
	        	if (timePeriod <= 0) {
	        		timePeriod = outageMonitor.getTimePeriod();
	        	}
	        	if (outageMonitor.getScheduledCommandJobId() > 0) {
	        		
	        		scheduleGroupCommand = true;
	        		
	        		int scheduledCommandJobId = outageMonitor.getScheduledCommandJobId();
	        		expression = scheduledGroupRequestExecutionService.getCronExpression(scheduledCommandJobId);
	        	}
	        }
	        
        } catch (OutageMonitorNotFoundException e) {
        	mav = new ModelAndView("redirect:edit");
        	mav.addObject("editError", e.getMessage());
        	return mav;
        }
        
	        
        mav.addObject("editError", editError);
        mav.addObject("outageMonitorId", outageMonitorId);
        mav.addObject("name", name);
        mav.addObject("deviceGroupName", deviceGroupName);
        mav.addObject("numberOfOutages", numberOfOutages);
        mav.addObject("timePeriod", timePeriod);
        mav.addObject("scheduleGroupCommand", scheduleGroupCommand);
        
        mav.addObject("outageGroupBase", SystemGroupEnum.OUTAGE_PROCESSING.getFullPath());
        mav.addObject("outageMonitor", outageMonitor);
        
        // cron tag setup
        mav.addObject("cronExpressionTagId", CRON_TAG_ID);
        CronExpressionTagState cronExpressionTagState = CronExpressionTagUtils.parse(expression);
        mav.addObject("cronExpressionTagState", cronExpressionTagState);
        
        return mav;
	}
	
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception, ServletException {
        
        ModelAndView mav = new ModelAndView("redirect:edit");
        
        String editError = null;
        int outageMonitorId = ServletRequestUtils.getIntParameter(request, "outageMonitorId", 0);
        String name = ServletRequestUtils.getStringParameter(request, "name", null);
        String deviceGroupName = ServletRequestUtils.getStringParameter(request, "deviceGroupName", null);
        int numberOfOutages = ServletRequestUtils.getIntParameter(request, "numberOfOutages", 0);
        int timePeriod = ServletRequestUtils.getIntParameter(request, "timePeriod", 0);
        boolean scheduleGroupCommand = ServletRequestUtils.getBooleanParameter(request, "scheduleGroupCommand", false);
        
        String expression = "";
        try {
        	expression = CronExpressionTagUtils.build(CRON_TAG_ID, request);
        } catch (IllegalArgumentException e) {
        	editError = e.getMessage();
        }
        
        // new processor?
        boolean isNewMonitor = true;
    	OutageMonitor outageMonitor;
        try {
        	if (outageMonitorId <= 0) {
        		outageMonitor = new OutageMonitor();
        	} else {
        		outageMonitor = outageMonitorDao.getById(outageMonitorId);
        		isNewMonitor = false;
        	}
        } catch (OutageMonitorNotFoundException e) {
        	mav.addObject("editError", e.getMessage());
        	return mav;
        }
        
        if (StringUtils.isBlank(name)) {
        	editError = "Name required.";
        } else if (CtiUtilities.isContainsInvalidDeviceGroupNameCharacters(name)) {
        	editError = "Name may not contain slashes.";
        } else if (isNewMonitor && outageMonitorDao.processorExistsWithName(name)) { // new monitor, check name
        	editError = "Outage Monitor with name \"" + name + "\" already exists.";
        } else if (!isNewMonitor && !outageMonitor.getName().equals(name) && outageMonitorDao.processorExistsWithName(name)) { // existing monitor, new name, check name
        	editError = "Outage Monitor with name \"" + name + "\" already exists.";
        } else if (StringUtils.isBlank(deviceGroupName)) {
        	editError = "Device group required.";
        } else if (numberOfOutages < 1) {
        	editError= "Number of outages must be greater than or equal to 1.";
        } else if (timePeriod <= 0) {
        	editError = "Time period must be greater than 0.";
        } 
        
        // editError. redirect to edit page with error
        if (editError != null) {
        	
        	mav.addObject("editError", editError);
        	mav.addObject("outageMonitorId", outageMonitorId);
            mav.addObject("name", name);
            mav.addObject("deviceGroupName", deviceGroupName);
            mav.addObject("numberOfOutages", numberOfOutages);
            mav.addObject("timePeriod", timePeriod);
            mav.addObject("scheduleGroupCommand", scheduleGroupCommand);
            mav.addObject("expression", expression);
        	
        // ok. save or update
        } else {
        	
        	YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        	
        	// SCHEDULED BLINK COUNT REQUEST JOB
        	if (scheduleGroupCommand) {
        		
        		// new job
            	if (isNewMonitor || (!isNewMonitor && outageMonitor.getScheduledCommandJobId() <= 0)) {
            		
                	YukonJob newJob = scheduledGroupRequestExecutionService.schedule(deviceGroupName, BLINK_COUNT_ATTRIBUTE, CommandRequestExecutionType.OUTAGE_PROCESSING_BLINK_COUNT_READ, expression, userContext);
                    outageMonitor.setScheduledCommandJobId(newJob.getId());
            	
            	// replacement job
            	} else {
            		
            		int exisitingJobId = outageMonitor.getScheduledCommandJobId();
            		YukonJob replacementJob = scheduledGroupRequestExecutionService.scheduleReplacement(exisitingJobId, deviceGroupName, BLINK_COUNT_ATTRIBUTE, CommandRequestExecutionType.OUTAGE_PROCESSING_BLINK_COUNT_READ, expression, userContext);
            		
            		outageMonitor.setScheduledCommandJobId(replacementJob.getId());
            	}
        		
            // disable scheduled command job if processor has one
        	} else {
        		
        		if (!isNewMonitor) {
        			
        			int scheduleCommandJobId = outageMonitor.getScheduledCommandJobId();
        			if (scheduleCommandJobId > 0) {
        				scheduledGroupRequestExecutionService.disableJob(scheduleCommandJobId);
        				outageMonitor.setScheduledCommandJobId(0);
        			}
        		}
        	}
        	
        	// OUTAGE GROUP
        	if (isNewMonitor) {
        		
        		// create new group
        		outageMonitorService.getOutageGroup(name);
        		
        	} else {
        		
        		// outage group needs new name 
        		String currentProcessorName = outageMonitor.getName();
        		if (!currentProcessorName.equals(name)) {
        			
        			String newOutageGroupName = SystemGroupEnum.OUTAGE_PROCESSING.getFullPath() + name;
        			
        			// try to retrieve group by new name (possible it could exist)
        			// if does not exist, get old group, give it new name
        			try {
        				
        				deviceGroupEditorDao.getStoredGroup(newOutageGroupName, false);
        				
        			} catch (NotFoundException e) {
						
        				// ok, it doesn't yet exist
        				StoredDeviceGroup outageGroup = outageMonitorService.getOutageGroup(currentProcessorName);
    	        		outageGroup.setName(name);
    	        		deviceGroupEditorDao.updateGroup(outageGroup);
					}
        		}
        	}
        	
        	// ENABLE MONITORING
        	if (isNewMonitor) {
        		
        		outageMonitor.setEvaluatorStatus(OutageMonitorEvaluatorStatus.ENABLED);
        	}
        	
        	// finish processor setup, save/update
        	outageMonitor.setName(name);
    		outageMonitor.setGroupName(deviceGroupName);
    		outageMonitor.setNumberOfOutages(numberOfOutages);
    		outageMonitor.setTimePeriod(timePeriod);
    		
    		log.debug("Saving outageMonitor: isNewMonitor=" + isNewMonitor + ", outageMonitor=" + outageMonitor.toString());
    		outageMonitorDao.saveOrUpdate(outageMonitor);
    		outageMonitorId = outageMonitor.getOutageMonitorId();
        	
    		// redirect to edit page with processor
    		mav.addObject("outageMonitorId", outageMonitorId);
        	mav.setViewName("redirect:edit");
        }
        
        return mav;
	}
	
	// DELETE
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception, ServletException {
        
        ModelAndView mav = new ModelAndView("redirect:edit");
        
        int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "deleteOutageMonitorId");
        
        try {
        	outageMonitorService.deleteOutageMonitor(outageMonitorId);
        } catch (OutageMonitorNotFoundException e) {
        	mav.addObject("editError", e.getMessage());
        }
        
        return mav;
	}
	
	// TOGGLE MONITOR EVALUATION SERVICE ENABLED/DISABLED
	public ModelAndView toggleMonitorEvaluationEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception, ServletException {
        
        ModelAndView mav = new ModelAndView("redirect:edit");
        
        int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorId");
        boolean enable = ServletRequestUtils.getRequiredBooleanParameter(request, "enable");
        
        try {
        
	        // get monitor
	        OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
	        
	        // set status
	        OutageMonitorEvaluatorStatus newEvaluatorStatus;
	        if (enable) {
	        	newEvaluatorStatus = OutageMonitorEvaluatorStatus.ENABLED;
	        } else {
	        	newEvaluatorStatus = OutageMonitorEvaluatorStatus.DISABLED;
	        }
	        outageMonitor.setEvaluatorStatus(newEvaluatorStatus);
	        
	        // update
    		outageMonitorDao.saveOrUpdate(outageMonitor);
    		log.debug("Updated outageMonitor evaluator status: status=" + newEvaluatorStatus + ",outageMonitor=" + outageMonitor.toString());
    		
        	mav.addObject("outageMonitorId", outageMonitorId);
	        
        } catch (OutageMonitorNotFoundException e) {
        	mav.addObject("editError", e.getMessage());
        	return mav;
        }
        
        return mav;
	}
	
	@Autowired
	public void setOutageMonitorDao(OutageMonitorDao outageMonitorDao) {
		this.outageMonitorDao = outageMonitorDao;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionService(
			ScheduledGroupRequestExecutionService scheduledGroupRequestExecutionService) {
		this.scheduledGroupRequestExecutionService = scheduledGroupRequestExecutionService;
	}
	
	@Autowired
	public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
		this.deviceGroupEditorDao = deviceGroupEditorDao;
	}
	
	@Autowired
	public void setOutageMonitorService(OutageMonitorService outageMonitorService) {
		this.outageMonitorService = outageMonitorService;
	}
}
