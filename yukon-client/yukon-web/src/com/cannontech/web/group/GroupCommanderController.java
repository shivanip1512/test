package com.cannontech.web.group;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionInput;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.common.util.WebserverUrlResolver;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailAttachmentMessage;
import com.cannontech.tools.email.EmailFileDataSource;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.bulk.CollectionActionAlertHelper;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/commander/*")
@CheckRoleProperty(YukonRoleProperty.GROUP_COMMANDER)
public class GroupCommanderController {

    private static final Logger log = YukonLogManager.getLogger(GroupCommanderController.class);

    
    @Autowired private ContactDao contactDao;
    @Autowired private AlertService alertService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoCommandAuthorizationService commandAuthorizationService;
    @Autowired private CommandExecutionService commandExecutionService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private EmailService emailService;
    @Autowired private WebserverUrlResolver webserverUrlResolver;
    @Autowired private DateFormattingService dateFormattingService;
    
    private final static String baseKey = "yukon.web.modules.tools.bulk.sendCommand.";
    private String emailBasekey = "yukon.web.modules.tools.collectionActions.emailMessage";


    @RequestMapping(value = "collectionProcessing", method = RequestMethod.GET)
    public String collectionProcessing(DeviceCollection deviceCollection, YukonUserContext userContext, ModelMap model) {
        setupModel(deviceCollection, userContext, model);
        model.addAttribute("action", CollectionAction.SEND_COMMAND);
        model.addAttribute("actionInputs", "/WEB-INF/pages/group/commander/collectionProcessing.jsp");
        return "../collectionActions/collectionActionsHome.jsp";
    }
    
    @RequestMapping(value = "sendCommandInputs", method = RequestMethod.GET)
    public String sendCommandInputs(DeviceCollection deviceCollection, YukonUserContext userContext, ModelMap model) {
        setupModel(deviceCollection, userContext, model);
        return "commander/collectionProcessing.jsp";
    }
    
    private void setupModel(DeviceCollection deviceCollection, YukonUserContext userContext, ModelMap model) {
        List<LiteCommand> commands = deviceGroupService.getDeviceCommands(deviceCollection.getDeviceList(), userContext.getYukonUser());
        model.addAttribute("commands", commands);
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("email", contactDao.getUserEmail(userContext.getYukonUser()));
        boolean isSmtpConfigured = StringUtils.isBlank(globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        model.addAttribute("isSmtpConfigured", isSmtpConfigured);
    }

    @RequestMapping(value = "executeCollectionCommand", method = RequestMethod.POST)
    public String executeCollectionCommand(HttpServletRequest request, DeviceCollection collection,
            String commandFromDropdown, String commandSelectValue, String commandString, final String emailAddress, boolean sendEmail,
            final YukonUserContext context, ModelMap model, HttpServletResponse resp) {
        model.addAttribute("deviceCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(context);
        if (StringUtils.isBlank(commandString)) {
            model.addAttribute("errorMsg", messageSourceAccessor.getMessage(baseKey + "noCommandSpecified"));
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupModel(collection, context, model);
            return "commander/collectionProcessing.jsp";
        }

        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        if (!StringUtils.isBlank(commandSelectValue)) {
            userInputs.put(CollectionActionInput.SELECTED_COMMAND.name(), commandFromDropdown);
        }
        userInputs.put(CollectionActionInput.COMMAND.name(), commandString);
        final URL hostURL = ServletUtil.getHostURL(request);
        final String partialUrl = ServletUtil.createSafeUrl(request, "/collectionActions/progressReport/view");

        if (commandAuthorizationService.isAuthorized(context.getYukonUser(), commandString)) {
            SimpleCallback<CollectionActionResult> emailCallback = new SimpleCallback<CollectionActionResult>() {
                @Override
                public void handle(CollectionActionResult result) throws Exception {
                    if (sendEmail) {
                        sendEmail(emailAddress, hostURL, partialUrl, commandString, result, context, request);
                    }
                }
            };
            SimpleCallback<CollectionActionResult> alertCallback =
                CollectionActionAlertHelper.createAlert(AlertType.GROUP_COMMAND_COMPLETION, alertService,
                    messageSourceAccessor, emailCallback, request);
            int cacheKey = commandExecutionService.execute(CollectionAction.SEND_COMMAND, userInputs, collection,
                commandString, CommandRequestType.DEVICE, DeviceRequestType.GROUP_COMMAND, alertCallback, context);
            return "redirect:/collectionActions/progressReport/detail?key=" + cacheKey;
        } else {
            model.addAttribute("errorMsg", messageSourceAccessor.getMessage(baseKey + "notAuthorized"));
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupModel(collection, context, model);
            return "commander/collectionProcessing.jsp";
        }
    }

    private void sendEmail(String emailAddress, URL hostUrl, String partialUrl, String commandString, CollectionActionResult result,
                           YukonUserContext userContext, HttpServletRequest request) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        String subject = accessor.getMessage(emailBasekey + ".action") + ": " + accessor.getMessage(result.getAction().getFormatKey()) + "   " + System.lineSeparator();
        DecimalFormat format = new DecimalFormat("0.#");
        StringBuilder builder = new StringBuilder();
        builder.append(accessor.getMessage(emailBasekey + ".action") + ": " + accessor.getMessage(result.getAction().getFormatKey()) + "   " + System.lineSeparator());
        for (CollectionActionDetail detail : result.getAction().getDetails()) {
            int count = result.getDeviceCollection(detail).getDeviceCount();
            if (count > 0) {
                builder.append(accessor.getMessage(detail) + ": " + count + " ("
                    + format.format(result.getCounts().getPercentages().get(detail)) + "%)  " + "   " + System.lineSeparator());
            }
        }
        for (String inputKey : result.getInputs().getInputs().keySet()) {
            builder.append(inputKey + ": " + result.getInputs().getInputs().get(inputKey) + "   " + System.lineSeparator());
        }
        
        
        builder.append(accessor.getMessage(emailBasekey + ".devices") + ": "  +result.getInputs().getCollection().getDeviceCount() + "   " + System.lineSeparator());
        builder.append(accessor.getMessage(emailBasekey + ".startDateTime") + ": " + dateFormattingService.format(result.getStartTime(), DateFormatEnum.BOTH, userContext) + "   " + System.lineSeparator());
        builder.append(accessor.getMessage(emailBasekey + ".stopDateTime") + ": " + dateFormattingService.format(result.getStopTime(), DateFormatEnum.BOTH, userContext) + "   " + System.lineSeparator());
        builder.append(accessor.getMessage(emailBasekey + ".userName") + ": " + result.getExecution().getUserName() + "   " + System.lineSeparator());

        builder.append("The full results are available online at " +  webserverUrlResolver.getUrl(partialUrl + "?key=" + result.getCacheKey(), hostUrl.toExternalForm()) + "   " + System.lineSeparator());
        
        InternetAddress internetAddress = new InternetAddress();
        internetAddress.setAddress(emailAddress);
        EmailAttachmentMessage message;
        try {
            message = new EmailAttachmentMessage(new InternetAddress[]{internetAddress}, subject, builder.toString());
            if (result.hasLogFile()) {
                File file = result.getLogFile();
                EmailFileDataSource dataSource = new EmailFileDataSource(file);
                message.addAttachment(dataSource);
            } 
            emailService.sendMessage(message);
        } catch (MessagingException e) {
            log.error("caught exception in sendEmail", e);
        }
        
        }

}