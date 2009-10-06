package com.cannontech.web.common.scheduledGroupRequestExecution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionService;
import com.cannontech.amr.scheduledGroupRequestExecution.tasks.ScheduledGroupRequestExecutionTask;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.AttributeNameComparator;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.device.commands.RetryStrategy;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.jobs.dao.ScheduledRepeatingJobDao;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagService;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputUtil;
import com.cannontech.web.util.AttributeSelectorHelperService;

public class ScheduledGroupRequestExecutionController extends MultiActionController implements InitializingBean {

	private CommandDao commandDao;
	private ScheduledGroupRequestExecutionService scheduledGroupRequestExecutionService;
	private RolePropertyDao rolePropertyDao;
	private PaoCommandAuthorizationService paoCommandAuthorizationService;
	private JobManager jobManager;
	private YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition;
	private CronExpressionTagService cronExpressionTagService;
	private AttributeSelectorHelperService attributeSelectorHelperService;
	private ScheduledRepeatingJobDao scheduledRepeatingJobDao;
	
	private List<LiteCommand> meterCommands;
	
	// HOME
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("scheduledGroupRequestExecution/home.jsp");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		// pass-through
		String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
		String requestType = ServletRequestUtils.getStringParameter(request, "requestType", null);
		Set<? extends Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
		String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue", null);
		String commandString = ServletRequestUtils.getStringParameter(request, "commandString", null);
		String scheduleName = ServletRequestUtils.getStringParameter(request, "scheduleName", null);
		String cronExpression = ServletRequestUtils.getStringParameter(request, "cronExpression", null);
		boolean retryCheckbox = ServletRequestUtils.getBooleanParameter(request, "retryCheckbox", false);
		String retryCount = ServletRequestUtils.getStringParameter(request, "retryCount", null);
		String stopRetryAfterHoursCount = ServletRequestUtils.getStringParameter(request, "stopRetryAfterHoursCount", null);
		String turnOffQueuingAfterRetryCount = ServletRequestUtils.getStringParameter(request, "turnOffQueuingAfterRetryCount", null);
		String deviceGroupName = ServletRequestUtils.getStringParameter(request, "deviceGroupName", null);
		
		// edit existing job
		int editJobId = ServletRequestUtils.getIntParameter(request, "editJobId", 0);
		boolean editMode = false;
		if (editJobId > 0) {
			editMode = true;
		}
		
		// set the parameters to those of the current job if they are not already present in the request (which may exist due to error pass-through)
		ScheduledRepeatingJob existingJob = null;
		if (editMode) {
			
			existingJob = jobManager.getRepeatingJob(editJobId);
			ScheduledGroupRequestExecutionTask existingTask = new ScheduledGroupRequestExecutionTask();
			InputRoot inputRoot = scheduledGroupRequestExecutionJobDefinition.getInputs();
	        InputUtil.applyProperties(inputRoot, existingTask, existingJob.getJobProperties());
	        
			if (requestType == null) {
				requestType = existingTask.getCommandRequestExecutionType().name();
			}
			if (selectedAttributes.size() == 0 && existingTask.getAttributes() != null) {
			    selectedAttributes = existingTask.getAttributes();
			}
			if (commandString == null && existingTask.getCommand() != null) {
				
				commandString = existingTask.getCommand();
				
				// attempt to select the command to default the drop down to (commandSelectValue)
				int maxDifferenceIndex = 0;
				for (LiteCommand liteCommand : meterCommands) {
					
					String thisCommand = liteCommand.getCommand();
					int indexOfDifference = StringUtils.indexOfDifference(commandString, thisCommand);
					if (indexOfDifference == -1 || (indexOfDifference > maxDifferenceIndex && maxDifferenceIndex != -1)) {
						maxDifferenceIndex = indexOfDifference;
						commandSelectValue = liteCommand.getCommand();
					}
				}
			}
			if (scheduleName == null) {
				scheduleName = existingTask.getName();
			}
			if (cronExpression == null) {
				cronExpression = existingJob.getCronString();
			}
			if (retryCount == null && existingTask.getRetryCount() > 0) {
			    retryCount = String.valueOf(existingTask.getRetryCount());
			}
			if (stopRetryAfterHoursCount == null && existingTask.getStopRetryAfterHoursCount() != null && existingTask.getStopRetryAfterHoursCount() > 0) {
			    stopRetryAfterHoursCount = String.valueOf(existingTask.getStopRetryAfterHoursCount());
			}
			if (turnOffQueuingAfterRetryCount == null && existingTask.getTurnOffQueuingAfterRetryCount() != null && existingTask.getTurnOffQueuingAfterRetryCount() > 0) {
			    turnOffQueuingAfterRetryCount = String.valueOf(existingTask.getTurnOffQueuingAfterRetryCount());
			}
			if (!StringUtils.isBlank(retryCount) || !StringUtils.isBlank(stopRetryAfterHoursCount) || !StringUtils.isBlank(turnOffQueuingAfterRetryCount)) {
			    retryCheckbox = true;
			}
			if (deviceGroupName == null) {
			    DeviceGroup existingDeviceGroup = existingTask.getDeviceGroup();
			    if (existingDeviceGroup != null) {
			        deviceGroupName = existingTask.getDeviceGroup().getFullName();
			    }
			}
		}
		
		mav.addObject("editJobId", editJobId);
		if (editMode) {
		    mav.addObject("disabled", existingJob.isDisabled());
		}
		mav.addObject("editMode", editMode);
		mav.addObject("errorMsg", errorMsg);
		mav.addObject("requestType", requestType);
		mav.addObject("selectedAttributes", selectedAttributes);
		mav.addObject("commandSelectValue", commandSelectValue);
		mav.addObject("commandString", commandString);
		mav.addObject("scheduleName", scheduleName);
		mav.addObject("cronExpression", cronExpression);
		mav.addObject("retryCheckbox", retryCheckbox);
		mav.addObject("retryCount", retryCount);
		mav.addObject("stopRetryAfterHoursCount", stopRetryAfterHoursCount);
		mav.addObject("turnOffQueuingAfterRetryCount", turnOffQueuingAfterRetryCount);
		mav.addObject("deviceGroupName", deviceGroupName);
		
		// attributes
		List<BuiltInAttribute> allAttributes = Arrays.asList(BuiltInAttribute.values());
		Collections.sort(allAttributes, new AttributeNameComparator());
		mav.addObject("allAttributes", allAttributes);
		
		// commands
		List<LiteCommand> commands = commandDao.getAuthorizedCommands(meterCommands, userContext.getYukonUser());
		mav.addObject("commands", commands);
		
		// cron
		CronExpressionTagState cronExpressionTagState = cronExpressionTagService.parse(cronExpression, userContext);
        mav.addObject("cronExpressionTagState", cronExpressionTagState);
        
		return mav;
	}
	
	// SCHEDULE
	public ModelAndView schedule(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
	    YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
	    
		// request type
		String requestTypeStr = ServletRequestUtils.getRequiredStringParameter(request, "requestType");
		CommandRequestExecutionType requestType = CommandRequestExecutionType.valueOf(requestTypeStr);
		String formUniqueId = ServletRequestUtils.getRequiredStringParameter(request, "formUniqueId");
		
		// validate cron
		String cronExpression = null;
		try {
			cronExpression = cronExpressionTagService.build(formUniqueId, request, userContext);
		} catch (Exception e) {
			
			cronExpression = null;
			String deviceGroupName = ServletRequestUtils.getStringParameter(request, "deviceGroupName_" + formUniqueId);
			String scheduleName = ServletRequestUtils.getStringParameter(request, "scheduleName");
			Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
			String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue");
			String commandString = ServletRequestUtils.getStringParameter(request, "commandString");
			boolean retryCheckbox = ServletRequestUtils.getBooleanParameter(request, "retryCheckbox", false);
			String retryCountStr = ServletRequestUtils.getStringParameter(request, "retryCount", null);
            String stopRetryAfterHoursCountStr = ServletRequestUtils.getStringParameter(request, "stopRetryAfterHoursCount", null);
            String turnOffQueuingAfterRetryCountStr = ServletRequestUtils.getStringParameter(request, "turnOffQueuingAfterRetryCount", null);
			return makeErrorMav("Invalid Schedule Time.", requestType, scheduleName, cronExpression, makeSelectedAttributeStrsParameter(selectedAttributes), commandSelectValue, commandString, deviceGroupName,
			                    retryCheckbox, retryCountStr, stopRetryAfterHoursCountStr, turnOffQueuingAfterRetryCountStr);
		}
		
		// validate device group
        String deviceGroupName = ServletRequestUtils.getStringParameter(request, "deviceGroupName_" + formUniqueId);
        if (StringUtils.isBlank(deviceGroupName)) {
            
            String scheduleName = ServletRequestUtils.getStringParameter(request, "scheduleName");
            Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
            String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue");
            String commandString = ServletRequestUtils.getStringParameter(request, "commandString");
            boolean retryCheckbox = ServletRequestUtils.getBooleanParameter(request, "retryCheckbox", false);
            String retryCountStr = ServletRequestUtils.getStringParameter(request, "retryCount", null);
            String stopRetryAfterHoursCountStr = ServletRequestUtils.getStringParameter(request, "stopRetryAfterHoursCount", null);
            String turnOffQueuingAfterRetryCountStr = ServletRequestUtils.getStringParameter(request, "turnOffQueuingAfterRetryCount", null);
            return makeErrorMav("No Device Group Selected.", requestType, scheduleName, cronExpression, makeSelectedAttributeStrsParameter(selectedAttributes), commandSelectValue, commandString, null,
                                retryCheckbox, retryCountStr, stopRetryAfterHoursCountStr, turnOffQueuingAfterRetryCountStr);
        }
		
		// validate schedule name
		String scheduleName = ServletRequestUtils.getStringParameter(request, "scheduleName");
		if (StringUtils.isBlank(scheduleName)) {
			
			Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
			String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue");
			String commandString = ServletRequestUtils.getStringParameter(request, "commandString");
			boolean retryCheckbox = ServletRequestUtils.getBooleanParameter(request, "retryCheckbox", false);
            String retryCountStr = ServletRequestUtils.getStringParameter(request, "retryCount", null);
            String stopRetryAfterHoursCountStr = ServletRequestUtils.getStringParameter(request, "stopRetryAfterHoursCount", null);
            String turnOffQueuingAfterRetryCountStr = ServletRequestUtils.getStringParameter(request, "turnOffQueuingAfterRetryCount", null);
			return makeErrorMav("Schedule Must Have Name.", requestType, scheduleName, cronExpression, makeSelectedAttributeStrsParameter(selectedAttributes), commandSelectValue, commandString, deviceGroupName,
			                    retryCheckbox, retryCountStr, stopRetryAfterHoursCountStr, turnOffQueuingAfterRetryCountStr);
		}
		
		// validate retry options
		String retryErrorReason = null;
		
		boolean retryCheckbox = ServletRequestUtils.getBooleanParameter(request, "retryCheckbox", false);
		int retryCount = 0;
		Integer stopRetryAfterHoursCount = null;
		Integer turnOffQueuingAfterRetryCount = null;
		
		if (retryCheckbox) {
		    
		    String retryCountStr = ServletRequestUtils.getStringParameter(request, "retryCount", null);
	        String stopRetryAfterHoursCountStr = ServletRequestUtils.getStringParameter(request, "stopRetryAfterHoursCount", null);
	        String turnOffQueuingAfterRetryCountStr = ServletRequestUtils.getStringParameter(request, "turnOffQueuingAfterRetryCount", null);
	        
	        // parse retry options
	        if (!StringUtils.isBlank(retryCountStr)) {
    	        try {
	                retryCount = Integer.valueOf(retryCountStr);
    	        } catch (NumberFormatException e) {
    	            if (retryErrorReason == null) {
    	                retryErrorReason = "Invalid value: " + retryCountStr;
    	            }
    	        }
	        }
	        if (!StringUtils.isBlank(stopRetryAfterHoursCountStr)) {
    	        try {
	                stopRetryAfterHoursCount = Integer.valueOf(stopRetryAfterHoursCountStr);
                } catch (NumberFormatException e) {
                    if (retryErrorReason == null) {
                        retryErrorReason = "Invalid value: " + stopRetryAfterHoursCountStr;
                    }
                }
	        }
	        if (!StringUtils.isBlank(turnOffQueuingAfterRetryCountStr)) {
                try {
                    turnOffQueuingAfterRetryCount = Integer.valueOf(turnOffQueuingAfterRetryCountStr);
                } catch (NumberFormatException e) {
                    if (retryErrorReason == null) {
                        retryErrorReason = "Invalid value: " + turnOffQueuingAfterRetryCountStr;
                    }
                }
	        }
            
            // additional retry options validation
            if (retryErrorReason == null && retryCount < 1 || retryCount > 10) {
                retryErrorReason = "Retry count must be between 1-10.";
            }
            
            if (retryErrorReason == null && retryCount < 1 && stopRetryAfterHoursCount != null && stopRetryAfterHoursCount > 0) {
                retryErrorReason = "Retry count must have a value of at least 1 for maximum number of hours to retry to have an effect.";
            }
            
            if (retryErrorReason == null && retryCount < 1 && turnOffQueuingAfterRetryCount != null && turnOffQueuingAfterRetryCount > 0) {
                retryErrorReason = "Retry count must have a value of at least 1 for number of retries after which request queuing should be turned off to have an effect.";
            }
            
            if (retryErrorReason == null && turnOffQueuingAfterRetryCount != null && turnOffQueuingAfterRetryCount >= 0 && turnOffQueuingAfterRetryCount > retryCount) {
                retryErrorReason = "Number of retries after which request queuing should be turned off must be greater than or equal to the retry count.";
            }
            
            if (retryErrorReason != null) {
                
                Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
                String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue");
                String commandString = ServletRequestUtils.getStringParameter(request, "commandString");
                return makeErrorMav(retryErrorReason, requestType, scheduleName, cronExpression, makeSelectedAttributeStrsParameter(selectedAttributes), commandSelectValue, commandString, deviceGroupName,
                                    retryCheckbox, retryCountStr, stopRetryAfterHoursCountStr, turnOffQueuingAfterRetryCountStr);
            }
		}
        
        
		
		// edit job
		int editJobId = ServletRequestUtils.getIntParameter(request, "editJobId", 0);
		
		// schedule / edit
		if (requestType.equals(CommandRequestExecutionType.SCHEDULED_GROUP_ATTRIBUTE_READ)) {
			
			return scheduleAttributeRead(request, scheduleName, cronExpression, deviceGroupName, editJobId, retryCheckbox, retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
		
		} else if (requestType.equals(CommandRequestExecutionType.SCHEDULED_GROUP_COMMAND)) {
		
			return scheduleCommand(request, scheduleName, cronExpression, deviceGroupName, editJobId, retryCheckbox, retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
		
		} else {
			throw new IllegalArgumentException("Unsupported requestType: " + requestType);
		}
	}
	
	// SCHEDULE ATTRIBUTE READ
	private ModelAndView scheduleAttributeRead(HttpServletRequest request, String scheduleName, String cronExpression, String deviceGroupName, int editJobId, 
	                                           boolean retryCheckbox, int retryCount, Integer stopRetryAfterHoursCount, Integer turnOffQueuingAfterRetryCount) throws ServletException {
	
		ModelAndView mav = new ModelAndView("redirect:/spring/group/scheduledGroupRequestExecutionResults/detail");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
		// attribute
		Set<Attribute> selectedAttributes = attributeSelectorHelperService.getAttributeSet(request, null, null);
		if (selectedAttributes.size() == 0) {
			return makeErrorMav("No Attribute(s) Selected", CommandRequestExecutionType.SCHEDULED_GROUP_ATTRIBUTE_READ, scheduleName, cronExpression, null, null, null, deviceGroupName,
			                    retryCheckbox, retryCount == 0 ? "" : String.valueOf(retryCount), stopRetryAfterHoursCount == null ? "" : stopRetryAfterHoursCount.toString(), turnOffQueuingAfterRetryCount == null ? "" : turnOffQueuingAfterRetryCount.toString());
		}
		
		// schedule / re-schedule
		YukonJob job = null;
		RetryStrategy retryStrategy = new RetryStrategy(retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
		
		if (editJobId <= 0) {
			job = scheduledGroupRequestExecutionService.schedule(scheduleName, deviceGroupName, selectedAttributes, CommandRequestExecutionType.SCHEDULED_GROUP_ATTRIBUTE_READ, cronExpression, userContext, retryStrategy);
		} else {
			job = scheduledGroupRequestExecutionService.scheduleReplacement(editJobId, scheduleName, deviceGroupName, selectedAttributes, CommandRequestExecutionType.SCHEDULED_GROUP_ATTRIBUTE_READ, cronExpression, userContext, retryStrategy);
		}
		
		mav.addObject("jobId", job.getId());
		return mav;
	}
	
	// SCHEDULE COMMAND
	private ModelAndView scheduleCommand(HttpServletRequest request, String scheduleName, String cronExpression, String deviceGroupName, int editJobId, 
	                                     boolean retryCheckbox, int retryCount, Integer stopRetryAfterHoursCount, Integer turnOffQueuingAfterRetryCount) throws ServletException {
	
		ModelAndView mav = new ModelAndView("redirect:/spring/group/scheduledGroupRequestExecutionResults/detail");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		String errorReason = null;
		
		// validate command string
		String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue");
		String commandString = ServletRequestUtils.getStringParameter(request, "commandString");
		if (StringUtils.isBlank(commandString)) {
		    errorReason = "No Command Selected.";
		} else if (StringUtils.isBlank(commandString)) {
		    errorReason = "You must enter a valid command.";
        } else if (rolePropertyDao.checkProperty(YukonRoleProperty.EXECUTE_MANUAL_COMMAND, userContext.getYukonUser())) {
        	// check that it is authorized
            if (!paoCommandAuthorizationService.isAuthorized(userContext.getYukonUser(), commandString)) {
                errorReason = "You are not authorized to execute this command: " + commandString;
            }
            
        } else {
            // check that the command is in the authorized list (implies that it is authorized)
            List<LiteCommand> commands = commandDao.getAuthorizedCommands(meterCommands, userContext.getYukonUser());
            List<String> commandStrings = new MappingList<LiteCommand, String>(commands, new ObjectMapper<LiteCommand, String>() {
                @Override
                public String map(LiteCommand from) throws ObjectMappingException {
                    return from.getCommand();
                }
            });
            if (!commandStrings.contains(commandString)) {
                errorReason = "You are not authorized to execute this command: " + commandString;
            }
        }
		
		if (errorReason != null) {
		    
		    return makeErrorMav(errorReason, CommandRequestExecutionType.SCHEDULED_GROUP_COMMAND, scheduleName, cronExpression, null, commandSelectValue, commandString, deviceGroupName,
                                retryCheckbox, retryCount == 0 ? "" : String.valueOf(retryCount), stopRetryAfterHoursCount == null ? "" : stopRetryAfterHoursCount.toString(), turnOffQueuingAfterRetryCount == null ? "" : turnOffQueuingAfterRetryCount.toString());
		}
        
        // schedule /  re-schedule
        YukonJob job = null;
        RetryStrategy retryStrategy = new RetryStrategy(retryCount, stopRetryAfterHoursCount, turnOffQueuingAfterRetryCount);
        
        if (editJobId <= 0) {
        	job = scheduledGroupRequestExecutionService.schedule(scheduleName, deviceGroupName, commandString, CommandRequestExecutionType.SCHEDULED_GROUP_COMMAND, cronExpression, userContext, retryStrategy);
        } else {
        	job = scheduledGroupRequestExecutionService.scheduleReplacement(editJobId, scheduleName, deviceGroupName, commandString, CommandRequestExecutionType.SCHEDULED_GROUP_COMMAND, cronExpression, userContext, retryStrategy);
        }
		
        mav.addObject("jobId", job.getId());
        
		return mav;
	}
	
	// ERROR MAV
	private ModelAndView makeErrorMav(String errorMsg, CommandRequestExecutionType requestType, String scheduleName, String cronExpression, String selectedAttributeStrs, String commandSelectValue, String commandString, String deviceGroupName,
	                                  boolean retryCheckbox, String retryCountStr, String stopRetryAfterHoursCountStr, String turnOffQueuingAfterRetryCountStr) {
		
		ModelAndView mav = new ModelAndView("redirect:home");
		mav.addObject("errorMsg", errorMsg);
		mav.addObject("requestType", requestType.name());
		mav.addObject("selectedAttributeStrs", selectedAttributeStrs);
		mav.addObject("commandSelectValue", commandSelectValue);
		mav.addObject("commandString", commandString);
		mav.addObject("scheduleName", scheduleName);
		mav.addObject("cronExpression", cronExpression);
		mav.addObject("retryCheckbox", retryCheckbox);
        mav.addObject("retryCount", retryCountStr);
        mav.addObject("stopRetryAfterHoursCount", stopRetryAfterHoursCountStr);
        mav.addObject("turnOffQueuingAfterRetryCount", turnOffQueuingAfterRetryCountStr);
        mav.addObject("deviceGroupName", deviceGroupName);
		
		return mav;
	}
	
	// TOGGLE JOB ENABLED
    public ModelAndView toggleJobEnabled(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("redirect:home");
        
        int toggleJobId = ServletRequestUtils.getRequiredIntParameter(request, "toggleJobId");
        
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(toggleJobId);
        
        if (job.isDisabled()) {
            jobManager.enableJob(job);
        } else {
            jobManager.disableJob(job);
        }
        
        mav.addObject("editJobId", toggleJobId);
        
        return mav;
        
    }
    
    // DELETE JOB (not really a hard delete, but set Job.Disabled = 'D' to hide it)
    public ModelAndView deleteJob(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/scheduledGroupRequestExecutionResults/jobs");
        
        int deleteJobId = ServletRequestUtils.getRequiredIntParameter(request, "deleteJobId");
        
        ScheduledRepeatingJob job = scheduledRepeatingJobDao.getById(deleteJobId);
        jobManager.deleteJob(job);
        
        return mav;
    }
	
	
	// HELPERS
    private String makeSelectedAttributeStrsParameter(Set<Attribute> attributeParameters) {
        return StringUtils.join(attributeParameters, ",");
    }
	
	// INIT
	public void afterPropertiesSet() {
        
        this.meterCommands = new ArrayList<LiteCommand>();
        
        Vector<LiteDeviceTypeCommand> devTypeCmds = commandDao.getAllDevTypeCommands(DeviceTypes.STRING_MCT_410IL[0]);
        for (LiteDeviceTypeCommand devTypeCmd : devTypeCmds) {
            int cmdId = devTypeCmd.getCommandID();
            LiteCommand liteCmd = commandDao.getCommand(cmdId);
            this.meterCommands.add(liteCmd);
        }
    }
	
	@Autowired
	public void setCommandDao(CommandDao commandDao) {
		this.commandDao = commandDao;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionService(ScheduledGroupRequestExecutionService scheduledGroupRequestExecutionService) {
		this.scheduledGroupRequestExecutionService = scheduledGroupRequestExecutionService;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
	@Autowired
	public void setPaoCommandAuthorizationService(PaoCommandAuthorizationService paoCommandAuthorizationService) {
		this.paoCommandAuthorizationService = paoCommandAuthorizationService;
	}
	
	@Resource(name="jobManager")
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}
	
	@Resource(name="scheduledGroupRequestExecutionJobDefinition")
	public void setScheduledGroupRequestExecutionjobDefinition(
			YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition) {
		this.scheduledGroupRequestExecutionJobDefinition = scheduledGroupRequestExecutionJobDefinition;
	}
	
	@Autowired
	public void setCronExpressionTagService(CronExpressionTagService cronExpressionTagService) {
        this.cronExpressionTagService = cronExpressionTagService;
    }
	
	@Autowired
	public void setAttributeSelectorHelperService(AttributeSelectorHelperService attributeSelectorHelperService) {
        this.attributeSelectorHelperService = attributeSelectorHelperService;
    }
	
	@Autowired
	public void setScheduledRepeatingJobDao(ScheduledRepeatingJobDao scheduledRepeatingJobDao) {
        this.scheduledRepeatingJobDao = scheduledRepeatingJobDao;
    }
}
