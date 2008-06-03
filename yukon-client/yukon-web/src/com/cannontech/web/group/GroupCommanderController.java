package com.cannontech.web.group;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.util.ServletUtil;
import com.cannontech.util.Validator;

@SuppressWarnings("unchecked")
public class GroupCommanderController extends MultiActionController implements InitializingBean {

    private Logger log = YukonLogManager.getLogger(GroupCommanderController.class);

    private DeviceGroupService deviceGroupService = null;
    private DeviceGroupProviderDao deviceGroupDao = null;
    

    private CommandDao commandDao = null;
    private GroupCommandExecutor groupCommandExecutor = null;

    private PaoCommandAuthorizationService commandAuthorizationService = null;
    
    // available meter commands
    private Vector<LiteCommand> meterCommands;

    public void afterPropertiesSet() {
        this.meterCommands = commandDao.getAllCommandsByCategory(DeviceTypes.STRING_MCT_410IL[0]);
    }
    
    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @Required
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    @Required
    public void setCommandDao(CommandDao commandDao) {
        this.commandDao = commandDao;
    }

    @Required
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }

    @Required
	public void setCommandAuthorizationService(
			PaoCommandAuthorizationService commandAuthorizationService) {
		this.commandAuthorizationService = commandAuthorizationService;
	}

    public ModelAndView groupProcessing(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("groupProcessing.jsp");
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        List<LiteCommand> commands = commandDao.getAuthorizedCommands(meterCommands, user);
        mav.addObject("commands", commands);

        List<? extends DeviceGroup> groups = deviceGroupDao.getAllGroups();
        mav.addObject("groups", groups);

        String defaultEmailSubject = "Group Processing completed";
        if (!groups.isEmpty() && commands.size() > 0) {
            defaultEmailSubject = "Group Processing for " + groups.get(0).getFullName() + " completed. (" + (commands.get(0)).getCommand() + ")";
        }
        String emailSubject = ServletRequestUtils.getStringParameter(request,
                                                                     "emailSubject",
                                                                     defaultEmailSubject);
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

        String emailSubject = ServletRequestUtils.getStringParameter(request,
                                                                     "emailSubject",
                                                                     "Group Processing for " + group.getFullName() + " completed. (" + command + ")");
        mav.addObject("emailSubject", emailSubject);

        boolean error = false;
        if (StringUtils.isBlank(emailAddresses)) {
            error = true;
            mav.addObject("errorMsg", "You must enter at least one valid email address.");
        } else if (!checkEmailAddresses(emailAddresses)) {
            error = true;
            mav.addObject("errorMsg",
                          "One or more of the email addresses is not formatted correctly. " + "\nPlease enter a comma separated list of valid email addresses.");
        } else if (StringUtils.isBlank(command)) {
            error = true;
            mav.addObject("errorMsg", "You must enter a valid command");
        } else if (!commandAuthorizationService.isAuthorized(user, command)) {
        	error = true;
            mav.addObject("errorMsg", "User is not authorized to execute this command.");
        }
        
        
        if (error) {
            mav.setViewName("groupProcessing.jsp");

            List<LiteCommand> commands = commandDao.getAuthorizedCommands(meterCommands, user);
            mav.addObject("commands", commands);

            List<? extends DeviceGroup> groups = deviceGroupDao.getAllGroups();
            mav.addObject("groups", groups);
        } else {
            mav.setViewName("redirect:/spring/group/commander/commandExecuted");

            try {
                groupCommandExecutor.execute(deviceIds, command, emailAddresses, emailSubject, user);
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

            if (!Validator.isEmailAddress(address)) {
                return false;
            }
        }

        return true;
    }
}
