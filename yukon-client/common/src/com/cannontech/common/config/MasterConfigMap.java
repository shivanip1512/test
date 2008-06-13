package com.cannontech.common.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class MasterConfigMap implements ConfigurationSource {
    private Map<String, String> configMap = new HashMap<String, String>();
    private InputStream inputStream;
    private Logger log = YukonLogManager.getLogger(MasterConfigMap.class);

    public MasterConfigMap() {
        super();
    }
    
    public void reset() {
        log.debug("reseting");
        configMap.clear();
    }
        
    public void setConfigSource(InputStream is) {
        this.inputStream = is;
    }
    
    public void initialize() {
        log.debug("starting initialization");
        Pattern pattern = Pattern.compile("^\\s*([^:#\\s]+)\\s*:\\s*([^#]+)");
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        try {
            while ((line = bufReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    log.debug("Found line match: " + line);
                    String key = matcher.group(1);
                    String value = matcher.group(2).trim();
                    if (configMap.containsKey(key)) {
                        log.warn("Duplicate key found while reading Master Config file: " + key);
                    }
                    log.debug("Storing key='" + key + "', value='" + value + "'");
                    configMap.put(key, value);
                }
            }
            bufReader.close();
        } catch (IOException e) {
            log.error(e);
        }
    }
    
    public String getRequiredString(String key) throws UnknownKeyException {
        if (!configMap.containsKey(key)) {
            throw new UnknownKeyException(key);
        }
        String string = configMap.get(key);
        log.debug("Returning '" + string + "' for '" + key + "'");
        return string;
    }

    public String getString(String key) {
        if (!configMap.containsKey(key)) {
            return "";
        }
        String string = configMap.get(key);
        log.debug("Returning '" + string + "' for '" + key + "'");
        return string;
    }
    
}
