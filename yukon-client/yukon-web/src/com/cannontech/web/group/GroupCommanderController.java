package com.cannontech.web.group;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.analysis.tablemodel.GroupCommanderFailureResultsModel;
import com.cannontech.analysis.tablemodel.GroupCommanderSuccessResultsModel;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.ResultResultExpiredException;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.roles.application.CommanderRole;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.simplereport.SimpleReportOutputter;
import com.cannontech.simplereport.SimpleYukonReportDefinition;
import com.cannontech.tools.email.DefaultEmailAttachmentMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;

@Controller
@RequestMapping("/commander/*")
@CheckRoleProperty(YukonRoleProperty.GROUP_COMMANDER)
public class GroupCommanderController implements InitializingBean {

    private Logger log = YukonLogManager.getLogger(GroupCommanderController.class);

    private RolePropertyDao rolePropertyDao;
    private CommandDao commandDao;
    private GroupCommandExecutor groupCommandExecutor;
    private AlertService alertService;
    private DeviceGroupService deviceGroupService;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    
    private SimpleYukonReportDefinition<GroupCommanderSuccessResultsModel> successReportDefinition;
    private SimpleYukonReportDefinition<GroupCommanderFailureResultsModel> failureReportDefinition;
    private SimpleReportOutputter simpleReportOutputter;
    
    private EmailService emailService;
    
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    private PaoCommandAuthorizationService commandAuthorizationService;
    
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    // available meter commands
    private List<LiteCommand> meterCommands;

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
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }

    @Autowired
	public void setCommandAuthorizationService(
			PaoCommandAuthorizationService commandAuthorizationService) {
		this.commandAuthorizationService = commandAuthorizationService;
	}
    
    @Autowired
    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }

    @RequestMapping
    public void collectionProcessing(DeviceCollection deviceCollection, LiteYukonUser user, ModelMap model)
    throws ServletException {
        
        List<LiteCommand> commands = commandDao.getAuthorizedCommands(meterCommands, user);
        model.addAttribute("commands", commands);
        
        model.addAttribute("deviceCollection", deviceCollection);
        
    }
    
    @RequestMapping
    public void groupProcessing(LiteYukonUser user, ModelMap model)
            throws ServletException {

        List<LiteCommand> commands = commandDao.getAuthorizedCommands(meterCommands, user);
        model.addAttribute("commands", commands);
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String executeGroupCommand(HttpServletRequest request, String groupName, String commandSelectValue, String commandString, String emailAddress, YukonUserContext userContext, ModelMap map) throws ServletException {
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(group);
        boolean success = doCollectionCommand(request, deviceCollection, commandSelectValue, commandString, emailAddress, groupName, userContext, map);
        if (success) {
            return "redirect:resultDetail";
        } else {
            return "redirect:groupProcessing";
        }
    }

    @RequestMapping(method=RequestMethod.POST)
    public String executeCollectionCommand(HttpServletRequest request, DeviceCollection deviceCollection, String commandSelectValue, String commandString, final String emailAddress, final YukonUserContext userContext, ModelMap map)
    throws ServletException {
        boolean success = doCollectionCommand(request, deviceCollection, commandSelectValue, commandString, emailAddress, null, userContext, map);
        if (success) {
            return "redirect:resultDetail";
        } else {
            map.addAllAttributes(deviceCollection.getCollectionParameters());
            return "redirect:collectionProcessing";
        }

    }

    public boolean doCollectionCommand(HttpServletRequest request, 
            DeviceCollection deviceCollection, 
            String commandSelectValue, 
            String commandString, 
            final String emailAddress, 
            String groupName,
            final YukonUserContext userContext, 
            ModelMap map)
            throws ServletException {
        
        // get host string
        final URL hostURL = ServletUtil.getHostURL(request);

        boolean error = false;
        if (StringUtils.isBlank(commandString)) {
            error = true;
            addErrorStateToMap(map, "No Command Selected", commandSelectValue, commandString, groupName);
        } else if (rolePropertyDao.checkProperty(YukonRoleProperty.EXECUTE_MANUAL_COMMAND, userContext.getYukonUser())) {
            // check that it is authorized
            if (!commandAuthorizationService.isAuthorized(userContext.getYukonUser(), commandString)) {
                error = true;
                addErrorStateToMap(map, "You are not authorized to execute that command.", commandSelectValue, commandString, groupName);
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
                throw NotAuthorizedException.trueProperty(userContext.getYukonUser(), CommanderRole.EXECUTE_MANUAL_COMMAND);
            }
        }
        
        if (error) {
            return false;
        }
        
        SimpleCallback<GroupCommandResult> callback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult result) {
                GroupCommandCompletionAlert commandCompletionAlert = new GroupCommandCompletionAlert(new Date(), result);
                alertService.add(commandCompletionAlert);
                
                sendEmail(emailAddress, hostURL, result, userContext);
            }

        };

        String key = groupCommandExecutor.execute(deviceCollection, commandString, DeviceRequestType.GROUP_COMMAND, callback, userContext.getYukonUser());
        map.addAttribute("resultKey", key);
        return true;
    }

    private void addErrorStateToMap(ModelMap map, String errorMsg, String commandSelectValue, String commandString, String groupName) {
    	
    	map.addAttribute("errorMsg", errorMsg);
        map.addAttribute("commandSelectValue", commandSelectValue);
        map.addAttribute("commandString", commandString);
        map.addAttribute("groupName", groupName);
    }
    
    private void sendEmail(String emailAddress, URL hostUrl, GroupCommandResult result, YukonUserContext userContext) {
        try {
            if (StringUtils.isBlank(emailAddress)) return;
            
            // produce success report
            GroupCommanderSuccessResultsModel successModel = successReportDefinition.createBean();
            successModel.setResultKey(result.getKey());
            successModel.loadData();
            
            ByteArrayOutputStream successReportBytes = new ByteArrayOutputStream();
            simpleReportOutputter.outputPdfReport(successReportDefinition, successModel, successReportBytes, userContext);
            
            // produce failure report
            GroupCommanderFailureResultsModel failureModel = failureReportDefinition.createBean();
            failureModel.setResultKey(result.getKey());
            failureModel.loadData();
            
            ByteArrayOutputStream failureReportBytes = new ByteArrayOutputStream();
            simpleReportOutputter.outputPdfReport(failureReportDefinition, failureModel, failureReportBytes, userContext);
            
            // get subject
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String subject = messageSourceAccessor.getMessage("yukon.web.commander.groupCommander.completionEmail.subject");
            
            // figure out URL
            String resultUrl = hostUrl.toExternalForm() + "/spring/group/commander/resultDetail?resultKey=" + result.getKey();
            
            int successCount = result.getResultHolder().getSuccessfulDevices().size();
            int failureCount = result.getResultHolder().getFailedDevices().size();
            String body = messageSourceAccessor.getMessage("yukon.web.commander.groupCommander.completionEmail.body", resultUrl, successCount, failureCount, result.getCommand());
            
            // build up email
            DefaultEmailAttachmentMessage email = new DefaultEmailAttachmentMessage();
            email.setRecipient(emailAddress);
            email.setBody(body);
            email.setSubject(subject);
            
            // create success attachment
            ByteArrayDataSource successDataSource = new ByteArrayDataSource(successReportBytes.toByteArray(), "application/pdf");
            String successFileName = messageSourceAccessor.getMessage("yukon.web.commander.groupCommander.completionEmail.successFileName");
            successDataSource.setName(successFileName);
            email.addAttachment(successDataSource);
            
            // create failure attachment
            ByteArrayDataSource failureDataSource = new ByteArrayDataSource(failureReportBytes.toByteArray(), "application/pdf");
            String failureFileName = messageSourceAccessor.getMessage("yukon.web.commander.groupCommander.completionEmail.failureFileName");
            failureDataSource.setName(failureFileName);
            email.addAttachment(failureDataSource);
            
            emailService.sendAttachmentMessage(email);
            log.info("Sent results email to " + emailAddress);
            
        } catch (IOException e) {
            log.error("Unable to output reports for scheduled emails", e);
        } catch (MessagingException e) {
            log.error("Unable to send scheduled emails", e);
        } catch (Exception e) {
            log.error("Received unknown error sending email", e);
        }
        
    }

    @RequestMapping
    public void resultList(ModelMap map) {
        
        List<GroupCommandResult> completed = groupCommandExecutor.getCompleted();
        List<GroupCommandResult> pending = groupCommandExecutor.getPending();
        
        ArrayList<GroupCommandResult> allResults = new ArrayList<GroupCommandResult>(completed.size() + pending.size());
        allResults.addAll(completed);
        allResults.addAll(pending);
        
        map.addAttribute("resultList", allResults);
    }
    
    @RequestMapping
    public void resultDetail(String resultKey, ModelMap map) throws ResultResultExpiredException {
        
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        map.addAttribute("result", result);
    }
    
    @RequestMapping
    public ModelAndView cancelCommands(String resultId, YukonUserContext userContext) {
        
        ModelAndView mav = new ModelAndView(new JsonView());
        String errorMsg = "";
        
        try {
            groupCommandExecutor.cancelExecution(resultId, userContext.getYukonUser());
        } catch (Exception e) {
            errorMsg = e.getMessage();
            mav.addObject("errorMsg", errorMsg);
        }
        
        return mav;
    }
    
    @RequestMapping({"errorsList", "successList"})
    public void results(HttpServletRequest request, String resultKey, ModelMap map) {
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);  
		
        /*
         *  RFN devices do not use porter. Replacing the porter error with the error from error-code.xml - Invalid Action for Device Type.
         */
        Map<SimpleDevice, SpecificDeviceErrorDescription> errors = result
				.getCallback().getErrors();
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		if (!errors.isEmpty()) {
			for (SimpleDevice device : errors.keySet()) {
				if (device.getDeviceType().getPaoClass() == PaoClass.RFMESH && errors.get(device).getErrorCode() != 2000) {
					DeviceErrorDescription error = deviceErrorTranslatorDao.translateErrorCode(2000, userContext);
					SpecificDeviceErrorDescription deviceError = new SpecificDeviceErrorDescription(error, null);
					errors.put(device, deviceError);
				}
			}
		}
		
        map.addAttribute("result", result);
    }

    @Resource(name="groupCommanderSuccessResultDefinition")
    public void setSuccessReportDefinition(SimpleYukonReportDefinition<GroupCommanderSuccessResultsModel> successReportDefinition) {
        this.successReportDefinition = successReportDefinition;
    }

    @Resource(name="groupCommanderFailureResultDefinition")
    public void setFailureReportDefinition(SimpleYukonReportDefinition<GroupCommanderFailureResultsModel> failureReportDefinition) {
        this.failureReportDefinition = failureReportDefinition;
    }

    @Autowired
    public void setSimpleReportOutputter(SimpleReportOutputter simpleReportOutputter) {
        this.simpleReportOutputter = simpleReportOutputter;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
}
