package com.cannontech.web.user;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CommandPriority;
import com.cannontech.core.users.model.PreferenceType;
import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.tools.commander.model.CommandParams;
import com.cannontech.web.user.service.UserPreferenceService;

@Component
public class UserPreferencesHelper {

    private static final Logger log = YukonLogManager.getLogger(UserPreferencesHelper.class);
    @Autowired private UserPreferenceService prefService;

    /**
     * This sets up both "userPreferenceMap" and "allPreferenceNames" within model.
     */
    public void setupUserPreferences(ModelMap model, LiteYukonUser user) {

        Map<UserPreferenceName, UserPreference> prefMap =
            prefService.getUserPreferencesByPreferenceType(user, PreferenceType.EDITABLE);
        
        model.addAttribute("userPreferenceMap", prefMap);
        model.addAttribute("allPreferenceNames",
            UserPreferenceName.getUserPreferencesByType(PreferenceType.EDITABLE));
        Integer priority = null;
        try {
            priority = Integer.valueOf(prefService.getPreference(user, UserPreferenceName.COMMANDER_PRIORITY));
            if (priority == null || !CommandPriority.isCommandPriorityValid(priority)) {
                priority = CommandPriority.maxPriority;
            }
        } catch (NumberFormatException e) {
            log.warn("Could not parse commander priority." + " : " + e.getMessage());
            log.warn("Setting commander priority to " + CommandPriority.minPriority);
            priority = CommandPriority.minPriority;
        }
        
        model.addAttribute("priority", priority);
        Boolean queueCmd =
            Boolean.valueOf(prefService.getPreference(user, UserPreferenceName.COMMANDER_QUEUE_COMMAND));
        if (queueCmd == null) {
            queueCmd = false; // default
        }
        model.addAttribute("queueCommand", queueCmd);
        CommandParams commandParams = new CommandParams();
        commandParams.setQueueCommand(queueCmd);
        model.addAttribute("commandParams", commandParams);
        model.addAttribute("minCmdPriority",CommandPriority.minPriority);
        model.addAttribute("maxCmdPriority", CommandPriority.maxPriority);
    }
}