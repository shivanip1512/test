package com.cannontech.web.group;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.analysis.tablemodel.GroupCommanderFailureResultsModel;
import com.cannontech.analysis.tablemodel.GroupCommanderSuccessResultsModel;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.NonHiddenDeviceGroupPredicate;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.simplereport.SimpleReportOutputter;
import com.cannontech.simplereport.SimpleYukonReportDefinition;
import com.cannontech.tools.email.DefaultEmailAttachmentMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.ExtTreeNode;

@Controller
@RequestMapping("/commander/*")
public class GroupCommanderController implements InitializingBean {

    private Logger log = YukonLogManager.getLogger(GroupCommanderController.class);

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
    
    // available meter commands
    private List<LiteCommand> meterCommands;

    public void afterPropertiesSet() {
        this.meterCommands = commandDao.getAllCommandsByCategory(DeviceTypes.STRING_MCT_410IL[0]);
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
        
        // make a device group hierarchy starting at root, only modifiable groups
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupService.getDeviceGroupHierarchy(rootGroup, new NonHiddenDeviceGroupPredicate());
        
        ExtTreeNode root = DeviceGroupTreeUtils.makeDeviceGroupExtTree(groupHierarchy, "Groups", null);
        
        JSONObject jsonObj = new JSONObject(root.toMap());
        String dataJson = jsonObj.toString();
        model.addAttribute("dataJson", dataJson);
    }
    
    @RequestMapping
    public String executeGroupCommand(HttpServletRequest request, String groupName, String commandString, String emailAddress, YukonUserContext userContext, ModelMap map) throws ServletException {
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(group);
        return executeCollectionCommand(request, deviceCollection, commandString, emailAddress, userContext, map);
    }

    @RequestMapping
    public String executeCollectionCommand(HttpServletRequest request, DeviceCollection deviceCollection, String commandString, final String emailAddress, final YukonUserContext userContext, ModelMap map)
            throws ServletException {
        
        // get host string
        final URL hostURL = ServletUtil.getHostURL(request);

        boolean error = false;
        if (StringUtils.isBlank(commandString)) {
            error = true;
            map.addAttribute("errorMsg", "You must enter a valid command");
        } else if (!commandAuthorizationService.isAuthorized(userContext.getYukonUser(), commandString)) {
        	error = true;
            map.addAttribute("errorMsg", "User is not authorized to execute this command.");
        }
        
        if (error) {
            return "redirect:/spring/group/commander/groupProcessing";
        }
        
        SimpleCallback<GroupCommandResult> callback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult result) {
                ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion");
                int successCount = result.getResultHolder().getResultStrings().size();
                resolvableTemplate.addData("successCount", successCount);
                int failureCount = result.getResultHolder().getErrors().size();
                resolvableTemplate.addData("failureCount", failureCount);
                int total = failureCount + successCount;
                resolvableTemplate.addData("percentSuccess", (float)successCount *100 / total);
                resolvableTemplate.addData("command", result.getCommand());
                resolvableTemplate.addData("resultKey", result.getKey());
                
                CommandCompletionAlert commandCompletionAlert = new CommandCompletionAlert(new Date(), resolvableTemplate);
                
                alertService.add(commandCompletionAlert);
                
                sendEmail(emailAddress, hostURL, result, userContext);

            }

        };

        try {
            String key = 
                groupCommandExecutor.execute(deviceCollection, commandString, callback, userContext.getYukonUser());
            map.addAttribute("resultKey", key);
            return "redirect:/spring/group/commander/resultDetail";
        } catch (PaoAuthorizationException e) {
            log.warn("Unable to execute, putting error in model", e);
            map.addAttribute("error", "Unable to execute specified commands");
        }

        // must have been an error, try again
        map.addAttribute("command", commandString);
        return "redirect:/spring/group/commander/groupProcessing";
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
    public void resultDetail(String resultKey, ModelMap map) {
        
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        
        map.addAttribute("result", result);
    }
    
    @RequestMapping({"errorsList", "successList"})
    public void results(String resultKey, ModelMap map) {
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        
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
}
