package com.cannontech.web.logging;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.util.ServletUtil;

/**
 * LogMenuController handles the retrieving of log
 * file names from the local and remote log directories,
 * and returns names in two lists thru the ModelAndView
 * @see view for this controller: menu.jsp
 * @author dharrington
 */
public class LogMenuController extends LogController {
    private AuthDao authDao;
    
    /**
    * Stores all log filenames from local and remote directories
    * in two lists and then saves the lists in the ModelAndView
    * @Override
    * @return a model and view containing lists of log files
    * names (a menu of file names)
    */
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        authDao.verifyRole(ServletUtil.getYukonUser(request), AdministratorRole.ROLEID);
        
        ModelAndView mav = new ModelAndView("menu");
       
        //lists to hold log file names
        List<String> localLogList = new ArrayList<String>();
        localLogList = populateFileList(localLogList, new String(), getLocalDir());
        java.util.Collections.reverse(localLogList);

        //add local list to model
        mav.addObject("localLogList", localLogList);
        
        return mav;
    }
    
    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
    private List<String> populateFileList(List<String> localLogList, String parent, File file) {
        if (!file.isDirectory()) return localLogList;
        
        if (!file.getName().equals("Log")) {
            parent = parent + file.getName() + System.getProperty("file.separator");
        }

        File[] localFiles = file.listFiles();
        for (File logFile : localFiles) {
            if (logFile.isDirectory()) {
                localLogList = populateFileList(localLogList, parent, logFile);
            }
            if (logFile.getName().endsWith("xml") || logFile.getName().endsWith("log")) {
                localLogList.add(parent + logFile.getName());
            }
        }
        return localLogList;
    }
}
