package com.cannontech.dbtools.updater;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.spring.YukonSpringHook;

public class MasterConfigUpdateHandler {

    private static final String configTokenRegex = "\\{master.cfg:[A-Za-z_]+:[A-Za-z0-9\\.\\-\\s,]+\\}";
    
    private static boolean isConfigurationCommand(String command) {
        return command.matches(".*" + configTokenRegex + ".*");
    }
    
    public static String handleConfigurationCommand(String command) {
        if (!isConfigurationCommand(command)) {
            return command;
        }
        
        YukonSpringHook.getContext();

        ConfigurationSource config = MasterConfigHelper.getConfiguration();
        
        String retval = command.substring(0, command.indexOf("{"));
        command = command.substring(command.indexOf("{"));
        
        int begin = command.indexOf(":");
        int end = command.indexOf(":", begin+1);
        
        while (begin > 0) { 
            String cparm = command.substring(begin+1, end);
            String value = config.getString(cparm) != null ? 
                                config.getString(cparm) :
                                command.substring(end+1, command.indexOf("}"));
            
            command = command.replaceFirst(configTokenRegex, value);
            
            // Are there more cparms to process?
            if (command.indexOf("{") != -1) {
                retval += command.substring(0, command.indexOf("{"));
                command = command.substring(command.indexOf("{"));

                begin = command.indexOf(":");
                end = command.indexOf(":", begin+1);
            } else {
                // All we have left is regular stuff.
                retval += command.substring(0);
                begin = -1;
            }
        }
        
        return retval;
    }
}
