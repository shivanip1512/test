package com.cannontech.web.amr.scheduledGroupRequestExecution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagUtils;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputUtil;

public class ScheduledGroupRequestExecutionController extends MultiActionController implements InitializingBean {

	private CommandDao commandDao;
	private ScheduledGroupRequestExecutionService scheduledGroupRequestExecutionService;
	private RolePropertyDao rolePropertyDao;
	private PaoCommandAuthorizationService paoCommandAuthorizationService;
	private JobManager jobManager;
	private YukonJobDefinition<ScheduledGroupRequestExecutionTask> scheduledGroupRequestExecutionJobDefinition;
	
	private List<LiteCommand> meterCommands;
	private static final String CRON_TAG_ID = "scheduledGroupRequestExecution";
	
	// HOME
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("scheduledGroupRequestExecution/home.jsp");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		// pass-through
		String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
		String requestType = ServletRequestUtils.getStringParameter(request, "requestType", null);
		String attribute = ServletRequestUtils.getStringParameter(request, "attribute", null);
		String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue", null);
		String commandString = ServletRequestUtils.getStringParameter(request, "commandString", null);
		String cronExpression = ServletRequestUtils.getStringParameter(request, "cronExpression", null);
		String groupName = ServletRequestUtils.getStringParameter(request, "groupName", null);
		
		// edit existing job
		int editJobId = ServletRequestUtils.getIntParameter(request, "editJobId", 0);
		mav.addObject("editJobId", editJobId);
		
		// set the parameters to those of the current job if they are not already present in the request (which may exist due to error pass-through)
		if (editJobId > 0) {
			
			ScheduledRepeatingJob existingJob = (ScheduledRepeatingJob)jobManager.getJob(editJobId);
			ScheduledGroupRequestExecutionTask existingTask = new ScheduledGroupRequestExecutionTask();
			InputRoot inputRoot = scheduledGroupRequestExecutionJobDefinition.getInputs();
	        InputUtil.applyProperties(inputRoot, existingTask, existingJob.getJobProperties());
	        
			if (requestType == null) {
				requestType = existingTask.getCommandRequestExecutionType().name();
			}
			if (attribute == null && existingTask.getAttribute() != null) {
				attribute = existingTask.getAttribute().getKey();
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
			if (cronExpression == null) {
				cronExpression = existingJob.getCronString();
			}
			if (groupName == null) {
				groupName = existingTask.getGroupName();
			}
		}
		
		
		mav.addObject("errorMsg", errorMsg);
		mav.addObject("requestType", requestType);
		mav.addObject("attribute", attribute);
		mav.addObject("commandSelectValue", commandSelectValue);
		mav.addObject("commandString", commandString);
		mav.addObject("cronExpression", cronExpression);
		mav.addObject("groupName", groupName);
		
		// attributes
		List<BuiltInAttribute> allAttributes = Arrays.asList(BuiltInAttribute.values());
		mav.addObject("allAttributes", allAttributes);
		
		// commands
		List<LiteCommand> commands = commandDao.getAuthorizedCommands(meterCommands, userContext.getYukonUser());
		mav.addObject("commands", commands);
		
		// cron
		CronExpressionTagState cronExpressionTagState = CronExpressionTagUtils.parse(cronExpression);
		mav.addObject("cronExpressionTagId", CRON_TAG_ID);
        mav.addObject("cronExpressionTagState", cronExpressionTagState);
        
		return mav;
	}
	
	// SCHEDULE
	public ModelAndView schedule(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		// request type
		String requestTypeStr = ServletRequestUtils.getRequiredStringParameter(request, "requestType");
		CommandRequestExecutionType requestType = CommandRequestExecutionType.valueOf(requestTypeStr);
		
		// cron
		String cronExpression = CronExpressionTagUtils.build(CRON_TAG_ID, request);
		
		// device group
		String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
		if (StringUtils.isBlank(groupName)) {
			
			String attributeStr = ServletRequestUtils.getStringParameter(request, "attribute");
			String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue");
			String commandString = ServletRequestUtils.getStringParameter(request, "commandString");
			return makeErrorMav("No Device Group Selected", requestType, cronExpression, attributeStr, commandSelectValue, commandString);
		}
		
		// edit job
		int editJobId = ServletRequestUtils.getIntParameter(request, "editJobId", 0);
		
		// schedule / edit
		if (requestType.equals(CommandRequestExecutionType.SCHEDULED_GROUP_ATTRIBUTE_READ)) {
			
			return scheduleAttributeRead(request, response, cronExpression, groupName, editJobId);
		
		} else if (requestType.equals(CommandRequestExecutionType.SCHEDULED_GROUP_COMMAND)) {
		
			return scheduleCommand(request, response, cronExpression, groupName, editJobId);
		
		} else {
			throw new IllegalArgumentException("Unsupported requestType: " + requestType);
		}
	}
	
	// SCHEDULE ATTRIBUTE READ
	private ModelAndView scheduleAttributeRead(HttpServletRequest request, HttpServletResponse response, String cronExpression, String groupName, int editJobId) throws ServletException {
	
		ModelAndView mav = new ModelAndView("redirect:/spring/group/scheduledGroupRequestExecutionResults/detail");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		// attribute
		String attributeStr = ServletRequestUtils.getRequiredStringParameter(request, "attribute");
		BuiltInAttribute attribute = BuiltInAttribute.valueOf(attributeStr);
		
		// schedule / re-schedule
		YukonJob job = null;
		
		if (editJobId <= 0) {
			job = scheduledGroupRequestExecutionService.schedule(groupName, attribute, CommandRequestExecutionType.SCHEDULED_GROUP_ATTRIBUTE_READ, cronExpression, userContext);
		} else {
			job = scheduledGroupRequestExecutionService.scheduleReplacement(editJobId, groupName, attribute, CommandRequestExecutionType.SCHEDULED_GROUP_ATTRIBUTE_READ, cronExpression, userContext);
		}
		
		mav.addObject("jobId", job.getId());
		return mav;
	}
	
	// SCHEDULE COMMAND
	private ModelAndView scheduleCommand(HttpServletRequest request, HttpServletResponse response, String cronExpression, String groupName, int editJobId) throws ServletException {
	
		ModelAndView mav = new ModelAndView("redirect:/spring/group/scheduledGroupRequestExecutionResults/detail");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		
		// command string
		String commandSelectValue = ServletRequestUtils.getStringParameter(request, "commandSelectValue");
		String commandString = ServletRequestUtils.getStringParameter(request, "commandString");
		
        if (StringUtils.isBlank(commandString)) {
        	
        	return makeErrorMav("You must enter a valid command", CommandRequestExecutionType.SCHEDULED_GROUP_COMMAND, cronExpression, null, commandSelectValue, commandString);
        
        } else if (rolePropertyDao.checkProperty(YukonRoleProperty.EXECUTE_MANUAL_COMMAND, userContext.getYukonUser())) {
        
        	// check that it is authorized
            if (!paoCommandAuthorizationService.isAuthorized(userContext.getYukonUser(), commandString)) {
            	
            	return makeErrorMav("You are not authorized to execute that command.", CommandRequestExecutionType.SCHEDULED_GROUP_COMMAND, cronExpression, null, commandSelectValue, commandString);
            }
            
        } else {
        	
            // check that the command is in the authorized list (implies that it is authorized)
            List<LiteCommand> commands = commandDao.getAuthorizedCommands(meterCommands, userContext.getYukonUser());
            List<String> commandStrings = new MappingList<LiteCommand, String>(commands, new ObjectMapper<LiteCommand, String>() {
                @Override
                public String map(LiteCommand from)
                        throws ObjectMappingException {
                    return from.getCommand();
                }
            });
            if (!commandStrings.contains(commandString)) {
            	
            	return makeErrorMav("You are not authorized to execute that command.", CommandRequestExecutionType.SCHEDULED_GROUP_COMMAND, cronExpression, null, commandSelectValue, commandString);
            }
        }
        
        // schedule /  re-schedule
        YukonJob job = null;
        
        if (editJobId <= 0) {
        	job = scheduledGroupRequestExecutionService.schedule(groupName, commandString, CommandRequestExecutionType.SCHEDULED_GROUP_COMMAND, cronExpression, userContext);
        } else {
        	job = scheduledGroupRequestExecutionService.scheduleReplacement(editJobId, groupName, commandString, CommandRequestExecutionType.SCHEDULED_GROUP_COMMAND, cronExpression, userContext);
        }
		
        mav.addObject("jobId", job.getId());
        
		return mav;
	}
	
	// ERROR MAV
	private ModelAndView makeErrorMav(String errorMsg, CommandRequestExecutionType requestType, String cronExpression, String attribute, String commandSelectValue, String commandString) {
		
		ModelAndView mav = new ModelAndView("redirect:home");
		mav.addObject("errorMsg", errorMsg);
		mav.addObject("requestType", requestType);
		mav.addObject("attribute", attribute);
		mav.addObject("commandSelectValue", commandSelectValue);
		mav.addObject("commandString", commandString);
		mav.addObject("cronExpression", cronExpression);
		
		return mav;
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
}
