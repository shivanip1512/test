package com.cannontech.common.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.CtiUtilities;

public class MasterConfigMap implements ConfigurationSource {
    private Map<String, String> configMap = new HashMap<String, String>();

    public MasterConfigMap() {
        super();
    }
    
    public void reset() {
        configMap.clear();
    }
    
    public void readYukonConfig() {
        String yukonBase = CtiUtilities.getYukonBase();
        String masterCfgPath = yukonBase + File.pathSeparator + 
            "Server" + File.pathSeparator + 
            "Config" + File.pathSeparator + 
            "master.cfg";
        try {
            FileReader reader = new FileReader(masterCfgPath);
            read(reader);
        } catch (FileNotFoundException e) {
            CTILogger.error(e);
        }
    }
    
    public void read(Reader reader) {
        Pattern pattern = Pattern.compile("^\\s*([^:#\\s]+)\\s*:\\s*([^#]+)");
        BufferedReader bufReader = new BufferedReader(reader);
        String line = null;
        try {
            while ((line = bufReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String key = matcher.group(1);
                    String value = matcher.group(2).trim();
                    if (configMap.containsKey(key)) {
                        CTILogger.warn("Duplicate key found while reading Master Config file: " + key);
                    }
                    configMap.put(key, value);
                }
            }
        } catch (IOException e) {
            CTILogger.error(e);
        }
    }
    
    public String getRequiredString(String key) {
        if (!configMap.containsKey(key)) {
            throw new BadConfigurationException("\"" + key + "\" was not found in the configuration map.");
        }
        return configMap.get(key);
    }

    public String getString(String key) {
        if (!configMap.containsKey(key)) {
            return "";
        }
        return configMap.get(key);
    }
    
}
