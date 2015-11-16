package com.cannontech.web.group;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.analysis.tablemodel.GroupCommanderFailureResultsModel;
import com.cannontech.analysis.tablemodel.GroupCommanderSuccessResultsModel;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.ResultExpiredException;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.simplereport.SimpleReportOutputter;
import com.cannontech.simplereport.SimpleYukonReportDefinition;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailAttachmentMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/commander/*")
@CheckRoleProperty(YukonRoleProperty.GROUP_COMMANDER)
public class GroupCommanderController {

    private Logger log = YukonLogManager.getLogger(GroupCommanderController.class);

    @Autowired private CommandDao commandDao;
    @Autowired private ContactDao contactDao;
    @Autowired private AlertService alertService;
    @Autowired private EmailService emailService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private GroupCommandExecutor groupCommandExecutor;
    @Autowired private SimpleReportOutputter simpleReportOutputter;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private PaoCommandAuthorizationService commandAuthorizationService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private SimpleYukonReportDefinition<GroupCommanderSuccessResultsModel> successReportDefinition;
    @Resource(name="groupCommanderSuccessResultDefinition")
    public void setSuccessReportDefinition(SimpleYukonReportDefinition<GroupCommanderSuccessResultsModel> successReportDefinition) {
        this.successReportDefinition = successReportDefinition;
    }
    private SimpleYukonReportDefinition<GroupCommanderFailureResultsModel> failureReportDefinition;
    @Resource(name="groupCommanderFailureResultDefinition")
    public void setFailureReportDefinition(SimpleYukonReportDefinition<GroupCommanderFailureResultsModel> failureReportDefinition) {
        this.failureReportDefinition = failureReportDefinition;
    }
    
    // available meter commands
    private List<LiteCommand> meterCommands;

    @PostConstruct
    public void init() {
            
        this.meterCommands = new ArrayList<LiteCommand>();
        
        List<LiteDeviceTypeCommand> devTypeCmds = commandDao.getAllDevTypeCommands(DeviceTypes.STRING_MCT_410IL[0]);
        for (LiteDeviceTypeCommand devTypeCmd : devTypeCmds) {
            int cmdId = devTypeCmd.getCommandId();
            LiteCommand liteCmd = commandDao.getCommand(cmdId);
            this.meterCommands.add(liteCmd);
        }
    }

    @RequestMapping("collectionProcessing")
    public void collectionProcessing(DeviceCollection deviceCollection, YukonUserContext userContext, ModelMap model)
            throws ServletException {

        List<LiteCommand> commands = getCommands(deviceCollection.getDeviceList(), userContext.getYukonUser());
        model.addAttribute("commands", commands);
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("email", contactDao.getUserEmail(userContext.getYukonUser()));
        boolean isSmtpConfigured = StringUtils.isBlank(globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        model.addAttribute("isSmtpConfigured", isSmtpConfigured);
    }
    
    /**
     * Returns all commands that can be executed by the user for the devices selected.
     * Authorization checks:
     *  - If the user can execute a manual command
     *  - If command is visible
     *  - If the user is authorized to execute commands
     * Sort order 
     *  - By PaoType db string 
     *  - By Display Order of the command
     *  
     *  If the duplicate command is found the first command will be kept.
     */
    private List<LiteCommand> getCommands(List<SimpleDevice> devices, LiteYukonUser user) {
        Map<String, LiteCommand> authorized = new LinkedHashMap<String, LiteCommand>();
        if (rolePropertyDao.checkProperty(YukonRoleProperty.EXECUTE_MANUAL_COMMAND, user)) {
            ImmutableMap<Integer, LiteCommand> commands =
                Maps.uniqueIndex(commandDao.getAllCommands(), new Function<LiteCommand, Integer>() {
                    @Override
                    public Integer apply(LiteCommand from) {
                        return from.getLiteID();
                    }
                });

            Set<PaoType> paoTypes = new TreeSet<PaoType>(new Comparator<PaoType>() {
                @Override
                public int compare(PaoType o1, PaoType o2) {
                    return o1.getDbString().compareTo(o2.getDbString());
                }
            });
            paoTypes.addAll(Collections2.transform(devices, SimpleDevice.TO_PAO_TYPE));

            for (PaoType type : paoTypes) {
                List<LiteDeviceTypeCommand> all = commandDao.getAllDevTypeCommands(type.getDbString());
                for (LiteDeviceTypeCommand command : all) {
                    if (command.isVisible()) {
                        LiteCommand liteCommand = commands.get(command.getCommandId());
                        if (!authorized.containsKey(liteCommand.getCommand())
                            && commandAuthorizationService.isAuthorized(user, liteCommand.getCommand())) {
                            authorized.put(liteCommand.getCommand(), liteCommand);
                        }
                    }
                }
            }
        }
        return new ArrayList<LiteCommand>(authorized.values());
    }

    @RequestMapping("groupProcessing")
    public void groupProcessing(YukonUserContext userContext, ModelMap model) throws ServletException {

        List<LiteCommand> commands = commandDao.filterCommandsForUser(meterCommands, userContext.getYukonUser());
        model.addAttribute("commands", commands);
        model.addAttribute("email", contactDao.getUserEmail(userContext.getYukonUser()));
        boolean isSmtpConfigured = StringUtils.isBlank(globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        model.addAttribute("isSmtpConfigured", isSmtpConfigured);
    }
    
    @RequestMapping(value="executeGroupCommand", method=RequestMethod.POST)
    public String executeGroupCommand(HttpServletRequest request, String groupName, String commandSelectValue, String commandString, String emailAddress, boolean sendEmail, YukonUserContext userContext, ModelMap map) throws ServletException {
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(group);
        boolean success = doCollectionCommand(request, deviceCollection, commandSelectValue, commandString, emailAddress, sendEmail, groupName, userContext, map);
        if (success) {
            return "redirect:resultDetail";
        } else {
            return "redirect:groupProcessing";
        }
    }

    @RequestMapping(value="executeCollectionCommand", method=RequestMethod.POST)
    public String executeCollectionCommand(HttpServletRequest request, DeviceCollection deviceCollection, String commandSelectValue, String commandString, final String emailAddress, boolean sendEmail, final YukonUserContext userContext, ModelMap map)
    throws ServletException {
        boolean success = doCollectionCommand(request, deviceCollection, commandSelectValue, commandString, emailAddress, sendEmail, null, userContext, map);
        if (success) {
            return "redirect:resultDetail";
        } else {
            map.addAllAttributes(deviceCollection.getCollectionParameters());
            return "redirect:collectionProcessing";
        }

    }
    
    @RequestMapping(value = "initCommands", method = RequestMethod.GET)
    public Map<String, Object> initCommands( String groupName, YukonUserContext userContext) 
            throws ServletException {
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(group);
        List<LiteCommand> commands = getCommands(deviceCollection.getDeviceList(), userContext.getYukonUser());
        Map<String, Object> commandList = new HashMap<>();
        commandList.put("commands", commands);
        return commandList;
    }

    public boolean doCollectionCommand(HttpServletRequest request, 
            DeviceCollection deviceCollection, 
            String commandSelectValue, 
            String commandString, 
            final String emailAddress, final boolean sendEmail,
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
            List<LiteCommand> commands = commandDao.filterCommandsForUser(meterCommands, userContext.getYukonUser());
            List<String> commandStrings = new MappingList<LiteCommand, String>(commands, new ObjectMapper<LiteCommand, String>() {
                @Override
                public String map(LiteCommand from)
                        throws ObjectMappingException {
                    return from.getCommand();
                }
            });
            if (!commandStrings.contains(commandString)) {
                throw NotAuthorizedException.trueProperty(userContext.getYukonUser(), YukonRoleProperty.EXECUTE_MANUAL_COMMAND);
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
                if (sendEmail) {
                    sendEmail(emailAddress, hostURL, result, userContext);
                }
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
            String resultUrl = hostUrl.toExternalForm() + "/group/commander/resultDetail?resultKey=" + result.getKey();
            
            int successCount = result.getResultHolder().getSuccessfulDevices().size();
            int failureCount = result.getResultHolder().getFailedDevices().size();
            String body = messageSourceAccessor.getMessage("yukon.web.commander.groupCommander.completionEmail.body", resultUrl, successCount, failureCount, result.getCommand());
            
            // build up email
            EmailAttachmentMessage email = 
                    new EmailAttachmentMessage(InternetAddress.parse(emailAddress), subject, body);
            
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
            
            emailService.sendMessage(email);
            log.info("Sent results email to " + emailAddress);
            
        } catch (IOException e) {
            log.error("Unable to output reports for scheduled emails", e);
        } catch (MessagingException e) {
            log.error("Unable to send scheduled emails", e);
        } catch (Exception e) {
            log.error("Received unknown error sending email", e);
        }
        
    }

    @RequestMapping("resultList")
    public void resultList(ModelMap map) {
        
        List<GroupCommandResult> completed = groupCommandExecutor.getCompleted();
        List<GroupCommandResult> pending = groupCommandExecutor.getPending();
        
        List<GroupCommandResult> allResults = new ArrayList<GroupCommandResult>(completed.size() + pending.size());
        allResults.addAll(pending);
        allResults.addAll(completed);
        
        //sorting by date
        Comparator<GroupCommandResult> comparator = resultsCompare;
        Collections.sort(allResults, comparator);
        
        map.addAttribute("resultList", allResults);
    }
    
    private Comparator<GroupCommandResult> resultsCompare = new Comparator<GroupCommandResult>() {
        @Override
        public int compare(GroupCommandResult o1, GroupCommandResult o2) {
            return o2.getStartTime().compareTo(o1.getStartTime());
        }
    };
    
    @RequestMapping("resultDetail")
    public void resultDetail(String resultKey, ModelMap map) throws ResultExpiredException {
        
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        map.addAttribute("result", result);
    }
    
    @RequestMapping("cancelCommands")
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
    
    @RequestMapping(value = { "errorsList", "successList" })
    public void results(YukonUserContext userContext, String resultKey, ModelMap map) {
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);

        Map<SimpleDevice, SpecificDeviceErrorDescription> errors = result.getCallback().getErrors();
        for (SimpleDevice device : errors.keySet()) {
            SpecificDeviceErrorDescription specificError = errors.get(device);
            errors.put(device, specificError);
        }
        map.addAttribute("result", result);
    }
}