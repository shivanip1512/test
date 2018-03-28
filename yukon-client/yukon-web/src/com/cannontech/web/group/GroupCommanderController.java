package com.cannontech.web.group;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.events.loggers.CommanderEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.CollectionActionAlertHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/commander/*")
@CheckRoleProperty(YukonRoleProperty.GROUP_COMMANDER)
public class GroupCommanderController {

    @Autowired private CommanderEventLogService commanderEventLogService;
    @Autowired private ContactDao contactDao;
    @Autowired private AlertService alertService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoCommandAuthorizationService commandAuthorizationService;
    @Autowired private CommandExecutionService commandExecutionService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private final static String baseKey = "yukon.web.modules.tools.bulk.sendCommand.";
    

    @RequestMapping(value = "collectionProcessing", method = RequestMethod.GET)
    public String collectionProcessing(DeviceCollection deviceCollection, YukonUserContext userContext, ModelMap model) {

        List<LiteCommand> commands = deviceGroupService.getDeviceCommands(deviceCollection.getDeviceList(), userContext.getYukonUser());
        model.addAttribute("commands", commands);
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("email", contactDao.getUserEmail(userContext.getYukonUser()));
        boolean isSmtpConfigured = StringUtils.isBlank(globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        model.addAttribute("isSmtpConfigured", isSmtpConfigured);
        return "commander/collectionProcessing.jsp";
    }

    @RequestMapping(value = "executeCollectionCommand", method = RequestMethod.POST)
    public String executeCollectionCommand(HttpServletRequest request, DeviceCollection collection,
            String commandFromDropdown, String commandSelectValue, String commandString, final String emailAddress, boolean sendEmail,
            final YukonUserContext context, ModelMap model, FlashScope flash) {
        
        model.addAttribute("deviceCollection", collection);
        model.addAllAttributes(collection.getCollectionParameters());
        if (StringUtils.isBlank(commandString)) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "noCommandSpecified"));
            return "redirect:collectionProcessing";
        }

        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        if (!StringUtils.isBlank(commandSelectValue)) {
            userInputs.put("Selected Command", commandFromDropdown);
        }
        userInputs.put("Command", commandString);

        if (commandAuthorizationService.isAuthorized(context.getYukonUser(), commandString)) {
            SimpleCallback<CollectionActionResult> emailCallback = new SimpleCallback<CollectionActionResult>() {
                @Override
                public void handle(CollectionActionResult result) throws Exception {
                    sendEmail(null, null, commandString, result, context);
                    commanderEventLogService.groupCommandCompleted(
                        result.getDetail(CollectionActionDetail.SUCCESS).getDevices().getDeviceCount(),
                        result.getCounts().getFailedCount(),
                        0,
                        result.getExecutionExceptionText(), String.valueOf(result.getCacheKey()));
                }
            };
            SimpleCallback<CollectionActionResult> alertCallback =
                CollectionActionAlertHelper.createAlert(AlertType.GROUP_COMMAND_COMPLETION, alertService,
                    messageResolver.getMessageSourceAccessor(context), emailCallback, request);
            int cacheKey = commandExecutionService.execute(CollectionAction.SEND_COMMAND, userInputs, collection,
                commandString, CommandRequestType.DEVICE, DeviceRequestType.GROUP_COMMAND, alertCallback, context);
            commanderEventLogService.groupCommandInitiated(collection.getDeviceCount(), commandString,
                String.valueOf(cacheKey), context.getYukonUser());
            return "redirect:/bulk/progressReport/detail?key=" + cacheKey;
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "notAuthorized"));
            return "redirect:collectionProcessing";
        }
    }

    private void sendEmail(String emailAddress, URL hostUrl, String commandString, CollectionActionResult result,
            YukonUserContext userContext) {

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        System.out.println("\"" + commandString + "\" completed.");
        DecimalFormat format = new DecimalFormat("0.#");
        StringBuilder builder = new StringBuilder();
        for (CollectionActionDetail detail : result.getAction().getDetails()) {
            int count = result.getDeviceCollection(detail).getDeviceCount();
            if (count > 0) {
                builder.append(accessor.getMessage(detail) + ":" + count + " ("
                    + format.format(result.getCounts().getPercentages().get(detail)) + "%)  ");
            }
        }
        System.out.println(builder.toString());
        System.out.println(
            "The full results are available online at /bulk/progressReport/detail?key=" + result.getCacheKey());
        System.out.println("---attach log file---");
        // attach to email
        if (result.hasLogFile()) {
            File file = result.getLogFile();
        }
    }

}