package com.cannontech.web.logging;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * LogMenuController handles the retrieving of log
 * file names from the local and remote log directories,
 * and returns names in two lists thru the ModelAndView
 * @see view for this controller: menu.jsp
 * @author dharrington
 */
public class LogMenuController extends LogController {
    
    /**
    * Stores all log filenames from local and remote directories
    * in two lists and then saves the lists in the ModelAndView
    * @Override
    * @return a model and view containing lists of log files
    * names (a menu of file names)
    */
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("menu");
       
        //lists to hold log file names
        List<String> localLogList = new ArrayList<String>();
        List<String> remoteLogList = new ArrayList<String>();
               
        //create file filter to only allow log files thru
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml") || name.endsWith(".log");
            }
        };
        //extract local log file names and add to a list
        File[] localFiles = getLocalDir().listFiles(filter);
        if (!ArrayUtils.isEmpty(localFiles)) {
            for (File logFile : localFiles) {
                localLogList.add(logFile.getName());
            }
        }
        
        //extract remote file names and add to a list
        File[] remoteFiles = getRemoteDir().listFiles(filter);
        if (!ArrayUtils.isEmpty(remoteFiles)) {
            for (File logFile : remoteFiles) {
                remoteLogList.add(logFile.getName());
            }
        }

        //add local and remote log lists to model and view
        mav.addObject("localLogList", localLogList);
        mav.addObject("remoteLogList", remoteLogList);
        
        return mav;
    }
}
