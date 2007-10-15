package com.cannontech.web.command;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.util.ServletUtil;

public class CommandController extends MultiActionController {

    Logger log = YukonLogManager.getLogger(CommandController.class);
    private CommandDao commandDao = null;
    private DeviceGroupProviderDao deviceGroupDao = null;
    private DeviceGroupService deviceGroupService = null;

    private GroupCommandExecutor groupCommandExecutor = null;

    public void setCommandDao(CommandDao commandDao) {
        this.commandDao = commandDao;
    }

    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }

    public ModelAndView configure(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("configure.jsp");

        Vector commands = commandDao.getAllCommandsByCategory(DeviceTypes.STRING_MCT_410IL[0]);
        mav.addObject("commands", commands);

        List<? extends DeviceGroup> groups = deviceGroupDao.getAllGroups();
        mav.addObject("groups", groups);
        
        String defaultEmailSubject = "Group Processing completed";
        if(!groups.isEmpty()){
            defaultEmailSubject = "Group Processing for " + groups.get(0).getFullName() + " completed. (" + ((LiteCommand)commands.get(0)).getCommand() + ")";
        }
        String emailSubject = ServletRequestUtils.getStringParameter(request, "emailSubject", defaultEmailSubject);
        mav.addObject("emailSubject", emailSubject);

        return mav;
    }

    public ModelAndView executeCommand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView();

        String groupName = ServletRequestUtils.getStringParameter(request, "groupSelect");
        LiteYukonUser user = ServletUtil.getYukonUser(request);

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        mav.addObject("group", groupName);
        Set<Integer> deviceIds = deviceGroupService.getDeviceIds(Collections.singletonList(group));

        String command = ServletRequestUtils.getStringParameter(request, "commandString");
        mav.addObject("command", command);

        String emailAddresses = ServletRequestUtils.getStringParameter(request, "emailAddresses");
        mav.addObject("emailAddresses", emailAddresses);
        
        String emailSubject = ServletRequestUtils.getStringParameter(request, "emailSubject", "Group Processing for " + group.getFullName() + " completed. (" + command + ")");
        mav.addObject("emailSubject", emailSubject);
        
        boolean error = false;
        if (StringUtils.isBlank(emailAddresses)) {
            error = true;
            mav.addObject("errorMsg", "You must enter at least one valid email address.");
            mav.setViewName("configure.jsp");
        } else if (!checkEmailAddresses(emailAddresses)) {
            error = true;
            mav.addObject("errorMsg",
                          "One or more of the email addresses is not formatted correctly. " + "\nPlease enter a comma separated list of valid email addresses.");
            mav.setViewName("configure.jsp");
        } else if (StringUtils.isBlank(command)) {
            error = true;
            mav.addObject("errorMsg", "You must enter a valid command");
            mav.setViewName("configure.jsp");
        }

        if (error) {
            mav.setViewName("configure.jsp");

            Vector commands = commandDao.getAllCommandsByCategory(DeviceTypes.STRING_MCT_410IL[0]);
            mav.addObject("commands", commands);

            List<? extends DeviceGroup> groups = deviceGroupDao.getAllGroups();
            mav.addObject("groups", groups);
        } else {
            mav.setViewName("redirect:/spring/command/commandExecuted");

            try {
                groupCommandExecutor.execute(deviceIds, command, emailAddresses, emailSubject, user);
            } catch (MessagingException e) {
                log.warn("Unable to execute, putting error in model", e);
                mav.addObject("error", "Unable to send email");
            } catch (PaoAuthorizationException e) {
                log.warn("Unable to execute, putting error in model", e);
                mav.addObject("error", "Unable to execute specified commands");
            }
        }

        return mav;
    }

    public ModelAndView commandExecuted(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("commandExecuting.jsp");

        String command = ServletRequestUtils.getStringParameter(request, "command");
        mav.addObject("command", command);

        String group = ServletRequestUtils.getStringParameter(request, "group");
        mav.addObject("group", group);
        
        String emailAddresses = ServletRequestUtils.getStringParameter(request, "emailAddresses");
        mav.addObject("emailAddresses", emailAddresses);
        
        return mav;
    }

    /**
     * Helper method to check for email address format
     * @param emailAddresses - comma separated list of email addresses
     * @return True if all email addresses are formatted correctly
     */
    private boolean checkEmailAddresses(String emailAddresses) {

        String[] addresses = emailAddresses.split(",");

        for (String address : addresses) {

            if (!address.matches(".+@.+\\.[a-z]+")) {
                return false;
            }

        }

        return true;
    }

}
