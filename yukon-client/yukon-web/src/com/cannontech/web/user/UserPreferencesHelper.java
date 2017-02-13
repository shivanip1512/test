package com.cannontech.web.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.cannontech.common.device.commands.CommandPriority;
import com.cannontech.core.users.model.PreferenceType;
import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.tools.commander.model.CommandParams;
import com.cannontech.web.user.service.UserPreferenceService;

@Component
public class UserPreferencesHelper {

    @Autowired private UserPreferenceService prefService;

    /**
     * This sets up both "userPreferenceMap" and "allPreferenceNames" within model.
     */
    public void setupUserPreferences(ModelMap model, LiteYukonUser user) {

        Map<String,UserPreference> prefMap = prefService.findUserPreferencesByPreferenceType(user, PreferenceType.EDITABLE);
        
        model.addAttribute("userPreferenceMap", prefMap);
        model.addAttribute("allPreferenceNames",
            UserPreferenceName.getUserPreferencesByType(PreferenceType.EDITABLE));
        
        Integer priority = Integer.valueOf(prefService.getPreference(user, UserPreferenceName.COMMANDER_PRIORITY));
        if (priority == null || !CommandPriority.isCommandPriorityValid(priority)) {
            priority = CommandPriority.maxPriority;
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