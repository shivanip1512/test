package com.cannontech.common.i18n;

import java.util.List;

import com.google.common.collect.Lists;

public class I18nBasenamesFactory {
    /**
     * In development mode, we want the i18n files to correspond to file path rather than class path
     * so they can be modified on the fly. The list returned by the factory method getBaseNames is a
     * list of i18n base names formatted accordingly in development mode and left untouched in
     * production mode.
     */
    public static List<String> getBaseNames(List<String> basenames){
        String wsDir = System.getProperty("com.cooperindustries.dev.wsdir");
        if (wsDir == null) {
            return basenames;
        }

        List<String> fileBasenames = Lists.newArrayList();
        for (String basename : basenames) {
            fileBasenames.add(basename.replace("classpath:", 
                    "file:" + wsDir + "/common/i18n/en_US/"));
        }
        return fileBasenames;
    }
}